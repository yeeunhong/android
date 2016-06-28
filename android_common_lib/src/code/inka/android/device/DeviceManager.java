package code.inka.android.device;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Android ����̽��� ���� ������ ��� ���� CLASS
 * @see getAndroidOsBuildMajorVersion() 
 * @see getAndroidOsBuildMinorVersion()
 * @see getCPUsageIdle()
 * @see getCPUsageInfo()
 * @see getCPUsageNice()
 * @see getCPUsageSystem()
 * @see getCPUsageUse()
 * @see getDeviceID()
 * @see getDeviceIdOfAndroidID()
 * @see getDeviceIdOfTelephony()
 * @see getDeviceIdOfWifi()
 * @see getMemoryFree()
 * @see getMemoryInfo()
 * @see getMemoryTotal()
 * @see isOpenCore()
 * 
 * @author 	����(��)ȣ��(ȣ)
 */
public class DeviceManager extends DeviceManagerInner {
	private Context 		m_context 		= null;
	private String			m_strDeviceID 	= null;
	
	public DeviceManager( Context context ) {
		super();
		
		m_context = context;
	}
	
	public boolean isEmulator() {
		if( Debug.isDebuggerConnected()) {
			return true;
		}
		
		if( "google_sdk".equals( Build.PRODUCT ) || "sdk".equals( Build.PRODUCT )) {
			//Log.e( LOG_TAG, String.format( "Build.PRODUCT is '%s'", Build.PRODUCT ));
			
			return true;
		}
		
		if( "google_sdk".equals( Build.MODEL ) || "sdk".equals( Build.MODEL )) {
			//Log.e( LOG_TAG, String.format( "Build.MODEL is '%s'", Build.MODEL ));
			
			return true;
		}
		
		if( Build.MANUFACTURER.equals("unknown")) {
			//Log.e( LOG_TAG, "Build.MANUFACTURER is unknown" );
			
			return true;
		}

		if( Build.FINGERPRINT.startsWith("generic") ) {
			//Log.e( LOG_TAG, "Build.FINGERPRINT.startsWith 'generic'" );
			
			return true;
		}
		
		final String androidId = Secure.getString( m_context.getContentResolver(), Secure.ANDROID_ID);
		if( androidId != null ) {
			if ( "9774d56d682e549c".equals(androidId)) {   
				//Log.e( LOG_TAG, "ANDROID_ID is '9774d56d682e549c'" );
				
				return true;
			}
		}
		
		TelephonyManager tm = (TelephonyManager) m_context.getSystemService(Context.TELEPHONY_SERVICE); 
		String networkOperator = tm.getNetworkOperatorName(); 
		
		final String telID = tm.getDeviceId();   
		if( telID != null ) {
			if( "000000000000000".equals(networkOperator)) {
				//Log.e( LOG_TAG, String.format( "TEL ID : %s", telID ));
				
				return true;
			}		
		}
		
		if("Android".equals(networkOperator)) { 
			// Emulator
			//Log.e( LOG_TAG, "TelephonyManager getNetworkOperatorName is Android" );
			return true;
			
		} else { 
			// Device 
		}	
		
		return false;
	}
	
	/**
	 * ����̽����� ����ũ�� ID���� ��ȸ�Ͽ� UUID ������ ���ڿ��� ���� �ݴϴ�. <br>
	 * ����ũ�� ID�� ��ȸ ������ <br>
	 * ��ȭ ��� ID -> WIFI ��� ID -> Android ID -> �����ϰ� ����
	 * @return UUID ������ ����̽��� ����ũ�� ID��, ���� �߻� �� null ���ϵ�
	 */
	public String getDeviceID() {
		if( m_strDeviceID != null ) {
			return m_strDeviceID;
		}
		
		final String PREFS_FILE = "device_id.xml";     
		final String PREFS_DEVICE_ID = "device_id"; 
		final String PREFS_VER_ID 	 = "prefs_ver";
		final String PREFS_VERSION	 = "1.0";
		
		synchronized ( this ) {   
			
			// TODO : preference ���� �ش� ������ �����Ǿ��� �� ������ �����ϴ�.
			// ������ ����� preference �� ����� �ʿ��� �κ���
			SharedPreferences prefs = m_context.getSharedPreferences( PREFS_FILE, 0); 
			String ver = prefs.getString(PREFS_VER_ID, null );
			String id  = prefs.getString(PREFS_DEVICE_ID, null );
			
			if( ver == null ) {
				id = null;
			} else {
				if( ver.compareTo( PREFS_VERSION ) != 0 ) {
					id = null;
				}
			}
			
			if (id != null ) { 
				return id;
			}
								
			String strDeviceID = getDeviceIdOfTelephony();
			if( strDeviceID == null ) {
				strDeviceID = getDeviceIdOfWifi();
				if( strDeviceID == null ) {
					strDeviceID = getDeviceIdOfAndroidID();
					if( strDeviceID == null ) {
						strDeviceID = UUID.randomUUID().toString();
					}
				}
			}				
							
			if( strDeviceID != null ) {
				prefs.edit().putString(PREFS_VER_ID, PREFS_VERSION ).commit();
				prefs.edit().putString(PREFS_DEVICE_ID, strDeviceID ).commit();
			}
			
			return strDeviceID;
		}
	}
	
