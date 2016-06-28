package kr.co.uniquantum.ui.activity;

import kr.co.uniquantum.R;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.views.MainScreenView;
import kr.co.uniquantum.ui.views.sample.GPSFileList;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
//import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.Stack;

public class UIScreen extends Activity 
{
	static public UIScreenView MakeScreenView = null;
	Stack<LinearLayout> screen_stack = new Stack<LinearLayout>();
	Stack<UIScreenView> view_stack = new Stack<UIScreenView>();
	boolean m_bSubUIScreen = true;	
	
	private final boolean bUsedActivity = false;
	public void changeScreen( UIScreenView make_screen_view )
	{
		if( make_screen_view == null ) return;
		
		// Activity Base 화면 전환 
		if( bUsedActivity )
		{
			MakeScreenView = make_screen_view;
			Intent intent = new Intent( UIScreen.this, UIScreen.class );
			startActivity( intent );
		}
		else
		{		
			// ViewBase 화면 전환
			boolean runReflash = MakeScreenView == make_screen_view;
			int orien = MakeScreenView != null ? MakeScreenView.getOrientation() : make_screen_view.getOrientation();
			
			MakeScreenView = make_screen_view;
			LinearLayout ll = new LinearLayout( this );
			ll.setOrientation( LinearLayout.VERTICAL );
			
			setContentView( ll, new LinearLayout.LayoutParams( 
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT ));
			
			make_screen_view.onCreate( this, ll );
			
			if( !runReflash )
			{
				view_stack.push( make_screen_view );
				screen_stack.push( ll );
			}
			
			MakeScreenView.setOrientation( orien );
		}
	}
	
	public void goBack()
	{
		// Activity Base 화면 전환
		if( bUsedActivity )
		{
			this.finish();
		}
		else
		{
			// ViewBase 화면 전환
			UIScreenView current_view = view_stack.lastElement(); 
			view_stack.pop();
			
			current_view.onDestroy();
			current_view = null;
			
			MakeScreenView = view_stack.lastElement();
			
			screen_stack.pop();
			setContentView( screen_stack.lastElement() );
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// ViewBase 화면 전환
		int orien = Configuration.ORIENTATION_PORTRAIT;
		switch( getWindowManager().getDefaultDisplay().getRotation())
		{
		case Surface.ROTATION_0 	:
		case Surface.ROTATION_180 	:
			orien = Configuration.ORIENTATION_PORTRAIT;		// 세로
			break;
		case Surface.ROTATION_90	:
		case Surface.ROTATION_270	:
			orien = Configuration.ORIENTATION_LANDSCAPE;	// 가로
			break;
		}
		
		Intent intent = getIntent();
		if( intent.getExtras() == null )
		{
			// Activity Base 화면 전환		
			if( bUsedActivity )
			{
				if( MakeScreenView == null )
					MakeScreenView = new MainScreenView( this, orien );
		
				setContentView( R.layout.main );
				LinearLayout ll = ( LinearLayout ) findViewById( R.id.mainLayout );
				MakeScreenView.onCreate( this, ll );
			}
			else
			{
				SharedPreferences preferences = getSharedPreferences( "ActivityInfo", Activity.MODE_PRIVATE );
				
				if( preferences.getInt( "ORIENTATION" , Configuration.ORIENTATION_PORTRAIT ) == orien )
					changeScreen( new MainScreenView( this, orien ));
				else if( MakeScreenView != null )
					changeScreen( MakeScreenView );
				else
					changeScreen( new MainScreenView( this, orien ));
			}
		}
		else
		{
			String viewName = intent.getStringExtra( "viewName" );
			if( viewName.compareTo( "GPSFileList" ) == 0 )
			{
				changeScreen( new GPSFileList( this, orien ));
			}
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if( MakeScreenView != null )
			MakeScreenView.onActivityResult(requestCode, resultCode, data);
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if( MakeScreenView != null )
			return MakeScreenView.onCreateDialog(id);
		return null;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if( MakeScreenView != null )
			MakeScreenView.onDestroy();
		
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if( MakeScreenView != null )
			MakeScreenView.onPause();
		
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		if( MakeScreenView != null )
			MakeScreenView.onRestart();
		
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if( MakeScreenView != null )
			MakeScreenView.onResume();
		
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if( MakeScreenView != null )
			MakeScreenView.onStart();
		
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if( MakeScreenView != null )
			MakeScreenView.onStop();
		
		super.onStop();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if( keyCode == KeyEvent.KEYCODE_BACK )
		{
			if( screen_stack.size() > 1 ) 
			{
				MakeScreenView.goBack();
				return true;
			}
		}
		
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		switch( newConfig.orientation )
		{
		case Configuration.ORIENTATION_LANDSCAPE :	// 가로
			break;
		case Configuration.ORIENTATION_PORTRAIT  :	// 세로
			break;
		}
		
		if( MakeScreenView != null ) MakeScreenView.setOrientation( newConfig.orientation );
		
		SharedPreferences.Editor edit = getSharedPreferences( "ActivityInfo", Activity.MODE_PRIVATE ).edit();
		edit.putInt( "ORIENTATION" , newConfig.orientation );
		edit.commit();
	}
	
	
	
}
