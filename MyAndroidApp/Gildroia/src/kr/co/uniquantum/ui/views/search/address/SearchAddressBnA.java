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

public class SearchAddressBnA extends UIScreenView implements OnItemClickListener {

	protected SearchModule search_module;
	protected int nSidoIdx;
	protected int nSigunguIdx;
	protected int nDongIdx;
	
	protected SearchAddressBunji BunjiView;
	protected SearchAddressApt 	 AptView;
	
	public SearchAddressBnA(Context context,
			int _nSidoIdx, int _nSigunguIdx, int _nDongIdx ) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module 	= SearchModule.getInstance();
		nSidoIdx		= _nSidoIdx;
		nSigunguIdx		= _nSigunguIdx;
		nDongIdx		= _nDongIdx;
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		LinearLayout.LayoutParams vl = new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT );
		
		BunjiView = new SearchAddressBunji( screen, nSidoIdx, nSigunguIdx, nDongIdx, false );
		BunjiView.SetOnItemClickListener( this );
		BunjiView.setId( 100 );
		
		AptView = new SearchAddressApt( screen, nSidoIdx, nSigunguIdx, nDongIdx );
		AptView.SetOnItemClickListener( this );
		AptView.setId( 200 );
		
		UITabView tabView	= new UITabView( screen );
		tabView.AddView( screen, "번지" , BunjiView );
		tabView.AddView( screen, "아파트" , AptView );
		tabView.CreateTabView( 0 );
		
		layout.addView( tabView, vl );
		
		tabView.SetTabTitle( 0, String.format( "%s(%d)", tabView.GetTabTitle( 0 ), BunjiView.nItemCount ));
		tabView.SetTabTitle( 1, String.format( "%s(%d)", tabView.GetTabTitle( 1 ), AptView.nItemCount ));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch( arg1.getId())
		{
		case 100 : changeScreen( BunjiView.GetNextView( arg2 )); break;
		//case 200 : changeScreen( AptView.GetNextView( arg2 )); break;
		case 200 : AptView.GetNextView( arg2 ); break;
		}		
	}
}
