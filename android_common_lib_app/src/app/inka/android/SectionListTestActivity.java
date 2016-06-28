package app.inka.android;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import code.inka.android.ui.listview.OnListViewListener;
import code.inka.android.ui.listview.SectionListData;
import code.inka.android.ui.listview.SectionListViewController;

public class SectionListTestActivity extends Activity implements OnListViewListener {

	private SectionListViewController listViewCtrl = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.layout_list_view_test );
		
		ViewGroup vg = ( ViewGroup ) findViewById( R.id.list_area );
		
		listViewCtrl = new SectionListViewController( this );
		listViewCtrl.init( vg, R.layout.table_cell_local_file_list, R.layout.table_section_local_file_list );
		listViewCtrl.setOnListViewListener( this );
		listViewCtrl.UpdateList();	// 화면에 표시 한다. 		-> OnGetListCellView 가 호출된다.
	}
	
	@Override
	public void OnSetListData(ListView view, ArrayList<Object> items) {
		// 리스트에 표시 하려고 하는 데이터를 items 에 넣는다. 
		for( int nSectionIdx = 0; nSectionIdx < 10; nSectionIdx++ ) {
			SectionListData sectionInfo = new SectionListData( String.format( "section %d", nSectionIdx ));
			for( int i = 0; i < 10; i++ ) {
				sectionInfo.add( String.format( "section %d data %d", nSectionIdx, i ));
			}
			
			items.add( sectionInfo );
		}
	}

	@Override
	public View OnGetListCellView( ListView view, int position, Object item, View convertView) {
		//Log.message( "OnGetListCellView %d", position );
		
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
