import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;


public class TouchEventHistory {
	Process process = null;
	Thread thread 	= null;
	
	private Vector<TouchEventData> historyDatas = new Vector<TouchEventData>(); 
	
	BufferedReader reader = null;
	BufferedWriter writer = null;
		
	public void start( String deviceName ) {
		stop();
		
		ProcessBuilder builder = new ProcessBuilder( "adb", "shell", "getevent", "|", "grep", deviceName );
		builder.redirectErrorStream(true);
		try {
			
			historyDatas.clear();
			process = builder.start();
			
			reader = new BufferedReader(new InputStreamReader ( process.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter( process.getOutputStream()));
			
			if( reader != null && writer != null ) {
				thread = new Thread( reader_handler );
				thread.start();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if( process != null ) {
			process.destroy();
			process = null;
		}
		
		if( reader != null ) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			reader = null;
		}
		
		if( writer != null ) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			writer = null;
		}
		
		
		
		historyDatas.add( new TouchEventData( "" ));
	}
	
	CommandCallback readCallback = new CommandCallback() {
		@Override
		public void callback(String data) {
			data = data.trim();
			if( data.length() < 1 ) return;
			
			try {
				String [] token = data.split(" ");
				
				String devNm = token[0];
				devNm = devNm.replace(":", "").trim();
				int type  = Integer.valueOf( token[1], 16 );
				int code  = Integer.valueOf( token[2], 16 );
				int value = token[3].compareTo("ffffffff") == 0 ? -1 : Integer.valueOf( token[3], 16 );
				
				historyDatas.add( new TouchEventData( String.format( "%s %d %d %d", devNm, type, code, value ) ));
				System.out.println( "Saved history size is " + historyDatas.size());
			} catch( Exception e ) {
				//e.printStackTrace();
				System.out.println( "ErrorData : " + data );
			}
		}
		
	};
	
	Runnable reader_handler = new Runnable() {
		@Override
		public void run() {
			String line;
			try {
				while ((line = reader.readLine ()) != null) {
					if( readCallback != null ) {
						readCallback.callback( line );
					}				
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	DeviceConnector deviceConnector = null;
	boolean playFlag = false;
	long waitMillis = 0;
	
	Runnable playThread = new Runnable() {

		@Override
		public void run() {
			while( playFlag ) {
				for( int i = 0; i < historyDatas.size() - 1 && playFlag ; i++ ) {
					TouchEventData a1 = historyDatas.get(i);
					TouchEventData a2 = historyDatas.get(i+1);
					
					deviceConnector.command( "sendevent " + a1.strEvent, notiongCallback, waitMillis );
					
					waitMillis = a2.time - a1.time;
				}
			}
		}
		
	};
	
	CommandCallback notiongCallback = new CommandCallback() {
		@Override
		public void callback(String data) {
			System.out.println( data );
		}
		
	};
	
	public void play( DeviceConnector deviceConnector ) {
		playFlag = true;
		this.deviceConnector = deviceConnector;
		new Thread( playThread ).start();
	}
	
	public void playStop() {
		playFlag = false;
	}
}