	/**
	 * ��ȭ����� ��ġ ID�� �� UUID ������ ���ڿ��� ���� �ݴϴ�.
	 * <br><br><b>[2012.08.29]</b><br>telephone device id ���� ����
	 * <br>&nbsp;&nbsp;&nbsp;&nbsp; : ���� imei ���� ��� => imei & imsi �� ���
	 * @return UUID ������ ��ȭ����� ��ġ ID ���ڿ� ��, ���� �߻� �� null ���ϵ�
	 */
	public String getDeviceIdOfTelephony() {
		TelephonyManager tm = ( TelephonyManager ) m_context.getSystemService( Context.TELEPHONY_SERVICE );
		if( tm == null ) {
			return null;
		}
		
		final String imsi = tm.getSubscriberId();
		final String imei = tm.getDeviceId();
		
		String tel_device_id = "";
		if( imsi != null ) tel_device_id = tel_device_id + imsi;
		if( imei != null ) {
			if( !"000000000000000".equals(imei)) {
				tel_device_id = tel_device_id + imei;
			}
		}
		
		if( tel_device_id.length() > 2 ) {
			
			try {
				return UUID.nameUUIDFromBytes(tel_device_id.getBytes("utf8")).toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * WIFI ����� ��ġ ID���� UUID ������ ���ڿ��� ���� �ݴϴ�. 
	 * @return UUID ������ WIFI����� ��ġ ID ���ڿ� ��, ���� �߻� �� null ���ϵ�
	 */
	public String getDeviceIdOfWifi() {
		WifiManager	wifiMan = (WifiManager) m_context.getSystemService(Context.WIFI_SERVICE);
		if( wifiMan != null ) {
			WifiInfo	wifiInf = wifiMan.getConnectionInfo();
			String strMacAddress = wifiInf.getMacAddress();
			strMacAddress.replace("-", "");
			
			try {
				return UUID.nameUUIDFromBytes(strMacAddress.toString().getBytes("utf8")).toString();
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * Android���� ����ϴ� ANDROID_ID ���� UUID ������ ���ڿ��� ��ȯ�Ͽ� ���� �ݴϴ�. 
	 * @return ANDROID_ID ���� UUID ������ ���ڿ�, ���� �߻� �� null
	 */
	public String getDeviceIdOfAndroidID() {
		final String androidId = Secure.getString( m_context.getContentResolver(), Secure.ANDROID_ID);
		if( androidId == null ) return null;
		
		if (!"9774d56d682e549c".equals(androidId)) {   
			try {
				return UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}   
		}
		
		return null;
	}
	
	/**
	 * �ܸ��⿡ �����Ǿ� �ִ� IP Address ���� �˷��ش�. 
	 * @return �ܸ����� IP Address ��, ������  IP���� ������ null ��ȯ
	 */
	public static String getLocalIpAddress( Context context ) {
		
		WifiManager wifimng = ( WifiManager ) context.getSystemService( Context.WIFI_SERVICE );
		WifiInfo wifiInfo = wifimng.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		
		return String.format( "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress>>8&0xff), (ipAddress>>16&0xff), (ipAddress>>24&0xff));
		/*
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while ( en.hasMoreElements() ) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				
				while( enumIpAddr.hasMoreElements() ) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
					
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
				
		return null;
		*/
	}	
	
	/**
	 * �ܸ����� MediaFrameWork���� OpenCore�� ����ϴ� ���� �˷� �ݴϴ�.<br>
	 * �ܸ����� MediaFrameWork���� OpenCore�� ����ϴ��� Android ������ 2.3 �̻��̸� true�� �����Ѵ�.<br> 
	 * Android 2.3�̻���ʹ� http ����ÿ� stagefright�� ����Ѵ�. 
	 * @return true:OpenCore ���, false:OpenCore �� ���
	 */
	public boolean isOpenCore() {
		if( _backend == MP_UNKNOWN ) {
			getMediaPlayerBackend();
		} 
		boolean bRet = _backend < MP_STAGEFRIGHT;
		if( bRet && m_android_os_major >= 2 && m_android_os_minor > 2 ) {
			return true;
		}		
		
		return false;
	}
	
	/**
	 * �ܸ��Ⱑ ������ �Ǿ� �ִ����� Ȯ�� �մϴ�. 
	 * @return true:���õ� �ܸ�, �׷��� ������ false
	 */
	public static boolean isRooting() {
		String buildTags = android.os.Build.TAGS;
		if( buildTags != null && buildTags.contains("test-keys")) {
			return true;
		}
		
		try {
			File file = new File("/system/app/Superuser.apk");
			if( file.exists()) {
				return true;
			}
		} catch( Exception e ) {}
		
		try {
			Runtime.getRuntime().exec( new String[] {"su","-c"});
			return true;
		} catch (IOException e) {}
		
		return false;
	}
	
	/**
	 * @brief get result string of cat command
	 * @param target shell command parameter string
	 * @return result string
	 * @throws Exception
	 */
	public static synchronized final String shell( String cmd, String param ) {
		String[] shell_cmd = {"/system/bin/sh", "-c", cmd + " " + param };
		Runtime operator = Runtime.getRuntime();
		 
		BufferedReader buffer = null;
		try {		
			Process process;
			process = operator.exec( shell_cmd );
			process.waitFor();
			
			buffer = new BufferedReader( new InputStreamReader( process.getInputStream()));
			
			String result = null;
			
			String temp = buffer.readLine();
			while( temp != null ) {
				if( result == null ) result = temp;
				else {
					result += "\n" + temp;
				}
				temp = buffer.readLine();
			}
			
			buffer.close();
			buffer = null;
				
			if( result != null ) {
				return result;
			}
		} catch( Exception e ) {}
		
		if( buffer != null ) {
			try {
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * Ȱ��ȭ�� ��Ʈ��ũ�߿� promiscuous mode�� �ִ����� Ȯ�� �մϴ�. 
	 * @return true:promiscuous �� ������ network interface�� ����, �׷��� ������ false
	 */
	public static boolean isPromiscuousMode() {
		String result = shell( "ls", "/sys/class/net" );
		
		String[] net_ifs = result.split( "\n" );
		
		for( String net_name : net_ifs ) {
			String strflags = "";
			try{
				strflags = shell( "cat", "/sys/class/net/" + net_name + "/flags" );
				if( strflags != null ) {
					long flags = 0;
					
					if( strflags.startsWith("0x")) {
						strflags = strflags.substring(2);
					}
					flags = Long.parseLong( strflags, 16 );
					if( flags != 0 ) {
						if((flags & 0x100) != 0 ) {
							return true;
						}
					}
				}
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
		
	
	
	public int	getAndroidOsBuildMajorVersion() { return m_android_os_major; }
	public int	getAndroidOsBuildMinorVersion() { return m_android_os_minor; }
	
	/**
	 * ���� CPU�� ������ �˷� �ݴϴ�. 
	 * @see getCPUsageInfo()�� ���� ����� true �̶��� ��ȿ�� ����� ���� �� �ֽ��ϴ�. 
	 * @return ���� CPU�� ������ �˷� �ݴϴ�.(����:%)
	 */
	public float getCPUsageUse() 	{ return m_cpu_use; }
	public float getCPUsageNice() 	{ return m_cpu_nice; }
	public float getCPUsageSystem() { return m_cpu_system; }
	public float getCPUsageIdle() 	{ return m_cpu_idle; }
	
	/**
	 * �ܸ����� CPU ������ ���� ������ �����մϴ�. 
	 * @return true:����, false:����
	 */
	public boolean getCPUsageInfo() {
		String[] cmd = {"/system/bin/sh", "-c", "cat /proc/stat" };
		
		Runtime operator = Runtime.getRuntime();
		Process process;
		try {
			process = operator.exec( cmd );
			BufferedReader buffer = new BufferedReader( new InputStreamReader( process.getInputStream()));
			String temp = buffer.readLine();
			
			String [] str_temp = temp.split("[ ]");
			
			m_cpu_use 		= Float.parseFloat( str_temp[2] );
			m_cpu_nice		= Float.parseFloat( str_temp[3] );
			m_cpu_system 	= Float.parseFloat( str_temp[4] );
			m_cpu_idle		= Float.parseFloat( str_temp[5] );
						
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * �ܸ����� ��밡����(����) �޸� ũ�⸦ ����ϴ�.
	 * @see getMemoryInfo()�� ���� ����� true �̶��� ��ȿ�� ����� ���� �� �ֽ��ϴ�. 
	 * @return �ܸ����� ��밡����(����) �޸��� ũ��( KB ���� )
	 */
	public int getMemoryFree() { return m_memory_free; }
	
	/**
	 * �ܸ����� ��ü �޸� ũ�⸦ ����ϴ�.
	 * @see		getMemoryInfo() �� ���� ����� true �̶��� ��ȿ�� ����� ���� �� �ֽ��ϴ�. 
	 * @return �ܸ����� ��ü �޸� ũ��( KB ���� )
	 */
	public int getMemoryTotal() { return m_memory_total; }
	
	/**
	 * �ܸ����� �޸� ������ �����մϴ�.  
	 * @return true:����, false:����
	 */
	public boolean getMemoryInfo() {
		String[] cmd = {"/system/bin/sh", "-c", "cat /proc/meminfo" };
		Runtime operator = Runtime.getRuntime();
		 
		Process process;
		try {
			process = operator.exec( cmd );
			BufferedReader buffer = new BufferedReader( new InputStreamReader( process.getInputStream()));
			
			// ���� �޷θ� ��
			String temp = buffer.readLine();
			String [] temp_split 	= temp.split(":");
			String [] space_split	= temp_split[1].split("kB");
			
			// ���� �޸� ��
			String temp2= buffer.readLine();
			String [] temp_split2	= temp2.split(":");
			String [] space_split2	= temp_split2[1].split("kB");
			
			m_memory_total	= Integer.parseInt( space_split[0].trim());
			m_memory_free	= Integer.parseInt( space_split2[0].trim());
						
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * ���ϵǴ� ���� null �� �ƴ����� ���� �� ����Ѵ�. 
	 * @return �ܸ��⿡ ����� �ܺ� ��ũ(SDCard)���� CID ����  
	 */
	public static String[] getExternalStorageCIDs() 
	{
		String[] strCIDs	= null;

		File	fList[]		= new File("/sys/block/").listFiles();
		int		nFileCount	= fList.length;
		ArrayList<String>	strFilenames	= new ArrayList<String>();
			
		// ���� �߰�
		for( int i = 0; i < nFileCount ; i++ )
		{
			if( fList[i].getAbsolutePath().startsWith("/sys/block/mmcblk"))
				strFilenames.add(fList[i].getAbsolutePath() + "/device/cid");
		}
		
		// ����
		Collections.sort(strFilenames, 
			new Comparator<String>() {
				private final Collator   collator = Collator.getInstance();
				public int compare(String object1,String object2) {
					return collator.compare(object1, object2);
				}
			} 
		);
		nFileCount	= strFilenames.size();
		/*
		if( nFileCount < 2 )
		{
			// ������ �����Ƿ� ����
			return	strCID;
		}
		*/
		
		if( nFileCount < 1 ) return null;
		
		strCIDs = new String[ nFileCount ];
		
		for( int i = 0; i < nFileCount; i++ ) {
			try {
				BufferedReader input = new BufferedReader(new FileReader(strFilenames.get(i)));
				strCIDs[i] = input.readLine();
				input.close();
				
				if( strCIDs[i].length() >= 32 ) {
					strCIDs[i] = strCIDs[i].substring(0, 30);
					strCIDs[i] += strCIDs[i].substring(0,2);
				} else {
					for( int k = strCIDs[i].length(); k < 32; k++ ) {
						strCIDs[i] += "0";
					}
				}
				
				Log.i( "Util", "SD Card ID (" + i + ", " + strCIDs[i].length() + " bytes): " + strCIDs[i]);
				
			} catch (Exception e) {
				Log.e( "Util", "Can not read SD-card cid");
				strCIDs[i] = null;
			}
		}		
		
		return	strCIDs;
	}
}
