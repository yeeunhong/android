package code.inka.android.storage;

import android.os.Environment;
import android.os.StatFs;

public class StorageManager {
	long 	m_dataDirectoryBlockSize 		= 0;
	long 	m_dataDirectoryAvailableBlocks 	= 0;
	long 	m_dataDirectoryTotalBlocks 		= 0;
	
	long 	m_externalDirectoryBlockSize 		= 0;
	long 	m_externalDirectoryAvailableBlocks 	= 0;
	long 	m_externalDirectoryTotalBlocks 		= 0;
	
	public StorageManager(){
		String  dataDirectory 			= Environment.getDataDirectory().getPath();
		StatFs 	dataDirectoryStat 		= new StatFs( dataDirectory );
				
		m_dataDirectoryBlockSize 		= dataDirectoryStat.getBlockSize();
		m_dataDirectoryAvailableBlocks 	= dataDirectoryStat.getAvailableBlocks();
		m_dataDirectoryTotalBlocks 		= dataDirectoryStat.getBlockCount();
		
		String  externalDirectory 		= Environment.getExternalStorageDirectory().getPath();
		StatFs 	externalDirectoryStat 	= new StatFs( externalDirectory );
		
		m_externalDirectoryBlockSize 		= externalDirectoryStat.getBlockSize();
		m_externalDirectoryAvailableBlocks 	= externalDirectoryStat.getAvailableBlocks();
		m_externalDirectoryTotalBlocks 		= externalDirectoryStat.getBlockCount();	
	}
	
	/**
	 * @return ��밡���� ���� ��ũ �뷮
	 */
	public long getDataDirectoryAvailableSize(){
		return m_dataDirectoryAvailableBlocks * m_dataDirectoryBlockSize;
	}
	
	/**
	 * @return ���� ��ü ��ũ �뷮
	 */
	public long getDataDirectoryTotalInternalSize()	{
		return m_dataDirectoryTotalBlocks * m_dataDirectoryBlockSize;
	}
	
	/**
	 * �ܺ� ��ũ(SDCard)�� ����Ʈ �Ǿ������� �˷��ش�.
	 * @return true:�ܺ� ��ũ ����Ʈ �Ǿ� ����, false:�ܺ� ��ũ ���� 
	 */
	static public boolean isExternalDirectoryAvailable() {    
		String state = Environment.getExternalStorageState();    
		
		if( state.equals( Environment.MEDIA_MOUNTED )) return true;        
		if( state.equals( Environment.MEDIA_MOUNTED_READ_ONLY )) return true;
		 
		return false;
	}
	
	/**
	 * @return ��� ������ �ܺ� ��ũ(SDCard) �뷮
	 */
	public long getExternalDirectoryAvailableSize() {
		 return m_externalDirectoryAvailableBlocks * m_externalDirectoryBlockSize;
	}
	
	/**
	 * @return �ܺ� ��ũ(SDCard) �뷮
	 */
	public long getExternalDirectoryTotalInternalSize()	{
		return m_externalDirectoryTotalBlocks * m_externalDirectoryBlockSize;
	}

}
