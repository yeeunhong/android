package kr.co.uniquantum.ui.views.search.poi.name;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import kr.co.uniquantum.search.SearchModule;

public class SearchPoiNameResultCate extends SearchPoiNameResult 
{
	protected ArrayList<Integer> 	Indexlist = new ArrayList<Integer>();
	protected ArrayList<String> 	Textlist;
	
	public SearchPoiNameResultCate(Context context,
			SearchModule _search_module, String text) {
		super(context, _search_module, text);
		// TODO Auto-generated constructor stub
	}

	public void SetArrayList( ArrayList<String> _list )
	{
		Textlist = _list;
	}
	
	public void OnSetListData()
	{
		Indexlist.clear();
		list.clear();
		
		int nCount = Textlist.size();
		for( int i = 0; i < nCount; ++i )
		{
			if( flagCategory.get( i )) 
			{
				Indexlist.add( i );
				list.add( Textlist.get( i ));
			}
		}
				
		nItemCount = list.size();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if( itemClickListener != null )
		{
			itemClickListener.onItemClick(arg0, arg1, GetItemIndex(arg2), arg3);
		}
	}
	
	public int GetItemIndex( int index )
	{
		return Indexlist.get(index);
	}
}
