package kr.co.uniquantum.ui.views.search;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.view.ViewGroup;
import com.google.android.maps.*;

public class SearchResult extends UIScreenView {
	protected MapView mapView = null;
	protected MapController mapController = null;
	protected double x;
	protected double y;
	
	protected final String MapApiKey = "0vi6ybSD8ygkHtCccTCuD9U2eZytjQuB2y8DjYw";
	protected final double _1E6_ = 1000000.0;
	
	public SearchResult(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SearchResult(Context context, double _x, double _y ) {
		super(context);
		// TODO Auto-generated constructor stub
		x = _x;
		y = _y;
	}
	
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) 
	{
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams( 
				MapView.LayoutParams.FILL_PARENT,
				MapView.LayoutParams.FILL_PARENT  );
		
		mapView = new MapView( screen, MapApiKey );
		
		layout.addView( mapView, vl );
		
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		 
		Double x_pos = x * _1E6_;
		Double y_pos = y * _1E6_;
		
		GeoPoint point = new GeoPoint( y_pos.intValue(), x_pos.intValue() );
		mapController.setCenter( point );
		mapController.setZoom( 14 );
	}	
}
