package code.inka.android.device;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.media.MediaPlayer;

class DeviceManagerInner {
	protected int 		m_android_os_major = 0;
	protected int 		m_android_os_minor = 0;
	
	protected int 		m_memory_total 	= 0;
	protected int		m_memory_free	= 0;
	
	protected float 	m_cpu_use 		= 0.0F;
	protected float 	m_cpu_nice		= 0.0F;
	protected float 	m_cpu_system 	= 0.0F;
	protected float 	m_cpu_idle		= 0.0F;
	
	public DeviceManagerInner(){
		String version = android.os.Build.VERSION.RELEASE;
		String [] token = version.split("\\.");
		if( token.length >= 2 ) {
			m_android_os_major = Integer.valueOf( token[0] );
			m_android_os_minor = Integer.valueOf( token[1] );
		}
	}
	
	protected final int MP_UNKNOWN = 0;
	protected final int MP_OPENCORE = 1;
	protected final int MP_STAGEFRIGHT = 2;
	protected int _socketPort, _backend = MP_UNKNOWN;
	
	protected int getMediaPlayerBackend() {

		final CountDownLatch latch1 = new CountDownLatch(1); 
		final CountDownLatch latch2 = new CountDownLatch(1);
				
		Executors.newSingleThreadExecutor().submit(new Runnable() {
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(0);
					_socketPort = serverSocket.getLocalPort();
					latch1.countDown();

					Socket socket = serverSocket.accept();
					InputStream is = socket.getInputStream();

					byte [] temp = new byte [2048];
					int bsize = -1;
					while(bsize <= 0) {
						bsize = is.read(temp);
					}
					String res = new String(temp, 0, bsize);

					if(res.indexOf("stagefright") >= 0) {
						_backend = MP_STAGEFRIGHT;
					} else if(res.indexOf("OpenCORE") >= 0) {
						_backend = MP_OPENCORE;
					} 

					socket.close();
					serverSocket.close();
				} catch(IOException e) {
				} finally {
					latch2.countDown();
				}
			}

		});

		try {
			latch1.await(500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}

		MediaPlayer mp = new MediaPlayer();
		try {
			String url = String.format("http://127.0.0.1:%d/", _socketPort);
			mp.setDataSource(url);
			mp.prepareAsync();
		} catch (Exception e) {
			mp.release();
			return MP_UNKNOWN;
		}

		try {
			latch2.await(500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
		
		mp.release();
		
		return _backend;
	}
}
