package kr.co.uniquantum.ui.views.search;

//import unquantum.search.SearchModuleHelper;
import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import kr.co.uniquantum.ui.views.search.address.SearchAddressMain;
import kr.co.uniquantum.ui.views.search.favorite.SearchFavorite;
import kr.co.uniquantum.ui.views.search.poi.category.SearchPoiCategory;
import kr.co.uniquantum.ui.views.search.poi.name.SearchPoiName;
import kr.co.uniquantum.ui.views.search.poi.tel.SearchPoiCall;
import kr.co.uniquantum.ui.views.search.recent.SearchRecent;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

public class SearchMenu extends UIScreenView implements View.OnClickListener {
	
	final private int ID_MENU_FAVORITES = 0;
	final private int ID_MENU_RECENTS	= 1;
	final private int ID_MENU_ADDRESS	= 2;
	final private int ID_MENU_POI_NAME	= 3;
	final private int ID_MENU_POI_CATE	= 4;
	final private int ID_MENU_POI_TEL	= 5;
	final private int ID_MENU_SUBWAY	= 6;
	
	public SearchMenu(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
  
		this.addButton("즐겨찾기", 		ID_MENU_FAVORITES, 	layout, vl, this);
		this.addButton("최근 목적지", 		ID_MENU_RECENTS, 	layout, vl, this);
		this.addButton("주소 검색", 		ID_MENU_ADDRESS, 	layout, vl, this);
		this.addButton("명칭/상호 검색", 	ID_MENU_POI_NAME, 	layout, vl, this);
		this.addButton("업종 검색", 		ID_MENU_POI_CATE, 	layout, vl, this);
		this.addButton("전화 번호 검색", 	ID_MENU_POI_TEL, 	layout, vl, this);
		this.addButton("지하철역 검색", 	ID_MENU_SUBWAY, 	layout, vl, this);

		this.addButton("TEST", 800, layout, vl, this);
		
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
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		SearchModule.getInstance().ExitModule();
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case ID_MENU_FAVORITES :
			this.changeScreen( new SearchFavorite( this_screen ) );
			break;
		case ID_MENU_RECENTS :
			this.changeScreen( new SearchRecent( this_screen ) );
			break;
		case ID_MENU_ADDRESS :
			this.changeScreen( new SearchAddressMain( this_screen ) );
			break;
		case ID_MENU_POI_NAME :
			this.changeScreen( new SearchPoiName( this_screen ) );
			break;
		case ID_MENU_POI_CATE :
			this.changeScreen( new SearchPoiCategory( this_screen ) );
			break;
		case ID_MENU_POI_TEL :
			this.changeScreen( new SearchPoiCall( this_screen ) );
			break;
		case ID_MENU_SUBWAY :
			break;
		default : break;			
		}
	}
}
