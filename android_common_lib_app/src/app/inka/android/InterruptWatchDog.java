package app.inka.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import code.inka.android.ui.listview.ListViewController;
import code.inka.android.ui.listview.OnListViewListener;

public class InterruptWatchDog extends Activity implements OnListViewListener {
	private ListViewController listViewCtrl = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_interrupts_view);
		
		ViewGroup vg = ( ViewGroup ) findViewById( R.id.listArea );
		
		listViewCtrl = new ListViewController( this );
		listViewCtrl.init( this, vg, R.layout.table_cell_interrupt_list, -1 );
		listViewCtrl.setOnListViewListener( this );
		listViewCtrl.SetListData();	// list의 내용을 채우고 	-> OnSetListData 가 호출되고
		listViewCtrl.UpdateList();	// 화면에 표시 한다. 		-> OnGetListCellView 가 호출된다.
	}

	private String removeDbSpace( String src ) {
		if( src == null ) return "";
		byte[] src_bytes = src.getBytes();
		
		int nLen = src_bytes.length - 1;
		int nIdx = 0;
		
		for( int i = 0; i < nLen; i++ ) {
			if( src_bytes[i] == ' ' && src_bytes[i+1] == ' ') continue;
			src_bytes[nIdx++] = src_bytes[i];
		}
		
		src_bytes[nIdx] = 0;
		
		return new String( src_bytes, 0, nIdx );
	}
	
	@Override
	public void OnSetListData(ListView view, ArrayList<Object> items) {

		Process process = null;
		try {
			process = Runtime.getRuntime().exec( new String[]{ "cat", "/proc/interrupts" } );
			process.waitFor();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
				
		InputStream is = process.getInputStream();
		BufferedReader br = new BufferedReader( new InputStreamReader( is ));
		
		String line = null;
		
		while( true ) {
			try {
				line = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
				break;
			}
				
			if( line == null ) {
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
				break;
			}
				
			String[] tokens = line.split( ":" );
			
			if( tokens == null ) continue;
			if( tokens.length < 2 ) continue;
			
			tokens[1] = removeDbSpace( tokens[1] ).trim();
			String[] values = tokens[1].split( " " );
			
			if( values == null ) continue;
			if( values.length < 3 ) continue;
			
			items.add( values );			
		}
		
		try { 
			br.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new Handler().postDelayed( new Runnable(){
			@Override
			public void run() {
				listViewCtrl.SetListData();
				listViewCtrl.UpdateList();
			}}, 1000 );
	}

	@Override 
	public View OnGetListCellView(ListView view, int position, Object item, View convertView) {
		
		TextView txText1 = ( TextView ) convertView.findViewById( R.id.tvText1 );
		TextView txText2 = ( TextView ) convertView.findViewById( R.id.tvText2 );
		TextView txText3 = ( TextView ) convertView.findViewById( R.id.tvText3 );
		TextView txText4 = ( TextView ) convertView.findViewById( R.id.tvText4 );
		
		String[] values = ( String[] ) item;
		
		txText1.setText(String.format("%d", position ));
		txText2.setText( values[0] );
		txText3.setText( values[1] );
		txText4.setText( values[2] );
			
		return convertView;
	}	
}
