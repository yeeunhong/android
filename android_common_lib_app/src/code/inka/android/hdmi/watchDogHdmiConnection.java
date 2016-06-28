package code.inka.android.hdmi;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import code.inka.android.http.HttpManager;
import code.inka.android.logcat.logcatProcess;
import code.inka.android.network.NetworkManager;
import code.inka.android.storage.LocalStorage;

public class watchDogHdmiConnection extends logcatProcess {
	private final String WDHC_SERVER_URL = "http://dahost.netsync.co.kr/@LiveUpdate/hdmi/HdmiConnectionInfo.txt";
	
	private ArrayList<String> 	logFilters 			= new ArrayList<String>();
	private ArrayList<String> 	logConnections 		= new ArrayList<String>();
	private ArrayList<String> 	logDisconnections	= new ArrayList<String>();
	private ArrayList<Integer> 	logConnectionsDir	= new ArrayList<Integer>();
	private int logLength = 0; 
	
	private static final String ACTION_EXTDISP_DISPLAY[] = {
		"com.motorola.intent.action.EXTDISP_STATUS_CONNECTION",
		"com.motorola.intent.action.EXTDISP_STATUS_DISPLAY",
		"android.intent.action.HDMI_PLUGGED"
	};
		
	private Activity m_act = null;
	private boolean isHdmiConnected = false;
	private onHdmiConnectionListener connectionListener = null;
	
	public boolean init( Activity act ) {
		if( !super.init()) return false;
		
		m_act = act;
		return true;
	}

	@Override
	public boolean OnInitInThread() {
		
		readWdhcInfo();
		
		logLength 			= logConnectionsDir.size();
		
		for( String logFilter : logFilters ) {
			addFilter( logFilter);
		}
		addFilter( "*:S" );
		
		if( m_act != null ) {
			IntentFilter filter = new IntentFilter();
			
			for( String strAction : ACTION_EXTDISP_DISPLAY ) {
				filter.addAction( strAction );
			}		
						
			m_act.registerReceiver( androidActionReceiver, filter );
		}
		
		return true;
	}
	
	private void addWdhcInfos( String strInfo ) throws Exception {
		if( strInfo.startsWith( "logFilter:")) {
			String infos[] = strInfo.substring("logFilter:".length()).split(",");
							
			if( infos.length > 0 ) {
				if( infos[0].length() > 3 ) {
					logFilters.add( infos[0] );
				}
				
				if( infos.length > 1 ) {
					if( infos[1].length() > 0 ) {
						logConnectionsDir.add( Integer.valueOf( (int)( infos[1].charAt(0)) ));
					}
				
					if( infos.length > 2 ) {
						if( infos[2].length() > 5 ) {
							logConnections.add( infos[2] );
						}
						
						if( infos.length > 3 ) {
							if( infos[3].length() > 5 ) {
								logDisconnections.add( infos[3] );
							}
						}
					}
				}
			}
		}
	}
	public void readWdhcInfo() {
		
		String static_info[] = new String[] {
			"logFilter:AudioService:W,c, connected, disconnected",
			"logFilter:AudioService:V,c,_connected,_disconnected",
			"logFilter:HDMIService:E,e,state = 1,state = 0",
			"logFilter:DisplayService:D,e,enableHDMIOutput(true),enableHDMIOutput(false),HTC_X515E",
			"logFilter:hdmid:D,c,sethdmistatus:1,sethdmistatus:0",
			"logFilter:hdmidisplay:W,e,enable,disable,LT15i",
			"logFilter:HDMIService:E,e,HDMI_CABLE_CONNECTED,HDMI_CABLE_DISCONNECTED",
			"logFilter:LIBHDMI:E,e,HDMIStart(),HDMIStop()"
		};
		
		int nCnt = static_info.length;
		for( int i = 0; i < nCnt; i++ ) {
			try {
				addWdhcInfos( static_info[i] );
			} catch (Exception e) {}					
		}		
		
		String wdhc_info = null;
		
		LocalStorage localStorage = new LocalStorage( m_act, "wdhc_info" );
		
		NetworkManager nm = new NetworkManager( m_act );
		if( nm.isConnected()) {
			wdhc_info = HttpManager.readString( HttpManager.safeUrlEncoder( WDHC_SERVER_URL ));
		} 
		
		if( wdhc_info != null ) {
			String wdhc_infos[] = wdhc_info.split("\r\n");
			localStorage.setValue( "cnt", String.format( "%d", wdhc_infos.length ) );
			
			for( int i = 1; i < wdhc_infos.length; i++ ) {
				localStorage.setValue( String.format( "%d", i-1 ), wdhc_infos[i] );
				try {
					addWdhcInfos( wdhc_infos[i] );
				} catch (Exception e) {}
			}
		} else {
			nCnt = Integer.valueOf( localStorage.getValue( "cnt", "0"));
			if( nCnt > 0 ) {
				for( int i = 0; i < nCnt; i++ ) {
					try {
						addWdhcInfos( localStorage.getValue( String.format("%d", i), ""));
					} catch (Exception e) {}					
				}						
			}
		}
	}
	
