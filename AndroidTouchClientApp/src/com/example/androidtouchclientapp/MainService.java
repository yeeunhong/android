package com.example.androidtouchclientapp;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {
	private final String LOG_TAG = "@>>-- MainService";
	
	private SocketServerThread socketServerThread = null;
	
	@Override
	public IBinder onBind(Intent intent) {		
		Log.d( LOG_TAG, "MainService::onBind" );
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d( LOG_TAG, "MainService::onStartCommand" );

		// Start socket server thread
		new Handler().postDelayed( new Runnable(){
			@Override
			public void run() {
				if( socketServerThread != null ) {
					socketServerThread.threadStop();
				}
				socketServerThread = new SocketServerThread( MainService.this.getApplicationContext() );			
				socketServerThread.start();
				
				Map<State,String> messageState = new HashMap<State,String>();
				messageState.put( State.NEW, "NEW(The thread has been created, but has never been started.)" );
				messageState.put( State.RUNNABLE, "RUNNABLE(The thread may be run.)" );
				messageState.put( State.BLOCKED, "BLOCKED(The thread is blocked and waiting for a lock.)" );
				messageState.put( State.WAITING, "WAITING(The thread is waiting.)" );
				messageState.put( State.TIMED_WAITING, "TIMED_WAITING(The thread is waiting for a specified amount of time.)" );
				messageState.put( State.TERMINATED, "TERMINATED(The thread has been terminated.)" );
				
				Log.d( LOG_TAG, String.format( "SocketServer State => %s", messageState.get( socketServerThread.getState() ) ));
				
			}}, 1000 );
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d( LOG_TAG, "MainService::onUnbind" );
		
		if( socketServerThread != null ) {
			if( socketServerThread.getState() != State.TERMINATED ) {
				socketServerThread.threadStop();
			}
		}
		
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		Log.d( LOG_TAG, "MainService::onCreate" );
		
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d( LOG_TAG, "MainService::onDestroy" );
		
		super.onDestroy();
	}
	
}
