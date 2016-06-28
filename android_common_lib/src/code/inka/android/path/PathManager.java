package code.inka.android.path;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


import android.content.Context;
import android.os.Build;

/**
 * �ܸ����� ��ο� ���� CLASS
 * @author ����(��)ȣ��(ȣ)
 *
 */
public class PathManager extends PathManagerInner {
	private Context 		m_context = null;
	
	public PathManager( Context context ) {
		m_context = context;
	}
	
	/**
	 * ���ó��� /files ��θ� ��´�.
	 * @return ���ó��� /files ���� ���
	 */
	public String getFilesDir() {
		return m_context.getFilesDir().getAbsolutePath();
	}
	
	/**
	 * �ܸ��� ��ġ�� ���� SD Memory Path ���� ��´�.<br>
	 * Android API �������� ���� SD Memory ��θ� ���Ѵ�.  
	 * @return ���� SD Memory �� ���� ���
	 */
	public static String getExternalSDPath() {
		return android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * �ۿ��� ��밡���� Path ������ ��´�. <br>
	 * �ۿ� Binding�Ǿ� �־ ���� �����ϸ� �������� ��� ���� �� ������ �Բ� ���� �ȴ�. <br>
	 * ��밡���� SDCard�� �˻��Ǹ� SDCard���� Path�� ���ȴ�. 
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
	 * �ܸ��� ��ġ�� ���� SD Memory Path ���� ��´�.<br>
	 * �ܸ����� ���� ���� ���ϵ��� �˻��Ͽ� SD Memory ����� ����
	 * @return ���� SD Memory �� ���� ���
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
        
        // vold.fstab �� ǥ��� mmc device ������ system/block�� �ִ� mmc device ���� �۴ٴ� �ǹ̴�
        // �ܺ� SD �޸𸮰� ���ٰ� �� �� ����
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

        	//vold.fstab ���Ͽ��� dev_mount�� �����ϴ� ���θ� ������.
        	//str = str.replaceAll( "\\p{Space}", "");
        	if(str.startsWith("dev_mount")) {
        		for( int j = 0 ; j < 5 ; j++) {
        			array2 = str.split(" ");
        		}

        		// partition number �� auto�� ���ϰ�, sysfs_path ���� mmc ��� Ű���尡 ���� ����Ʈ ������ ������
        		if((array2[3].equals("auto")) && (array2[4].indexOf("mmc") != -1)) {
        			
        			//���� ��θ� ������ ":" �� �κ��� �߶󳻱�
        			int index = array2[2].indexOf(":");
        			if(index == -1) {
        				return array2[2];
        				
        			} else { 
        				return array2[2].substring(0, index);
        			}

        		}
        		
        	} else { // #�� �����ϴ� �ּ� �κ��� ������.
        		
        	}
        }            

        return strPath;
	}
}
