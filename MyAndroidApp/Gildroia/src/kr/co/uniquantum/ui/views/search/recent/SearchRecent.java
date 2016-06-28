package kr.co.uniquantum.ui.views.search.recent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import kr.co.uniquantum.ui.views.search.favorite.SearchFavorite;

public class SearchRecent extends SearchFavorite 
{
	public SearchRecent(Context context) {		
		super(context);
		// TODO Auto-generated constructor stub
	} 
	
	public SQLiteDatabase GetSQLiteDatabase(){ return new RecentSQLHelper( getContext() ).getReadableDatabase(); }
	public String GetTableName(){ return RecentSQLHelper.DB_TABLE_RECENT; }
}
