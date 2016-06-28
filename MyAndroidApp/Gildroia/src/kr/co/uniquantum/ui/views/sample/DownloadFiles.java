package kr.co.uniquantum.ui.views.sample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import kr.co.uniquantum.Global;
import kr.co.uniquantum.interfaces.IFDownloadProgress;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;

public class DownloadFiles extends UIScreenView implements View.OnClickListener, OnItemClickListener, IFDownloadProgress
{
	private final String FILE_LIST_URL = "http://192.168.2.131/SearchFiles/";
	private final String FILE_LIST_NAME = "filelist.html";
	private final String FileVersion = "SearchFileVersion";
	
	private ArrayList<DownloadFileInfo> basefileInfos = new ArrayList<DownloadFileInfo>(); 
	private ArrayList<DownloadFileInfo> fileInfos 	  = new ArrayList<DownloadFileInfo>();
	private ArrayList<DownloadFileInfo> downloadinfos = new ArrayList<DownloadFileInfo>();
	
	private DownloadFileAdapter adapter;
	private boolean checked_item[] = new boolean[100];
	private ProgressDialog progressDlg = null;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor edit;
	
	public DownloadFiles(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		LinearLayout ll = ( LinearLayout ) layout;
		ll.setOrientation( LinearLayout.VERTICAL );
		
		addButton( "다운로드", 100, ll, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT), this );
		
		adapter = new DownloadFileAdapter( 
			screen, 
			android.R.layout.simple_list_item_multiple_choice, 
			fileInfos );
		
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		
		ListView listView = new ListView( screen );
		ll.addView( listView, vl );
		listView.setAdapter( adapter );
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		//listView.setOnItemSelectedListener( this );
		listView.setOnItemClickListener( this );
		
		
		for( int i = 0; i < 100; ++i ) checked_item[i] = false;
		
		progressDlg = new ProgressDialog( screen );
		progressDlg.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
		progressDlg.setCancelable( true );
		progressDlg.setTitle( "file : " );
		
		preferences = screen.getSharedPreferences( FileVersion, Activity.MODE_PRIVATE );;
		edit = GetPreferencesEditor( FileVersion );
		
		new File( Global.GetSDCardPath() + "/MapData" ).mkdir();
		new File( Global.GetSDCardPath() + "/MapData/Search" ).mkdir();
				
		SetListData();
	}
	
	private void checkUpdateFiles()
	{
		try 
		{
			InputStream inputStream = new URL(FILE_LIST_URL + FILE_LIST_NAME).openStream();
			int nSize = inputStream.available();
			
			basefileInfos.clear();
			fileInfos.clear();
			
			byte f_buff[] = new byte[ nSize ];
			inputStream.read();
			inputStream.read();
			inputStream.read();
			inputStream.read( f_buff );
			inputStream.close();
			
			StringTokenizer st = new StringTokenizer( new String( f_buff ), "\r\n");
			while( st.hasMoreTokens() )
			{
				String oneLineInfo = st.nextToken().trim();
				if( oneLineInfo.length() < 2 ) continue;
				
				String preStr = oneLineInfo.substring(0,2);
				
				if( preStr.compareTo("//") == 0 ) continue;
				
				StringTokenizer oneLineSt = new StringTokenizer( oneLineInfo, "," );
				
				int nIndex = 0;
				DownloadFileInfo info = new DownloadFileInfo();
				while( oneLineSt.hasMoreTokens() )
				{
					String strItem = oneLineSt.nextToken().trim();
					switch( nIndex )
					{
					case 0 : info.baseItems = strItem.compareTo("0") == 0 ? true : false;
					case 1 : info.filename = strItem; break;
					case 2 : info.description = strItem; break;
					case 3 : info.version = strItem; break;
					case 4 : info.update_date = strItem; break;
					default : break;
					}
					++ nIndex;
				}
				
				int new_ver = Integer.parseInt( info.version ); 
				int old_ver = preferences.getInt( info.filename , 0 ); 
				if( new_ver > old_ver )
				{
					if( info.baseItems ) basefileInfos.add( info );
					else fileInfos.add( info );
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		edit.commit();
	}
	
	protected void SetListData()
	{
		checkUpdateFiles();
		adapter.notifyDataSetChanged();
	}
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case 100 :
			downloadinfos.clear();
			for( DownloadFileInfo info : basefileInfos ) downloadinfos.add( info );
			
			int nLen = fileInfos.size();
			for( int i = 0; i < nLen; ++i ) 
			{
				if( checked_item[i] == false ) continue;
				downloadinfos.add( fileInfos.get(i) );
			}
			
			progressDlg.setTitle( "file : " );
			progressDlg.setMax( 0 );
        	progressDlg.setProgress( 0 );
			
			new Thread( null, download_thread2 ).start();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		checked_item[arg2] = !checked_item[arg2]; 
	}

	public class DownloadFileInfo
	{
		public boolean baseItems;
		public String filename;
		public String description;
		public String version;
		public String update_date;
	}
	
	private class DownloadFileAdapter extends ArrayAdapter<DownloadFileInfo>
	{
		private ArrayList<DownloadFileInfo> items;
		private int layoutID = 0;
		public DownloadFileAdapter( Context context, int layoutID, ArrayList<DownloadFileInfo> _items )
		{
			super( context, layoutID, _items );
			items = _items;
			this.layoutID = layoutID;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = convertView;
			if( v == null )
			{
				LayoutInflater vi = (LayoutInflater)this_screen.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate( layoutID, null );
			}
			
			DownloadFileInfo info = items.get( position );
			if( info != null )
			{
				TextView tv = ( TextView ) v;
				tv.setText( info.filename + " - " + info.description );
			}
			
			return v;
		}
	}

	private Handler mProgressHandler = new Handler(){
		public void handleMessage(Message msg) 
        {
            //super.handleMessage(msg);
			int nIndex  	= msg.getData().getInt( "index" );
			int nTotal 		= msg.getData().getInt( "total" );
			int nCurrent 	= msg.getData().getInt( "current" );
           
			if( msg.what == -1 )
			{
				progressDlg.dismiss();
				SetListData();
			}
			else
			{
				if( nCurrent == 0 )
	            {
					progressDlg.setTitle( 
							String.format( "file : %s[%d/%d]", downloadinfos.get( nIndex ).filename, nIndex+1, downloadinfos.size() ));
	            	progressDlg.setMax( nTotal );
	            	progressDlg.setProgress( nCurrent );
	            	
	            	if( !progressDlg.isShowing())
	            		progressDlg.show();
	            }
	            else
	            {
	            	progressDlg.setProgress( nCurrent );
	            }
			}
        }
	};
	
	
	@Override
	public void updateDownloadProgress(String name, int total, int progress,
			int arg1, int arg2) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		Message msg = mProgressHandler.obtainMessage();
		
		bundle.putInt( "index", arg1 );
		bundle.putInt( "total", total );
		bundle.putInt( "current", progress );
		msg.setData(bundle);
		
		mProgressHandler.sendMessage( msg );
	}
	
	private Runnable download_thread2 = new Runnable() 
	{
		public void run()
		{
			int nFileIndex = 0;
			for( DownloadFileInfo info : downloadinfos )
			{
				Global.downLoadFileFromWebServer( 
						FILE_LIST_URL, info.filename, 
						Global.GetSearchFilePath() + "/", info.filename, 
						nFileIndex ++, 0, DownloadFiles.this );
					
				edit.putInt( info.filename, Integer.parseInt( info.version ));
				edit.commit();
			}
			 
			mProgressHandler.sendEmptyMessage(-1);
		}
	};
}
