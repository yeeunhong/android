package com.example.android_common_lib_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import app.inka.android.R;
import code.inka.android.ui.util.UTIL;

public class MainActivity extends Activity implements OnClickListener {
	private final int REQUEST_ENABLE_BT = 0xBA;
	@SuppressWarnings("unused")
	private final String TAG = "android_common_lib"; 
	
	private Bluetooth bluetooth = new Bluetooth();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        int btnIds[] = { 
        		R.id.btnBtDiscoverable, R.id.btnBtDiscovering, R.id.btnBtStartServer, R.id.btnBtStopServer,
        		R.id.btnBtConnectionServer, R.id.btnBtConnectionServer2,
        		R.id.btnDialogTest };
        for( int btnId : btnIds ) {
	        Button btn = ( Button ) findViewById( btnId );
	        if( btn != null ) {
	        	btn.setOnClickListener( this );
	        }
        }
        
        bluetooth.init( this, REQUEST_ENABLE_BT );
	}
    
	@Override
	protected void onDestroy() {
		bluetooth.release();
		super.onDestroy();
	}

	public void onClick(View v) {
		switch( v.getId()) {
		case R.id.btnBtDiscoverable :
			bluetooth.enableDiscoverableMode( this );		// 다른 장치에서 나를 찾을 수 있도록 검색을 허용 함( 300초 동안 )
			break;
			
		case R.id.btnBtDiscovering  :
			bluetooth.startDiscovery( 10 * 1000 );	// 10 동안 만 검색
			break;
			
		case R.id.btnBtStartServer :
			bluetooth.startListenServer( this );
			break;
			
		case R.id.btnBtStopServer :
			bluetooth.stopListenServer();
			break;
			
		case R.id.btnBtConnectionServer :
			bluetooth.connectServer("8C:77:12:AA:48:B2"); // Connection SHW-M380W
			break;
			
		case R.id.btnBtConnectionServer2 :
			bluetooth.connectServer("B0:EC:71:12:AD:F0");	// Galaxy Nexus
			break;
			
		case R.id.btnDialogTest :
			dialogTest( this );
			break;
		}
	}
	
	static int g_num = 1;
	
	ViewGroup view = null; 
	public void dialogTest( final Activity act ){
		Builder dlg = new AlertDialog.Builder( act );
        
		dlg.setTitle("Bluetooth 장치를 검색 합니다.");
		dlg.setMessage( "연결할 장치를 선택해 주세요" );
		//dlg.setIcon(R.drawable.ic_launcher);
		dlg.setPositiveButton("취 소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( act, "그럼 계속 보셈", Toast.LENGTH_SHORT).show();
            }
        });
		
		dlg.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( act, "빠잉", Toast.LENGTH_SHORT).show();
            }
        });
        
		ViewGroup layout = UTIL.GetExternalView( act, R.layout.dialog_discovery );
		dlg.setView( layout );
		
		view = ( ViewGroup ) layout.findViewById( R.id.scrollViewInner );
		
		dlg.show();
	
		new Handler().postDelayed( runnable , 1000 );
	}
	
	Runnable runnable = new Runnable() {
		public void run() {
			
			MainActivity.this.runOnUiThread( new Runnable(){

				public void run() {
					Button btn = new Button( MainActivity.this );
					btn.setText( String.format( "button %d", g_num ));
					view.addView( btn );
				}});
			
			if( g_num++ < 10 ) {
				new Handler().postDelayed( runnable , 1000 );
			}
		}
	};
	
}
