package kr.co.uniquantum.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import kr.co.uniquantum.ui.activity.UIScreen;

interface MyCurstomListIF {
	public View getListItemView( int position, View convertView, ViewGroup parent );
	public View preListItemView();
	public void SetListData();
}
public class MyCustomListView extends UIScreenView implements OnItemClickListener, MyCurstomListIF
{
	protected final int MAX_TABLE_CELL_INDEX = 20;
	
	protected OnItemClickListener itemClickListener = null;
	protected ArrayList<MyListItem> 	items;
	protected MyListAdapter 			adapter;
	protected ArrayList<View> 			tableCellView = new ArrayList<View>();
	protected int						tableCellIndex = 0;
	
	public MyCustomListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyCustomListView(Context context, int orientation) {
		super(context, orientation);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		items = new ArrayList<MyListItem>();
		adapter = new MyListAdapter( screen, items );
				
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		
		ListView listView = new ListView( screen );
		layout.addView( listView, vl );
		listView.setAdapter( adapter );
		listView.setOnItemClickListener( this );
		
		for( int i = 0; i < MAX_TABLE_CELL_INDEX; ++i ) 
		{
			View view = preListItemView();
			if( view != null ) tableCellView.add( view );
		}
		
		SetListData();
		UpdateList();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void SetOnItemClickListener( OnItemClickListener _itemClickListener )
	{
		itemClickListener = _itemClickListener;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if( itemClickListener != null ) 
			itemClickListener.onItemClick(arg0, arg1, arg2, arg3);
	}
 		
	@Override
	public View preListItemView() 
	{
		return null;
	}

	@Override
	public View getListItemView(int position, View convertView, ViewGroup parent) 
	{
		if( tableCellView.size() > 0 )
		{
			if( tableCellIndex >= MAX_TABLE_CELL_INDEX ) tableCellIndex = 0;
			return tableCellView.get( tableCellIndex++ );
		}
		return convertView;
	}
	
	public void UpdateList(){ adapter.notifyDataSetChanged(); }
	public void SetListData()
	{
	}
	
	private class MyListAdapter extends ArrayAdapter<MyListItem>
	{
		public MyListAdapter( Context context, ArrayList<MyListItem> objects) {
			super(context, -1, objects);
			// TODO Auto-generated constructor stub
		}
		@Override
		public View getView( int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return MyCustomListView.this.getListItemView(position, convertView, parent);
		}
	}
	
	protected class MyListItem{ public MyListItem(){}}
}
