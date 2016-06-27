package com.example.android;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        FileOutputStream fos = null;
        try {
        	AssetFileDescriptor testFileD = getAssets().openFd("test.mp3");
        	//AssetFileDescriptor testFileD = getAssets().openFd("file:///android_asset/test.txt");
        	
        	//if( testFileD.getLength() == AssetFileDescriptor.UNKNOWN_LENGTH ) 
        	{
        		fos = testFileD.createOutputStream();
        		
        		String testMessage = "test message";
        		byte [] byteTestMessage = testMessage.getBytes();
        		
        		fos.write( byteTestMessage, 0, byteTestMessage.length );
        		fos.close();
        	}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( fos != null ) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        
        InputStream fis = null;
        try {
        	fis = getAssets().open("test.mp3", AssetManager.ACCESS_BUFFER );
			
        	byte buffer [] = new byte[1024];
        	
        	int nRead = fis.read(buffer, 0, 1024);
        	String read_message = new String( buffer, 0, nRead );
        	Log.d( "MY", read_message );
        	
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if( fis != null ) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
}

