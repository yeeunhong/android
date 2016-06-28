package code.inka.android.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SimpleCrypto
{
	public static String encrypt(String seed, String cleartext) throws Exception
	{
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] result = encrypt(rawKey, cleartext.getBytes());
		return toHex(result);
	}
	
	public static String decrypt(String seed, String encrypted) throws Exception
	{
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	private static byte[] getRawKey(byte[] seed) throws Exception
	{
		MessageDigest msgDigest = null;
	    byte[] digest = null;
	     
	    try {
	        msgDigest = MessageDigest.getInstance("MD5");
	        msgDigest.update( seed, 0, seed.length );
	        digest = msgDigest.digest(); 
	    } catch (NoSuchAlgorithmException e1) {
	        e1.printStackTrace();
	        return null;
	    }
        
	    byte new_seed[] = new byte[16]; 
	    if( digest == null ) {
	    	for( int i = 0; i < 16; i++ ) new_seed[ i ] = (byte)i;
	    } else {
		    if( digest.length >= 16 ) {
		    	for( int i = 0; i < 16; i++ ) new_seed[ i ] = digest[ i ];
		    } else {
		    	int i = 0;
		    	for( ; i < digest.length; i++ ) new_seed[ i ] = digest[ i ];
		    	for( ; i < 16; i++ ) new_seed[ i ] = (byte)i ;
		    }
	    }
	    
    	return new_seed;
	}

	
	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception
	{
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	    byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception
	{
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	    byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static String toHex(String txt)
	{
		return toHex(txt.getBytes());
	}
	
	public static String fromHex(String hex)
	{
		return new String(toByte(hex));
	}
	
	public static byte[] toByte(String hexString)
	{
		int len = hexString.length()/2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf)
	{
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2*buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}
	
	private final static String HEX = "0123456789ABCDEF";
	private static void appendHex(StringBuffer sb, byte b)
	{
		sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	}
	
}
