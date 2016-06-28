package kr.co.uniquantum;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import kr.co.uniquantum.interfaces.GpsListener;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GpsThread extends Object implements LocationListener{

	private LocationManager locationManager;
	private GpsListener		gpsListener = null;
	private Thread			gpsLogThread = null;
	private boolean 		gpsLogThreadRunning = false;
	private String			gpsLogUrl = null;
	
	public GpsThread( Context context, GpsListener _gpsListener ) {
		super();
		// TODO Auto-generated constructor stub
		
		gpsListener = _gpsListener;
		   
		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );
		criteria.setAltitudeRequired( false );
		criteria.setBearingRequired( false );
		criteria.setCostAllowed( true );
		criteria.setPowerRequirement( Criteria.POWER_LOW );
		
		locationManager = ( LocationManager ) context.getSystemService( Context.LOCATION_SERVICE );
		
		String provider = locationManager.getBestProvider( criteria, true );
		locationManager.requestLocationUpdates( provider, 1000, 5, this );
	}
	
	public void Release()
	{
		locationManager.removeUpdates( this );
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if( gpsListener != null ) gpsListener.OnGpsLocation( location );
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
		Log.d( "GPS", String.format( "onProviderDisabled:%s", provider ));
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.d( "GPS", String.format( "onProviderEnabled:%s", provider ));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.d( "GPS", String.format( "onStatusChanged:%s status:%d", provider, status ));
	}
	
	public void playLogFile( String url )
	{
		if( gpsLogThread != null )
		{
			gpsLogThreadRunning = false;
			try {
				gpsLogThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		gpsLogThread = new Thread( null, play_log_file_thread );
		
		gpsLogThreadRunning = true;
		gpsLogUrl = url;
		gpsLogThread.start();
	}
	
	private void parseLocationLog( String logString )
	{
		String token[] 	= logString.split( " " );
		if( token.length < 13 ) return;
		
		String strLat  	= token[0].substring( 2, token[0].length() - 1 ); 
		String strLon  	= token[1].substring( 1, token[1].length() - 1 );
		//String strAccu	= token[3].substring( 1, token[2].length() - 1 );	// m
		String speed   	= token[5];	// mps
		String course	= token[9].substring( 0, token[9].length() - 1 );	// m
		String date		= token[11];
		String time		= token[12];
		//String timeZone = token[13];
		
		int nYear 	= Integer.valueOf( date.substring( 0, 4 ));
		int nMon	= Integer.valueOf( date.substring( 5, 7 ));
		int nDay	= Integer.valueOf( date.substring( 8, 10 ));
		
		int nHour 	= Integer.valueOf( time.substring( 0, 2 ));
		int nMin	= Integer.valueOf( time.substring( 3, 5 ));
		int nSec	= Integer.valueOf( time.substring( 6, 8 ));
		 
		Date _date = new Date( nYear-1900, nMon-1, nDay, nHour, nMin, nSec );
		
		if( gpsListener != null ) 
		{
			//double wgs[] = Global.BSL2WGS( Double.valueOf( strLon ) , Double.valueOf( strLat ), 1 );
			//gpsListener.OnGpsLogLocation( wgs[0], wgs[1], _date );
			gpsListener.OnGpsLogLocation( 
					Double.valueOf( strLon ), 
					Double.valueOf( strLat ), 
					Float.valueOf( speed ),
					Float.valueOf( course ),
					_date );
		}
	}
	private Runnable play_log_file_thread = new Runnable() 
	{
		public void run()
		{
			boolean repeat = false;
			
			do
			{
				InputStream inputStream = null;
				try 
				{
					repeat = false;
					inputStream = new URL( GpsThread.this.gpsLogUrl ).openStream();
										
					//inputStream.read();
					//inputStream.read();
					//inputStream.read();
					
					while( gpsLogThreadRunning )
					{
						String strOneLine = Global.readOneLineStream( inputStream );
						if( strOneLine.length() <= 20 ) 
						{
							repeat = true;
							break;
						}
						
						parseLocationLog( strOneLine );
						try 
						{
							Thread.sleep( 500 );
						} 
						catch (InterruptedException e) { e.printStackTrace(); }
					}
				} 
				catch (MalformedURLException e1){ e1.printStackTrace();	} 
				catch (IOException e1){ e1.printStackTrace(); }
				
				try {
					if( inputStream != null ) inputStream.close();
					inputStream = null;
				} catch (IOException e) { e.printStackTrace(); }
			}
			while( repeat );
						
			gpsLogThreadRunning = false;
			gpsLogThread = null;
		}
	};
}
