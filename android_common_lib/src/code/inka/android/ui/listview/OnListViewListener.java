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
	 * ListView�� ǥ���� �����͵��� �����մϴ�. 
	 * @param view ListView ��ü
	 * @param items ListView�� ǥ�� �� �������� ArrayList
	 * @see ���⼭ add �� �����ʹ� ListView�� ǥ�õǱ� ���� OnSetListData�� ���޵Ǿ� ListView�� ������ �׸���� �����մϴ�.  
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
