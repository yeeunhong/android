package kr.co.uniquantum.ui.views.search.address;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SearchAddressDong extends SearchAddressSigungu 
{
	protected int nSigunguIdx = -1;
	public int nItemCount = 0;
	
	protected boolean bTextSearchMode = false;
	protected String searchText = null;
	
	public SearchAddressDong(Context context, int _nSidoIdx, int _nSigunguIdx  ) {
		super(context,_nSidoIdx);
		// TODO Auto-generated constructor stub
		nSigunguIdx 	= _nSigunguIdx;
		bTextSearchMode	= false;
	}
	
	public SearchAddressDong( Context context, String text )
	{
		//super(context, _search_module, _nSidoIdx);
		// TODO Auto-generated constructor stub
		super( context );
		
		searchText 		= text;
		bTextSearchMode	= true;
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
	}
	
	public UIScreenView GetNextView( int nDongIdx )
	{
		/*
		return new SearchAddressBunji( 
				this_screen, 
				search_module, 
				nSidoIdx, 
				nSigunguIdx, 
				nDongIdx, false );
				*/
		/*
		return new SearchAddressApt(
				this_screen,
				search_module,
				nSidoIdx,
				nSigunguIdx,
				nDongIdx
		);
		*/
		return new SearchAddressBnA(
				this_screen,
				nSidoIdx,
				nSigunguIdx,
				nDongIdx
				);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//super.onItemClick(arg0, arg1, arg2, arg3);
		// 반드시 재 정의 하고 super.onItemClick 는 호출하지 않는다.  
		SharedPreferences.Editor edit = GetPreferencesEditor( "Search" );
		edit.putString( "DongName", list.get( arg2 ));
		edit.putInt( "SearchMode" , SearchModeEnum.SEARCH_MODE_DONG );
		edit.commit();
		
		if( itemClickListener == null ) 
		{
			int nDongIdx = arg2;
			this.changeScreen( GetNextView( nDongIdx ));				
		}
		else
		{
			itemClickListener.onItemClick(arg0, this, arg2, arg3);
		}
	}

	protected void SetListData()
	{
		if( nSigunguIdx == -1 && bTextSearchMode == false ) return;
		
		if( bTextSearchMode )
			nItemCount = search_module.GetSearchDongCount( searchText );
		else
			nItemCount = search_module.GetDongCount( nSigunguIdx );
		for( int i = 0; i < nItemCount; i++ ) 
			list.add( search_module.GetDongName( i ));
		adapter.notifyDataSetChanged();
	}
}
