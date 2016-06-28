package kr.co.uniquantum.ui.views.search.poi.tel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import kr.co.uniquantum.Global;
import kr.co.uniquantum.ui.SimpleListView;
import kr.co.uniquantum.ui.activity.UIScreen;

public class SearchPoiCall extends SimpleListView implements View.OnClickListener, View.OnKeyListener
{
	final private int ID_EDT_NAME_TEXT 		= 100;
	final private int ID_BTN_NAME_SEARCH 	= 200;
	
	protected SQLiteDatabase sqlite = null;
	
	protected LinearLayout searchLayer = null;
	protected EditText editText = null;
	
	public SearchPoiCall(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		sqlite = GetSQLiteDatabase();
		
		searchLayer = new LinearLayout( screen );
		searchLayer.setOrientation( LinearLayout.HORIZONTAL );
		layout.addView( searchLayer );
		
		super.onCreate(screen, layout);
				
		editText = new EditText( getContext() );
		editText.setId( ID_EDT_NAME_TEXT );
		editText.setSingleLine();
		editText.setOnKeyListener( this );
				
		searchLayer.addView( editText, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT, 9 ));
		addButton( "°Ë»ö", ID_BTN_NAME_SEARCH, searchLayer, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT ), this );
		
		itemClickListener = this;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if( sqlite != null ) sqlite.close();
		super.onDestroy();
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case ID_BTN_NAME_SEARCH :
			startSearch();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		String text = list.get( arg2 );
		editText.setText( text );
		
		startSearch();		
	}
	
	private void startSearch()
	{
		Global.ShowSoftKeyboard( getContext(), editText, false );
		String text = editText.getText().toString();
		this.changeScreen( new SearchPoiCallResult( this_screen, text, this ));
	}
	
	@Override
	protected void SetListData() {
		// TODO Auto-generated method stub
		String text = null;
		if( editText == null )
			text = "";
		else
			text = editText.getText().toString();
		String selectQuery = "select text FROM ";
		selectQuery += GetTableName();
		if( text.length() > 0 )
			selectQuery += String.format( " where text like '%%%s%%'", text );
		selectQuery += " order by date desc limit 200";
		
		list.clear();
		
		Cursor cur = sqlite.rawQuery( selectQuery, null );
		if( cur.moveToFirst() )
		{
			do
			{
				list.add( cur.getString( 0 ) );
			} while( cur.moveToNext());
		}
		cur.close();
		UpdateList();
	}
	
	public SQLiteDatabase GetSQLiteDatabase(){ return new SearchPoiCallSQLHelper( getContext()).getReadableDatabase();}
	public String GetTableName(){ return SearchPoiCallSQLHelper.DB_TABLE_POICALL; }
	
	public void AddInputPoiCallNumberDB( String text )
	{
		String selectQuery = "delete from ";
		selectQuery += GetTableName();
		selectQuery += " where text='" + text + "';";
		sqlite.execSQL( selectQuery );
		
		selectQuery = "insert into ";
		selectQuery += GetTableName();
		selectQuery += "(text) values('" + text + "');";
		sqlite.execSQL( selectQuery );
	}
}
