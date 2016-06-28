package kr.co.uniquantum.ui.views.search.poi.name;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SearchPoiNameSQLHelper extends SQLiteOpenHelper 
{
	static final public String DB_TABLE_POINAME = "InputPoiNameTbl";
	static final private int DB_VERSION = 1;
	
	public SearchPoiNameSQLHelper( Context context ) {
		super( context, "InputPoiName.db", null, DB_VERSION );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createQuery = "CREATE TABLE IF NOT EXISTS ";
		createQuery += DB_TABLE_POINAME;
		createQuery += " ( text TEXT PRIMARY KEY, date DATETIME DEFAULT CURRENT_TIMESTAMP );";
		db.execSQL( createQuery );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL( "DROP TABLE IF EXISTS " + DB_TABLE_POINAME );
		onCreate( db );
	}

}
