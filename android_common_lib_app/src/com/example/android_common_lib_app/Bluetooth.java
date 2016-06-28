package com.example.android_common_lib_app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import app.inka.android.R;
import code.inka.android.storage.LocalStorage;
import code.inka.android.ui.util.UTIL;

public class Bluetooth {
	private static final String TAG = "BLUETOOTH";
	
	public static int ACTION_DISCOVERY_FOUND 		= 0x01;
	public static int ACTION_DISCOVERY_FINISHED 	= 0x02;
	private Handler action_event_handler = null;
	
	private final String NAME = "PUREHERO";
	private UUID uuid = null;
	
	private BluetoothAdapter btApt = null;
	private LocalStorage device_infos = null;
	private Context context;
	private boolean enabled = false;
	
	private AlertDialog discoveryAlertDialog 	= null;
	private ViewGroup 	discoveryView 			= null;
	
	private AcceptThread acceptThread = null;
	
	private final String MSG_DISCOVERY 		= "Bluetooth 장치를 검색 합니다.";
	private final String MSG_DISCOVERY_STOP = "Bluetooth 장치 검색 중지";
	private final String MSG_DISCOVERY_DONE = "Bluetooth 장치 검색 완료";
	private final String MSG_SELECT_DEVICE 	= "연결할 장치를 선택해 주세요.";
	
