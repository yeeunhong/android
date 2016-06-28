package kr.co.uniquantum.ui.views.search.address;

import kr.co.uniquantum.Global;
import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

public class SearchAddressMain extends UIScreenView implements View.OnClickListener, OnItemClickListener
{
	final private int ID_EDT_NAME_TEXT 		= 100;
	final private int ID_BTN_NAME_SEARCH 	= 200;
	
	protected SearchAddressSido sidoView = null;
	protected SearchModule search_module = null;
	
	protected LinearLayout searchLayer = null;
	protected EditText editText = null;
	
	public SearchAddressMain(Context context ) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module = SearchModule.getInstance();
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		searchLayer = new LinearLayout( screen );
		searchLayer.setOrientation( LinearLayout.HORIZONTAL );
		editText = new EditText( screen );
		editText.setId( ID_EDT_NAME_TEXT );
		editText.setSingleLine();
		searchLayer.addView( editText, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT, 9 ));
		addButton( "°Ë»ö", ID_BTN_NAME_SEARCH, searchLayer, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT ), this );
		
		layout.addView( searchLayer );
		
		sidoView = new SearchAddressSido( screen );
		sidoView.onCreate( screen, layout );
		sidoView.SetOnItemClickListener( this );
		layout.addView( sidoView );
		
		editText.setOnKeyListener( 
			new View.OnKeyListener() 
			{
				public boolean onKey(View v, int keyCode, KeyEvent event) 
				{
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) 
					{
						Button bt = ( Button ) searchLayer.findViewById( ID_BTN_NAME_SEARCH );
						if( bt != null ) onClick( bt );
						return true;
					}
					return false;
				}
			}
		);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case ID_BTN_NAME_SEARCH :
			Global.ShowSoftKeyboard( getContext(), editText, false );
			this.changeScreen( new SearchAddressDnR( this_screen, search_module, editText.getText().toString()));
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		this.changeScreen( sidoView.GetNextView( arg2 ) );
	}
	
	
}
