package kr.co.uniquantum.ui.views.search.recent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecentSQLHelper extends SQLiteOpenHelper 
{
	static final public String DB_TABLE_RECENT = "RecentTbl";
	static final private int DB_VERSION = 1;
	
	public RecentSQLHelper(Context context ) {
		super(context, "SearchRecent.db", null, DB_VERSION );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createQuery = "CREATE TABLE IF NOT EXISTS ";
		createQuery += DB_TABLE_RECENT;
		createQuery += " ( nID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, lon double, lat double, route_lon double, route_lat double, sido TEXT, sigungu TEXT, dong TEXT, bunji INTEGER, ho INTEGER, phone TEXT, search_type INTEGER, icon INTEGER, nDate DATETIME DEFAULT CURRENT_TIMESTAMP );";
		db.execSQL( createQuery );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL( "DROP TABLE IF EXISTS " + DB_TABLE_RECENT );
		onCreate( db );
	}
}
