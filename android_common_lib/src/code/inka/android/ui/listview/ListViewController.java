package code.inka.android.ui.listview;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import code.inka.android.ui.util.UTIL;

public class ListViewController extends ListView {

	protected OnListViewListener 	listViewListener = null;
	protected ArrayList<Object> items = null;
	protected ArrayAdapter<Object> adapter = null;
	
	protected int listcell_res_layout1 = -1;
	protected int listcell_res_layout2 = -1;
	
	public ListViewController(Context context) { super(context); }
	
	/**
	 * customListView�� �ʱ�ȭ �մϴ�. 
	 * @param context context ��ü, �ַ� activity ��ü�� ���� �ѱ�� ��
	 * @param container customListView�� ǥ�õ� ViewGroup ��ü, �Էµ� container�ȿ� fill_parent ������ customListView�� add ��
	 * @param cell_res_layout1 customListView�� Ȧ�� �÷��� ���� LayoutID
	 * @param cell_res_layout2 customListView�� ¦�� �÷��� ���� LayoutID, -1 �� �Է��ϸ� cell_res_layout1 ���� �����
	 */
	public void init( Context context, ViewGroup container, int cell_res_layout1, int cell_res_layout2 ) {
		items = new ArrayList<Object>();
		adapter = new MyListAdapter(context, items);
		
		listcell_res_layout1 = cell_res_layout1;
		listcell_res_layout2 = cell_res_layout2;
		
		if( container != null ) {
			ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT );
	 
			container.addView( this, vl );
		}
		setAdapter(adapter);
				
		this.setCacheColorHint( 0x00000000 );
	}
	
	/**
	 * OnListViewListener�� ���� ��
	 * @param listener OnListViewListener ��ü
	 * @see OnListViewListener
	 */
	public void setOnListViewListener( OnListViewListener listener ) {
		listViewListener = listener;
	}
	
	protected View getListCellView( int position, View convertView, ViewGroup parent) {
		if( listViewListener != null ) {
			if( convertView == null && listcell_res_layout1 != -1 ) {
				if( listcell_res_layout2 == -1 ) {
					convertView = UTIL.GetExternalView( getContext(), listcell_res_layout1 );
				} else {
					if( position % 2 == 0 ) {
						convertView = UTIL.GetExternalView( getContext(), listcell_res_layout2 );
					} else {
						convertView = UTIL.GetExternalView( getContext(), listcell_res_layout1 );
					}
				}
				if( convertView == null ) return null;
			} 
			
			return listViewListener.OnGetListCellView( this, position, items.get( position ), convertView );
		}
		
		return convertView;
	}
	
	/**
	 * ListView�� ������ �ִ� Item�� ������ ������
	 * @return ListView�� ������ �ִ� �׸� ����
	 */
	public int GetItemCount() { return items.size(); }
	
	/** 
	 * ListView�� ������ �ִ� Item�� ArrayList ��ü�� ������
	 * @return ListView�� ������ �ִ� �׸��� ������ ��ü ArrayList 
	 */
	public ArrayList<Object> GetListData(){ return items; }

	/**
	 * ListView�� �׸���� ���� ���� �մϴ�. 
	 */
	public void SetListData() {
		items.clear();
		if( listViewListener != null ) listViewListener.OnSetListData( this, items );
	}
	
	/**
	 * ListView�� �׸��� ������ ������ �����մϴ�. 
	 */
	public void UpdateList() {
		adapter.notifyDataSetChanged();
	}

	/**
	 * ListView�� Ư�� �׸��� ���� �մϴ�. 
	 * @param index ������ �׸��� index ��
	 */
	public void DeleteListCell( int index ) { 
		adapter.remove( adapter.getItem( index ));
		items.remove( index );
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	protected class MyListAdapter extends ArrayAdapter<Object> {
		public MyListAdapter(Context context, ArrayList<Object> objects) {
			super(context, -1, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getListCellView(position, convertView, parent);
		}
	}
}


