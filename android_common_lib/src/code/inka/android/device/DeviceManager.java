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
 * Android 디바이스에 대한 정보를 얻기 위한 CLASS
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
 * @author 	맑은(준)호걸(호)
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
	 * 디바이스에서 유니크한 ID들을 조회하여 UUID 형식의 문자열로 돌려 줍니다. <br>
	 * 유니크한 ID의 조회 순서는 <br>
	 * 전화 모듈 ID -> WIFI 모듈 ID -> Android ID -> 램덤하게 생성
	 * @return UUID 형식의 디바이스의 유니크한 ID값, 오류 발생 시 null 리턴됨
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
			
			// TODO : preference 에서 해당 내용이 수정되었을 시 변경이 가능하다.
			// 보안이 적용된 preference 의 사용이 필요한 부분임
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
	 * 전화모듈의 장치 ID을 얻어서 UUID 형식을 문자열로 돌려 줍니다.
	 * <br><br><b>[2012.08.29]</b><br>telephone device id 로직 변경
	 * <br>&nbsp;&nbsp;&nbsp;&nbsp; : 기존 imei 값만 사용 => imei & imsi 값 사용
	 * @return UUID 형식의 전화모듈의 장치 ID 문자열 값, 오류 발생 시 null 리턴됨
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
	 * WIFI 모듈의 장치 ID값을 UUID 형식의 문자열로 돌려 줍니다. 
	 * @return UUID 형식의 WIFI모듈의 장치 ID 문자열 값, 오류 발생 시 null 리턴됨
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
	 * Android에서 재공하는 ANDROID_ID 값을 UUID 형식의 문자열로 변환하여 돌려 줍니다. 
	 * @return ANDROID_ID 값의 UUID 형식의 문자열, 오류 발생 시 null
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
	 * 단말기에 설정되어 있는 IP Address 값을 알려준다. 
	 * @return 단말기의 IP Address 값, 설정된  IP값이 없으면 null 반환
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
	 * 단말기의 MediaFrameWork에서 OpenCore를 사용하는 지를 알려 줍니다.<br>
	 * 단말기의 MediaFrameWork에서 OpenCore을 사용하더라도 Android 버전이 2.3 이상이면 true를 리턴한다.<br> 
	 * Android 2.3이상부터는 http 재생시에 stagefright을 사용한다. 
	 * @return true:OpenCore 사용, false:OpenCore 미 사용
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
	 * 단말기가 루팅이 되어 있는지를 확인 합니다. 
	 * @return true:루팅된 단말, 그렇지 않으면 false
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
	 * 활성화된 네트워크중에 promiscuous mode가 있는지를 확인 합니다. 
	 * @return true:promiscuous 로 설정된 network interface가 있음, 그렇지 않으면 false
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
	 * 현재 CPU의 사용률을 알려 줍니다. 
	 * @see getCPUsageInfo()의 수행 결과가 true 이때만 유효한 결과를 얻을 수 있습니다. 
	 * @return 현재 CPU의 사용률을 알려 줍니다.(단윈:%)
	 */
	public float getCPUsageUse() 	{ return m_cpu_use; }
	public float getCPUsageNice() 	{ return m_cpu_nice; }
	public float getCPUsageSystem() { return m_cpu_system; }
	public float getCPUsageIdle() 	{ return m_cpu_idle; }
	
	/**
	 * 단말기의 CPU 사용률에 대한 정보를 수집합니다. 
	 * @return true:성공, false:실패
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
	 * 단말기의 사용가능한(남은) 메모리 크기를 얻습니다.
	 * @see getMemoryInfo()의 수행 결과가 true 이때만 유효한 결과를 얻을 수 있습니다. 
	 * @return 단말기의 사용가능한(남은) 메모리의 크기( KB 단위 )
	 */
	public int getMemoryFree() { return m_memory_free; }
	
	/**
	 * 단말기의 전체 메모리 크기를 얻습니다.
	 * @see		getMemoryInfo() 의 수행 결과가 true 이때만 유효한 결과를 얻을 수 있습니다. 
	 * @return 단말기의 전체 메모리 크기( KB 단위 )
	 */
	public int getMemoryTotal() { return m_memory_total; }
	
	/**
	 * 단말기의 메모리 정보를 수집합니다.  
	 * @return true:성공, false:실패
	 */
	public boolean getMemoryInfo() {
		String[] cmd = {"/system/bin/sh", "-c", "cat /proc/meminfo" };
		Runtime operator = Runtime.getRuntime();
		 
		Process process;
		try {
			process = operator.exec( cmd );
			BufferedReader buffer = new BufferedReader( new InputStreamReader( process.getInputStream()));
			
			// 실제 메로리 값
			String temp = buffer.readLine();
			String [] temp_split 	= temp.split(":");
			String [] space_split	= temp_split[1].split("kB");
			
			// 남은 메모리 값
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
	 * 리턴되는 값이 null 이 아닌지를 검증 후 사용한다. 
	 * @return 단말기에 연결된 외부 디스크(SDCard)들의 CID 값들  
	 */
	public static String[] getExternalStorageCIDs() 
	{
		String[] strCIDs	= null;

		File	fList[]		= new File("/sys/block/").listFiles();
		int		nFileCount	= fList.length;
		ArrayList<String>	strFilenames	= new ArrayList<String>();
			
		// 파일 추가
		for( int i = 0; i < nFileCount ; i++ )
		{
			if( fList[i].getAbsolutePath().startsWith("/sys/block/mmcblk"))
				strFilenames.add(fList[i].getAbsolutePath() + "/device/cid");
		}
		
		// 정렬
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
			// 외장이 없으므로 종료
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
