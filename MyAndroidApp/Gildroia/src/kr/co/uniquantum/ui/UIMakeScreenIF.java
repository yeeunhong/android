package kr.co.uniquantum.ui;

import kr.co.uniquantum.ui.activity.UIScreen;
import android.view.ViewGroup;
//import android.widget.LinearLayout;

public interface UIMakeScreenIF {
	public void onCreate( UIScreen screen, ViewGroup layout );
	public void onStart();
	public void onResume();
	
	public void onPause();
	public void onStop();
	public void onDestroy();
	
	public void onRestart();
	
	public void onOrientationChanged( int newOrientation ); 
}


