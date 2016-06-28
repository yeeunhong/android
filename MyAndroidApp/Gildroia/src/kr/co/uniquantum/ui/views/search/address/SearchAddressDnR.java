package kr.co.uniquantum.ui.views.search.address;

import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.UITabView;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

// 주소검색 화면, 동 and 도로명
public class SearchAddressDnR extends UIScreenView implements View.OnClickListener, OnItemClickListener 
{
	protected SearchModule search_module;
	protected int nSidoIdx;
	protected int nSigunguIdx;
	
	protected SearchAddressDong DongView;
	protected SearchAddressRoad RoadView;
	
	protected boolean bTextSearchMode = false;
	protected String searchText = null;
	
	public SearchAddressDnR(Context context, SearchModule _search_module,
			int _nSidoIdx, int _nSigunguIdx) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module 	= _search_module;
		nSidoIdx		= _nSidoIdx;
		nSigunguIdx		= _nSigunguIdx;
		
		bTextSearchMode = false;
	}
	
	public SearchAddressDnR(Context context, SearchModule _search_module, String text ) 
	{
		super(context);
		search_module 	= _search_module;
		searchText		= text;
		bTextSearchMode = true;
	}
	
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		LinearLayout.LayoutParams vl = new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT );
		
		if( bTextSearchMode )
			DongView = new SearchAddressDong( screen, new String( searchText ));
		else
			DongView = new SearchAddressDong( screen, nSidoIdx, nSigunguIdx );
		DongView.SetOnItemClickListener( this );
		DongView.setId( 100 );
		
		if( bTextSearchMode )
			RoadView = new SearchAddressRoad( screen, new String( searchText ) );
		else
			RoadView = new SearchAddressRoad( screen, nSidoIdx, nSigunguIdx );
		RoadView.SetOnItemClickListener( this );
		RoadView.setId( 200 );
		
		UITabView tabView	= new UITabView( screen );
		tabView.AddView( screen, "동" , DongView );
		tabView.AddView( screen, "도로명" , RoadView );
		tabView.CreateTabView( 0 );
		
		layout.addView( tabView, vl );
		
		tabView.SetTabTitle( 0, String.format( "%s(%d)", tabView.GetTabTitle( 0 ), DongView.nItemCount ));
		tabView.SetTabTitle( 1, String.format( "%s(%d)", tabView.GetTabTitle( 1 ), RoadView.nItemCount ));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch( arg1.getId())
		{
		case 100 : changeScreen( DongView.GetNextView( arg2 )); break;
		case 200 : changeScreen( RoadView.GetNextView( arg2 )); break;
		}		
	}
}
