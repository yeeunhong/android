package kr.co.uniquantum.ui.views.search.address;

import kr.co.uniquantum.Global;
import kr.co.uniquantum.search.POI_DATA;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;

public class SearchAddressApt extends SearchAddressBunji 
{
	public SearchAddressApt(Context context,
			int _nSidoIdx, int _nSigunguIdx, int _nDongIdx, boolean _bRoadBunji) {
		super(context, _nSidoIdx, _nSigunguIdx, _nDongIdx, _bRoadBunji);
		// TODO Auto-generated constructor stub
	}
	
	public SearchAddressApt(Context context, 
			int _nSidoIdx, int _nSigunguIdx, int _nDongIdx ) {
		super(context, _nSidoIdx, _nSigunguIdx, _nDongIdx, false );
		// TODO Auto-generated constructor stub
	}
 
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//super.onItemClick(arg0, arg1, arg2, arg3);
		// 반드시 재 정의 하고 super.onItemClick 는 호출하지 않는다.
		SharedPreferences.Editor edit = GetPreferencesEditor( "Search" );
		edit.putString( "AptName", list.get( arg2 ));
		edit.putInt( "SearchMode" , SearchModeEnum.SEARCH_MODE_APT );
		edit.commit();
		 
		if( itemClickListener == null ) 
		{  
			int nAptIdx = arg2;
			POI_DATA aptData = search_module.GetAptData( nAptIdx );
			showToast(
					String.format( "X좌표[%f], Y좌표[%f]", 
							(double)aptData.x / Global._60x60x120_, 
							(double)aptData.y / Global._60x60x120_ ),
				true );
		}   
		else
		{
			itemClickListener.onItemClick(arg0, this, arg2, arg3);
		}
	}
 
	protected void SetListData()
	{
		if( nDongIdx == -1 ) return;
		
		nItemCount = search_module.GetAptCount( nDongIdx );
		for( int i = 0; i < nItemCount; i++ ) 
			list.add( search_module.GetAptName( i ));
		adapter.notifyDataSetChanged();
	}
	 
	public UIScreenView GetNextView( int nAptIdx )
	{
		POI_DATA aptData = search_module.GetAptData( nAptIdx );
		
		Intent intent = new Intent( getContext(), kr.co.uniquantum.ui.activity.SearchResultOnMap.class );
		intent.putExtra( "x", (double)aptData.x / Global._60x60x120_ );
		intent.putExtra( "y", (double)aptData.y / Global._60x60x120_ );
		intent.putExtra( "route_x", (double)aptData.route_x / Global._60x60x120_ );
		intent.putExtra( "route_y", (double)aptData.route_y / Global._60x60x120_ );
		intent.putExtra( "name", list.get( nAptIdx ));
		this_screen.startActivity( intent );
		return null;
		
		/*
		return new SearchResult( this_screen, (double)aptData.x / Global._60x60x120_, 
						(double)aptData.y / Global._60x60x120_ );
					*/
				/*
		showToast( 
				String.format( "X좌표[%f], Y좌표[%f]", 
						(double)aptData.x / Global._60x60x120_, 
						(double)aptData.y / Global._60x60x120_ ),
					true);
		
		return null;
		*/
	}
}
