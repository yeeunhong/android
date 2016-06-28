package kr.co.uniquantum.ui.views.search.poi.name;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import kr.co.uniquantum.Global;
import kr.co.uniquantum.search.POI_DATA;
import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.UITabView;
import kr.co.uniquantum.ui.activity.UIScreen;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;

public class SearchPoiResultNnC extends UIScreenView implements OnItemClickListener
{
	//final private int ID_PROGRESS_DLG = 0;
	
	private SearchModule search_module = null;
	private String searchText = null;
	
	private ProgressDialog progressDialog = null;
	
	private UITabView tabView = null;
	private SearchPoiNameResult nameResult = null;
	private SearchPoiNameResultCate cateResult = null;
	
	public SearchPoiResultNnC(Context context, SearchModule _search_module,  String text ) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module 	= _search_module;
		searchText 		= text;
		
		progressDialog = new ProgressDialog( context );
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage( String.format( "'%s'을/를 검색 중입니다...", searchText ));
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		LinearLayout.LayoutParams vl = new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT );
		
		nameResult = new SearchPoiNameResult( screen, search_module, searchText );
		nameResult.SetOnItemClickListener( this );
		nameResult.setId( 100 );
		
		cateResult = new SearchPoiNameResultCate( screen, search_module, searchText );
		cateResult.SetOnItemClickListener( this );
		cateResult.setId( 200 );
		
		tabView	= new UITabView( screen );
		tabView.setId( 300 );
		tabView.AddView( screen, "명칭", nameResult );
		tabView.AddView( screen, "상호", cateResult );
		tabView.CreateTabView( 0 );
		
		layout.addView( tabView, vl );
		
		//tabView.SetTabTitle( 0, String.format( "%s(%d)", tabView.GetTabTitle( 0 ), BunjiView.nItemCount ));
		//tabView.SetTabTitle( 1, String.format( "%s(%d)", tabView.GetTabTitle( 1 ), AptView.nItemCount ));
		
		
		progressDialog.show();
		
		new Thread( null, search_poi_name_thread ).start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		tabView 	= null;
		nameResult 	= null;
		cateResult	= null;
		progressDialog = null;
		
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		POI_DATA poiData = search_module.GetPoiData( arg2 );
		
		SharedPreferences.Editor edit = GetPreferencesEditor( "Search" );
		edit.putInt( "SearchMode" , SearchModeEnum.SEARCH_MODE_POI );
		edit.putString( "SidoName", poiData.sidoName );
		edit.putString( "SigunguName", poiData.sigunguName );
		edit.putString( "DongName", poiData.dongName );
		edit.putString( "BunjiValue", String.format("%d", poiData.bunji ) );
		edit.putString( "HoValue", String.format("%d", poiData.ho ) );
		edit.commit();
		
		Intent intent = new Intent( getContext(), kr.co.uniquantum.ui.activity.SearchResultOnMap.class );
		intent.putExtra( "x", (double)poiData.x / Global._60x60x120_ );
		intent.putExtra( "y", (double)poiData.y / Global._60x60x120_ );
		intent.putExtra( "route_x", (double)poiData.route_x / Global._60x60x120_ );
		intent.putExtra( "route_y", (double)poiData.route_y / Global._60x60x120_ );
		intent.putExtra( "name", nameResult.getText( arg2 ));
		this_screen.startActivity( intent );
	}
	
	private Runnable search_poi_name_thread = new Runnable() 
	{
		public void run()
		{
			nameResult.OnSetListData();
			
			cateResult.SetArrayList( nameResult.GetArrayList() );
			cateResult.SetCateFlagList( nameResult.GetCateFlagList() );
			cateResult.OnSetListData();
						
			handle_end_search.sendMessage( handle_end_search.obtainMessage());
		}
	};
	
	private Handler handle_end_search = new Handler(){
		public void handleMessage( Message msg )
		{
			nameResult.UpdateList();
			cateResult.UpdateList();
			
			tabView.SetTabTitle( 0, String.format( "%s(%d)", tabView.GetTabTitle( 0 ), nameResult.nItemCount ));
			tabView.SetTabTitle( 1, String.format( "%s(%d)", tabView.GetTabTitle( 1 ), cateResult.nItemCount ));
			
			if( progressDialog != null )
				progressDialog.dismiss();
			//this_screen.removeDialog( ID_PROGRESS_DLG );
		}
	};
}
