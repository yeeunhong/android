package app.inka.android;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import app.inka.android.test.checkFilesystemInfo;
import code.inka.android.hdmi.FHLC;
import code.inka.android.hdmi.WDHC;
import code.inka.android.hdmi.onHdmiConnectionListener;
import code.inka.android.screen.ScreenManager;
import code.inka.android.ui.activity.SimpleImageViewActivity;
import code.inka.android.ui.popup.OnDoModalListener;
import code.inka.android.ui.popup.popupActivity;
import code.inka.android.ui.popup.popupHostActivity;

/**
 * @hide
 */
public class mainActivity extends popupHostActivity implements OnClickListener, OnDoModalListener, onHdmiConnectionListener {
	private final String LOG_TAG = "code.inka.android";
	
	private boolean running_finding_hdmi_logcat = false; 
	private boolean running_dectect_hdmi_logcat = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final int btn_ids[] = { R.id.btnTestCameraPreview, R.id.btnTestTabAct, R.id.btnTestListAct, R.id.btnTestPopupOK, 
        		R.id.btnInterruptWatchDog,
        		R.id.btnTestPopupOKnCancel, R.id.btnTestPopupCustom, R.id.btnFileSystemCheck, R.id.btnFindingHdmiLog, R.id.btnServerLogcat,
        		R.id.btnTestDebugMode, 
        		R.id.btnScreenCapture };
        
        for( int i = 0; i < btn_ids.length; i++ ) {
        	Button btn = ( Button ) findViewById( btn_ids[i] );
            if( btn != null ) {
            	btn.setOnClickListener( this );
            }
        }
                       
        setOnDoModalListener( this ); 
        
