package code.inka.android.path;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import android.os.Build;
import android.os.Environment;

class PathManagerInner {
	protected static int getMountedMMCDeviceNum() {
        File   fList[]             = new File("/sys/block/").listFiles(); 
        int          nFileCount   = fList.length; 
        int          i = 0; 
        int          index = 0;

        for( i=0 ; i < nFileCount ; i++ ) { 
        	if( fList[i].getAbsolutePath().startsWith("/sys/block/mmcblk")) { 
        		index++;
        	}
        } 

        return index;
	}

	protected static int getVoldMMCDeviceNum() {
        File fstab;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
        	fstab = new File("/system/etc/vold.conf");

        } else {
        	fstab = new File("/system/etc/vold.fstab");

        }

        String str;
        String array2[] = { "", "", "", "", "" };
        int index = 0;

        Scanner scanner = null;
        try {
        	scanner = new Scanner(fstab);

        } catch (FileNotFoundException e) {
        	e.printStackTrace();

        }

        if( scanner == null ) return -1;
        
        while (scanner.hasNext()) {
        	str = scanner.nextLine();

            //vold.fstab 파일에서？ dev_mount로？ 시작하는？ 라인만？ 추출함？.

        	if(str.startsWith("dev_mount")) {
        		for( int j = 0 ; j < 5 ; j++) {
        			array2 = str.split(" ");
        		}

                // partition number 를？ auto로？ 정하고？, sysfs_path 내에？ mmc 라는？ 키워드가？ 들어가는？ 마운트？ 정보를？ 가져옴？
        		if(array2[4].indexOf("mmc") != -1) {
        			index++;
        		}
        	}
        }
        
        return index;
	}
	
	/**
	 * Helper Method to Test if external Storage is Available
	 */
	protected final static boolean isExternalStorageAvailable() {
	    boolean state = false;
	    String extStorageState = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
	        state = true;
	    }
	    return state;
	}
	
	/**
	 * Helper Method to Test if external Storage is read only
	 */
	protected final static boolean isExternalStorageReadOnly() {
	    boolean state = false;
	    String extStorageState = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
	        state = true;
	    }
	    return state;
	}
}