	/**
	 * Bluetooth 장치를 사용 할 수 있도록 초기화 한다.
	 * <br> Bluetooth 장치가 활성화 되어 있지 않다면 활성화 요청 창을 띄운다.  
	 * @param act 
	 * @param requestCode 장치 활성화 요청 시에 onActivityResult 에 전달될 requestCode 
	 * @return true : 사용 가능 함
	 */
	public boolean init( Activity act, int requestCode ) {
		context = act;
		
		btApt = BluetoothAdapter.getDefaultAdapter();
	    
		if( btApt == null ) {
	    	Log.d( TAG, "device does not support bluetooth");
	    	return false;
	    }
	
		enabled = btApt.isEnabled(); 
	    if( !enabled ) {
	    	Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
	    	act.startActivityForResult( enableBtIntent, requestCode );
	    	
	    	Log.d( TAG, "request device enable");
	    	return false;
	    }
	    
	    try {
			uuid = UUID.nameUUIDFromBytes( btApt.getAddress().getBytes("utf-8") );
			Log.i( TAG, String.format( "addr:%s of %s", btApt.getAddress(), btApt.getName()));
			Log.i( TAG, String.format( "uuid:%s of %s", uuid.toString(), btApt.getName()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			enabled = false;
		}
	    
	    if( device_infos == null ) {
	    	device_infos = new LocalStorage( context, "bluetooth_devices" );
	    }
	    
	    return true;
	}
	
	/**
	 * 사용했던 리소스들을 반환한다. 
	 */
	public void release() {
		stopListenServer();
		
		device_infos = null;
	}
	
	/**
	 * Buletooth 장치가 사용 가능한 지를 확인 한다. 
	 * @return true : 사용 가능
	 */
	public boolean isEnable() {
		if( !enabled ) {
			enabled = btApt.isEnabled();
			if( enabled ) {
				try {
					uuid = UUID.nameUUIDFromBytes( btApt.getAddress().getBytes("utf-8") );
					Log.i( TAG, String.format( "addr:%s of %s", btApt.getAddress(), btApt.getName()));
					Log.i( TAG, String.format( "uuid:%s of %s", uuid.toString(), btApt.getName()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					
					enabled = false;
				}
			}
		}
		
		if( enabled && device_infos != null ) {
			device_infos = new LocalStorage( context, "bluetooth_devices" );
		}
		
		return enabled;
	}
	
	/**
	 * bluetooth 장치 검색에 대한 이벤트를 전달 받을 핸들러를 설정 한다. <br><br>
	 * ACTION_DISCOVERY_FOUND 		: 장치를 발견 함<br>
	 * ACTION_DISCOVERY_FINISHED	: 장치 검색이 완료 됨
	 * @param handler 
	 */
	public void setActionEventHandler( Handler handler ) {
		action_event_handler = handler;
	}
	
	/**
	 * 한번이라도 discover 에서 검색된 bluetooth 장치의 정보를 모두 돌려 준다. 
	 * <br>discover 에서 검색된 장치는 내부적으로 정보를 저장 시킨다. 
	 * @return Map 형식의 데이터 <장치의 getAddress() 값, 장치의 getName() 값 > 이 돌려 진다. 
	 */
	public Map<String,?> getDeviceInfoAll() {
		return device_infos.getAll();
	}
	
	/**
	 * discover 하여 저장된 장치의 정보를 삭제한다.  
	 * @param devAddr 삭제 하려고 하는 장치의 getAddress() 값
	 */
	public void removeDeviceInfo( String devAddr ) {
		device_infos.removeValue( devAddr );
	}
	
	
	public void startListenServer( Activity act ){
		enableDiscoverableMode( act );
		
		if( acceptThread != null ) {
			acceptThread.release();
		}
		acceptThread = null;
		
		acceptThread = new AcceptThread();
		acceptThread.start();
	}
	
	public void stopListenServer() {
		if( acceptThread != null ) {
			acceptThread.release();
		}
		acceptThread = null;
	}
	
	public void connectServer( String serverAddr ) {
		new ConnectionThread( serverAddr ).start();
	}
	
	/**
	 * Bluetooth 장치를 다른 장치에서 검색이 가능하도록 설정한다. 
	 * <br>검색 가능한 시간은 300초 이다. 
	 * @param act
	 */
	public void enableDiscoverableMode( Activity act ) {
		Intent discoverableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		act.startActivity(discoverableIntent);
	}
	
	private final BroadcastReceiver findBtReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if( BluetoothDevice.ACTION_FOUND.equals( action )) {
				BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
				
				if( device_infos != null ) {
					device_infos.setValue( device.getAddress(), device.getName());
				}
				if( discoveryView != null ) {
					Button btn = new Button( context );
					btn.setText( String.format( "%s[%s]", device.getName(), device.getAddress() ));
					discoveryView.addView( btn );
				}
				
				if( action_event_handler != null ) {
					action_event_handler.sendEmptyMessage(ACTION_DISCOVERY_FOUND);
				}
				
				Log.i( TAG, "discovered : " + device.getName() + "\n" + device.getAddress() );
			
			// finished discovery
			} else if( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals( action )) {
				stopDiscovery();
				if( discoveryAlertDialog != null ) {
					if( discoveryAlertDialog.isShowing()) {
						discoveryAlertDialog.setTitle( MSG_DISCOVERY_DONE );
					}
				}
				if( action_event_handler != null ) {
					action_event_handler.sendEmptyMessage(ACTION_DISCOVERY_FINISHED);
				}
			}
		}
	};
	
	/**
	 * 주위의 응답 가능한 Bluetooth 장치들을 검색 한다.
	 * @param context
	 * @param duration 검색 시간(ms), duration(ms) 이후 검색을 중지 한다. 
	 */
	public void startDiscovery( final int duration ) {
		if( btApt == null ) {
			Log.e( TAG, "device does not support bluetooth" );
			return;
		}
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver( findBtReceiver, filter );
		
		showDiscoveryDialog();
		btApt.startDiscovery();
		
		new Handler().postDelayed( new Runnable(){
			public void run() {
				stopDiscovery();
			}}, duration );
	}
	
	/**
	 * 주위의 응답 가능한 bluetooth 장치를 찾는 작업을 중단한다. 
	 * @param context
	 */
	public void stopDiscovery() {
		if( btApt == null ) {
			Log.e( TAG, "device does not support bluetooth" );
			return;
		}
		
		if( btApt.isDiscovering()) {
			btApt.cancelDiscovery();
		}
		
		try{
			context.unregisterReceiver( findBtReceiver );
		} catch( Exception e ) {			
		}
	}
	
	private void showDiscoveryDialog(){
		Builder dlg = new AlertDialog.Builder( context );
        
		dlg.setTitle( MSG_DISCOVERY );
		dlg.setMessage( MSG_SELECT_DEVICE );
		//dlg.setIcon(R.drawable.ic_launcher);
		
		dlg.setPositiveButton( MSG_DISCOVERY_STOP, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	stopDiscovery();
            }
        });
		
		ViewGroup layout = UTIL.GetExternalView( context, R.layout.dialog_discovery );
		dlg.setView( layout );
		
		discoveryView = ( ViewGroup ) layout.findViewById( R.id.scrollViewInner );
		if( discoveryView != null ) {
			discoveryView.removeAllViews();
		}
		discoveryAlertDialog = dlg.show();
	}
	
