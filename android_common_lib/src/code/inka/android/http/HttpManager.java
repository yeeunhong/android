package code.inka.android.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import code.inka.android.log.Log;

public class HttpManager {
	private static HttpClient m_HttpClient = null;

	
	/**
	 * <li>Web Site에 POST 방식으로 데이터를 전달하여 그 결과를 String 형식으로 돌려 준다.<br>
	 * <li>httpClient는 보통 null로 설정하고, 따로 session 및 기타 목적으로 사용시에는 getHttpClient함수를 통해 객체를 전달 받은 후 입력하여 사용하면 됩니다. <br>
	 * <br>
	 * <pre>
	 * <b>[parameter 사용 예]</b>
	 * ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
	 * nameValuePairs.add( new BasicNameValuePair( "user_id",  "'testid'" ));
	 * nameValuePairs.add( new BasicNameValuePair( "user_pass", "'12345'" ));
	 * nameValuePairs.add( new BasicNameValuePair( "device_id", "'1qazws-edc34-wdkek-keise'" ));
	 * nameValuePairs.add( new BasicNameValuePair( "device_model", "'sch-p193'" ));
	 * </pre>
	 
	 * @param url 접속하려고 하는 web site 주소
	 * @param parameter POST 형식으로 전달하려는 key/value 데이터( value값이 string인 경우 value값이 'value' 로 해주어야 ' 가 표시 됨)
	 * @param httpClient 접속을 시도 하려는 client 객체( null로 설정하면 default로 접속을 시도한다. )
	 * @return null:오류발생, otherwise:site 접속 결과 문자열
	 */
	public static String requestPostMethod( String url, ArrayList<NameValuePair> parameter, HttpClient httpClient ) {
		HttpPost method = new HttpPost(url);			
		HttpResponse response = null;
		BasicResponseHandler myHandler = new BasicResponseHandler();
		String endResult = null;

		if( httpClient == null ) {
			httpClient = getHttpClient();
		}
		
		// timeout 설정 (5초)
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		
		if( parameter != null ) {
			try {
				UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity( parameter, "UTF-8");
				method.setEntity(entityRequest);
			} catch (UnsupportedEncodingException e) {
				Log.message( e.getMessage());
			}  
		}
		
		try {
			response = httpClient.execute(method);
		} catch (ClientProtocolException e) {
			Log.message( e.getMessage());
			return null;
		} catch (IOException e) {
			Log.message( e.getMessage());
			return null;
		}
		
		try {
			endResult = myHandler.handleResponse(response);
		} catch (HttpResponseException e) {
			Log.message( e.getMessage());
			return null;
		} catch (IOException e) {
			Log.message( e.getMessage());
			return null;
		}

		return endResult;
	}
	
	/**
	 * <li>Web Site에 GET 방식으로 데이터를 전달하여 그 결과를 String 형식으로 돌려 준다.<br>
	 * <li>param은 url 에 표시 하여도 되고, param을 이용하여 입력하여도 됨, 두가지 모두 사용 가능, param을 사용하지 않을 시에는 null로 설정하면 됨
	 * <li>httpClient는 보통 null로 설정하고, 따로 session 및 기타 목적으로 사용시에는 getHttpClient함수를 통해 객체를 전달 받은 후 입력하여 사용하면 됩니다. <br>
	 * <br>
	 * <pre>
	 * <b>[parameter 사용 예]</b>
	 * ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
	 * nameValuePairs.add( new BasicNameValuePair( "user_id",  "'testid'" ));
	 * nameValuePairs.add( new BasicNameValuePair( "user_pass", "'12345'" ));
	 * nameValuePairs.add( new BasicNameValuePair( "device_id", "'1qazws-edc34-wdkek-keise'" ));
	 * nameValuePairs.add( new BasicNameValuePair( "device_model", "'sch-p193'" ));
	 * </pre>
	 * @param url 접속하려고 하는 web site 주소
	 * @param parameter GET 형식으로 전달하려는 key/value 데이터( value값이 string인 경우 value값이 'value' 로 해주어야 ' 가 표시 됨) 
	 * @param httpClient 접속을 시도 하려는 client 객체( null로 설정하면 default로 접속을 시도한다. )
	 * @return null:오류발생, otherwise:site 접속 결과 문자열
	 */
	public static String requestGetMethod( String url, ArrayList<NameValuePair> param, HttpClient httpClient ) {		
		HttpResponse response = null;
		BasicResponseHandler myHandler = new BasicResponseHandler();
		String endResult = null;

		if( httpClient == null ) {
			httpClient = getHttpClient();
		}
		
		// timeout 설정 (5초)
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		
		if( param != null ) {
			boolean hasParam = url.indexOf( "?" ) != -1;
			for( NameValuePair value : param ) {
				if( hasParam ) { 
					url += "&";
				} else {
					url += "?";
				}
				
				url += String.format( "%s=%s", value.getName(), value.getValue());
				hasParam = true;
			}
		}
		
		try {
			response = httpClient.execute(new HttpGet(url));
		} catch (ClientProtocolException e) {
			//Log.message( e.getMessage());
			return null;
		} catch (IOException e) {
			//Log.message( e.getMessage());
			return null;
		}
		
		try {
			endResult = myHandler.handleResponse(response);
		} catch (HttpResponseException e) {
			Log.message( e.getMessage());
			return null;
		} catch (IOException e) {
			Log.message( e.getMessage());
			return null;
		}

		return endResult;
	}
	
