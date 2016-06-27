package com.example.androidtouchclientapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

public class SocketClientThread extends Thread implements Runnable {
	private final String LOG_TAG = "@>>-- SocketClientThread";
	private long threadID = -1;
	private TouchEvent touchEvent = new TouchEvent(); 
	
	private final Socket client_sock;
	public SocketClientThread( Context context, Socket socket ) {
		client_sock = socket;		
	}
	
	@Override
	public void run() {
		threadID = getId();
		Log.i( LOG_TAG, String.format( "ClientSocket Start(%d)", threadID ));
			
		if( !touchEvent.isEnable()) {
			Log.e( LOG_TAG, "This device don't support touchscreen event" );
			return;
		}
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream 	= client_sock.getInputStream();
			outputStream	= client_sock.getOutputStream();
			
			byte buffer[] = new byte[1024];
			int byteCount = buffer.length;
			int readCount = 0;
			
			while(( readCount = inputStream.read( buffer, 0, byteCount)) > 0 ) {
				Log.i( LOG_TAG, "recived Msg" );
				
				String reciveMsg = new String( buffer, 0, readCount );
				if( reciveMsg.startsWith("TOUCHEVENT_DEVICENAME")) {
					// format = TOUCHEVENT_DEVICENAME:(device name)
					String [] token = reciveMsg.split(":");
					touchEvent.setTouchEventDeviceName( token[1].trim() );
				} else if( reciveMsg.startsWith("TOUCHEVENT_POSITION")) {
					// format = TOUCHEVENT_POSITION:100,200
					String [] token = reciveMsg.split(":");
					if( token.length < 2 ) {
						Log.i( LOG_TAG, "Invalied cmd : " + reciveMsg );
						continue;
					}
					String [] value = token[1].split(",");
					
					int x = Integer.valueOf( value[0] );
					int y = Integer.valueOf( value[1] );
					
					touchEvent.touchDOWN(x, y);
					touchEvent.touchUP();
					
					Log.i( LOG_TAG, String.format( "TOUCH POSITION : X=%d, Y=%d", x, y ));
				}	
				Log.i( LOG_TAG, "reciveMsg:" + reciveMsg );
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if( inputStream != null ) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if( outputStream != null ) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		Log.i( LOG_TAG, String.format( "ClientSocket End(%d)", threadID ));
	}
}
