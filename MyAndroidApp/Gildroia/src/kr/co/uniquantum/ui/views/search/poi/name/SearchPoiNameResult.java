package kr.co.uniquantum.ui.views.search.poi.name;

import java.util.ArrayList;

import android.content.Context;
import kr.co.uniquantum.search.SearchModule;
import kr.co.uniquantum.ui.SimpleListView;

public class SearchPoiNameResult extends SimpleListView 
{
	protected SearchModule search_module = null;
	protected String searchText = null;
	protected int nItemCount = 0;
	protected ArrayList<Boolean> flagCategory = new ArrayList<Boolean>();
	
	public SearchPoiNameResult(Context context, SearchModule _search_module, String text ) {
		super(context);
		// TODO Auto-generated constructor stub
		search_module 	= _search_module;
		searchText		= text; 
	}

	@Override
	protected void SetListData() {}
	
	public ArrayList<Boolean> GetCateFlagList(){ return flagCategory; }
	public void SetCateFlagList( ArrayList<Boolean> _flags ){ flagCategory = _flags; }
	public void OnSetListData()
	{
		search_module.GetSearchPoiNameCount( -1, searchText );
		
		list.clear();
		 
		if( search_module.MoveFirstPoiData())
		{ 
			do
			{
				flagCategory.add( search_module.IsCatePoi() );
				String branchName = search_module.GetPoiBranchName();
				if( branchName != null )
					list.add( search_module.GetPoiName() + " " + branchName );
				else 
					list.add( search_module.GetPoiName());
			} while( search_module.MoveNextPoiData());
		}
		
		nItemCount = list.size();
	}
	
	public String getText( int index ){ return list.get(index); }
}