	@Override
	public void release() {
		super.release();
		
		if( m_act != null ) {
			m_act.unregisterReceiver( androidActionReceiver );
		}
		
		m_act = null;
	}

	public void setOnHdmiConnectionListener( onHdmiConnectionListener listener ) {
		connectionListener = listener;
	}
	
	boolean compareTo( ArrayList<String> stringSet, String data ) {
		boolean bFound = false;
		for( int i = 0; !bFound && i < logLength; i++ ) {
			
			int dir = logConnectionsDir.get( i );
			String src = stringSet.get(i);
			
			if( dir == 'c' ) {
				bFound = data.contains( src );
			} else if( dir == 'e' ) {
				bFound = data.endsWith(  src );
			} else if( dir == 's' ) {
				bFound = data.startsWith( src );
			} else {
				bFound = data.compareTo( src ) == 0;
			}
		}
		return bFound;
	}
	
	
	
	@Override
	public boolean start() {
		clearLogCat();
		return super.start();
	}

	@Override
	public void OnLogcatData(String data) {
		data = data.toLowerCase();

		if( !data.contains("hdmi") && !data.contains("tvout")) {
			return;
		}
		
		boolean connected = isHdmiConnected; 
		if( isHdmiConnected ) {
			if( compareTo( logDisconnections, data )) {
				isHdmiConnected = false;				
			}
			
		} else {
			if( compareTo( logConnections, data )) {
				isHdmiConnected = true;
			}
		}
		
		if( connected != isHdmiConnected ) {
			sendNotification();
		}
	}
	
	/**
	 * android.action event을 이용하여 HDMI 연결 상태를 확인하기 위한 broadcastReceiver
	 */
	BroadcastReceiver androidActionReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			//Toast.makeText( m_act, action, Toast.LENGTH_SHORT).show();
			
			boolean connection = isHdmiConnected;
			
			for( String strAction : ACTION_EXTDISP_DISPLAY ) {
				if( strAction.compareTo( action ) == 0 ) {
					try {
						
						Set<String> keys = intent.getExtras().keySet();
						if( keys.size() > 1 ) {
							connection = true;
						} else {
							connection = false;
						}			
					} catch( Exception e ) {}
					
					break;
				}
			}
						
			if( isHdmiConnected != connection ) {
				isHdmiConnected = connection;
				sendNotification();
			}
		}
	};
	
	private Handler notification_handler = new Handler();
	private Runnable notification_runnable = new Runnable() {
		public void run() {
			if( connectionListener != null ) {
				connectionListener.OnHdmiConnectionListener(isHdmiConnected);
			}
		}
	};
	private void sendNotification() {
		notification_handler.post( notification_runnable );
	}
}
