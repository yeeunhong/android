package kr.co.uniquantum.ui.views.search.address;

import java.util.StringTokenizer;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SearchAddressBunji extends SearchAddressDong 
{
	protected int nDongIdx = -1; 
	protected boolean bRoadBunji = false;
	
	public SearchAddressBunji(Context context,
			int _nSidoIdx, int _nSigunguIdx, int _nDongIdx, boolean _bRoadBunji ) {
		super(context, _nSidoIdx, _nSigunguIdx);
		// TODO Auto-generated constructor stub
		nDongIdx 	= _nDongIdx;
		bRoadBunji	= _bRoadBunji;
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
			edit.putString( "BunjiValue", sp.nextToken());
		}
		edit.commit();
		
		if( itemClickListener == null ) 
		{
			int nBunjiIdx = arg2;
			this.changeScreen( 
				new SearchAddressHo( 
						this_screen, 
						nSidoIdx, 
						nSigunguIdx, 
						nDongIdx,
						nBunjiIdx, bRoadBunji )
				);				
		}
		else
		{
			itemClickListener.onItemClick(arg0, this, arg2, arg3);
		}
	}
	
	public UIScreenView GetNextView( int nBunjiIdx )
	{
		return new SearchAddressHo( 
				this_screen, 
				nSidoIdx, 
				nSigunguIdx, 
				nDongIdx,
				nBunjiIdx, bRoadBunji );
	}
	
	protected void SetListData()
	{
		if( nDongIdx == -1 ) return;
		
		nItemCount = bRoadBunji ? search_module.GetRoadBunjiCount( nDongIdx ) : search_module.GetBunjiCount( nDongIdx );
		for( int i = 0; i < nItemCount; i++ ) 
			list.add( String.format( "%d 번지", bRoadBunji ? search_module.GetRoadBunjiValue( i ) : search_module.GetBunjiValue( i )));
		adapter.notifyDataSetChanged();
	}
}
