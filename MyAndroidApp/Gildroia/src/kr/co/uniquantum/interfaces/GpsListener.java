package kr.co.uniquantum.interfaces;

import java.util.Date;

import android.location.Location;

public interface GpsListener {
	public void OnGpsLocation( Location location );
	public void OnGpsLogLocation( double lon, double lat, float speed, float angle, Date date );
}
