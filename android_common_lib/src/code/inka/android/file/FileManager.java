package code.inka.android.file;

import java.io.File;
import java.io.FileFilter;

public class FileManager {
	private static final double SIZE_KB = 1024;
	private static final double SIZE_MB = (1024 * SIZE_KB);
	private static final double SIZE_GB = (1024 * SIZE_MB);
	
	/**
	 * path 폴더 안의 폴더리스트만을 모아 돌려준다.
	 * @param path 폴더 리스트를 얻을 경로
	 * @return path 폴더안의 폴더s
	 */
	public static File[] getFolderList( String path ) {
		File f = new File( path );
		return f.listFiles( new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}} );
	}
	
	/**
	 * path 폴더 안의 파일리스트만을 모아 돌려준다.
	 * @param path 파일리스트를 얻을 경로
	 * @return path 폴더안의 파일s
	 */
	public static File[] getFileList( String path ) {
		File f = new File( path );
		return f.listFiles( new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.isFile();
			}} );
	}
	
	
	/**
	 * 폴더를 생성합니다. <br>
	 * createSubDir값이 true 이면 path의 전체 경로를 모두 생성합니다.<br> 
	 * @param path 생성할 경로
	 * @param createSubDir 전체 경로 생성 여부
	 * @return true:성공, false:실패
	 */
	public static boolean mkdir( String path, boolean createSubDir ) {
		File newPath = new File( path );
		if(createSubDir ) {
			return newPath.mkdirs();
		} 
		return newPath.mkdir();
	}
	
	public static boolean moveFile( String src, String dest ) {
		File src_file = new File( src );
		if( !src_file.exists()) return false;
		
		File dest_file = new File( dest );
		if( dest_file.exists()) {
			if( src_file.getAbsolutePath().compareTo( dest_file.getAbsolutePath()) != 0 ){
				dest_file.delete();
			}
		}
		
		return src_file.renameTo( dest_file );		
	}
	
	public static boolean existsFile(String filePath) {
	    boolean ret = false;
		
	    File file = new File (filePath);
	    ret = file.exists();
	    file = null;
	    
	    return ret;
	} 
	
	public static boolean deleteFile( String filePath ) {
		boolean ret = false;
		
		File file = new File (filePath);
        ret = file.delete();
        file = null;
        
        return ret;
	}
	
	/**
	 * 파일의 전체 경로에서 파일명만을 추려 낸다. 
	 * @param filePath 전체 경로
	 * @return 성공 시:파일 명, 실패 시:null
	 */
	public static String getFilenameFromFilePath( String filePath ) {
		filePath = filePath.replace( '\\', '/' );
		String[] token = filePath.split( "/" );
		if( token == null ) 	return null;
		if( token.length < 1 )	return filePath;
		
		String ret = token[token.length - 1];
		return ret;
	}
	
	/**
	 * 파일의 확장자를 얻습니다.<br>
	 * mp4.ncg 이런식으로 끝나는 파일은 .ncg 이전의 확장자 .mp4를 돌려 줍니다. 
	 * @param fileStr 파일명 or 파일 전체 패스
	 * @return 파일의 확장자
	 */
	public static String getExtension(String fileStr){
		String strExt = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
		if( strExt.compareTo("ncg") == 0 ) {
			strExt = getExtension( fileStr.substring(0, fileStr.length()-strExt.length()-1)).toLowerCase();
		}
		return strExt;
	}
	
	/**
	 * 파일의 사이즈를 문자열로 변화하여 줍니다. 
	 * @param nSize 파일의 크기
	 * @return 파일크기를 GB/MB/KB/byte 크기로 변환한 문자열 값
	 */
	public static String fileSizeToString(long nSize) {
		String fileSize = "";
		double dSize = ( double ) nSize;
		
		try{
			if(nSize <= 0) {
				fileSize = String.format("0");
				
			} else if(nSize > SIZE_GB) {
				fileSize = String.format("%.2f GB", dSize/SIZE_GB);
				
			} else if(nSize > SIZE_MB) {
				fileSize = String.format("%.2f MB", dSize/SIZE_MB);
				
			} else if(nSize > SIZE_KB) {
				fileSize = String.format("%.2f KB", dSize/SIZE_KB);
				
			} else { 
				fileSize = String.format("%d byte", nSize);
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		return fileSize;
	}
}
