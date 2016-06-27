package lib.java.common.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ExecUtil {
	/**
	 * 외부 프로그램을 실행하고 그 출력값을 전달 받을 InputStream 을 반환한다. 
	 *  
	 * @param prog 외부 프로그램 실행 명령어
	 * @return 외부 프로그램의 실행 결과를 받을 InputStream 객체, 명령어 실행 실패시 null 이 반환됨
	 */
	public static InputStream getRuntimeExec( String prog ) {
		InputStream ret = null;
		try {
			ret = Runtime.getRuntime().exec( prog ).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 외부 프로그램을 실행하고 그 출력값을 String 형으로 반환한다. 
	 * 
	 * @param prog 외부 프로그램 실행 명령어
	 * @return 외부 프로그램의 실행 결과 문자열, 명령어 실행 실패시 null 이 반환됨
	 */
	@SuppressWarnings("resource")
	public static String getRuntimeExecResult( String prog ) {
		InputStream is = getRuntimeExec( prog );
		if( is == null ) return null;
		
		String ret = null;
		Scanner scanner = new Scanner(is).useDelimiter("\\A");
		
		while( scanner.hasNext() ) {
			if( ret == null ) {
				ret = scanner.next();
			} else {
				ret = ret + scanner.next();
			} 
		}
		
		if( is != null ) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
}
