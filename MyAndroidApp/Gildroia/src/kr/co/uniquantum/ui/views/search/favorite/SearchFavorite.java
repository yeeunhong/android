package kr.co.uniquantum.ui.views.search.favorite;

import java.util.ArrayList;
import kr.co.uniquantum.ui.SimpleListView;
import kr.co.uniquantum.ui.activity.UIScreen;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SearchFavorite extends SimpleListView 
{
	protected SQLiteDatabase sqlite = null;
	protected ArrayList<Integer> itemID = new ArrayList<Integer>();
	
	public SearchFavorite(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		sqlite = GetSQLiteDatabase();
		super.onCreate(screen, layout);				
	} 
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if( sqlite != null ) sqlite.close();
		super.onDestroy();
	}
	
	public SQLiteDatabase GetSQLiteDatabase()
	{ 
		return new FavoriteSQLHelper( getContext()).getReadableDatabase(); 
	}
	public String GetTableName(){ return FavoriteSQLHelper.DB_TABLE_FAVORITE; }
	
	@Override
	protected void SetListData() {
		// TODO Auto-generated method stub
		//super.SetListData();
		String selectQuery = "select nID, name FROM ";
		selectQuery += GetTableName();
		selectQuery += " order by nDate desc limit 200";
		
		itemID.clear();
		list.clear();
		
		Cursor cur = sqlite.rawQuery( selectQuery, null );
		if( cur.moveToFirst() )
		{
			do
			{
				itemID.add( cur.getInt( 0 ) );
				list.add( cur.getString( 1 ) );
			} while( cur.moveToNext());
		}
		cur.close();
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//super.onItemClick(arg0, arg1, arg2, arg3);
		final int SEARCH_TYPE = 0;
		final int NAME = 1;
		final int LON = 2;
		final int LAT = 3;
		final int ROUTE_LON = 4;
		final int ROUTE_LAT = 5;
		final int SIDO_NAME = 6;
		final int SIGUNGU_NAME = 7;
		final int DONG_NAME = 8;
		final int BUNJI = 9;
		final int HO = 10;
		final int CALL_NUMBER = 11;
		
		String selectQuery = "select search_type,name,lon,lat,route_lon,route_lat,sido,sigungu,dong,bunji,ho,phone FROM ";
		selectQuery += GetTableName();
		selectQuery += " where nID=";
		selectQuery += String.format("%d", itemID.get( arg2 ));
		
		Cursor cur = sqlite.rawQuery( selectQuery, null );
		if( cur.moveToFirst() )
		{
			int nSearchType = cur.getInt( SEARCH_TYPE );
			SharedPreferences.Editor edit = GetPreferencesEditor( "Search" );
			edit.putInt( "SearchMode" , nSearchType );
			
			switch( nSearchType )
			{
			case SearchModeEnum.SEARCH_MODE_NONE :
			case SearchModeEnum.SEARCH_MODE_DONG :
			case SearchModeEnum.SEARCH_MODE_ROAD :
			case SearchModeEnum.SEARCH_MODE_POI :
				edit.putString( "SidoName", 	cur.getString( SIDO_NAME ));
				edit.putString( "SigunguName", 	cur.getString( SIGUNGU_NAME ));
				edit.putString( "DongName", 	cur.getString( DONG_NAME ));
				edit.putString( "BunjiValue", 	String.format("%d", cur.getInt( BUNJI )));
				edit.putString( "HoValue", 		String.format("%d", cur.getInt( HO )));
				break;
				
			case SearchModeEnum.SEARCH_MODE_APT :
				edit.putString( "SidoName", 	cur.getString( SIDO_NAME ));
				edit.putString( "SigunguName", 	cur.getString( SIGUNGU_NAME ));
				edit.putString( "DongName", 	cur.getString( DONG_NAME ));
				edit.putString( "AptName", 		cur.getString( CALL_NUMBER ));			
				break;
			default : return;
			}
			
			edit.commit();
		}		
				
		double x = cur.getDouble( LON );
		double y = cur.getDouble( LAT );
		double route_x = cur.getDouble( ROUTE_LON );
		double route_y = cur.getDouble( ROUTE_LAT );
		String name = cur.getString( NAME );
		
		cur.close();
		
		Intent intent = new Intent( getContext(), kr.co.uniquantum.ui.activity.SearchResultOnMap.class );
		intent.putExtra( "x", x );
		intent.putExtra( "y", y );
		intent.putExtra( "route_x", route_x );
		intent.putExtra( "route_y", route_y );
		intent.putExtra( "name", name );
		intent.putExtra( "posType", 1 );
		
		this_screen.startActivity( intent );
	}
		
}
