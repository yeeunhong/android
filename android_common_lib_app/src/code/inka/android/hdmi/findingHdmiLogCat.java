package code.inka.android.hdmi;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Build;
import code.inka.android.http.HttpManager;
import code.inka.android.logcat.logcatProcess;

public class findingHdmiLogCat extends logcatProcess {
	private static final String LOG_SERVER_URL = "http://211.47.137.240/NCG2/hdmi_log.asp";
				
	@Override
	public boolean init() {
		if( !super.init()) return false;
		
		addFilter("*:*");
		return true;
	}

	@Override
	public void OnLogcatData(String data) {
		String low_data = data.toLowerCase();
		
		if( low_data.contains("hdmi") || low_data.contains("tvout")) {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//nameValuePairs.add( new BasicNameValuePair( "MODEL",  URLEncoder.encode( Build.MODEL, "UTF-8" )));
			//nameValuePairs.add( new BasicNameValuePair( "LOG", URLEncoder.encode( data, "UTF-8" )));
			nameValuePairs.add( new BasicNameValuePair( "MODEL",  Build.MODEL ));
			nameValuePairs.add( new BasicNameValuePair( "LOG", data ));
			
			HttpManager.requestGetMethod( LOG_SERVER_URL, nameValuePairs, null );
		}				
	}

	@Override
	public boolean OnInitInThread() {
		return true;
	}
}
