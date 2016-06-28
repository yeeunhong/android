package kr.co.uniquantum.ui.views.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Stack;

import kr.co.uniquantum.Global;
import kr.co.uniquantum.ui.MyCustomListView;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GPSFileList extends MyCustomListView implements OnItemClickListener
{
	private final String FILE_LIST_URL = "http://192.168.2.131/";
	private final String FILE_INFO_LIST = "FileInfoList.php?dir=./";
	
	Stack<String>  page_history = new Stack<String>();
	private String current_directory_url = "";
		
	public GPSFileList(Context context, int orientation) {
		super(context, orientation);
		// TODO Auto-generated constructor stub
	}

	public GPSFileList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		this.SetOnItemClickListener( this );
	}

	
	
	@Override
	public View preListItemView() 
	{
		LinearLayout ll = new LinearLayout( this_screen );
		ll.setOrientation( LinearLayout.VERTICAL );
		ll.setGravity( 6 );
		ll.setPadding( 10, 0, 10, 0 );
		
		TextView t1 = new TextView( this_screen );
		TextView t2 = new TextView( this_screen );
		TextView t3 = new TextView( this_screen );
		
		t1.setId( 100 ); t1.setTextSize( 40 );
		t2.setId( 200 ); t1.setTextSize( 25 );
		t3.setId( 300 ); t1.setTextSize( 25 );
		
		ll.addView( t1, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT, 40 ));
		ll.addView( t2, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT, 25 ));
		ll.addView( t3, new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT, 25 ));
		return ll;		
	}

	@Override
	public View getListItemView(int position, View convertView, ViewGroup parent) 
	{
		// TODO Auto-generated method stub
		View v = super.getListItemView(position, convertView, parent);
		if( v == null )	v = preListItemView();

		FileInfo item = ( FileInfo ) items.get( position ); 
		
		TextView t1 = ( TextView ) v.findViewById( 100 );
		TextView t2 = ( TextView ) v.findViewById( 200 );
		TextView t3 = ( TextView ) v.findViewById( 300 );
		
		t1.setText( item.name );
		t2.setText( String.format( "type : %s    size : %d byte", item.is_file?"FILE":"DIR", item.size ));
		t3.setText( String.format( "date : %s", item.date ));
			
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		FileInfo item = ( FileInfo ) items.get( arg2 );
		if( !item.is_file )
		{
			try 
			{
				page_history.add( current_directory_url );
				
				if( current_directory_url.length() > 0 )
					current_directory_url = 
						current_directory_url + "/" + URLEncoder.encode( item.name, "euc-kr" );
				else 
					current_directory_url = URLEncoder.encode( item.name, "euc-kr" );
				
				SetListData();
				UpdateList();
			} catch (UnsupportedEncodingException e) {	e.printStackTrace(); }
		}
		else
		{
			//try 
			//{
				Intent intent = new Intent( "kr.co.uniquantum.action.GPS_LOGFILE_URL" );
				intent.putExtra( "LogFileServer", FILE_LIST_URL );
				if( current_directory_url.substring(1).compareTo("/") == 0 )
					intent.putExtra( "LogFileUrl", current_directory_url.substring(1) + "/" + item.name );
				else
					intent.putExtra( "LogFileUrl", current_directory_url + "/" + item.name );
				this_screen.sendBroadcast( intent );
			//} 
			//catch (UnsupportedEncodingException e) {e.printStackTrace();}
						
		}
	}

	@Override
	public void SetListData() {
		// TODO Auto-generated method stub
		items.clear();
		readUrlFileList( current_directory_url );		
	}
	
	public void readUrlFileList( String filename )
	{
		InputStream inputStream;
		try {
			
			String url = FILE_LIST_URL + FILE_INFO_LIST + filename;
			//"http://aaaaa.com/param1="+java.net.URLEncoder.encode(param1,"euc-kr"); 이런식이나
			//안되면 java.net.URLEncoder.encode(new String(param.getByte("euc-kr")),"euc-kr") 이정도일듯요..
			
			inputStream = new URL( url ).openStream();
			
			//byte f_buff[] = new byte[ inputStream.available() ];
			
			inputStream.read();
			inputStream.read();
			inputStream.read();
			//while( inputStream.read( f_buff ) != -1 ) sb.append( new String( f_buff, "UTF-8" ));
			String content = Global.readOneLineStream_br( inputStream );
			do
			{
				if( content.length() < 10 ) break;
				
				FileInfo fileInfo = new FileInfo();
				
				int nIndex = 0;
				String ItemToken[] = content.split( ",", 4 );
				for( int k = 0; k < ItemToken.length; ++k )
				{
					String strItem = ItemToken[k].trim();
					
					switch( nIndex++ )
					{
					case 0 : fileInfo.is_file = strItem.compareTo( "0" ) == 1; break;
					case 1 : fileInfo.name = strItem; break;
					case 2 : fileInfo.size = Long.parseLong( strItem );break;
					case 3 : fileInfo.date = strItem; break;
					default : break;
					}					
				}
				
				items.add( fileInfo );
				content = Global.readOneLineStream_br( inputStream );
			}
			while( true );
			
			inputStream.close();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class FileInfo extends MyListItem
	{
		public boolean is_file = true;
		public String name = null;
		public long size = 0;
		public String date = null;
	}
	
	public void goBack() 
	{ 
		if( page_history.size() > 0 )
		{
			current_directory_url = page_history.lastElement();
			page_history.pop();
			SetListData();
			UpdateList();
		}
		else
		{
			super.goBack();
		}
	}
}
