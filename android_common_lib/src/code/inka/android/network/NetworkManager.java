package code.inka.android.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 단말기의 Network 연결 상태를 확인해 주는 CLASS
 * @author 	맑은(준)호걸(호)
 */
public class NetworkManager {
	private Context 		m_context = null;
	
	/**
	 * WIFI가 아닌 network 연결은 요금이 부과 되기 때문에 WIPI만을 구별한다.
	 */
	static final public int TYPE_WIPI = ConnectivityManager.TYPE_WIFI;  
	
	public NetworkManager( Context context ) {
		m_context = context;
	}

	/**
	 * network에 연결되어 있는지를 확인한다. 
	 * @return true:연결되어 있음, false:연결되지 않음
	 */
	public boolean isConnected() {
		return getConnectType() > 0;
	}
	
	/**
	 * network에 연결된 type 값을 알려 둔다. 
	 * @return -1:연결된 network 가 없음, otherwise:network type 
	 */
	public int getConnectType() {
		ConnectivityManager cm = (ConnectivityManager) m_context.getSystemService (Context.CONNECTIVITY_SERVICE);
    	
		NetworkInfo networkInofs[] = cm.getAllNetworkInfo();
    	for( NetworkInfo networkInof : networkInofs ){
    		if( networkInof.isConnectedOrConnecting()) {
    			return networkInof.getType();
    		}
    	}
    	
    	return -1;
	}
	
	/**
	 * 이미 알려진 TimeServer로 부터 Time 정보를 얻어 온다.<br> 
	 * http://www.epochconverter.com site에서 값을 확인할 수 있다. 
	 * @return 현재 시간 값(초 단위)
	 */
	static public long getNetworkTimeFromTimeServer() {
		String[] machines = new String[]{ "time.nist.gov", "time.bora.net", "time.ewha.net", "time.korserve.net", "time.nuri.net"  };
				
	    final int daytimeport = 37;
	    byte[] buff = new byte[8];
	    
		for( int i = 0; i < machines.length; i++ ) {
		    try {
		    	SocketAddress addr = new InetSocketAddress( machines[i], daytimeport ); 
		    	
		    	Socket socket = new Socket();
			   	socket.setSoTimeout(1000);		// read timeout	
			   	socket.connect( addr, 1000);	// connect timeout
			   				   	
			    BufferedInputStream bis = new BufferedInputStream( socket.getInputStream());
			     
			    int nLen = bis.read( buff );
			    bis.close();
			    socket.close();
			    
		    	// Convert the byte array to an long value
			    long ret = 0;
			    for ( i = 0; i < nLen; i++ ) {
			    	int shift = (nLen - 1 - i) * 8;
		            ret += ((long) buff[i] & 0x000000FF) << shift;
		        }
		        			    
			    return ret - 2208988800L;
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return -1;		
	}
}
