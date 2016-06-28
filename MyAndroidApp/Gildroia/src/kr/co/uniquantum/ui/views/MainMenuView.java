package kr.co.uniquantum.ui.views;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import kr.co.uniquantum.ui.views.sample.UITestView;
import kr.co.uniquantum.ui.views.search.SearchMenu;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

class MainMenuView extends UIScreenView implements View.OnClickListener {

	public MainMenuView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams( 
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT );
		
		screen.setTitle("MainMenuView");
		
		addButton( "지도 보기", 100, layout, vl, this );
		addButton( "환경 설정", 200, layout, vl, this );
		addButton( "위치 찾기", 300, layout, vl, this );
		addButton( "경로 관리", 400, layout, vl, this );
		
		addButton( "UI Test View", 500, layout, vl, this );		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case 100 :	// 지도 보기
		{
			SharedPreferences.Editor edit = GetPreferencesEditor( "Search" );
			edit.putInt( "SearchMode", SearchModeEnum.SEARCH_MODE_NONE );
			edit.putString( "SidoName", "" );
			edit.putString( "SigunguName", "" );
			edit.putString( "DongName", "" );
			edit.putString( "BunjiValue", "" );
			edit.putString( "HoValue", "" );
			edit.putString( "RoadName", "" );
			edit.putString( "AptName", "" );		
			edit.putString( "CallNumber", "" );
			edit.commit();
			
			//LocationManager locationManager = (LocationManager) this_screen.getSystemService( Context.LOCATION_SERVICE );
			//Location location = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
			
			//Intent intent = new Intent( getContext(), kr.co.uniquantum.ui.activity.SearchResultOnMap.class );
			Intent intent = new Intent( getContext(), com.uniquantum.www.UMapViewActivity.class );
			//intent.putExtra( "x", location.getLongitude() );
			//intent.putExtra( "y", location.getLatitude() );
			intent.putExtra( "posType", 1 );	// 좌표가 WGS 좌표임을 나타냄
			intent.putExtra( "name", " " );
			intent.putExtra( "driveMode", true );
			this_screen.startActivity( intent );
		}
			break;
			
		case 200 :	// 환경 설정
			
			break;
			
		case 300 :	// 위치 찾기
			changeScreen(new SearchMenu( this_screen ));
			break;
		
		case 400 :	// 경로 관리
			
			break;
			
		case 500 :	// UI Test View
			changeScreen(new UITestView( this_screen ));
			break;
		}
	}
}
