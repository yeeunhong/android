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
 * �ܸ����� Network ���� ���¸� Ȯ���� �ִ� CLASS
 * @author 	����(��)ȣ��(ȣ)
 */
public class NetworkManager {
	private Context 		m_context = null;
	
	/**
	 * WIFI�� �ƴ� network ������ ����� �ΰ� �Ǳ� ������ WIPI���� �����Ѵ�.
	 */
	static final public int TYPE_WIPI = ConnectivityManager.TYPE_WIFI;  
	
	public NetworkManager( Context context ) {
		m_context = context;
	}

	/**
	 * network�� ����Ǿ� �ִ����� Ȯ���Ѵ�. 
	 * @return true:����Ǿ� ����, false:������� ����
	 */
	public boolean isConnected() {
		return getConnectType() > 0;
	}
	
	/**
	 * network�� ����� type ���� �˷� �д�. 
	 * @return -1:����� network �� ����, otherwise:network type 
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
	 * �̹� �˷��� TimeServer�� ���� Time ������ ��� �´�.<br> 
	 * http://www.epochconverter.com site���� ���� Ȯ���� �� �ִ�. 
	 * @return ���� �ð� ��(�� ����)
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
