package kr.co.uniquantum.ui.views.search.favorite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteSQLHelper extends SQLiteOpenHelper 
{
	static final public String DB_TABLE_FAVORITE = "FavoriteTbl";
	static final private int DB_VERSION = 1;
	
	public FavoriteSQLHelper( Context context ) {
		super( context, "SearchFavorite.db", null, DB_VERSION );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createQuery = "CREATE TABLE IF NOT EXISTS ";
		createQuery += DB_TABLE_FAVORITE;
		createQuery += " ( nID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, lon double, lat double, route_lon double, route_lat double, sido TEXT, sigungu TEXT, dong TEXT, bunji INTEGER, ho INTEGER, phone TEXT, search_type INTEGER, icon INTEGER, nDate DATETIME DEFAULT CURRENT_TIMESTAMP );";
		db.execSQL( createQuery );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL( "DROP TABLE IF EXISTS " + DB_TABLE_FAVORITE );
		onCreate( db );
	}
}
