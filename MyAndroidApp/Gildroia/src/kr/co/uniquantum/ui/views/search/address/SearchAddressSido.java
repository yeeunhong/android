package kr.co.uniquantum.ui.views.search.address;

import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import kr.co.uniquantum.ui.views.search.SearchModeEnum;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SearchAddressSido extends kr.co.uniquantum.ui.SimpleListView
{
	protected SearchModule 			search_module;
	 
	public SearchAddressSido(Context context ) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module = SearchModule.getInstance();
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		SharedPreferences.Editor edit = GetPreferencesEditor( "Search" );
		edit.putString( "SidoName", list.get( arg2 ));
		edit.putInt( "SearchMode", SearchModeEnum.SEARCH_MODE_NONE );
		edit.commit();
				
		if( itemClickListener == null )
		{
			int nSidoIdx = arg2;
			this.changeScreen( GetNextView( nSidoIdx ) );
		}
		else
		{
			itemClickListener.onItemClick(arg0, arg1, arg2, arg3);
		}
	}

	protected void SetListData()
	{
		int sidoCnt = search_module.GetSidoCount();
		for( int i = 0; i < sidoCnt; i++ ) 
			list.add( search_module.GetSidoName(i));
		adapter.notifyDataSetChanged();
	}
	
	public UIScreenView GetNextView( int nSidoIdx )
	{
		return new SearchAddressSigungu( this_screen, nSidoIdx );
	}
}
