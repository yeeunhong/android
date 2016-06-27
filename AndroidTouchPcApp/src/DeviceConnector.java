import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class DeviceConnector implements Runnable {
	private Process	process		 = null;
	private Thread  readThread	 = null;
	private CommandCallback readCallback = null;
	
	private BufferedReader reader = null; 
	private BufferedWriter writer = null;
	
	private boolean enable		 = false;
		
	public void close() {
		enable = false;
		
		if( writer != null ) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writer = null;
		}
		
		if( reader != null ) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			reader = null;
		}
		
		if( process != null ) {
			process.destroy();
			process = null;
		}
	}
			
	public boolean connection() {
		try {
			close();
			
			ProcessBuilder builder = new ProcessBuilder( "adb", "shell" );
			builder.redirectErrorStream(true);
			process = builder.start();
			
			reader = new BufferedReader(new InputStreamReader ( process.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter( process.getOutputStream()));
			
			if( reader != null && writer != null ) {
				enable = true;
				
				readThread = new Thread( this );
				readThread.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return enable;
	}
	
	/**
	 * @param cmd
	 * @param callback
	 */
	public void command( String cmd, CommandCallback callback, long waitMillis  ) {
		if( !enable ) return;
		
		try {
			try {
				Thread.sleep( waitMillis );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			readCallback = callback;
			
			writer.write( cmd + "\r\n" );
			writer.flush();
						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
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
}
