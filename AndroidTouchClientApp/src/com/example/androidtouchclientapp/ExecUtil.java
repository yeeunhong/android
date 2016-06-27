package com.example.androidtouchclientapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 외부 프로그램을 실행하고 그 출력값을 전달 받는 기능을 담당한다.  
 * 
 * @author MY
 *
 */
public class ExecUtil {
		
	/**
	 * 외부 프로그램을 실행만 시킨다. 
	 * 
	 * @param prog
	 */
	public static void RuntimeExec( String prog ) {
		try {
			Runtime.getRuntime().exec( prog );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 외부 프로그램을 실행하고 그 출력값을 String 형으로 반환한다. 
	 * 
	 * @param prog 외부 프로그램 실행 명령어
	 * @return 외부 프로그램의 실행 결과 문자열, 명령어 실행 실패시 size 0인 ArrayList<String> 객체가 반환됨
	 */
	public static ArrayList<String> getRuntimeExecResult( String prog ) {
		InputStream input = null;
		InputStream error = null;
		Process process = null;
		
		ArrayList<String> ret = new ArrayList<String>();
		try {
			process = Runtime.getRuntime().exec( prog );
			
			input = process.getInputStream();
			error = process.getErrorStream();
		
			Scanner input_scaner = new Scanner(input).useDelimiter("\\n");
			while( input_scaner.hasNext() ) {
				ret.add( input_scaner.next());
			}
			
			Scanner error_scaner = new Scanner(error).useDelimiter("\\n");
			while( error_scaner.hasNext() ) {
				ret.add( error_scaner.next());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			
		} finally {
			if( input != null ) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if( error != null ) {
				try {
					error.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
}