	/**
	 * SSL 연결시 서버 인증서를 체크하지 않도록 처리한 루틴<br>
	 * (javax.net.ssl.SSLPeerUnverifiedException: No peer certificate 오류 해결)
	 * @return http/https에서 사용 가능한 HttpClient 객체
	 */
	public static HttpClient getHttpClient() {
		if ( m_HttpClient == null) {
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
			 
			HttpParams params = new BasicHttpParams();
			params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
			params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
			params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			 
			ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
			m_HttpClient = new DefaultHttpClient(cm, params);			
		}
		
		m_HttpClient.getConnectionManager().closeExpiredConnections();
		
		return m_HttpClient;
	}
	
	
	
	/**
	 * HTTP URL 파일을 읽어서 String 으로 넘겨 줍니다. 
	 * @param strURL http url 주소
	 * @return http url file의 String
	 */
	public static String readString( String strURL ) {
		byte[] byteResult = readBytes( strURL );
		if( byteResult == null ) return null;
			
		String result = new String( byteResult );
		return result;
	}
	
	/**
	 * HTTP URL 파일을 읽어서 byte 배열로 넘겨 줍니다.
	 * @param strURL http url 주소
	 * @return http url file의 byte 배열
	 */
	public static byte[] readBytes( String strURL ) {
		URL url = null;
		HttpURLConnection urlConnection = null;
		
		try 
		{
			url = new URL( strURL );
			
			urlConnection = ( HttpURLConnection ) url.openConnection();
			urlConnection.setRequestMethod( "GET" );
			urlConnection.connect();
			urlConnection.setConnectTimeout(1000);
			urlConnection.setReadTimeout(1000);
			
			InputStream inputStream = urlConnection.getInputStream();
						
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[ inputStream.available() ];
						
			while( inputStream.read( buffer ) > 0 )	{
				outputStream.write( buffer );
			}
			
			buffer = null;
			
			inputStream.close();
			inputStream = null;
		
			urlConnection.disconnect();
			
			byte[] byteResult = outputStream.toByteArray();
			return byteResult;
			
		}
		catch (MalformedURLException e) { Log.message( e.getMessage()); }
		catch (IOException e) { Log.message( e.getMessage()); } 
		
		return null;
	}
	
	/**
	 * HTTP URL에 있는 파일의 크기를 얻어 옵니다.
	 * @param strURL http url 주소
	 * @return 파일의 크기
	 */
	public static long getFileSize( String strURL ) 
	{
		URL url = null;
		long ret = -1;
		
		try 
		{
			url = new URL( strURL );
			
			HttpURLConnection urlConnection = ( HttpURLConnection ) url.openConnection();
			urlConnection.setRequestMethod( "HEAD" );
			urlConnection.connect();
			
			// 해당 파일이 서버에 없는 경우..
            int resCode = urlConnection.getResponseCode();
            if( resCode == HttpURLConnection.HTTP_OK || resCode == HttpURLConnection.HTTP_PARTIAL ) {
            	
            	String contentLength = urlConnection.getHeaderField("Content-Range");
	            if( contentLength != null )
	            {
	            	String[]	strLength	= contentLength.split("/");
	            	if( strLength.length > 1 ) {
	            		ret	= Long.parseLong(strLength[1]);		            		
	            	}
	            } else {
	            	ret = urlConnection.getContentLength();
	            }
            }
            urlConnection.disconnect();
            url = null;
		}
		catch (MalformedURLException e) { Log.message( e.getMessage()); } 
		catch (IOException e) { Log.message( e.getMessage()); } 
			
		return ret;
	}
	
	public static String	safeUrlEncoder(String strInput)
	{
		return	safeUrlEncoder(strInput, "UTF-8");
	}
	public static String	safeUrlEncoder(String strInput, String strEnc)
	{
		StringBuilder	strResult	= new StringBuilder();
		StringBuilder	strResult2	= new StringBuilder();
		String	strPorcess;
		
		if( strInput.startsWith("http://") )
		{
			strPorcess	= strInput.substring(7);
		}
		else
		{
			strPorcess	= strInput;
		}
		
		// / 를 기준으로 나누기
		String	strURLUnit[]	= strPorcess.split("\\/");
		int		i = 0;
		int		nUnitCount		= strURLUnit.length;
		
		strResult.append("http://");
		strResult.append(strURLUnit[0]);
		
		// url 인코딩
		try {
			for( i=1 ; i < nUnitCount ; i++ )
			{
				strResult.append("/");
				strResult.append(URLEncoder.encode(strURLUnit[i], strEnc));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// + -> %20으로 바꾸기
		String	strURLUnit2[]	= strResult.toString().split("\\+");
		nUnitCount				= strURLUnit2.length;
		
		strResult2.append(strURLUnit2[0]);
		for( i=1 ; i < nUnitCount ; i++ )
		{
			strResult2.append("%20");
			strResult2.append(strURLUnit2[i]);
		}
		
		// 결과
		strPorcess = strResult2.toString();
		
		return	strPorcess;
	}
	
}
