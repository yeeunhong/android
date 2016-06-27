package com.example.androidtouchclientapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

public class SocketServerThread extends Thread implements Runnable {
	private final String LOG_TAG = "@>>-- SocketServerThread";
	
	private ServerSocket server = null;
	private long threadID = -1;
	private Context context = null;
	
	public SocketServerThread( Context context ) {
		super();		
		this.context = context;
	}

	@Override
	public void run() {
		threadID = this.getId();
		
		Log.d( LOG_TAG, String.format( "SocketServerThread::run(%d)", threadID ));
		
		int port = new PropManager().getPortNumberFromFirstboot(12000);
		Log.i( LOG_TAG, "ServerSocket port : " + port );
		
		try {
			server = new ServerSocket( port );
		} catch (IOException e) {
			e.printStackTrace();
			
			Log.i( LOG_TAG, String.format( "ServerSocket Error(%d) : %s", threadID, e.getMessage() ));
			return;
		}	
		
		while( true ) {
			Socket client = null;
			
			try {
				client = server.accept();
			} catch (IOException e) {				
				break;
			} 
			
			Log.i( LOG_TAG, "incomming new socket : " + client.toString() );
			new SocketClientThread( context, client ).start();
		}
		
		Log.d( LOG_TAG, String.format( "SocketServerThread::terminated(%d)", threadID ));
	}

	public void threadStop() {
		Log.d( LOG_TAG, String.format( "SocketServerThread::threadStop(%d)", threadID ));
		
		try {
			if( server != null ) {
				server.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
