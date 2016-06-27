package lib.java.common.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.util.Log;

public class TouchEvent {
	private final String LOG_TAG = "@>>-- TouchEvent";
	private String deviceName = null;
	
	
	/**
	 * Android 장치에 TouchEvent 값을 읽고 쓸 수 있는지를 확인 합니다. 
	 * 
	 * @return
	 */
	public boolean isEnable() {
		Log.i( LOG_TAG, "called TouchEvent::isEnable" );
		
		String result = ExecUtil.getRuntimeExecResult( "/system/bin/sendevent" );
		if( result == null ) {
			Log.i( LOG_TAG, "result == null" );
			return false;
		}
		if( result.length() < 1 ) {
			Log.i( LOG_TAG, "result.length() < 1" );
			return false;
		}
		
		deviceName = getTouchEventDevice();
		if( deviceName == null ) return false;
		
		Log.i( LOG_TAG, "Find TouchScreen device name is " + deviceName );
		
		return true;
	}
	
	/**
	 * Android 장치에서 TouchEvent 값을 담당하는 장치 문자열을 얻는다. 
	 * 
	 * @return TouchEvent 담당 장치의 문자열 값, 실패 시 null 반환
	 */
	private String getTouchEventDevice() {
		Log.i( LOG_TAG, "called TouchEvent::getTouchEventDevice" );
		
		String ret = null;
		String findDeviceName = null;
		
		InputStream is = ExecUtil.getRuntimeExec( "/system/bin/getevent -p" );
		if( is != null ) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String strLine;
				
				while(( strLine = br.readLine()) != null ) {
					strLine = strLine.trim();
					
					if( strLine.startsWith( "add device" )) {
						String token[] = strLine.split(":");
						if( token.length == 2 ) {
							findDeviceName = token[1].trim();
						}
					} else if( strLine.startsWith( "name:" )) {
						String token[] = strLine.split(":");
						if( token.length == 2 ) {
							if( token[1].contains( "touchscreen" )) {
								ret = findDeviceName;
								break;
							}
						}
					}
				}
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
		return ret;
	}
}
