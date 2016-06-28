package code.inka.android.ui.util;

import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class UTIL {
	/**
	 * Layout 리소스에서 viewLayoutID에 해당하는 ViewGroup을 불러 옵니다. 
	 * 
	 * @param context
	 * @param viewLayoutID 불러올 layout id 값
	 * @return ViewGroup 객체
	 */
	public final static ViewGroup GetExternalView( Context context, int viewLayoutID ) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup vg = (ViewGroup) inflater.inflate(viewLayoutID, null);
		return vg;
	}
	
	public final static String GetCurrentTime() {
		Calendar cal = Calendar.getInstance();
		return String.format( "%04d-%02d-%02dT%02d:%02d:%02d", 
        		cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH) + 1,
        		cal.get( Calendar.DAY_OF_MONTH), cal.get( Calendar.HOUR_OF_DAY),
        		cal.get( Calendar.MINUTE ), cal.get( Calendar.SECOND));
	}
	
	public final static String GetCurrentTimeForFilename() {
		Calendar cal = Calendar.getInstance();
		return String.format( "%04d%02d%02d_%02d%02d%02d", 
        		cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH) + 1,
        		cal.get( Calendar.DAY_OF_MONTH), cal.get( Calendar.HOUR_OF_DAY),
        		cal.get( Calendar.MINUTE ), cal.get( Calendar.SECOND));
	} 
}
