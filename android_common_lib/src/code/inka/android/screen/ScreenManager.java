package code.inka.android.screen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.View;


/**
 * 화면 회전방향 알아내기 (가로/세로 여부)
 * @author 맑은(준)호걸(호)
 *
 */
public class ScreenManager {
	private Activity 		m_act = null;
	
	public ScreenManager( Activity act ) {
		m_act = act;
	}

	/**
	 * 현재 화면이 가로 상태인지를 확인 함
	 * @return true:가로상태, false:세로 상태
	 */
	public boolean isScreenLandscape() {
		if ( m_act.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) {
		    return true;
		}

		return false;
	}
	
	/**
	 * 현재 화면의 반향 전환의 허용 여부를 설정한다.
	 * @param enable true:방향전환 가능, false:현재 방향으로 고정
	 */
	public void setEnableRotation( boolean enable ) {
		if( enable ) {
			m_act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			
		} else {
			if( isScreenLandscape()) {
				m_act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				
			} else {
				m_act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
	}
	
	/**
	 * 전체 화면이미지를 캡쳐하여 path에 png 파일로 저장한다. 
	 * @param path 캡쳐한 화면을 저장할 파일 경로
	 * @throws Exception
	 */
	public void screenShot( String path ) throws Exception {
		screenShot( null, path );
	}
	
	/**
	 * View나 전체 화면이미지를 캡쳐하여 path에 png 파일로 저장한다. 
	 * @param view 화면 캡쳐를 받으려는 View, null 이면 전체 화면을 캡쳐 한다. 
	 * @param path 캡쳐한 화면을 저장할 파일 경로
	 * @throws Exception
	 */
	public void screenShot( View view, String path ) throws Exception {      
		if( view == null ) {
			view = m_act.getWindow().getDecorView();
		} else {
			view = view.getRootView();
		}
		
		view.setDrawingCacheEnabled(true);
		Bitmap screenshot = view.getDrawingCache();
		
		String filename = path;
		 
		try {
		 
			File f = new File(filename);
			f.createNewFile();
			
			OutputStream outStream = new FileOutputStream(f);
		 	screenshot.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		 	outStream.flush();
		 	outStream.close();
		 
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		view.setDrawingCacheEnabled(false);
	}

}
