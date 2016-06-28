package com.uniquantum.www;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import kr.co.uniquantum.Global;
import kr.co.uniquantum.R;
import kr.co.uniquantum.ui.activity.UIScreen;
import kr.co.uniquantum.ui.UIControls.*;

public class UMapViewActivity 
	extends Activity
	implements DrawingFacade.EventListener, OnClickListener {
    
	final private int MENU_START_ROUTE 		= 10000;
	final private int MENU_SAVE_FAVORITE 	= MENU_START_ROUTE + 1;
	final private int MENU_PLAY_GPS_LOG		= MENU_SAVE_FAVORITE + 1;
	final private int ID_BTN_GPS_LOG		= MENU_PLAY_GPS_LOG;
	
	private static final String TAG = "UMapViewer";
	private static Bitmap mBitmap;
	private static ChoImageView mView;
	private static float m_oldx;
	private static float m_oldy;    
	private static Handler m_handler = new Handler();
	             
	private GPSLocationReceiver gps_receiver = null;
		
	@Override         
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mView = new ChoImageView( this );
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView( mView ); 
                       
       // loadlibrary();           
		DrawingFacade.setListener(this);         
                     
		//sdcard 가 사용가능해야 한다. 
        String ext = Environment.getExternalStorageState();
        if (!ext.equals(Environment.MEDIA_MOUNTED)) {      
        	System.exit(-1);           
        }                           
        String sdcardpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                           
	    DrawingFacade.DrawingCreate(sdcardpath);  
	                                   
        //mView = (ChoImageView)findViewById( R.id.umap ); 
        mView.setListener(new ChoImageView.EventListener() {
			@Override         
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
			  	String text = String.format("size changed:(%d, %d), (%d, %d)", w, h, oldw, oldh);
				Log.d(TAG, text); 
				mBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
				int res = DrawingFacade.DrawingResize(w, h);
				res++;           
			}                 
		});           
        
        mView.controls.AddImgButton( 5, 100, R.drawable.zoom_plus, 3, null, 100, this );
        mView.controls.AddImgButton( 5, 146, R.drawable.zoom_minus, 3, null, 200, this );
        
        UIImgButton btn = mView.controls.AddImgButton( 5, 194, R.drawable.direct_mode, 6, 0, 2, null, 1000, this );
        btn.setVisible( false );
        btn = mView.controls.AddImgButton( 5, 194, R.drawable.direct_mode, 6, 2, 2, null, 1100, this );
        btn.setVisible( false );
        btn = mView.controls.AddImgButton( 5, 194, R.drawable.direct_mode, 6, 4, 2, null, 1200, this );
                
		IntentFilter filter = new IntentFilter( "kr.co.uniquantum.ui.activity.action.GPS_LOCATON" );
		gps_receiver = new GPSLocationReceiver();
		registerReceiver( gps_receiver, filter );
	}
		
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		UIImgButton btn = null;
		switch( v.getId() )
		{
		case 100 : Toast.makeText( UMapViewActivity.this, "ZoomIn", Toast.LENGTH_SHORT ).show();
			break;
		case 200 : Toast.makeText( UMapViewActivity.this, "ZoomOut", Toast.LENGTH_SHORT ).show();
			break;
		case 1000 : Toast.makeText( UMapViewActivity.this, "Bird ViewMode", Toast.LENGTH_SHORT ).show();
			DrawingFacade.changeDirectionMode( 2 );	// BirdView
			btn = mView.controls.GetImgButton( 1000 ); btn.setVisible( false );
			btn = mView.controls.GetImgButton( 1100 ); btn.setVisible( false );
			btn = mView.controls.GetImgButton( 1200 ); btn.setVisible( true );
			break;
		case 1100 : Toast.makeText( UMapViewActivity.this, "HeadingUP ViewMode", Toast.LENGTH_SHORT ).show();
			DrawingFacade.changeDirectionMode( 1 );	// HeadingUP
			btn = mView.controls.GetImgButton( 1000 ); btn.setVisible( true );
			btn = mView.controls.GetImgButton( 1100 ); btn.setVisible( false );
			btn = mView.controls.GetImgButton( 1200 ); btn.setVisible( false );	
			break;
		case 1200 : Toast.makeText( UMapViewActivity.this, "NorthUP ViewMode", Toast.LENGTH_SHORT ).show();
			DrawingFacade.changeDirectionMode( 0 );	// NorthUP
			btn = mView.controls.GetImgButton( 1000 ); btn.setVisible( false );
			btn = mView.controls.GetImgButton( 1100 ); btn.setVisible( true );
			btn = mView.controls.GetImgButton( 1200 ); btn.setVisible( false );
			break;
		}
		
		if( btn != null ) mView.invalidate( btn.getRect() );
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver( gps_receiver );
		super.onDestroy();
	}
	
	/*
	static public void loadlibrary() {
		String name = "chomap"; //libumap.so
		final String LD_PATH = System.getProperty("java.library.path");
		Log.d(TAG, "Trying to load library " + name + " from LD_PATH: " + LD_PATH);
		try {
			System.loadLibrary("coreModule");
		} catch (UnsatisfiedLinkError e) {
			Log.e(TAG, e.toString());  
		}  
	}   
	  */
	static
	{
		System.loadLibrary("chomap");
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	int conv_keytype = getKeyClickType(keyCode);
    	int res = DrawingFacade.keyClicked(conv_keytype);
    	   
    	res++;
    	return super.onKeyDown(keyCode, event);
    }
	      
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	float x = event.getX();
    	float y = event.getY();
    	
    	if (event.getAction() == MotionEvent.ACTION_MOVE) {
    		float dx = x - m_oldx;
    		float dy = y - m_oldy;
    		
    		int res = DrawingFacade.moved((int)dx, (int)dy); 
    		res++; 
        } 
    	 
    	m_oldx = x;
		m_oldy = y;
		
    	return super.onTouchEvent(event);
    }
    
     
	
	 /******************************************************
    * Native Events
    ******************************************************/
	//@Override
	@Override
	public void OnSysError(final String text) {
		ChoUtil.MessageBox(this, "System Message", text);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
		System.exit(-1);
	}
     
	//@Override
	@Override
	public void OnImageUpdate(final int[] pixels, final int x ,final int y, final int w, final int h) {
		mBitmap.setPixels(pixels, 0, w, x, y, w, h);
		try { 
			mView.setImageBitmap(mBitmap);
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		        
		mView.invalidate();
		
//		m_handler.post(new Runnable() { 
//			@Override
//			public void run() {    
//				mBitmap.setPixels(pixels, 0, w, x, y, w, h);
//				try {
//					mView.setImageBitmap(mBitmap);
//				} catch (Throwable e) {
//					e.printStackTrace(); 
//				}		
//			}    
//		});   
	}      
       
	//@Override    
	@Override
	public void OnMessage(final String text) {
		//Log.i(TAG, "** Wolf Message: " + text);
		   
		m_handler.post(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "Message: " + text);  
			}    
		});         
		  
	}  
	 
    private int getKeyClickType(int keyCode) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:		return 1;
		case KeyEvent.KEYCODE_DPAD_DOWN:	return 2;
		case KeyEvent.KEYCODE_DPAD_LEFT:	return 3;
		case KeyEvent.KEYCODE_DPAD_RIGHT:	return 4;
		default: 							return 0;
		}
    } 

    public class GPSLocationReceiver extends BroadcastReceiver
	{   
		@Override 
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			final double _60x60x120_ = 60*60*120;
			 
			double bsl[] = Global.WGS2BSL( 
					arg1.getDoubleExtra( "org_lon", 0.0 ),
					arg1.getDoubleExtra( "org_lat", 0.0 ), 1 ); 
			
			long lon = (long)( _60x60x120_ * bsl[0] );
			long lat = (long)( _60x60x120_ * bsl[1] );
			int angle = arg1.getIntExtra( "angle", 0 );
			
			if( lon == 0 || lat == 0 ) return;
			
			DrawingFacade.SetLocationPos( lon, lat, angle );
		} 
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		
		switch( item.getItemId())
		{
		case MENU_START_ROUTE :
			//new Thread( null, route_search_thread ).start();
			//showDialog( ID_PROGRESS_DLG );
			break;
			
		case MENU_SAVE_FAVORITE :
			/*if( favoriteLayout != null ) 
			{
				favoriteLayout.setVisibility( View.VISIBLE );
				EditText et = ( EditText ) favoriteLayout.findViewById( ID_FAVORITE_EDIT_VIEW );
				et.requestFocus();
				
				Global.ShowSoftKeyboard( this, et, true );
			}
			else 
				Toast.makeText( this, "즐겨 찾기에 저장 오류 입니다..", Toast.LENGTH_SHORT ).show();*/
			break;
			
		case MENU_PLAY_GPS_LOG :
			Intent intent = new Intent( this, UIScreen.class );
			intent.putExtra( "viewName", "GPSFileList" );
			startActivity( intent );
			break;
		}
		
		return false;
	}
}