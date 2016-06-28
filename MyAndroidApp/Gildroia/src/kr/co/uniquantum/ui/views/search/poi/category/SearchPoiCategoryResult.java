package kr.co.uniquantum.ui.views.search.poi.category;

import kr.co.uniquantum.Global;
import kr.co.uniquantum.search.POI_DATA;
import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.SimpleListView;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;

public class SearchPoiCategoryResult extends SimpleListView 
{
	private SearchModule search_module = null;
	private Cursor db_cursor = null;
	public SearchPoiCategoryResult(Context context, SearchModule _search_module, Cursor _db_cursor ) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module 	= _search_module;
		db_cursor 		= _db_cursor;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if( db_cursor != null ) db_cursor.close();
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
		intent.putExtra( "name", list.get( arg2 ));
		this_screen.startActivity( intent );
	}

	@Override
	protected void SetListData() {
		// TODO Auto-generated method stub
		boolean bAddData = false;
		if( db_cursor.moveToFirst())
		{
			do
			{
				search_module.GetCatePoiCount( db_cursor.getString( 0 ), 0, bAddData );
				bAddData = true;
			} while( db_cursor.moveToNext());
		} 
		
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
				
		adapter.notifyDataSetChanged();
	}
}