        AndroidCommonLibAppSo so_test = new AndroidCommonLibAppSo();
        so_test.test_function();
        
    }

	protected void testDeviceID() {
		final String telID = (( TelephonyManager ) getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();   
		if( telID != null ) {
			Log.e( LOG_TAG, String.format( "TEL ID : %s", telID ));
			
			try {
				String telUUID = UUID.nameUUIDFromBytes( telID.getBytes("utf8")).toString();
				Log.e( LOG_TAG, String.format( "TEL ID(UUID) : %s", telUUID ));
				
			} catch (UnsupportedEncodingException e) {				
			}
		}
		
		WifiManager	wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if( wifiMan != null ) {
			WifiInfo	wifiInf = wifiMan.getConnectionInfo();
			StringBuilder strMacAddress	= new StringBuilder();
			
			String WifiMac = wifiInf.getMacAddress();
			Log.e( LOG_TAG, String.format( "WIFI MAC : %s", WifiMac ));
			
			strMacAddress.append( WifiMac );
			try {
				strMacAddress.deleteCharAt(2);
				strMacAddress.deleteCharAt(4);
				strMacAddress.deleteCharAt(6);
				strMacAddress.deleteCharAt(8);
				strMacAddress.deleteCharAt(10);
				
				String WifiMacUUID = UUID.nameUUIDFromBytes(strMacAddress.toString().getBytes("utf8")).toString();
				Log.e( LOG_TAG, String.format( "WIFI MAC(UUID) : %s", WifiMacUUID ));
				
			} catch( Exception e ) {
			}
		}
		
		final String androidId = Secure.getString( getContentResolver(), Secure.ANDROID_ID);
		if( androidId != null ) {
			Log.e( LOG_TAG, String.format( "ANDROID ID : %s", androidId ));
			
			if (!"9774d56d682e549c".equals(androidId)) {   
				try {
					String androidIDUUID = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
					Log.e( LOG_TAG, String.format( "ANDROID ID(UUID) : %s", androidIDUUID ));
					
				} catch (UnsupportedEncodingException e) {
				}   
			}
		}
		
		String randomDeviceID = UUID.randomUUID().toString();
		Log.e( LOG_TAG, String.format( "RANDOM ID(UUID) : %s", randomDeviceID ));
	}
	
	@Override
	public void onClick(View v) {
		switch( v.getId()) {
		case R.id.btnScreenCapture :
			ScreenManager sm = new ScreenManager( this );
			try {
				sm.screenShot( "/sdcard/screenshot.png" );
				
				Intent intent = new Intent( this, SimpleImageViewActivity.class );
				intent.putExtra( "filename", "/mnt/sdcard/screenshot.png" );
				startActivity( intent );
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
			
		case R.id.btnTestDebugMode :
			if( testDebugMode()) {
				doModal( 0x1002, "Test DebugMode", "에물레이터에서 실행이 되었네요..", new String[]{ "아이쿠 어떻게 알았지?" } );
				
			} else {
				doModal( 0x1002, "Test DebugMode", "단말기에서 실행이 되었네요..", new String[]{ "아이쿠 어떻게 알았지?" } );
			}
			break;
			
		case R.id.btnInterruptWatchDog :
			startActivity( new Intent( this, InterruptWatchDog.class ));
			break;
			
		case R.id.btnServerLogcat :
			if( !running_dectect_hdmi_logcat ) {
				//WDHC.getInstance(this).clearLogCat();
				WDHC.getInstance(this).start();
				doModal( 0x1000, "detecting HDMI connection", "HDMI 연결 Monitering이 시작되었습니다.\r\n이 메세지는 3초 후에 자동으로 사라 집니다.", popupActivity.DIALOG_BTN_TYPE_OK, 3000 );
			} else {
				WDHC.getInstance(this).release();
				doModal( 0x1000, "detecting HDMI connection", "HDMI 연결 Monitering이 중지되었습니다.\r\n이 메세지는 3초 후에 자동으로 사라 집니다.", popupActivity.DIALOG_BTN_TYPE_OK, 3000 );
			}
			
			running_dectect_hdmi_logcat = !running_dectect_hdmi_logcat;
			
			break;
			
		case R.id.btnFindingHdmiLog :
			if( !running_finding_hdmi_logcat ) {
				FHLC.getInstance().clearLogCat();
				FHLC.getInstance().start();
				doModal( 0x1000, "Finding HDMI Log", "HDMI 관련 Logcat 데이터 Monitering이 \r\n시작되었습니다.\r\n이 메세지는 3초 후에 자동으로 사라 집니다.", popupActivity.DIALOG_BTN_TYPE_OK, 3000 );
			} else {
				FHLC.getInstance().release();
				doModal( 0x1000, "Finding HDMI Log", "HDMI 관련 Logcat 데이터 Monitering이 \r\n중지되었습니다.\r\n이 메세지는 3초 후에 자동으로 사라 집니다.", popupActivity.DIALOG_BTN_TYPE_OK, 3000 );
			}
			
			running_finding_hdmi_logcat = !running_finding_hdmi_logcat;
			
			break;
		
		case R.id.btnFileSystemCheck :
			new checkFilesystemInfo( this ).start();
			doModal( 0x1000, "Dialog Test Title", "파일 시스템 자료 조사 시작", popupActivity.DIALOG_BTN_TYPE_OK, 3000 );
									
			break;
			
		case R.id.btnTestCameraPreview :
			startActivity( new Intent( this, CameraPreviewActivity.class ));
			break;
		
		case R.id.btnTestPopupOK :
			doModal( 0x1000, "Dialog Test Title", "메세지를 넣어 보아요..\n하하하 잘들어 갈까요?", popupActivity.DIALOG_BTN_TYPE_OK );			
			break;
		
		case R.id.btnTestPopupOKnCancel :
			doModal( 0x1001, "Dialog Test Title", "메세지를 넣어 보아요..\n하하하 잘들어 갈까요?", popupActivity.DIALOG_BTN_TYPE_YESnNO );
			break;
			
		case R.id.btnTestPopupCustom :
			doModal( 0x1002, "Dialog Test Title", "메세지를 넣어 보아요..\n하하하 잘들어 갈까요?", new String[]{"좋아요","생각해 보구요","싫어요"} );
			break;
		
		case R.id.btnTestTabAct :
			startActivity( new Intent( this, TabTestActivity.class ));
			break;
		
		case R.id.btnTestListAct :
			startActivity( new Intent( this, ListTestActivity.class ));
			break;
		} 
	}

	@Override
	public void onDoModalResult( int dlgID, int resultCode ) {
		Toast.makeText( this, String.format("%d 버튼이 클릭", resultCode ), Toast.LENGTH_SHORT ).show();
	}

	@Override
	public void OnHdmiConnectionListener(boolean isConnected) {
		Toast.makeText( mainActivity.this, String.format("HDMI %s", isConnected ? "connected" : "disconnected"), Toast.LENGTH_SHORT ).show();
	}
	
	/**
	 * 현재 실행 상태가 에물레이터인지 단말기 인지를 확인 한다. 
	 */
	private boolean testDebugMode() {
		if( "google_sdk".equals( Build.PRODUCT ) || "sdk".equals( Build.PRODUCT )) {
			Log.e( LOG_TAG, String.format( "Build.PRODUCT is '%s'", Build.PRODUCT ));
			
			return true;
		}
		
		if( "google_sdk".equals( Build.MODEL ) || "sdk".equals( Build.MODEL )) {
			Log.e( LOG_TAG, String.format( "Build.MODEL is '%s'", Build.MODEL ));
			
			return true;
		}
		
		if( Build.MANUFACTURER.equals("unknown")) {
			Log.e( LOG_TAG, "Build.MANUFACTURER is unknown" );
			
			return true;
		}

		if( Build.FINGERPRINT.startsWith("generic") ) {
			Log.e( LOG_TAG, "Build.FINGERPRINT.startsWith 'generic'" );
			
			return true;
		}
	
		/*
		int sdkVersion = Build.VERSION.SDK_INT;
		if( sdkVersion > Build.VERSION_CODES.FROYO ) {
			Log.e( LOG_TAG, String.format( "Build.SERIAL is '%s'", Build.SERIAL ));
			if( "unknown".equals( Build.SERIAL )) {
				return true;
			}			
		}
		*/
		
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		String networkOperator = tm.getNetworkOperatorName(); 
		
		final String telID = tm.getDeviceId();   
		if( telID != null ) {
			if( "000000000000000".equals(networkOperator)) {
				Log.e( LOG_TAG, String.format( "TEL ID : %s", telID ));
				
				return true;
			}		
		}
		
		if("Android".equals(networkOperator)) { 
			// Emulator
			Log.e( LOG_TAG, "TelephonyManager getNetworkOperatorName is Android" );
			return true;
			
		} else { 
			// Device 
		}
		
		final String androidId = Secure.getString( getContentResolver(), Secure.ANDROID_ID);
		if( androidId != null ) {
			Log.e( LOG_TAG, String.format( "Secure.ANDROID_ID is '%s'", androidId ));
			if ( "9774d56d682e549c".equals(androidId)) {   
				Log.e( LOG_TAG, "ANDROID_ID is '9774d56d682e549c'" );
				
				return true;
			}
		}
		
		
		Rect rectgle= new Rect();
        Window window= getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        Log.e( LOG_TAG, String.format( "rectgle.height() is '%d'", rectgle.height() ));
        Log.e( LOG_TAG, String.format( "rectgle.top is '%d'", rectgle.top ));
        
        return false;
	}
	
	public class CheckDebugModeTask extends AsyncTask<String, Void, String> { 
		public boolean IsDebug = false; 
		private boolean done = false;
		
		public CheckDebugModeTask(){} 
		public boolean doRunWaitResult() {
			done = false;
			
			execute("");
			while( !done ) {
				try { Thread.sleep( 333 ); } catch (InterruptedException e) { break; }
			}
			
			return IsDebug;
		}
		
		@Override 
		protected String doInBackground(String... params) { 
			try { 
				HttpParams httpParameters = new BasicHttpParams(); 
				
				int timeoutConnection = 1000; 
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
				
				int timeoutSocket = 2000; 
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
				
				String url2 = "http://10.0.2.2"; 
				HttpGet httpGet = new HttpGet(url2); 
				DefaultHttpClient client = new DefaultHttpClient(httpParameters); 
				
				HttpResponse response2 = client.execute(httpGet); 
				if (response2 == null || response2.getEntity() == null || response2.getEntity().getContent() == null ) {
					return "";
				}
				
				return "Debug"; 
			} catch (Exception e) { 
				return ""; 
			} 
		} 
		
		@Override 
		protected void onPostExecute (String result) { 
			if ( result == "Debug" ) { 
				IsDebug = true; 
			} 
			
			done = true;
		}
	}
}
