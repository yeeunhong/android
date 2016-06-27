package com.example.androidtouchclientapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootUpReceiver extends BroadcastReceiver {
	private final String LOG_TAG = "@>>-- BootUpReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d( LOG_TAG, "BootUpReceiver::onReceive" );
		
		//intent.setAction("com.example.androidtouchclientapp.MainService");
        context.startService( new Intent( context, MainService.class ));
	}

}
