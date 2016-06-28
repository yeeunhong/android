package kr.co.uniquantum.ui;

import java.util.ArrayList;

import kr.co.uniquantum.ui.activity.UIScreen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SimpleListView extends UIScreenView implements OnItemClickListener
{
	public OnItemClickListener itemClickListener = null;
	protected ArrayList<String> 	list;
	protected ArrayAdapter<String>	adapter;
	
	public SimpleListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		list = new ArrayList<String>();
		adapter = new ArrayAdapter<String>( screen, 
				android.R.layout.simple_list_item_1, list );
				
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		
		ListView listView = new ListView( screen );
		layout.addView( listView, vl );
		listView.setAdapter( adapter );
		//listView.setOnItemSelectedListener( this );
		listView.setOnItemClickListener( this );
		SetListData();
	}
	
	public void SetOnItemClickListener( OnItemClickListener _itemClickListener )
	{
		itemClickListener = _itemClickListener;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if( itemClickListener != null )
		{
			itemClickListener.onItemClick(arg0, arg1, arg2, arg3);
		}
	}

	protected void SetListData()
	{
	}
	
	public ArrayList<String> GetArrayList(){ return list; }
	public void SetArrayList( ArrayList<String> _list )
	{ list = null; list = _list; }
	
	public void UpdateList(){ adapter.notifyDataSetChanged(); }
}
