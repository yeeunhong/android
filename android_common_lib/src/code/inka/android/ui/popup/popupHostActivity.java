package code.inka.android.ui.popup;

import android.app.Activity;
import android.content.Intent;

/*
	popupHostActivity activity를 사용하기 위해서는 아래 코드를 AndroidManifest.xml 에서 추가하여 주어야 한다. 
*/
/*
  	<activity android:name="code.inka.android.ui.popup.popupActivity" android:theme="@android:style/Theme.Dialog" />
*/

/**
 * AndroidManifest.xml 에서 아래 Activity를 등록 시켜 줘야 한다. <br><br>
 * < activity android:name="code.inka.android.ui.popup.popupActivity" android:theme="@android:style/Theme.Dialog" / >
 * @author purehero
 *
 */
public class popupHostActivity extends Activity {
	protected int	m_DlgID = -1;
	protected OnDoModalListener doModalListener = null;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if( m_DlgID == requestCode && doModalListener != null ) {
			if( resultCode != -1 ) {
				doModalListener.onDoModalResult( m_DlgID, resultCode );
			}
		}
	}

	public void doModal( int dlgID, String title, String message, int btnType ) {
		doModal( dlgID, title, message, btnType, -1 );
	}
	public void doModal( int dlgID, String title, String message, int btnType, int nTimeOut ) {
		m_DlgID = dlgID;
		
		Intent intent = new Intent( this, popupActivity.class );
		intent.putExtra( "title", title );
		intent.putExtra( "message", message );
		intent.putExtra( "btnType", btnType );
		intent.putExtra( "timeOut", nTimeOut );
		
		startActivityForResult( intent, m_DlgID );
	}
	
	public void doModal( int dlgID, String title, String message, String [] strButtons ) {
		doModal( dlgID, title, message, strButtons, -1 );
	}
	public void doModal( int dlgID, String title, String message, String [] strButtons, int nTimeOut ) {
		m_DlgID = dlgID;
		
		Intent intent = new Intent( this, popupActivity.class );
		intent.putExtra( "title", title );
		intent.putExtra( "message", message );
		
		intent.putExtra( "btnType", popupActivity.DIALOG_BTN_TYPE_CUSTOM );
		intent.putExtra( "btnNames", strButtons );
		intent.putExtra( "timeOut", nTimeOut );
		
		startActivityForResult( intent, m_DlgID );
	}
	
	public void setOnDoModalListener( OnDoModalListener doModalListener ) {
		this.doModalListener = doModalListener;
	}
}
