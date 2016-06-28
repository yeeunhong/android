package app.inka.android.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import code.inka.android.device.DeviceManager;
import code.inka.android.log.Log;
import code.inka.android.ui.popup.popupActivity;
import code.inka.android.ui.popup.popupHostActivity;
import code.inka.android.ui.util.UTIL;
 
public class checkFilesystemInfo extends Thread implements Runnable {
	private popupHostActivity context = null;
		
	private ArrayList<String> dir_list = new ArrayList<String>(); 
	private FileOutputStream fos = null;
	
	final String [] skip_slab_list = {
		"shmem_inode_cache", "proc_inode_cache", "radix_tree_node", "inode_cache", "ext3_inode_cache",
		"fat_inode_cache", "ecryptfs_inode_cache", "fat_cache", "file_lock_cache", "sock_inode_cache",
		"anon_vma", "buffer_head", "bdev_cache", "sighand_cache", "UDPv6", 
		":at-0000136", ":at-0000064", ":at-0000024",
		":t-0004096", ":t-0002048", ":t-0001536", ":t-0001120", ":t-0001024", ":t-0000800", ":t-0000544", ":t-0000512", ":t-0000480", ":t-0000288", 
		":t-0000256", ":t-0000224", ":t-0000200", ":t-0000192", ":t-0000160", ":t-0000128", ":t-0000096", ":t-0000088", ":t-0000064",
		":t-0000048", ":t-0000040", ":t-0000032", ":t-0000024", ":t-0000016", ":t-0000008"		
	};
	final String [] skip_item_list = {		
		"/rx_packets",
		"/tx_packets",
		"/rx_bytes",
		"/tx_bytes",
		"/usage",
		"/uevent_seqnum",
		"/temp1_input",
		"/temp2_input",		
		"/runtime_active_time",
		"/runtime_suspended_time",
		"/connected_duration",
		"/extent_cache_hits",
		"/extent_cache_misses",
		"/lifetime_write_kbytes",
		"/temperature",
		"/at_pmrst"
	};
	
	final String [] skip_dir_list = {
			//"/mnt", 
			//"/data", 
			"/bin", 
			"/sbin" , 
			//"/lib", 
			"/ncg", 
			"/ncgsd", 
			"/sdrm", 
			"/app", 
			"/sys/power", 
			"/install", 
			"/preinstall", 
			"/clock",
			"sdcard"
			
			
			//"/share", 
			//"/proc"			
	};
	
	final String [] skip_dir_list2 = {
			"/proc/0", "/proc/1", "/proc/2", "/proc/3", "/proc/4", "/proc/5", "/proc/6", "/proc/7", "/proc/8", "/proc/9",
			"/proc/uid_stat"
	};
	
	public checkFilesystemInfo( popupHostActivity context ) { 
		this.context = context; 
	}
	
	public void release() {
		if( fos != null ) {
			try {
				fos.close();
			} catch (IOException e) {}
			fos = null;
		}
		
		dir_list.clear();
	}
	
