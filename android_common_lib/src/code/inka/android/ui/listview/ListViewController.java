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
	 * customListView를 초기화 합니다. 
	 * @param context context 객체, 주로 activity 객체로 값을 넘기면 됨
	 * @param container customListView가 표시된 ViewGroup 객체, 입력된 container안에 fill_parent 값으로 customListView가 add 됨
	 * @param cell_res_layout1 customListView의 홀수 컬럼에 사용될 LayoutID
	 * @param cell_res_layout2 customListView의 짝수 컬럼에 사용될 LayoutID, -1 을 입력하면 cell_res_layout1 만을 사용함
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
	 * OnListViewListener을 설정 함
	 * @param listener OnListViewListener 객체
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
	 * ListView가 가지고 있는 Item의 개수를 리턴함
	 * @return ListView가 가지고 있는 항목 개수
	 */
	public int GetItemCount() { return items.size(); }
	
	/** 
	 * ListView가 가지고 있는 Item의 ArrayList 객체를 리컨함
	 * @return ListView가 가지고 있는 항목의 데이터 객체 ArrayList 
	 */
	public ArrayList<Object> GetListData(){ return items; }

	/**
	 * ListView의 항목들을 새로 갱신 합니다. 
	 */
	public void SetListData() {
		items.clear();
		if( listViewListener != null ) listViewListener.OnSetListData( this, items );
	}
	
	/**
	 * ListView의 항목중 수정된 내용을 갱신합니다. 
	 */
	public void UpdateList() {
		adapter.notifyDataSetChanged();
	}

	/**
	 * ListView의 특정 항목을 삭제 합니다. 
	 * @param index 삭제할 항목의 index 값
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


