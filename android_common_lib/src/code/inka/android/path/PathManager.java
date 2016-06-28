package code.inka.android.path;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


import android.content.Context;
import android.os.Build;

/**
 * 단말기의 경로에 대한 CLASS
 * @author 맑은(준)호걸(호)
 *
 */
public class PathManager extends PathManagerInner {
	private Context 		m_context = null;
	
	public PathManager( Context context ) {
		m_context = context;
	}
	
	/**
	 * 어플내의 /files 경로를 얻는다.
	 * @return 어플내의 /files 절대 경로
	 */
	public String getFilesDir() {
		return m_context.getFilesDir().getAbsolutePath();
	}
	
	/**
	 * 단말에 설치된 외장 SD Memory Path 값을 얻는다.<br>
	 * Android API 기준으로 외장 SD Memory 경로를 구한다.  
	 * @return 외장 SD Memory 의 절대 경로
	 */
	public static String getExternalSDPath() {
		return android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * 앱에서 사용가능한 Path 정보를 얻는다. <br>
	 * 앱에 Binding되어 있어서 앱을 삭제하면 폴더내의 모든 파일 및 폴더도 함께 삭제 된다. <br>
	 * 사용가능한 SDCard가 검색되면 SDCard내의 Path가 사용된다. 
	 * @return app binding folder path
	 */
	public String getAppBindingPath() {
		File file = null;
		if( isExternalStorageAvailable()) {
			if( !isExternalStorageReadOnly()) {
				file = m_context.getExternalFilesDir(null);
				file.mkdirs();
			}
		}
		
		if( file == null ) {
			file = m_context.getFilesDir();
		}
		
		return file.getAbsolutePath();
	}
	
	/**
	 * 단말에 설치된 외장 SD Memory Path 값을 얻는다.<br>
	 * 단말기의 내부 설정 파일들을 검색하여 SD Memory 경로을 구함
	 * @return 외장 SD Memory 의 절대 경로
	 */
	public static String getRealExternalSDPath() {
		String str;
        String array2[] = { "", "", "", "", "" };

        String strPath = null;
        //ArrayList<String> mount_info = new ArrayList<String>();

        int realMMCDevice = getMountedMMCDeviceNum();
        int voldMMCDevice = getVoldMMCDeviceNum();

        if( voldMMCDevice < 0 ) {
        	return getExternalSDPath(); 
        }
        
        // vold.fstab 에 표기된 mmc device 수보다 system/block에 있는 mmc device 수가 작다는 의미는
        // 외부 SD 메모리가 없다고 볼 수 있음
        if (realMMCDevice < voldMMCDevice) {
               return strPath;
        }        

        File fstab;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
        	fstab = new File("/system/etc/vold.conf");

        } else {
        	fstab = new File("/system/etc/vold.fstab");
        }

        Scanner scanner = null;
		try {
			scanner = new Scanner(fstab);
		} catch (FileNotFoundException e) {
			return getExternalSDPath(); 
		}

        while (scanner.hasNext()) {
        	str = scanner.nextLine();

        	//vold.fstab 파일에서 dev_mount로 시작하는 라인만 추출함.
        	//str = str.replaceAll( "\\p{Space}", "");
        	if(str.startsWith("dev_mount")) {
        		for( int j = 0 ; j < 5 ; j++) {
        			array2 = str.split(" ");
        		}

        		// partition number 를 auto로 정하고, sysfs_path 내에 mmc 라는 키워드가 들어가는 마운트 정보를 가져옴
        		if((array2[3].equals("auto")) && (array2[4].indexOf("mmc") != -1)) {
        			
        			//순수 경로만 전달함 ":" 뒷 부분은 잘라내기
        			int index = array2[2].indexOf(":");
        			if(index == -1) {
        				return array2[2];
        				
        			} else { 
        				return array2[2].substring(0, index);
        			}

        		}
        		
        	} else { // #로 시작하는 주석 부분은 무시함.
        		
        	}
        }            

        return strPath;
	}
}
