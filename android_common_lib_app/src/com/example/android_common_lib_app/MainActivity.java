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
			bluetooth.enableDiscoverableMode( this );		// �ٸ� ��ġ���� ���� ã�� �� �ֵ��� �˻��� ��� ��( 300�� ���� )
			break;
			
		case R.id.btnBtDiscovering  :
			bluetooth.startDiscovery( 10 * 1000 );	// 10 ���� �� �˻�
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
        
		dlg.setTitle("Bluetooth ��ġ�� �˻� �մϴ�.");
		dlg.setMessage( "������ ��ġ�� ������ �ּ���" );
		//dlg.setIcon(R.drawable.ic_launcher);
		dlg.setPositiveButton("�� ��", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( act, "�׷� ��� ����", Toast.LENGTH_SHORT).show();
            }
        });
		
		dlg.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( act, "����", Toast.LENGTH_SHORT).show();
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
