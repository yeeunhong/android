package dexclass.loader.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Provider;

import dexclass.loader.interface2.testInterface;

public class testImpl implements testInterface {

	@Override
	public int must_ret_10() {
		return 10;
	}

	@Override
	public int must_ret_100() {
		return 100;
	}

	@Override
	public int must_ret_1000() {
		return 1000;
	}

	@Override
	public String must_ret_hello() {
		return "hello";
	}

	@Override
	public String must_ret_hello_world() {
		return getFileHash( "/data/app/com.sponge.adarkdragon-1.apk", "SHA-256", null );
		
		//return "hong junho";
	}

	
	/**
     * @param path hash 값을 구하고자 하는 file의 전체 경로
     * @param alg hash 알고리즘 : 'MD5' 이나 'SHA-256' 둘 중 하나를 사용
     * @return hash data
     */
    public String getFileHash( String path, String alg, Provider provider ) {
    	MessageDigest msgDigest = null;
        byte[] digest = null;
        
        try {
        	if( provider == null ) {
        		msgDigest = MessageDigest.getInstance(alg);
        	} else {
        		msgDigest = MessageDigest.getInstance(alg, provider);
        	}
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }

        int byteCount;
        FileInputStream fis = null;
        byte[] bytes = null;
        
        try {
            fis = new FileInputStream(path);
            int available = fis.available();
            if( available == 0 ) return null;
            
            // 100K로 크기 고정
            if( available > 102400 ) available = 102400;  
            
            bytes = new byte[available];
            
            while ((byteCount = fis.read(bytes)) > 0) {
                msgDigest.update(bytes, 0, byteCount);
            }
                        
            digest = msgDigest.digest();
            
            //String hexText = new java.math.BigInteger(bytes).toString(16);
            //Log.d("HASH", String.format( "%s => %s", path, hexText ));
            
            int len = digest.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((digest[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(digest[i] & 0x0f, 16));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            //e.printStackTrace();
            
        } finally {
            if( fis != null ) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            bytes = null;
        }
        
        return null;
    }
	
}
