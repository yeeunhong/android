package code.inka.android.file;

import java.io.File;
import java.io.FileFilter;

public class FileManager {
	private static final double SIZE_KB = 1024;
	private static final double SIZE_MB = (1024 * SIZE_KB);
	private static final double SIZE_GB = (1024 * SIZE_MB);
	
	/**
	 * path ���� ���� ��������Ʈ���� ��� �����ش�.
	 * @param path ���� ����Ʈ�� ���� ���
	 * @return path �������� ����s
	 */
	public static File[] getFolderList( String path ) {
		File f = new File( path );
		return f.listFiles( new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}} );
	}
	
	/**
	 * path ���� ���� ���ϸ���Ʈ���� ��� �����ش�.
	 * @param path ���ϸ���Ʈ�� ���� ���
	 * @return path �������� ����s
	 */
	public static File[] getFileList( String path ) {
		File f = new File( path );
		return f.listFiles( new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.isFile();
			}} );
	}
	
	
	/**
	 * ������ �����մϴ�. <br>
	 * createSubDir���� true �̸� path�� ��ü ��θ� ��� �����մϴ�.<br> 
	 * @param path ������ ���
	 * @param createSubDir ��ü ��� ���� ����
	 * @return true:����, false:����
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
	 * ������ ��ü ��ο��� ���ϸ��� �߷� ����. 
	 * @param filePath ��ü ���
	 * @return ���� ��:���� ��, ���� ��:null
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
	 * ������ Ȯ���ڸ� ����ϴ�.<br>
	 * mp4.ncg �̷������� ������ ������ .ncg ������ Ȯ���� .mp4�� ���� �ݴϴ�. 
	 * @param fileStr ���ϸ� or ���� ��ü �н�
	 * @return ������ Ȯ����
	 */
	public static String getExtension(String fileStr){
		String strExt = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
		if( strExt.compareTo("ncg") == 0 ) {
			strExt = getExtension( fileStr.substring(0, fileStr.length()-strExt.length()-1)).toLowerCase();
		}
		return strExt;
	}
	
	/**
	 * ������ ����� ���ڿ��� ��ȭ�Ͽ� �ݴϴ�. 
	 * @param nSize ������ ũ��
	 * @return ����ũ�⸦ GB/MB/KB/byte ũ��� ��ȯ�� ���ڿ� ��
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