	private void write( String msg ) { write( msg.getBytes()); }
	private void write( byte[] buffer ) {
		if( fos == null ) return;
		try {
			fos.write( buffer );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private String removeDbSpace( String src ) {
		if( src == null ) return "";
		byte[] src_bytes = src.getBytes();
		
		int nLen = src_bytes.length - 1;
		int nIdx = 0;
		
		for( int i = 0; i < nLen; i++ ) {
			if( src_bytes[i] == ' ' && src_bytes[i+1] == ' ') continue;
			src_bytes[nIdx++] = src_bytes[i];
		}
		
		src_bytes[nIdx] = 0;
		
		return new String( src_bytes, 0, nIdx );
	}
	
	private void write_info_file( FileOutputStream fos, String path ) {
		FileInputStream fis = null;
		try {
			fos.write(( path + " :" ).getBytes() );
			
			fis = new FileInputStream( path );
			byte [] buff = new byte[ fis.available() ];
			
			int nRead = 0;
			while(( nRead = fis.read( buff )) > 0 ) {
				fos.write( buff, 0, nRead );
			}
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		if( fis != null ) {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
				
		String pre_filename = "/mnt/sdcard/" + UTIL.GetCurrentTimeForFilename() + ".log";
		inner_run( pre_filename );
		/*
		String cur_filename = "/mnt/sdcard/" + UTIL.GetCurrentTimeForFilename() + ".log";
		inner_run( cur_filename );
		
		// 파일 비교 결과를 냅니다.
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		
		try {
			br1 = new BufferedReader( new FileReader( pre_filename ));
			br2 = new BufferedReader( new FileReader( cur_filename ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if( br1 != null && br2 != null ) {
			Log.message( "compare file start" );
			compareFileReader( br1, br2 );
			Log.message( "compare file end" );
		} else {
			Log.message( "compare file error" );
		}
		*/
		////////////////////////////////////////////////////////////////
		//FileManager.deleteFile( pre_filename );
		//FileManager.deleteFile( cur_filename );
	}
	
	private void inner_run( String output_filename ) {
		try {
			fos = new FileOutputStream( output_filename );
		} catch (FileNotFoundException e) {
			fos = null;
		}
		
		dir_list.add( "/" );
		
		if( fos != null ) {
		
			Log.message( "read file system start" );
			// 시작 시간을 기록
			write( String.format( "start time : %s\r\n", UTIL.GetCurrentTime() ));
			
			int nDirCnt = dir_list.size();
			while( nDirCnt > 0 ) {
				for( int i = 0; i < nDirCnt; i++ ) {
					process( dir_list.get(0) );
					dir_list.remove(0);
				}
				
				nDirCnt = dir_list.size();
			}
						
			// 종료 시간을 기록
			write( String.format( "end time : %s\r\n", UTIL.GetCurrentTime() ));
			
			Log.message( "read file system end" );
		}
		
		release();
		
		context.doModal( 0x9001, "Dialog Test Title", "작업을 완료 하였습니다.", popupActivity.DIALOG_BTN_TYPE_OK );
	}
		
	private void process( String path ) {
		
		for( String skip_dir : skip_dir_list ) {
			if( path.endsWith( skip_dir )) {
				//String logMsg = String.format( "%s => pass\r\n",  path );
				//Log.message( logMsg );
				//write( logMsg );
				
				return;
			}
		}
		
		for( String skip_dir : skip_dir_list2 ) {
			if( path.startsWith( skip_dir )) {
				//String logMsg = String.format( "%s => pass\r\n",  path );
				//Log.message( logMsg );
				//write( logMsg );
				
				return;
			}
		}
		
		String result_ls = removeDbSpace( DeviceManager.shell( "ls -l", path ));			
		String [] result_inofs = result_ls.split( "\n" );
		
		if( !path.endsWith("/")) path += "/";
		
		int nCnt = result_inofs.length;
		for( int i = 0; i < nCnt; i++ ) {
			String infos[] = result_inofs[i].split( " ", 7 );
			String full_path = path + infos[infos.length-1];
			
			Log.message( full_path );
			
			if( infos[0].startsWith("d")) {
				dir_list.add( full_path );
									
			} else if( infos[0].startsWith("-")) {
				
				boolean bSkip = false;
				if( full_path.startsWith("/sys/kernel/slab/")) {
					String strTemp = full_path.substring( "/sys/kernel/slab/".length() );
					for( String skip_slab : skip_slab_list ) {
						if( strTemp.startsWith( skip_slab )) {
							bSkip = true;
							break;
						}
					}
				} else {
					for( String skip_item : skip_item_list ) {
						if( full_path.endsWith( skip_item )) {
							bSkip = true;
							break;
						}					
					}
				}
				
				if( !bSkip ) {
					File file = new File( full_path );
					if( file.exists()) {
						if( file.length() < 4097 ) {
							write_info_file( fos, full_path );
							write( String.format( "\r\n" ));
						} else {
							//write( String.format( "%s => pass( great than 10K ) \r\n", full_path ));
						}
					} else {
						//write( String.format( "%s => pass( don't exist ) \r\n", full_path ));
					}
				} else {
					//write( String.format( "%s => skip\r\n", full_path ));
				}
			}
		}
	}
}
