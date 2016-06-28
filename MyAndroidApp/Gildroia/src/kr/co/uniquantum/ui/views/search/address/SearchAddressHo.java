package kr.co.uniquantum.ui.views.search.address;

import java.util.StringTokenizer;

import kr.co.uniquantum.Global;
import kr.co.uniquantum.search.HO_DATA;
import kr.co.uniquantum.search.ROAD_HO_DATA;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SearchAddressHo extends SearchAddressBunji {
	protected int nBunji = -1;
	protected boolean bRoadHo = false;
	
	public SearchAddressHo(Context context,
			int _nSidoIdx, int _nSigunguIdx, int _nDongIdx, int _nBunjiIdx, boolean _bRoadHo ) {
		super( context, _nSidoIdx, _nSigunguIdx, _nDongIdx, _bRoadHo );
		// TODO Auto-generated constructor stub
		nBunji 	= _nBunjiIdx;
		bRoadHo	= _bRoadHo;
	}
	
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
	}
	 
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//super.onItemClick(arg0, arg1, arg2, arg3);
		// 반드시 재 정의 하고 super.onItemClick 는 호출하지 않는다.  
		SharedPreferences.Editor edit = GetPreferencesEditor( "Search" );
		
		StringTokenizer sp = new StringTokenizer( list.get( arg2 ), " ");
		if( sp.hasMoreTokens())
		{
			edit.putString( "HoValue", sp.nextToken());
		}
		edit.commit();
		
		int nHoIdx = arg2;
		
		double x, y;
		if( bRoadHo )
		{
			ROAD_HO_DATA hoData = search_module.GetRoadHoData( nHoIdx );
			x = (double)hoData.x / Global._60x60x120_;
			y = (double)hoData.y / Global._60x60x120_;
			
			/*
			showToast( 
					String.format( "X좌표[%f], Y좌표[%f] 구주소:%s %d번지 %d호",
							(double)hoData.x / Global._60x60x120_, 
							(double)hoData.y / Global._60x60x120_,
							hoData.old_dongName, hoData.old_bunji, hoData.old_ho ),true);
			*/
		}
		else
		{
			HO_DATA hoData = search_module.GetHoData( nHoIdx );
			x = (double)hoData.x / Global._60x60x120_;
			y = (double)hoData.y / Global._60x60x120_;
			/*
			showToast(
					String.format( "X좌표[%f], Y좌표[%f]", 
							(double)hoData.x / Global._60x60x120_, 
							(double)hoData.y / Global._60x60x120_ ),
					true);
			*/
		}
		
		Intent intent = new Intent( getContext(), kr.co.uniquantum.ui.activity.SearchResultOnMap.class );
		intent.putExtra( "x", x );
		intent.putExtra( "y", y );
		intent.putExtra( "name", "Address Search" );
		
		this_screen.startActivity( intent );
	}

	protected void SetListData()
	{
		if( nBunji == -1 ) return;
		
		int hoCnt = bRoadHo ? search_module.GetRoadHoCount( nBunji ) : search_module.GetHoCount( nBunji );
		for( int i = 0; i < hoCnt; i++ ) 
			list.add( String.format( "%d 호", bRoadHo ? search_module.GetRoadHoValue( i ) : search_module.GetHoValue( i )));
		adapter.notifyDataSetChanged();
	}
}
