package code.inka.android.hdmi;

// shorter class of finding hdmi logcat class 
public class FHLC extends findingHdmiLogCat {
	private static FHLC singleTon = null;
	public static FHLC getInstance() {
		if( singleTon == null ) {
			singleTon = new FHLC();
			singleTon.init();
		}
		return singleTon;
	}
}
