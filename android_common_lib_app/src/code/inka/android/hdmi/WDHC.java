package code.inka.android.hdmi;

import android.app.Activity;

// shorter class of watchDogHdmiConnection class 
public class WDHC extends watchDogHdmiConnection {
	private static WDHC singleTon = null;
	public static WDHC getInstance( Activity act ) {
		if( singleTon == null ) {
			singleTon = new WDHC();
			singleTon.init( act );			
		}
		return singleTon;
	}
}
