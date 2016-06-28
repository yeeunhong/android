package kr.co.uniquantum.ui.views.search.poi.category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.SimpleListView;
import kr.co.uniquantum.ui.activity.UIScreen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SearchPoiCategory extends SimpleListView
{
	private SearchModule search_module = null;
	private final String DB_NAME = "CateTbl.dat";
	private final String DB_TABLE = "CateData";
	private final int    DB_VERSION = 1;
	
	protected SQLiteDatabase sqlite = null;
	protected SQLHelper helper = null;
	protected Cursor allRows = null;
	protected String sqlQuery = null;
	protected boolean dbOpened = false;
	protected int cateLevel = 0;
	protected int cateCode1 = 0;
	protected int cateCode2 = 0;
	protected int cateCode3 = 0;
	protected ArrayList<Integer> cateCodeTbl = new ArrayList<Integer>();
	protected String parentTitle = "";
	
	public SearchPoiCategory(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module = SearchModule.getInstance();
	}
	
	public SearchPoiCategory( Context context, SearchModule _search_module, SQLiteDatabase _sqlite,
			Cursor _allRows, String _parentTitle, 
			int _CateCode1, int _CateCode2, int _CateCode3, int _CateLevel
	) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module = _search_module;
		sqlite 		= _sqlite;
		allRows		= _allRows;
		parentTitle	= _parentTitle;
		cateCode1	= _CateCode1;
		cateCode2	= _CateCode2;
		cateCode3	= _CateCode3;
		cateLevel 	= _CateLevel;
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		if( sqlite == null )
		{
			try {
				File outfile = new File( "/data/data/" + screen.getPackageName() + "/databases/" + DB_NAME );
				//outfile.delete();
				
				if( outfile.length() <= 0 )
				{ 
					InputStream fi = getContext().getAssets().open( DB_NAME );
					byte buffer[] = new byte[ fi.available()];
					fi.read( buffer );
					fi.close();
					
					//FileOutputStream fo = new FileOutputStream( outfile );
					FileOutputStream fo = new FileOutputStream(outfile, false);
					fo.write( buffer );
					fo.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			helper = new SQLHelper( getContext(), DB_NAME, null, DB_VERSION );
			sqlite = helper.getReadableDatabase();
			
			dbOpened = true;
			
			sqlQuery = "select distinct lv1Code, lv1Name from CateData order by lv1Code;";
			allRows = sqlite.rawQuery( sqlQuery, null );
		}
		
		super.onCreate(screen, layout);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if( allRows != null ) allRows.close();
		if( dbOpened ) sqlite.close();
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//super.onItemClick(arg0, arg1, arg2, arg3);
		String _sqlQuery = "";
		int cateCode = cateCodeTbl.get( arg2 );
		Cursor _cur = null;
		
		if( cateCode == -1 )
		{
			switch( cateLevel )
			{
			case 1 :
				_sqlQuery = String.format( 
						"select distinct code from CateData where lv1Code=%d order by lv2Code, lv3Code", 
						cateCode1 );
				break;
			case 2 :
				_sqlQuery = String.format( 
						"select distinct code from CateData where lv1Code=%d and lv2Code=%d order by lv3Code", 
						cateCode1, cateCode2 );
				break;
			default : return;
			}
			
			_cur = sqlite.rawQuery( _sqlQuery, null );
			
			// 기본적으로 모든 CateCode에는 무효한 하위 코드 0이 포함되어 있다. 
			// 따라서 실재 하위 코드의 개수가 있는지 판단하려면 1보다 큰가를 비교해야 한다.
			if( _cur.getCount() > 1 )
			{
				changeScreen( 
						new SearchPoiCategoryResult( this_screen, search_module, _cur ));
			}
			
			return;
		}
		
		switch( cateLevel )
		{
		case 0 :
			cateCode1 = cateCode;
			_sqlQuery = String.format( 
				"select distinct lv2Code, lv2Name from CateData where lv1Code=%d order by lv2Code", 
				cateCode );
			break;
		case 1 :
			cateCode2 = cateCode;
			_sqlQuery = String.format( 
				"select distinct lv3Code, lv3Name from CateData where lv1Code=%d and lv2Code=%d order by lv3Code",
				cateCode1, cateCode );
			break;
			
		case 2 :
			cateCode3 = cateCode;
			_sqlQuery = String.format( 
					"select distinct code from CateData where lv1Code=%d and lv2Code=%d and lv3Code=%d order by lv3Code",
					cateCode1, cateCode2, cateCode3 );
			_cur = sqlite.rawQuery( _sqlQuery, null );
			// 검색한 결과가 있는지 판단하려면 0보다 큰가를 비교한다.
			if( _cur.getCount() > 0 )
				changeScreen( new SearchPoiCategoryResult( this_screen, search_module, _cur ));
			return;
			//break;
		}
		
		_cur = sqlite.rawQuery( _sqlQuery, null );
		
		// 하위 코드가 존재하는지를 판단하려면 1보다 큰가를 비교한다.
		if( _cur.getCount() > 1 )
		{
			changeScreen( 
				new SearchPoiCategory( 
						this_screen, 
						search_module, 
						sqlite, 
						_cur,
						list.get( arg2 ),
						cateCode1, cateCode2, cateCode3, cateLevel + 1 ));
		}
		else
		{
			_cur.close();
			
			switch( cateLevel )
			{
			case 1 :
				_sqlQuery = String.format( 
						"select distinct code from CateData where lv1Code=%d and lv2Code=%d order by lv2Code, lv3Code", 
						cateCode1, cateCode2 );
				break;
			default : return; 
			}
			
			_cur = sqlite.rawQuery( _sqlQuery, null );
			// 검색한 결과가 있는지 판단하려면 0보다 큰가를 비교한다.
			if( _cur.getCount() > 0 )
			{
				changeScreen( new SearchPoiCategoryResult( this_screen, search_module, _cur ));
			}
		}
	}

	@Override
	protected void SetListData() {
		// TODO Auto-generated method stub
		if( allRows.moveToFirst())
		{
			if( cateLevel > 0 )
			{
				list.add( parentTitle + " 전부" );
				cateCodeTbl.add( -1 );
			}
			
			do
			{
				list.add( allRows.getString( 1 ));
				cateCodeTbl.add( allRows.getInt( 0 ));
			} while( allRows.moveToNext());
		} 
		adapter.notifyDataSetChanged();
	}

	class SQLHelper extends SQLiteOpenHelper
	{
		public SQLHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL( "DROP TABLE IF EXISTS " + DB_TABLE );
			onCreate( db );
		}
	}
}
