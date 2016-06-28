package code.inka.android.logcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class logcatProcess implements Runnable {
	private Thread thread = null;
	private boolean alive = false;
	
	private ArrayList<String> filter = new ArrayList<String>(); 
	
	public boolean init() {
		if( thread != null ) return false;
		
		thread = new Thread( this );
		
		filter.clear();
		filter.add("logcat");
		
		return true;
	}
	
	/**
	 * LogCat filter ��� <br>
	 * <p>
	 * ex) SoundBooster:E(<b>E</b>rror/<b>D</b>ebug/<b>I</b>nfomation/<b>V</b>erbos/<b>*</b>:all/<b>S</b>ilence)<br>
	 *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*:S ( ��ϵ� Tag �̿� ��� ���� )
	 * </p>
	 * @param tag
	 */ 
	public void addFilter( String tag ) {
		filter.add( tag );
	}
	
	public boolean start() {
		if( alive ) return true;
		
		alive = true;
		
		if( thread != null ) {
			thread.start();
			return true;
		}
		
		return false;
	}
	
	public void clearLogCat() {
		try {
			Process logcatProc = Runtime.getRuntime().exec( new String[]{"logcat","-c"} );
			logcatProc.destroy();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * LogCat �����͸� ���� ���� �Լ�
	 * @param data
	 */
	public abstract void OnLogcatData( String data );
	
	/**
	 * LogCat Thread �� �ȿ��� �ʿ��� �ʱ�ȭ �۾��� �����Ѵ�. <br>
	 * �ַ� Network �۾��̳�, ��Ÿ �ð��� �����Ǵ� �۾��� ���⼭ �����Ѵ�. 
	 * @return �����ϸ� true, �����ϸ� false, false�� �����ϸ� Thread�� �ٷ� ���� �ȴ�. 
	 */
	public abstract boolean OnInitInThread();
	
	@Override
	public void run() {
			
		if( !OnInitInThread()) {
			alive = false;
			thread = null;
			
			return;
		}
		
		Process logcatProc = null;
				
		try {
			String[] param = new String[filter.size()];
			filter.toArray(param);
			
			logcatProc = Runtime.getRuntime().exec( param );
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		InputStream is = logcatProc.getInputStream();
		BufferedReader br = new BufferedReader( new InputStreamReader( is ));
		
		String line = null;
		while( alive ) {
			try { Thread.sleep(10); } catch (InterruptedException e) {}
			
			try {
				line = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
				break;
			}
			
			if( line == null ) {
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
				continue;
			}
			
			OnLogcatData( line );
		}
		
		try { 
			br.close();
			br = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if( logcatProc != null )
		{
			logcatProc.destroy();
			logcatProc = null;
		}
		
		alive = false;
		thread = null;
	}

	public void stop() {
		alive = false;
		
		if( thread != null ) {
			thread.stop();
		}
	}
	
	public void release() {
		stop();
		thread = null;
	}
}
