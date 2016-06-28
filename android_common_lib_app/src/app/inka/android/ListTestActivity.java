package app.inka.android;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import code.inka.android.log.Log;
import code.inka.android.ui.listview.ListViewController;
import code.inka.android.ui.listview.OnListViewListener;

public class ListTestActivity extends Activity implements OnListViewListener {

	private ListViewController listViewCtrl = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.layout_list_view_test );
		
		ViewGroup vg = ( ViewGroup ) findViewById( R.id.list_area );
		
		listViewCtrl = new ListViewController( this );
		listViewCtrl.init( this, vg, R.layout.table_cell_local_file_list, -1 );
		listViewCtrl.setOnListViewListener( this );
		listViewCtrl.SetListData();	// list�� ������ ä��� 	-> OnSetListData �� ȣ��ǰ�
		listViewCtrl.UpdateList();	// ȭ�鿡 ǥ�� �Ѵ�. 		-> OnGetListCellView �� ȣ��ȴ�.
	}

	@Override
	public void OnSetListData(ListView view, ArrayList<Object> items) {
		
		// ����Ʈ�� ǥ�� �Ϸ��� �ϴ� �����͸� items �� �ִ´�. 
		for( int i = 0; i < 100; i++ ) {
			items.add( String.valueOf( i ));
		}
		
	}

	@Override
	public View OnGetListCellView(ListView view, int position, Object item, View convertView ) {
		Log.message( "OnGetListCellView %d", position );
		
		String str = ( String ) item;
		
		TextView tv = ( TextView ) convertView.findViewById( R.id.local_file_cell_text_1 );
		if( tv != null ) {
			tv.setText( str );
		}
		
		final int icon_ids[] = { 
				R.drawable.emo_im_angel, R.drawable.emo_im_cool, R.drawable.emo_im_crying, R.drawable.emo_im_embarrassed,
				R.drawable.emo_im_foot_in_mouth, R.drawable.emo_im_happy, R.drawable.emo_im_kissing, R.drawable.emo_im_laughing,
				R.drawable.emo_im_lips_are_sealed, R.drawable.emo_im_money_mouth, R.drawable.emo_im_sad, R.drawable.emo_im_surprised,
				R.drawable.emo_im_tongue_sticking_out, R.drawable.emo_im_undecided, R.drawable.emo_im_winking, R.drawable.emo_im_wtf,
				R.drawable.emo_im_yelling };


		ImageView iv = ( ImageView ) convertView.findViewById( R.id.local_file_cell_icon );
		if( iv != null ) {
			iv.setImageResource( icon_ids[ position % icon_ids.length ] );
		}
		
		return convertView;
	}

	



}
