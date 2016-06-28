package kr.co.uniquantum.ui.views.search.address;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SearchAddressRoad extends SearchAddressDong 
{
	public SearchAddressRoad(Context context, int _nSidoIdx, int _nSigunguIdx) {
		super(context, _nSidoIdx, _nSigunguIdx);
		// TODO Auto-generated constructor stub
	}

	public SearchAddressRoad( Context context, String text )
	{
		//super(context, _search_module, _nSidoIdx);
		// TODO Auto-generated constructor stub
		super( context, text );
	}
	
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
	}
	
	protected void SetListData()
	{
		if( nSigunguIdx == -1 && bTextSearchMode == false ) return;
		
		if( bTextSearchMode )
			nItemCount = search_module.GetSearchRoadCount( searchText );
		else
			nItemCount = search_module.GetRoadCount( nSigunguIdx );
				
		for( int i = 0; i < nItemCount; i++ ) 
			list.add( search_module.GetRoadName( i ));
		adapter.notifyDataSetChanged();
	}
	 
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor edit = GetPreferencesEditor( "Search" );
		edit.putString( "RoadName", list.get( arg2 ));
		edit.putInt( "SearchMode" , SearchModeEnum.SEARCH_MODE_ROAD );
		edit.commit();
		
		if( itemClickListener == null ) 
		{
			int nRoadIdx = arg2;
			this.changeScreen( GetNextView( nRoadIdx ));				
		}
		else
		{
			itemClickListener.onItemClick(arg0, this, arg2, arg3);
		}
	}

	public UIScreenView GetNextView( int nRoadIdx )
	{
		return new SearchAddressBunji( 
				this_screen, 
				nSidoIdx, 
				nSigunguIdx, 
				nRoadIdx, true );
	}
}
