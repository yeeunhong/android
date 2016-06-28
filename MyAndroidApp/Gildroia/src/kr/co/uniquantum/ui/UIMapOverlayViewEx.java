package kr.co.uniquantum.ui;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class UIMapOverlayViewEx extends Overlay 
{
	public UIControls controls = null;
	
	public UIMapOverlayViewEx( View mapView ) {
		// TODO Auto-generated constructor stub
		controls = new UIControls( mapView );		
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		//canvas.drawColor(Color.WHITE);
		if( shadow == false )
		{
			controls.draw( canvas );				
		}
		else
			super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		// TODO Auto-generated method stub
		return controls.onTouchEvent(e);
		//return super.onTouchEvent( e, mapView );
	}
}
