package code.inka.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Handler;
import code.inka.android.file.FileManager;
import code.inka.android.log.Log;

public class HttpDownload extends Thread implements Runnable {

	public static final int DOWNLOAD_STATUS_TRY				= 0;
	public static final int DOWNLOAD_STATUS_SUCCESS			= 1;
	public static final int DOWNLOAD_STATUS_FAILED			= -1;
	
	public static final int DOWNLOAD_STATUS_CONNECTION 		= 1000;
	public static final int DOWNLOAD_STATUS_CONTENT_LENGTH 	= 1001;
	public static final int DOWNLOAD_STATUS_READ_WRITE 		= 1002;
	public static final int DOWNLOAD_STATUS_READING 		= 1003;
	public static final int DOWNLOAD_STATUS_READING_PERCENT = 1004;
	public static final int DOWNLOAD_STATUS_EXIST_FILE		= 1005;
	
	private String 	m_strUrl;
	private String 	m_strDestPath;
	private Handler m_eventHandler;
	private int		m_hashCode;
	
	public HttpDownload( String strURL, String path, Handler handler ) {
		super();
		
		m_strUrl = strURL;
		m_strDestPath = path;
		m_eventHandler = handler;
		m_hashCode = handler.hashCode();
	}
	
	private void sendEventMessage( int type, int value ) {
		m_eventHandler.sendMessage( m_eventHandler.obtainMessage( m_hashCode, type, value ) );
	}
	
	public void run() {
		
		// 저장할 파일이 이미 존재한다면 다운로드를 완료 시킨다. 
		File file = new File ( m_strDestPath );
		if( file.exists()) {
			sendEventMessage( DOWNLOAD_STATUS_EXIST_FILE, DOWNLOAD_STATUS_SUCCESS );
			return ;
		}
		
		HttpGet method = new HttpGet( m_strUrl );
		HttpClient httpClient = HttpManager.getHttpClient();
				
		// timeout 설정 (5초)
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		
		long downloadedSize = 0;
		
		// 이전에 파일이 존재 하는지 확인
        file = new File ( m_strDestPath + ".download" );
        if( !file.exists()) { 
        	downloadedSize = 0;
        } else {
        	downloadedSize = (int)file.length();
        }
				
		if( downloadedSize > 0 ) {
			method.setHeader( "Range", "bytes=" + downloadedSize + "-");
        }
		
		// 접속 연결을 알림
		sendEventMessage( DOWNLOAD_STATUS_CONNECTION, DOWNLOAD_STATUS_TRY );
				
		HttpResponse response = null;
		try {
			response = httpClient.execute(method);
			if( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK ) {
				// 접속 연결 실패를 알림
				sendEventMessage( DOWNLOAD_STATUS_CONNECTION, DOWNLOAD_STATUS_FAILED );
				return;
			}
		} catch (ClientProtocolException e) {
			Log.message( e.getMessage());
			sendEventMessage( DOWNLOAD_STATUS_CONNECTION, DOWNLOAD_STATUS_FAILED );
			return;
		} catch (IOException e) {
			Log.message( e.getMessage());
			sendEventMessage( DOWNLOAD_STATUS_CONNECTION, DOWNLOAD_STATUS_FAILED );
			return;
		}
		sendEventMessage( DOWNLOAD_STATUS_CONNECTION, DOWNLOAD_STATUS_SUCCESS );
		
		// 컨텐츠 크기 구함 시도
		long contentLength = 0;
		
		sendEventMessage( DOWNLOAD_STATUS_CONTENT_LENGTH, DOWNLOAD_STATUS_TRY );
		Header[] hd_contentLength = response.getHeaders( "Content-Range" );
        if( hd_contentLength != null ) {
        	if( hd_contentLength.length > 0 ) {
        		String[] strLength	= hd_contentLength[0].getValue().split("/");
            	if( strLength.length > 1 ) {
            		contentLength	= Long.parseLong(strLength[1]);		            		
            	}
        	}
        }
        
        if( contentLength == 0 ) {
        	// 컨텐츠 크기 획득 싪패
        	sendEventMessage( DOWNLOAD_STATUS_CONTENT_LENGTH, DOWNLOAD_STATUS_FAILED );
        	return;
        }
        sendEventMessage( DOWNLOAD_STATUS_CONTENT_LENGTH, DOWNLOAD_STATUS_SUCCESS );
		
        
        InputStream 	 inputStream 	= null;
		FileOutputStream outputStream	= null;
		byte[] download_buff			= null;
		
        // 데이터 read and imsi file Write
		sendEventMessage( DOWNLOAD_STATUS_READ_WRITE, DOWNLOAD_STATUS_TRY );
		try {
			inputStream 	= response.getEntity().getContent();
			outputStream	= new FileOutputStream( m_strDestPath + ".download" );
		
			download_buff = new byte[ inputStream.available() ];
		
			long nSendBytes = downloadedSize;
			int nReadByte = 0;
			
			// 다운로드 시작
			sendEventMessage( DOWNLOAD_STATUS_READING, 0 );
			while(( nReadByte = inputStream.read( download_buff )) > 0 ) {
				outputStream.write( download_buff, 0, nReadByte );
				nSendBytes += nReadByte;
				
				sendEventMessage( DOWNLOAD_STATUS_READING_PERCENT, (int)(( nSendBytes * 100 ) / contentLength ));
			}
		
			download_buff = null;
			
			inputStream.close();
			inputStream = null;
			
			outputStream.flush();
			outputStream.close();
			outputStream = null;
			
			boolean bResult = FileManager.moveFile( m_strDestPath + ".download", m_strDestPath );
			sendEventMessage( DOWNLOAD_STATUS_READING, bResult ? 1 : -1 );			
			
		} catch (IllegalStateException e) {
			Log.message( e.getMessage());
			sendEventMessage( DOWNLOAD_STATUS_READ_WRITE, DOWNLOAD_STATUS_FAILED );
			return;
			
		} catch (IOException e) {
			Log.message( e.getMessage());
			sendEventMessage( DOWNLOAD_STATUS_READ_WRITE, DOWNLOAD_STATUS_FAILED );
			return;
		}
		
		sendEventMessage( DOWNLOAD_STATUS_READ_WRITE, DOWNLOAD_STATUS_SUCCESS );
	}
}
