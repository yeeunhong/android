package kr.co.uniquantum.ui.views.sample;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.database.Cursor;
//import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactNameView extends UIScreenView {

	public ContactNameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams( 
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT );
		
		screen.setTitle("ContactNameView");
		
		ListView list = new ListView( screen );
		layout.addView( list, vl );
		list.setStackFromBottom( true );
		list.setTranscriptMode( ListView.TRANSCRIPT_MODE_NORMAL );
		list.setTextFilterEnabled( true );
		
		Cursor cursor = screen.getContentResolver().query( ContactsContract.Contacts.CONTENT_URI, null, null, null, null );
		//Cursor cursor = screen.getContentResolver().query( People.CONTENT_URI, null, null, null, null );
		if( cursor.getCount() > 0 )
		{
			ListAdapter adapter = new SimpleCursorAdapter( screen, android.R.layout.simple_list_item_1, cursor,
				new String[]{ ContactsContract.Contacts.DISPLAY_NAME }, new int[] { android.R.id.text1 });
				//new String[]{ People.NAME }, new int[] { android.R.id.text1 });
			list.setAdapter(adapter);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
