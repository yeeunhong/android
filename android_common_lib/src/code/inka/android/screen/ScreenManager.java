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
 * ȭ�� ȸ������ �˾Ƴ��� (����/���� ����)
 * @author ����(��)ȣ��(ȣ)
 *
 */
public class ScreenManager {
	private Activity 		m_act = null;
	
	public ScreenManager( Activity act ) {
		m_act = act;
	}

	/**
	 * ���� ȭ���� ���� ���������� Ȯ�� ��
	 * @return true:���λ���, false:���� ����
	 */
	public boolean isScreenLandscape() {
		if ( m_act.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) {
		    return true;
		}

		return false;
	}
	
	/**
	 * ���� ȭ���� ���� ��ȯ�� ��� ���θ� �����Ѵ�.
	 * @param enable true:������ȯ ����, false:���� �������� ����
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
	 * ��ü ȭ���̹����� ĸ���Ͽ� path�� png ���Ϸ� �����Ѵ�. 
	 * @param path ĸ���� ȭ���� ������ ���� ���
	 * @throws Exception
	 */
	public void screenShot( String path ) throws Exception {
		screenShot( null, path );
	}
	
	/**
	 * View�� ��ü ȭ���̹����� ĸ���Ͽ� path�� png ���Ϸ� �����Ѵ�. 
	 * @param view ȭ�� ĸ�ĸ� �������� View, null �̸� ��ü ȭ���� ĸ�� �Ѵ�. 
	 * @param path ĸ���� ȭ���� ������ ���� ���
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
