package kr.co.uniquantum.ui.views.search.poi.name;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import kr.co.uniquantum.Global;
import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.SimpleListView;
import kr.co.uniquantum.ui.activity.UIScreen;

public class SearchPoiName extends SimpleListView implements View.OnClickListener, TextWatcher, View.OnKeyListener
{
	final private int ID_EDT_NAME_TEXT 		= 100;
	final private int ID_BTN_NAME_SEARCH 	= 200;
	
	private SearchModule search_module = null;
	protected SQLiteDatabase sqlite = null;
	
	protected LinearLayout searchLayer = null;
	protected EditText editText = null;
	
	public SearchPoiName(Context context ) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module = SearchModule.getInstance();
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
		editText.addTextChangedListener( this );
		
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

	public SQLiteDatabase GetSQLiteDatabase(){ return new SearchPoiNameSQLHelper( getContext()).getReadableDatabase();}
	public String GetTableName(){ return SearchPoiNameSQLHelper.DB_TABLE_POINAME; }
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		String text = list.get( arg2 );
		editText.setText( text );
		
		Button bt = ( Button ) searchLayer.findViewById( ID_BTN_NAME_SEARCH );
		if( bt != null ) onClick( bt );
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case ID_BTN_NAME_SEARCH :
			
			Global.ShowSoftKeyboard( getContext(), editText, false );
			
			String text = editText.getText().toString();
			AddInputPoiNameDB( text );
			
			this.changeScreen( new SearchPoiResultNnC( this_screen, search_module, text ));
			break;
		}
	}
	
	protected void AddInputPoiNameDB( String text )
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

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) 
		{
			Button bt = ( Button ) searchLayer.findViewById( ID_BTN_NAME_SEARCH );
			if( bt != null ) onClick( bt );
			return true;
		}
		return false;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		SetListData();
	}
} 
