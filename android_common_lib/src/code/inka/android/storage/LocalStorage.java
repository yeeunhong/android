package code.inka.android.storage;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
	protected Context context;
	private SharedPreferences pref = null; 
	private SharedPreferences.Editor editor = null;
	
	public LocalStorage( Context context ) { this( context, "LocalStorage" ); }
	public LocalStorage( Context context, String storageName ) {
		this.context = context;
		pref = context.getSharedPreferences( storageName, 0 );
		editor = pref.edit();
	}
	
	public void setValue( String key, String value ) {
		editor.putString( key, value );
		editor.commit();
	}
			
	public String getValue( String key, String defValue ) {
		return pref.getString( key, defValue);
	}
	
	public Map<String,?> getAll() {		
		return pref.getAll();
	}
	
	public void removeValue( String key ) {
		editor.remove(key);
		editor.commit();
	}
}
