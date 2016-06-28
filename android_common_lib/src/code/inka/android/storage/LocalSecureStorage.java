package code.inka.android.storage;

import code.inka.android.crypto.SimpleCrypto;
import android.content.Context;
import android.provider.Settings.Secure;

public class LocalSecureStorage extends LocalStorage {
	public LocalSecureStorage(Context context) {super(context);	}
	public LocalSecureStorage(Context context, String storageName) { super(context, storageName); }
	
	@Override
	public void setValue(String key, String value) 
	{
		String androidId = Secure.getString( context.getContentResolver(), Secure.ANDROID_ID);
		try
		{
			String eKey		= SimpleCrypto.encrypt(androidId, key);
			String eValue 	= SimpleCrypto.encrypt(androidId, value);
		
			super.setValue(eKey, eValue);
		} 
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String getValue(String key, String defValue) {
		
		String androidId = Secure.getString( context.getContentResolver(), Secure.ANDROID_ID);
		try
		{		
			String eKey		= SimpleCrypto.encrypt(androidId, key);
			String eValue 	= super.getValue(eKey,"not found key");
		
			if( eValue.equals("not found key")) 
			{
				return null;
			}
			
			String value 	 = SimpleCrypto.decrypt(androidId, eValue);
			return value;
		} 
		catch( Exception e )
		{
			e.printStackTrace();
		}
				
		return null;
	}

	

}
