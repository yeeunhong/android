package kr.co.uniquantum.ui;

import java.util.ArrayList;

import kr.co.uniquantum.ui.activity.UIScreen;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class UITabView extends LinearLayout implements View.OnClickListener 
{
	protected FrameLayout		viewLayout;
	protected LinearLayout		btnLayout;
	
	protected ArrayList<LinearLayout>	views;
		
	public UITabView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		views		= new ArrayList<LinearLayout>();
		viewLayout 	= new FrameLayout( context );
		btnLayout	= new LinearLayout( context );
		
		btnLayout.setOrientation( LinearLayout.HORIZONTAL );
		setOrientation( LinearLayout.VERTICAL );
	}
	
	public void AddView( UIScreen screen, String title, UIScreenView view )
	{
		int nCnt = views.size();
		addTabButton( title, nCnt );
		
		LinearLayout layout = new LinearLayout( getContext());
		view.onCreate( screen, layout);
		layout.addView( view );
		
		views.add( layout );
	}
	
	public void AddViewNoCreate( UIScreen screen, String title, UIScreenView view )
	{
		int nCnt = views.size();
		addTabButton( title, nCnt );
		
		LinearLayout layout = new LinearLayout( getContext());
		layout.addView( view );
		
		views.add( layout );
	}
	
	public void CreateTabView( int nTabIndex )
	{
		addView( btnLayout, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT ) );
		addView( viewLayout, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT ) );
		
		if( views.size() > nTabIndex ) 
			viewLayout.addView( views.get( nTabIndex ));
	}
	
	public void ViewOnCreate( int nIndex, UIScreen screen, int viewID )
	{
		LinearLayout layout = views.get( nIndex );
		UIScreenView view = ( UIScreenView ) layout.findViewById( viewID );
		view.onCreate( screen, layout );		
	}
	
	public void UpdateTabView( int nIndex )
	{
		LinearLayout layout = views.get( nIndex );
		layout.invalidate();
	}
	
	public String GetTabTitle( int nIndex )
	{
		Button btn = (Button ) btnLayout.findViewById( nIndex );
		if( btn == null ) return null;
		return btn.getText().toString();
	}
	
	public void SetTabTitle( int nIndex, String title )
	{
		Button btn = (Button ) btnLayout.findViewById( nIndex );
		if( btn != null ) btn.setText( title );
	}
	
	public void addTabButton( String name, int nID )
	{
		LinearLayout.LayoutParams vl = new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1 );
		
		Button button1 = new Button( getContext() );
		button1.setText( name );
		button1.setId( nID );
		btnLayout.addView( button1, vl );
		button1.setOnClickListener( this );
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		viewLayout.removeAllViews();
		viewLayout.addView( views.get( v.getId()));
	}		
}