	private class AcceptThread extends Thread {
		private BluetoothServerSocket serverSock = null;
				
		public AcceptThread() {
			try {
				serverSock = btApt.listenUsingRfcommWithServiceRecord( NAME, uuid );
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			if( serverSock == null ) {
				Log.i( TAG, "bluetooth accept socket is null!!" );
				return;
			}
			
			BluetoothSocket socket = null;
			
			Log.i( TAG, "bluetooth accept thread, start" );
			
			while( socket == null ) {
				try {
					socket = serverSock.accept();
					Log.i( TAG, "accepted" );
				} catch (IOException e) {
					break;
				}
			}
				
			if( socket != null ) {
				Log.i( TAG, "client socket not null!!" );
				Log.i( TAG, "connected!!!" );
				
				new SocketProcessThread( socket ).start();
			}
		}
		
		public void cancel() {
			try {
				if( serverSock != null ) {
					serverSock.close();
				}
				serverSock = null;
			} catch (IOException e) {
			}
		}
		
		public void release() {
			cancel();
			Log.i( TAG, "bluetooth accept thread, release()" );
		}
	};
	
	private class SocketProcessThread extends Thread {
		private final BluetoothSocket socket;
		
		public SocketProcessThread( BluetoothSocket socket ) {
			this.socket = socket;
		}

		@Override
		public void run() {
			Log.i( TAG, "socket process thread, start()" );
			
			release();
			super.run();
		}
		
		public void cancel() {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
		
		public void release() {			
			cancel();
			Log.i( TAG, "socket process thread, release()" );
		}
	};
	
	private class ConnectionThread extends Thread {
		private String serverAddr;
		
		public ConnectionThread( String serverAddr ) {
			this.serverAddr = serverAddr;			
		}
		
		@Override
		public void run() {
			Log.i( TAG, "connection process thread, start()" );
		
			BluetoothSocket socket = null;
			
			// 장치 검색을 중지 함
			btApt.cancelDiscovery();
			
			if( !BluetoothAdapter.checkBluetoothAddress(serverAddr)) {
				Log.e( TAG, String.format( " address[%s] is invalid!", serverAddr ));
				return;
			}
			
			BluetoothDevice serverDevice = null;
			try {
				serverDevice = btApt.getRemoteDevice(serverAddr);
			} catch( IllegalArgumentException e ) {
				e.printStackTrace();
				Log.e( TAG, String.format( " address[%s] is invalid!", serverAddr ));
				return;
			}
		
			UUID serverUUID = null;
			try {
				serverUUID = UUID.nameUUIDFromBytes( serverAddr.getBytes("utf-8") );
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				return;
			}
			
			try {
				socket = serverDevice.createRfcommSocketToServiceRecord(serverUUID);
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			
			try {
				socket.connect();
			} catch (IOException e1) {
				e1.printStackTrace();
				
				try { 
					socket.close();
				} catch (IOException e) {					
				}
				
				return;
			}
			
			new SocketProcessThread( socket ).start();
			super.run();
		}
	};
}
