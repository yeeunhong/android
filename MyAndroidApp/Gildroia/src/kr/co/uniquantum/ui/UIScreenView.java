package kr.co.uniquantum.ui;

import kr.co.uniquantum.ui.activity.UIScreen;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class UIScreenView extends View implements UIMakeScreenIF {
	public UIScreen this_screen;
	protected int orientation = 0;
	public UIScreenView(Context context) { super(context); }
	public UIScreenView(Context context, int orientation ) { super(context); this.orientation = orientation; }

	public void changeScreen( UIScreenView view ){ this_screen.changeScreen(view); }
	
	public int m_nDialogID;
	public void showAlert( int nDlgID )
	{
		m_nDialogID = nDlgID;
		this_screen.showDialog( nDlgID );
	}
	
	public void showToast( CharSequence message, Boolean bShort )
	{
		Toast.makeText( this_screen, message, bShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG ).show();
	}
	
	public Dialog onCreateDialog(int id) {	return null; }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d("UI", String.format("onKeyDown[%d]",keyCode ));
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d("UI", String.format("onKeyUp[%d]",keyCode ));
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onCreate( UIScreen screen, ViewGroup layout) { this_screen = screen; }

	@Override
	public void onStart() {}

	@Override
	public void onResume() {}

	@Override
	public void onPause() {}

	@Override
	public void onStop() {}

	@Override
	public void onDestroy() {}

	@Override
	public void onRestart() {}
		
	@Override
	public void onOrientationChanged(int newOrientation) {	orientation = newOrientation;}
	public int  getOrientation(){ return orientation; }
	public void setOrientation( int orientation ){ this.orientation = orientation; }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){}
	
	public void goBack() { this_screen.goBack(); }
	
	public void addButton( String name, int nID, ViewGroup viewGroup, LayoutParams params, View.OnClickListener listener )
	{
		Button button1 = new Button( this_screen );
		button1.setText( name );
		button1.setId( nID );
		viewGroup.addView( button1, params );
		button1.setOnClickListener( listener );
	}
	
	public SharedPreferences.Editor GetPreferencesEditor( String name )
	{
		return this_screen.getSharedPreferences( name, Activity.MODE_PRIVATE ).edit();		
	}
}
