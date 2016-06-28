package com.example.dexclassloadertest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import dalvik.system.DexClassLoader;
import dexclass.loader.interface2.testInterface;

public class MainActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d( "TEST", "ASSETS START");
		
		
		String jarPath = String.format( "/data/data/%s/abc.jar", getPackageName());
		AssetManager am = getApplicationContext().getAssets(); 
		try {
			InputStream is = am.open( "classes.zip" );
			FileOutputStream fos = new FileOutputStream( jarPath );
			
			byte buff[] = new byte[ 1024 ];
			
			int nRead = 0;
			while(( nRead = is.read( buff, 0, 1024)) > 0 ) {
				fos.write( buff, 0, nRead );
			}
			
			is.close();
			fos.close();
			
			Log.d( "TEST", "ASSETS DONE");
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		Log.d( "TEST", "ASSETS END");
		
		Log.d( "TEST", "START");
		
		final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);
		
		//DexClassLoader dcl = new DexClassLoader( "/mnt/sdcard/libTestImpl.jar", "/data/data/com.example.dexclassloadertest", null, getClass().getClassLoader() ); 
		DexClassLoader dcl = new DexClassLoader( jarPath, optimizedDexOutputPath.getAbsolutePath(), null, getClassLoader() );
		try {
			
			Class<?> cls = dcl.loadClass("dexclass.loader.impl.testImpl");
			//Class<?> cls = dcl.loadClass("com.covault.appsec.sdk.CovaultMobileSecuritySolution");
			Log.d( "TEST", "1");
			
			Constructor<?> cons = cls.getConstructor();
			Log.d( "TEST", "2");
			
			//cons.newInstance();
			//Log.d( "TEST", "3");
			 
			testInterface testInf = (testInterface) cons.newInstance();
			
			Log.d( "TEST", String.format( "testInf.must_ret_10 = %d", testInf.must_ret_10()));
			Log.d( "TEST", String.format( "testInf.must_ret_100 = %d", testInf.must_ret_100()));
			Log.d( "TEST", String.format( "testInf.must_ret_hello_world = %s", testInf.must_ret_hello_world()));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d( "TEST", "END");
	}
}
