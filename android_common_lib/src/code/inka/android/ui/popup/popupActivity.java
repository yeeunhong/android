package code.inka.android.ui.popup;

import code.inka.android.libs.R;
import android.app.Activity;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class popupActivity extends Activity {
	public static final int DIALOG_BTN_TYPE_CUSTOM			= 0x1000;
	public static final int DIALOG_BTN_TYPE_OK 				= 0x1010;
	public static final int DIALOG_BTN_TYPE_YESnNO 			= 0x1011;
	public static final int DIALOG_BTN_TYPE_YESnOKnCancel 	= 0x1012;

	private Handler timeOutHandler = new Handler();
		
	@Override
	protected void onApplyThemeResource(Theme theme, int resid, boolean first) {
		super.onApplyThemeResource(theme, resid, first);
		theme.applyStyle(android.R.style.Theme_Panel, true );
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//this.setTheme( android.R.style.Theme_Dialog );
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.layout_popup_view_controller );
		
		Button btn00 	= ( Button ) findViewById( R.id.btnPopup00 );
		Button btn01 	= ( Button ) findViewById( R.id.btnPopup01 );
		Button btn02 	= ( Button ) findViewById( R.id.btnPopup02 );
		Button btnClose = ( Button ) findViewById( R.id.btnPopupClose );
		
		btn00.setOnClickListener( new OnClickListener(){
			@Override public void onClick(View v) {
				setResult( 0 );
				finish();
			}} );
		btn01.setOnClickListener( new OnClickListener(){
			@Override public void onClick(View v) {
				setResult( 1 );
				finish();
			}} );
		btn02.setOnClickListener( new OnClickListener(){
			@Override public void onClick(View v) {
				setResult( 2 );
				finish();
			}} );
		btnClose.setOnClickListener( new OnClickListener(){
			@Override public void onClick(View v) {
				setResult( -1 );
				finish();
			}} );
		
		int btnType = getIntent().getIntExtra( "btnType", DIALOG_BTN_TYPE_OK );
		
		if( btnType == DIALOG_BTN_TYPE_CUSTOM ) {
			String btnNames[] = getIntent().getStringArrayExtra( "btnNames" );
			if( btnNames != null ) {
				int nCnt = btnNames.length;
				
				btn00.setVisibility( View.GONE );
				btn01.setVisibility( View.GONE );
				btn02.setVisibility( View.GONE );
								
				switch( nCnt ) {
				case 3 :
					btn02.setText( btnNames[2] );
					btn02.setVisibility( View.VISIBLE );
				case 2 :
					btn01.setText( btnNames[1] );
					btn01.setVisibility( View.VISIBLE );
				default :
				case 1 :
					btn00.setText( btnNames[0] );
					btn00.setVisibility( View.VISIBLE );
					break;
				}				
			}
		} else {
			if( btnType == DIALOG_BTN_TYPE_OK ) {
				btn00.setText( android.R.string.ok );
				
				btn01.setVisibility( View.GONE );
				btn02.setVisibility( View.GONE );
				
			} else if( btnType == DIALOG_BTN_TYPE_YESnNO ) {
				btn00.setText( android.R.string.yes );
				btn01.setText( android.R.string.no );
				
				btn02.setVisibility( View.GONE );
				
			} else {
				btn00.setText( android.R.string.ok );
				btn01.setText( android.R.string.yes );
				btn02.setText( android.R.string.no );
			}
		}
		
		TextView tvMsg = ( TextView ) findViewById( R.id.txtPopupMessage );
		if( tvMsg != null ) {
			tvMsg.setText( getIntent().getStringExtra( "message" ));
		}
		
		TextView tvTit = ( TextView ) findViewById( R.id.txtPopupTitle );
		if( tvTit != null ) {
			tvTit.setText( getIntent().getStringExtra( "title" ));
		}
		
		int timeOut = getIntent().getIntExtra( "timeOut", -1 );
		if( timeOut == -1 ) {
			timeOutHandler = null;
		} else {
			timeOutHandler.postDelayed( new Runnable(){
				@Override
				public void run() {
					setResult( -1 );
					finish();
				}} ,  timeOut );
		}
	}
}
