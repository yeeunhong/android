package kr.co.uniquantum.ui.activity;

import java.util.ArrayList;
import java.util.List;

import kr.co.uniquantum.Global;
import kr.co.uniquantum.R;
import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.UIMapOverlayViewEx;
import kr.co.uniquantum.ui.UIControls.*;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import kr.co.uniquantum.ui.views.search.favorite.FavoriteSQLHelper;
import kr.co.uniquantum.ui.views.search.recent.RecentSQLHelper;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class SearchResultOnMap extends MapActivity implements OnClickListener 
{
	final private int MENU_START_ROUTE 		= 10000;
	final private int MENU_SAVE_FAVORITE 	= MENU_START_ROUTE + 1;
	final private int MENU_PLAY_GPS_LOG		= MENU_SAVE_FAVORITE + 1;
	
	final private int ID_PROGRESS_DLG = 0;
	
	final private int ID_FAVORITE_LAYOUT	= 1000;
	final private int ID_FAVORITE_EDIT_VIEW = 1100;
	final private int ID_FAVORITE_SAVE_BTN	= 1200;
	final private int ID_BTN_GPS_LOG		= MENU_PLAY_GPS_LOG;
	
	private GPSLocationReceiver gps_receiver = null;
	
	private MapView mapView = null;
	private MapController mapController = null;
	private Projection projection = null;
	private double x = 127.034618;
	private double y = 37.509139;
	private float speed = 0.0f;
	private int angle = 0;
	private String log_time;
	private double route_x = x;
	private double route_y = y;
	private String locationName = "";
	private String locationAddress = "";
	
	private ProgressDialog progressDialog;
	private SharedPreferences preferences = null;
	
	private LinearLayout favoriteLayout = null;
	private UIPanel address_panel = null;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
  
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView( R.layout.search_result_map );
		
		int posType = 0;
		boolean driveMode = false;
		Intent intent = getIntent();
		if( intent.getExtras() == null )
		{
			x = 127.034618;
			y = 37.509139;
			route_x = x;
			route_y = y;
		}
		else
		{
			x = intent.getDoubleExtra( "x", 127.034618 );
			y = intent.getDoubleExtra( "y", 37.509139 );
			route_x = intent.getDoubleExtra( "route_x", x );
			route_y = intent.getDoubleExtra( "route_y", y );
			posType = intent.getIntExtra( "posType", 0 );
			driveMode = intent.getBooleanExtra( "driveMode", false );
		}
				
		mapView = ( MapView ) findViewById( R.id.SearchResultMap );
				
		mapView.setStreetView( true );
		mapView.setSatellite( false );
		mapView.setTraffic( false );
		mapView.setBuiltInZoomControls(true);
		
		mapController = mapView.getController();
		projection = mapView.getProjection();
		
		Double dLon = .0;
		Double dLat = .0;
		
		if( posType == 0 )		// BSL
		{
			double wgs[] = Global.BSL2WGS( x , y, 1 ); 
			
			dLon = wgs[0] * 1E6;
			dLat = wgs[1] * 1E6;
			
			GeoPoint point = new GeoPoint( dLat.intValue(), dLon.intValue() );
			mapController.setCenter( point );
		}
		else if( posType == 1)	// WGS
		{
			dLon = x * 1E6;
			dLat = y * 1E6;
			
			GeoPoint point = new GeoPoint( dLat.intValue(), dLon.intValue() );
			mapController.setCenter( point );			
		}
		
		mapController.setZoom( 17 );
		
		preferences = getSharedPreferences( "Search", Activity.MODE_PRIVATE );
		
		if( !driveMode )
		{
			//Drawable marker=getResources().getDrawable( android.R.drawable.ic_menu_myplaces );   
			Drawable marker = getResources().getDrawable( R.drawable.pointonmap );
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			mapView.getOverlays().add(
					new InterestingLocations( marker, dLat.intValue()+20, dLon.intValue()+250 ));
			
			int nSearchMode = preferences.getInt( "SearchMode" , SearchModeEnum.SEARCH_MODE_NONE );
			
			locationAddress = preferences.getString( "SidoName", "" );
			locationAddress += " " + preferences.getString( "SigunguName", "" );
			
			switch( nSearchMode )
			{
			case SearchModeEnum.SEARCH_MODE_DONG :
				locationName = preferences.getString( "DongName", "" );
				locationName += " " + preferences.getString( "BunjiValue", "" );
				locationName += " " + preferences.getString( "HoValue", "" );
				locationAddress += " " + locationName;
				break;
			case SearchModeEnum.SEARCH_MODE_ROAD :
				locationName = preferences.getString( "RoadName", "" );
				locationName += " " + preferences.getString( "BunjiValue", "" );
				locationName += " " + preferences.getString( "HoValue", "" );
				locationAddress += " " + locationName;
				break;
			case SearchModeEnum.SEARCH_MODE_APT :
				locationName = preferences.getString( "DongName", "" );
				locationName += " " + preferences.getString( "AptName", "" );
				locationAddress += " " + locationName;
				break;
			case SearchModeEnum.SEARCH_MODE_POI :
				locationAddress += " " + preferences.getString( "DongName", "" );
				locationAddress += " " + preferences.getString( "BunjiValue", "" );
				locationAddress += " " + preferences.getString( "HoValue", "" );
				locationName = intent.getStringExtra( "name" );
				break;
			default : 
				locationAddress = "";
				locationName = "";
				break;
			}
		}
		
		AddFavoriteLayout();
				
		IntentFilter filter = new IntentFilter( "kr.co.uniquantum.ui.activity.action.GPS_LOCATON" );
		gps_receiver = new GPSLocationReceiver();
		registerReceiver( gps_receiver, filter );
		
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add( new MapPosOverlay());
		
		UIMapOverlayViewEx orverlay = new UIMapOverlayViewEx( mapView );
		//orverlay.controls.AddButton( 5, 350, 150, 25, "Play Gps Log File", ID_BTN_GPS_LOG, this );
		address_panel = orverlay.controls.AddPanel( 5, 400, 300, 25, "여기는 주소가 나오는 곳입니다.", ID_BTN_GPS_LOG + 1 ); 
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize( 16 );
		paint.setFakeBoldText( true );
		paint.setColor( Color.BLACK );
		paint.setTextAlign( Paint.Align.LEFT );
		address_panel.setTextPaint( paint  );
		
		overlays.add( orverlay );
		
		this.checkPosSetAddress( x, y );
		
		mapView.postInvalidate();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver( gps_receiver );
		super.onDestroy();
	}

	private void AddFavoriteLayout()
	{
		favoriteLayout = new LinearLayout( this );
		favoriteLayout.setId( ID_FAVORITE_LAYOUT );
				
		EditText et = new EditText( this );
		et.setId( ID_FAVORITE_EDIT_VIEW );
		et.setText( locationName );
		favoriteLayout.addView( et, 
				new LinearLayout.LayoutParams( 
						LinearLayout.LayoutParams.WRAP_CONTENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT, 9 ) );
		
		Button bt = new Button( this );
		bt.setId( ID_FAVORITE_SAVE_BTN );
		bt.setText( "저장" );
		bt.setOnClickListener( this );
		favoriteLayout.addView( bt, 
				new LinearLayout.LayoutParams( 
						LinearLayout.LayoutParams.WRAP_CONTENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT, 1 ) );
		
		FrameLayout fl = ( FrameLayout ) findViewById( R.id.SearchResultMapLayout );
		fl.addView( favoriteLayout );
		
		et.setOnKeyListener( 
			new View.OnKeyListener() 
			{
				public boolean onKey(View v, int keyCode, KeyEvent event) 
				{
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) 
					{
						Button bt = ( Button ) favoriteLayout.findViewById( ID_FAVORITE_SAVE_BTN );
						if( bt != null ) onClick( bt );
						return true;
					}
					return false;
				}
			}
		);
		
		favoriteLayout.setVisibility( View.GONE );		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch( id )
		{
		case ID_PROGRESS_DLG :
			progressDialog = new ProgressDialog(this);
    		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		progressDialog.setMessage("경로를 탐색 중입니다...");
    		return progressDialog;
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
		int group_id = 0;
		menu.add( group_id, MENU_START_ROUTE,   Menu.NONE, "여기로 경로탐색" );
		menu.add( group_id, MENU_SAVE_FAVORITE, Menu.NONE, "즐겨찾기에 추가" );
		menu.add( group_id, ID_BTN_GPS_LOG, 	Menu.NONE, "GPS LogPlay" );
		return true; 
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		int x_center = mapView.getWidth() / 2;
		int y_center = mapView.getHeight() / 2;
			
		switch( keyCode )
		{
		case KeyEvent.KEYCODE_DPAD_LEFT 	: x_center -= 10; break;
		case KeyEvent.KEYCODE_DPAD_RIGHT 	: x_center += 10; break;
		case KeyEvent.KEYCODE_DPAD_UP 		: y_center -= 10; break;
		case KeyEvent.KEYCODE_DPAD_DOWN 	: y_center += 10; break;
		case KeyEvent.KEYCODE_DPAD_CENTER 	: break;
		}
		
		GeoPoint gp = projection.fromPixels( x_center, y_center );
		x = gp.getLongitudeE6() / 1E6;
		y = gp.getLatitudeE6()  / 1E6;
				 
		this.checkPosSetAddress( x, y );
		
		mapController.setCenter( gp );
		mapView.postInvalidate();
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case ID_FAVORITE_SAVE_BTN :
			if( favoriteLayout != null )
			{
				favoriteLayout.setVisibility( View.GONE );
				EditText et = ( EditText ) favoriteLayout.findViewById( ID_FAVORITE_EDIT_VIEW );
				Global.ShowSoftKeyboard( this, et, false );
								 
				SQLiteDatabase sqlite = new FavoriteSQLHelper( SearchResultOnMap.this ).getWritableDatabase(); 
				saveFavoriteData( sqlite, FavoriteSQLHelper.DB_TABLE_FAVORITE );
				
				Toast.makeText( this, "즐겨 찾기에 저장하였습니다.", Toast.LENGTH_SHORT ).show();
			}
			else
				Toast.makeText( this, "즐겨 찾기에 저장 오류 입니다..", Toast.LENGTH_SHORT ).show();
			
			break;
		
		case ID_BTN_GPS_LOG :
			Intent intent = new Intent( this, UIScreen.class );
			intent.putExtra( "viewName", "GPSFileList" );
			startActivity( intent );
			break;
		}		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		
		switch( item.getItemId())
		{
		case MENU_START_ROUTE :
			new Thread( null, route_search_thread ).start();
			showDialog( ID_PROGRESS_DLG );
			break;
			
		case MENU_SAVE_FAVORITE :
			if( favoriteLayout != null ) 
			{
				favoriteLayout.setVisibility( View.VISIBLE );
				EditText et = ( EditText ) favoriteLayout.findViewById( ID_FAVORITE_EDIT_VIEW );
				et.requestFocus();
				
				Global.ShowSoftKeyboard( this, et, true );
			}
			else 
				Toast.makeText( this, "즐겨 찾기에 저장 오류 입니다..", Toast.LENGTH_SHORT ).show();
			break;
			
		case MENU_PLAY_GPS_LOG :
			Intent intent = new Intent( this, UIScreen.class );
			intent.putExtra( "viewName", "GPSFileList" );
			startActivity( intent );
			break;
		}
		
		return false;
	}

	class InterestingLocations extends ItemizedOverlay<OverlayItem>
	{    
		private List<OverlayItem> locations = new ArrayList<OverlayItem>();  
		private Drawable marker;  
		public InterestingLocations(Drawable defaultMarker, int LatitudeE6, int LongitudeE6) 
		{   
			super(defaultMarker);   // TODO Auto-generated constructor stub   
			this.marker=defaultMarker;   // create locations of interest   
			GeoPoint myPlace = new GeoPoint(LatitudeE6,LongitudeE6);   
			locations.add(new OverlayItem(myPlace , "My Place", "My Place"));   
			populate();  
		}  
		
		@Override  protected OverlayItem createItem(int i) 
		{   // TODO Auto-generated method stub   
			return locations.get(i);  
		}  
		
		@Override  public int size() 
		{   // TODO Auto-generated method stub   
			return locations.size();  
		}  
		
		@Override  public void draw(Canvas canvas, MapView mapView, boolean shadow) 
		{   // TODO Auto-generated method stub   
			super.draw(canvas, mapView, shadow);      
			boundCenterBottom(marker);
			
			Paint paint = new Paint();
			canvas.drawText( locationAddress, 10, 100, paint);
		} 
	}
	
	private Runnable route_search_thread = new Runnable() 
	{
		public void run()
		{
			try {
				for( int i = 0; i < 10; ++i )
				{
					Thread.sleep( 300 );
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			handle_end_route_search.sendMessage( handle_end_route_search.obtainMessage());
		}
	};
	
	private Handler handle_end_route_search = new Handler(){
		public void handleMessage( Message msg )
		{
			progressDialog.dismiss();
			SearchResultOnMap.this.removeDialog( ID_PROGRESS_DLG );
			
			favoriteLayout.setVisibility( View.GONE );
			EditText et = ( EditText ) favoriteLayout.findViewById( ID_FAVORITE_EDIT_VIEW );
			et.setText( locationName );
			
			saveFavoriteData( 
				new RecentSQLHelper( SearchResultOnMap.this ).getWritableDatabase(),
				RecentSQLHelper.DB_TABLE_RECENT
			);
		}
	};
	
	private void checkPosSetAddress( double _x, double _y )
	{
		final double _60x60x120_ = 60*60*120;
		
		SearchModule search_module = SearchModule.getInstance(); 
		search_module.SetLocationPos((long)( _x * _60x60x120_), (long)( _y * _60x60x120_ ));
		long nDCode = search_module.ChangedLocationCode();
		
		if( nDCode != 0 )
		{
			String sidoName 	= search_module.GetSidoNameByDCode( nDCode );
			String sigunguName 	= search_module.GetSigunguNameByDCode( nDCode );
			String dongName 	= search_module.GetDongNameByDCode( nDCode );
			
			SharedPreferences.Editor edit = preferences.edit();
			edit.putString( "SidoName", sidoName );
			edit.putString( "SigunguName", sigunguName );
			edit.putString( "DongName", dongName );
			edit.putString( "BunjiValue", "0" );
			edit.putString( "HoValue", "0" );
			edit.commit();
			
			address_panel.setText(  sidoName+ " " + sigunguName + " " + dongName );
		}
	}
	
	private void saveFavoriteData( SQLiteDatabase sqlite, String tableName )
	{
		preferences = getSharedPreferences( "Search", Activity.MODE_PRIVATE );
		int nSearchType = preferences.getInt( "SearchMode" , SearchModeEnum.SEARCH_MODE_NONE );
		int nIconIdx = 1;
		
		//SQLiteDatabase sqlite = new FavoriteSQLHelper( this ).getWritableDatabase();
		//FavoriteSQLHelper.DB_TABLE_FAVORITE
		String delQuery = String.format( 
				"delete from %s where lon=%f and lat=%f and search_type=%d", 
					tableName, x, y,					
					nSearchType
					);
		try
		{
			sqlite.execSQL( delQuery );
		}
		catch( SQLiteException ex )
		{
			 
		}
		
		EditText et = ( EditText ) favoriteLayout.findViewById( ID_FAVORITE_EDIT_VIEW );
				 
		String insQuery = null;
		String format = "insert into %s (name,lon,lat,route_lon,route_lat,sido,sigungu,dong,bunji,ho,phone,search_type,icon) values('%s',%f,%f,%f,%f,'%s','%s','%s',%s,%s,'%s',%d,%d);";
		
		switch( nSearchType )
		{
		case SearchModeEnum.SEARCH_MODE_NONE :
		case SearchModeEnum.SEARCH_MODE_DONG :
			insQuery = String.format( format, 
				tableName, 
				et.getText(),
				x, y,
				route_x, route_y,
				preferences.getString( "SidoName", "" ),
				preferences.getString( "SigunguName", "" ),
				preferences.getString( "DongName", "" ),
				preferences.getString( "BunjiValue", "0" ),
				preferences.getString( "HoValue", "0" ),
				"",
				nSearchType,
				nIconIdx
			);
			break;
			
		case SearchModeEnum.SEARCH_MODE_ROAD :
			insQuery = String.format( format, 
				tableName, 
				et.getText(),
				x, y,
				route_x, route_y,
				preferences.getString( "SidoName", "" ),
				preferences.getString( "SigunguName", "" ),
				preferences.getString( "RoadName", "" ),
				preferences.getString( "BunjiValue", "0" ),
				preferences.getString( "HoValue", "0" ),
				"",
				nSearchType,
				nIconIdx
			);
			break;
			
		case SearchModeEnum.SEARCH_MODE_APT :
			insQuery = String.format( format, 
				tableName, 
				et.getText(),
				x, y,
				route_x, route_y,
				preferences.getString( "SidoName", "" ),
				preferences.getString( "SigunguName", "" ),
				preferences.getString( "DongName", "" ),
				0,
				0,
				preferences.getString( "AptName", "" ),
				nSearchType,
				nIconIdx
			);
			break;
			
		case SearchModeEnum.SEARCH_MODE_POI :
			insQuery = String.format( format, 
				tableName, 
				et.getText(),
				x, y,
				route_x, route_y,
				preferences.getString( "SidoName", "" ),
				preferences.getString( "SigunguName", "" ),
				preferences.getString( "DongName", "" ),
				preferences.getString( "BunjiValue", "0" ),
				preferences.getString( "HoValue", "0" ),
				preferences.getString( "CallNumber", "00-0000-0000" ),
				nSearchType,
				nIconIdx
			);
			break;
		}
				
		sqlite.execSQL( insQuery );
		sqlite.close();
	}
	
	public class GPSLocationReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			//long lon = arg1.getLongExtra( "lon", 0 );
			//long lat = arg1.getLongExtra( "lat", 0 );
			x = arg1.getDoubleExtra( "org_lon", .0 );
			y = arg1.getDoubleExtra( "org_lat", .0 );
			route_x = x;
			route_y = y;
			
			speed = arg1.getFloatExtra( "speed", .0f );
			angle = arg1.getIntExtra( "angle", 0 );
			//double wgs[] = Global.BSL2WGS( x , y, 1 ); 
			
			checkPosSetAddress( x, y );
								
			log_time = arg1.getStringExtra( "time" );
			
			Double dLon = x/*wgs[0]*/ * 1E6;
			Double dLat = y/*wgs[1]*/ * 1E6;
			
			GeoPoint point = new GeoPoint( dLat.intValue(), dLon.intValue());
			
			//mapController.animateTo( point );
			mapController.setCenter( point );
			mapView.postInvalidate();
			
		}
	}
	
	public class MapPosOverlay extends Overlay
	{
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			if( shadow == false )
			{
				final int mRadius = 5;
				Paint textPaint = new Paint();
				textPaint.setAntiAlias( true );
				textPaint.setFakeBoldText( true );
				textPaint.setARGB( 255, 0, 0, 0 );
				textPaint.setTextSize( 16 );
				canvas.drawText( String.format("lon:%f, lat:%f",x,y), 10, 20, textPaint );
				canvas.drawText( String.format("speed:%.2f mps",speed ), 10, 40, textPaint );
				canvas.drawText( String.format("angle:%d",angle ), 10, 60, textPaint );
				canvas.drawText( String.format("time:%s",log_time ), 10, 80, textPaint );
								
				Point point = new Point();
				GeoPoint gp = new GeoPoint((int)( y*1E6), (int)(x*1E6) );
				
				projection.toPixels( gp , point );
				
				Paint paint = new Paint();
				paint.setARGB( 255, 255, 0, 0 );
				paint.setAntiAlias( true );
				paint.setFakeBoldText( true );
				
				Paint backPaint = new Paint();
				backPaint.setARGB( 180, 50, 50, 50 );
				backPaint.setAntiAlias( true );
				
				RectF oval = new RectF( point.x - mRadius, point.y - mRadius,
						point.x + mRadius, point.y + mRadius );
				RectF backRect = new RectF( point.x + 2 + mRadius, point.y - 3 * mRadius,
						point.x + 65, point.y + mRadius ); 
				
				canvas.drawOval( oval, paint );
				paint.setARGB( 255, 255, 255, 255 );
				
				canvas.drawRoundRect( backRect, 5, 5, backPaint);
				canvas.drawText( "Here I am", point.x + 2 * mRadius, point.y, paint);
			}
			
			super.draw( canvas, mapView, shadow );
		}

		@Override
		public boolean onTouchEvent(MotionEvent e, MapView mapView) {
			// TODO Auto-generated method stub
			if( e.getAction() == MotionEvent.ACTION_MOVE || e.getAction() == MotionEvent.ACTION_UP )
			{
				
				
				int width = mapView.getWidth();
				int height = mapView.getHeight();
				
				GeoPoint gp = projection.fromPixels(width/2, height/2);
				x = gp.getLongitudeE6() / 1E6;
				y = gp.getLatitudeE6()  / 1E6;
				route_x = x;
				route_y = y;
								
				checkPosSetAddress( x, y );
			}
			
			return super.onTouchEvent(e, mapView);
		}


		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
