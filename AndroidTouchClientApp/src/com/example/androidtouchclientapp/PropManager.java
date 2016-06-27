package com.example.androidtouchclientapp;

import java.util.ArrayList;
import java.util.Locale;

public class PropManager {
	public String getProp( String key ) {
		String prog = null;
		
		if( System.getProperty("os.name").toLowerCase( Locale.getDefault() ).indexOf("win") >= 0 ) {
			prog = String.format( "adb shell getprop %s", key );
		} else {
			prog = String.format( "/system/bin/getprop %s", key );
		}
				
		ArrayList<String> result = ExecUtil.getRuntimeExecResult( prog );
		return result.get(0);
	}
	
	/**
	 * @param defaultPort
	 * @return
	 */
	public int getPortNumberFromFirstboot( int defaultPort ) {
		try {
			String val = getProp( "ro.runtime.firstboot" ).trim();
			if( val.length() > 5 ) {
				val = val.substring( 0, 5 );
			} 
			return Integer.valueOf( val );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return defaultPort;
	}
	
}
