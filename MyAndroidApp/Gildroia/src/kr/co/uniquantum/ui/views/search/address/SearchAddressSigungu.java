package kr.co.uniquantum.ui.views.search.address;

import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SearchAddressSigungu extends SearchAddressSido 
{
	protected int nSidoIdx = -1;
	
	public SearchAddressSigungu(Context context, int _nSidoIdx  ) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		nSidoIdx = _nSidoIdx;		
	}
	
	public SearchAddressSigungu(Context context ) {
		// TODO Auto-generated constructor stub
		super( context );
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
		edit.putString( "SigunguName", list.get( arg2 ));
		edit.commit();
		
		if( itemClickListener == null )
		{
			int nSigunguIdx = arg2;
			//this.changeScreen( new SearchAddressDong( this_screen, search_module, nSidoIdx, nSigunguIdx ) );
			//this.changeScreen( new SearchAddressRoad( this_screen, search_module, nSidoIdx, nSigunguIdx ) );
			this.changeScreen( new SearchAddressDnR( this_screen, search_module, nSidoIdx, nSigunguIdx ) );
		}
		else
		{
			itemClickListener.onItemClick(arg0, arg1, arg2, arg3);
		}
	}
 
	protected void SetListData()
	{ 
		if( nSidoIdx == -1 ) return;
		
		int sigunguCnt = search_module.GetSigunguCount( nSidoIdx );
		for( int i = 0; i < sigunguCnt; i++ ) 
			list.add( search_module.GetSigunguName( i ) );
		adapter.notifyDataSetChanged();
	}
}
