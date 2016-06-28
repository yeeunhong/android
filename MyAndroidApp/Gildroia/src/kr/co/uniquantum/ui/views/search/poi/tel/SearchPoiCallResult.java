package kr.co.uniquantum.ui.views.search.poi.tel;

import kr.co.uniquantum.Global;
import kr.co.uniquantum.search.POI_DATA;
import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.SimpleListView;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class SearchPoiCallResult extends SimpleListView 
{
	protected SearchModule search_module = null;
	protected String searchText = null;
	protected int nItemCount = 0;
	protected SearchPoiCall searchPoiCall = null;
	private ProgressDialog progressDialog = null;
	
	public SearchPoiCallResult(Context context, String text, SearchPoiCall _searchPoiCall ) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module 	= SearchModule.getInstance();
		searchText		= text; 
		searchPoiCall	= _searchPoiCall;
	
		progressDialog = new ProgressDialog( context );
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage( String.format( "'%s'을/를 검색 중입니다...", searchText ));
	}
	
	protected void SetListData()
	{
		progressDialog.show();
		
		new Thread( null, search_poi_call_thread ).start();
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
		intent.putExtra( "name", list.get( arg2 ));
		this_screen.startActivity( intent );
	}
	
	private Runnable search_poi_call_thread = new Runnable() 
	{
		public void run()
		{
			search_module.GetSearchPoiCallCount( searchText );
			list.clear();
			
			if( search_module.MoveFirstPoiData())
			{ 
				do
				{
					String branchName = search_module.GetPoiBranchName();
					if( branchName != null )
						list.add( search_module.GetPoiName() + " " + branchName );
					else 
						list.add( search_module.GetPoiName());
				} while( search_module.MoveNextPoiData());
			}
			
			nItemCount = list.size();
			
			handle_end_search.sendMessage( handle_end_search.obtainMessage());
		}
	};
	
	private Handler handle_end_search = new Handler(){
		public void handleMessage( Message msg )
		{
			Log.d( "UI", searchText );
			UpdateList();
			//if( nItemCount > 0 ) searchPoiCall.AddInputPoiCallNumberDB( searchText );
			progressDialog.dismiss();
		}
	};
}
