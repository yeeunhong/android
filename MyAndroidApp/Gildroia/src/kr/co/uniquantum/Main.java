package kr.co.uniquantum;

import java.util.Date;

import kr.co.uniquantum.interfaces.GpsListener;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main extends Activity implements View.OnClickListener, GpsListener {
    /** Called when the activity is first created. */
   
	private GpsThread	gpsThread;
	private GPSLogFileReceiver gpsLogFileReceiver = null;
	
	//private String sidoName = new String("");
	//private String sigunguName = new String("");
	//private String dongName = new String("");
	
	final double _60x60x120_ = 60 * 60 * 120;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams( 
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT );
        
        LinearLayout layout = ( LinearLayout ) findViewById( R.id.mainLayout );
        
        Button button = new Button(this);
		button.setText( "Main Menu로 이동합니다." );
		button.setId( 100 ); 
		//button.setBackgroundColor(0x88CCCCCC);
		layout.addView( button, vl );
		button.setOnClickListener( this );
		 
		TextView tv = new TextView( this );
		tv.setText( "전체 디스크 용량:" + Global.formatSize( Global.GetTotalInternalMemorySize()) );
		layout.addView( tv, vl );
		
		TextView tv1 = new TextView( this );
		tv1.setText( "사용가능 디스크 용량:" + Global.formatSize( Global.GetAvailableInternalMemorySize()) );
		layout.addView( tv1, vl );
		
		TextView tv2 = new TextView( this );
		tv2.setText( "전체 SDCard 용량:" + Global.formatSize( Global.GetTotalExternalMemorySize()) );
		layout.addView( tv2, vl );
		
		TextView tv3 = new TextView( this );
		tv3.setText( "사용가능 SDCard 용량:" + Global.formatSize( Global.GetAvailableExternalMemorySize()) );
		layout.addView( tv3, vl );
		
		//startService( new Intent( this, GPService.class ));
		gpsThread = new GpsThread( this, this );
		 
		/*
		CFile file = new CFile();
		if( file.Open( Global.GetSDCardPath() + "/us.dat", CFile.FILE_OPEN_READ ))
		{
			byte[] read_buf = new byte[100];
			file.Read( 100, read_buf );
			file.Close();
		}
		*/
		
		IntentFilter filter = new IntentFilter( "kr.co.uniquantum.action.GPS_LOGFILE_URL" );
		gpsLogFileReceiver = new GPSLogFileReceiver();
		registerReceiver( gpsLogFileReceiver, filter );
    }

	
	@Override
	public void OnGpsLogLocation(double lon, double lat, float speed, float angle, Date date) {
		// TODO Auto-generated method stub
		gpsLocationProcess( lon, lat, speed, angle, date, false );
	}


	@Override
	public void OnGpsLocation(Location location) {
		// TODO Auto-generated method stub
		gpsLocationProcess( 
				location.getLongitude(), 
				location.getLatitude(), 
				location.getSpeed(),
				location.getBearing(),
				new Date( location.getTime()), true );
	}
 
	private void gpsLocationProcess( 
			double _lon, double _lat, 
			float speed, float angle, 
			Date _date, boolean bShowUI )
	{
		//final double _60x60x120_ = 60*60*120;
		
		double org_lon = _lon;
		double org_lat = _lat;
		
		long lon = ( long )( org_lon * _60x60x120_ );
		long lat = ( long )( org_lat * _60x60x120_ );
	    
		Intent intent = new Intent( "kr.co.uniquantum.ui.activity.action.GPS_LOCATON" );
		intent.putExtra( "org_lon", org_lon );
		intent.putExtra( "org_lat", org_lat );
		intent.putExtra( "lon" , lon );
		intent.putExtra( "lat" , lat );
		intent.putExtra( "speed", speed );
		intent.putExtra( "angle" , (int)angle );
		intent.putExtra( "time", _date.toLocaleString());
		
		//intent.putExtra( "DCode", nDCode );
						
		sendBroadcast( intent );
	}
	
	@Override 
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//stopService( new Intent( this, GPService.class ));
		gpsThread.Release();
		unregisterReceiver( gpsLogFileReceiver );
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case 100 :
			//UIScreen.MakeScreenView = new MainScreenView( this );
			Intent intent = new Intent( Main.this, UIScreen.class );
			startActivity( intent );
			
			break;
		}
	}
	
	public class GPSLogFileReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String server_url 	= arg1.getStringExtra( "LogFileServer" );
			String file_url 	= arg1.getStringExtra( "LogFileUrl" );
			
			gpsThread.playLogFile( server_url + file_url );
		}
	}
}