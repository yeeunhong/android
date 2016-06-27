package com.example.androidtouchclientapp;

import java.util.ArrayList;
import java.util.Locale;

import android.util.Log;

public class TouchEvent {
	private final String LOG_TAG = "@>>-- TouchEvent";
	private String deviceName = null;
	private boolean enable = false;
	
	/**
	 * TouchEvent 발생 장치로 사용할 장치명을 설정한다. 
	 * 
	 * @param name
	 */
	public void setTouchEventDeviceName( String name ) {
		deviceName = name;
		Log.i( LOG_TAG, "set TouchEventDeviceName : " + deviceName );
	}
	
	/**
	 * Android 장치에 TouchEvent 값을 읽고 쓸 수 있는지를 확인 합니다. 
	 * 
	 * @return
	 */
	public boolean isEnable() {
		Log.i( LOG_TAG, "called TouchEvent::isEnable" );
		
		String prog = "sendevent";
		ArrayList<String> result = ExecUtil.getRuntimeExecResult( prog );
		if( result.isEmpty()) {
			Log.i( LOG_TAG, String.format( "ERROR : %s", prog ));
			return enable;
		}
		if( !result.get(0).startsWith("use: sendevent")) {
			Log.i( LOG_TAG, String.format( "'%s' is not start with 'use: sendevent'", result.get(0) ));
			return enable;
		}
		enable = true;
		
		return enable;
	}
	
	
	public void touchMOVE( int x, int y ) {
		if( !enable || deviceName == null ) {
			Log.i( LOG_TAG, "touchUP cat't working, touchevent not enable" );
			return;
		}
		
		int type[]  = { 0x0003, 0x0003, 0x0000 };
		int code[]  = { 0x0035, 0x0036, 0x0000 };
		int value[] = { x, y, 0 };
		
		for( int i = 0; i < type.length; i++ ) {
			String prog = String.format( Locale.getDefault(), "sendevent %s %d %d %d", deviceName, type[i], code[i], value[i] );
			ArrayList<String> result = ExecUtil.getRuntimeExecResult( prog );
			if( result.size() > 0 ) {
				Log.i( LOG_TAG, result.get(0));
			}
		}
	}
	
	public void touchDOWN( int x, int y ) {
		if( !enable || deviceName == null ) {
			Log.i( LOG_TAG, "touchUP cat't working, touchevent not enable" );
			return;
		}
		
		int type[]  = { 0x0003, 0x0001, 0x0003, 0x0003, 0x0003, 0x0000 };
		int code[]  = { 0x0039, 0x014a, 0x0035, 0x0036, 0x0030, 0x0000 };
		int value[] = { 0, 1, x, y, 6, 0 };
		
		for( int i = 0; i < type.length; i++ ) {
			String prog = String.format( Locale.getDefault(), "sh sendevent %s %d %d %d", deviceName, type[i], code[i], value[i] );
			ArrayList<String> result = ExecUtil.getRuntimeExecResult( prog );
			if( result.size() > 0 ) {
				Log.i( LOG_TAG, result.get(0));
			}
		}
	}
	
	public void touchUP() {
		if( !enable || deviceName == null ) {
			Log.i( LOG_TAG, "touchUP cat't working, touchevent not enable" );
			return;
		}
		
		int type[]  = { 0x0003, 0x0001, 0x0000 };
		int code[]  = { 0x0039, 0x014a, 0x0000 };
		int value[] = { -1, 0, 0 };
		
		for( int i = 0; i < type.length; i++ ) {
			String prog = String.format( Locale.getDefault(), "sh sendevent %s %d %d %d", deviceName, type[i], code[i], value[i] );
			ArrayList<String> result = ExecUtil.getRuntimeExecResult( prog );
			if( result.size() > 0 ) {
				Log.i( LOG_TAG, result.get(0));
			}
		}
	}
	
	/**
	 * Android 장치에서 TouchEvent 값을 담당하는 장치 문자열을 얻는다. 
	 * 
	 * @return TouchEvent 담당 장치의 문자열 값, 실패 시 null 반환
	 */
	// getevent 실행 파일이 아래 함수에서는 퍼미션 문제로 단말기에서는 실행이 되지 않는다. 
	// 하지만 PC에서 adb shell getevent 로는 실행이된다. 
	/*
	private String getTouchEventDevice() {
		Log.i( LOG_TAG, "called TouchEvent::getTouchEventDevice" );
		
		String ret = null;
		String findDeviceName = null;
		
		String prog = "/system/bin/getevent -p";
		ArrayList<String> result = ExecUtil.getRuntimeExecResult( prog );
		
		String strLine;
		for( int i = 0; i < result.size(); i++ ) {		
			strLine = result.get(i).trim();
			
			Log.i( LOG_TAG, "==> " + strLine );
			
			if( strLine.startsWith( "add device" )) {
				Log.i( LOG_TAG, "add device ==> " + strLine );
				String token[] = strLine.split(":");
				if( token.length == 2 ) {
					findDeviceName = token[1].trim();
					Log.i( LOG_TAG, "findDeviceName ==> " + findDeviceName );
				}
			} else if( strLine.startsWith( "name:" )) {
				Log.i( LOG_TAG, "name ==> " + strLine );
				
				String token[] = strLine.split(":");
				if( token.length == 2 ) {
					Log.i( LOG_TAG, "name ==> " + token[1] );
					if( token[1].contains( "touchscreen" )) {
						ret = findDeviceName;
						Log.i( LOG_TAG, "deviceName ==> " + ret );
						break;
					}
				}
			}
		}
				
		return ret;
	}
	*/
}
