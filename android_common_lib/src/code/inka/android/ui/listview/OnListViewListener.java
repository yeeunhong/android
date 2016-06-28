package code.inka.android.ui.listview;

import java.util.ArrayList;

import android.view.View;
import android.widget.ListView;

/**
 * @author inka
 *
 */
public interface OnListViewListener {
	 
	/**
	 * ListView에 표시할 데이터들을 수집합니다. 
	 * @param view ListView 객체
	 * @param items ListView에 표시 할 데이터의 ArrayList
	 * @see 여기서 add 된 데이터는 ListView에 표시되기 전에 OnSetListData에 전달되어 ListView의 각각의 항목들을 구성합니다.  
	 */
	public void OnSetListData( ListView view, ArrayList<Object> items );
	
	
	/**
	 * @param view
	 * @param position
	 * @param item
	 * @param convertView
	 * @return
	 */
	public View OnGetListCellView( ListView view, int position, Object item, View convertView );
}
