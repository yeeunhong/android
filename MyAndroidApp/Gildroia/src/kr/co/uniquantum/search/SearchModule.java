package kr.co.uniquantum.search;

import kr.co.uniquantum.Global;

public class SearchModule 
{
	private static SearchModule search_module = new SearchModule();
	private static boolean is_init = false;
	
	public  static SearchModule getInstance()
	{
		if( !is_init )
		{
			search_module.InitModule(Global.GetSearchFilePath());
			is_init = true;
		}

		return search_module;
	}
	
	public native boolean 	InitModule( String path );
	public native void	  	ExitModule();

	public native int		GetSidoCount();
	public SIDO_DATA 		GetSidoData( int sidoIdx )
	{
		short [] sidoArray = JNI_GetSidoData( sidoIdx );
		if( sidoArray[0] == -1 ) return null;
		
		SIDO_DATA sidoData = new SIDO_DATA();
		sidoData.idx 		= (byte)sidoArray[0];
		sidoData.code		= (byte)sidoArray[1];
		sidoData.mesh_x_min = sidoArray[2]; 
		sidoData.mesh_x_max = sidoArray[3];
		sidoData.mesh_y_min = sidoArray[4];
		sidoData.mesh_y_max = sidoArray[5];
		
		return sidoData;
	} 
	private native short[]	JNI_GetSidoData( int sidoIdx );
	public native String 		GetSidoName( int sidoIdx );
	public native String		GetSidoNameByDCode( long dcode );
	
	public native int			GetSigunguCount( int sidoIdx );
	public native String		GetSigunguName( int sigunguIdx );
	public native String		GetSigunguNameByDCode( long dcode );
	public SIGUNGU_DATA 		GetSigunguData( int sigunguIdx )
	{ 
		long[] sigunguArray = JNI_GetSigunguData( sigunguIdx );
		if( sigunguArray[0] == -1 ) return null;
		
		SIGUNGU_DATA sigungu_data = new SIGUNGU_DATA();
		sigungu_data.code 		= ( short ) sigunguArray[0];
		sigungu_data.offset		= sigunguArray[1];
		sigungu_data.road_ofs	= sigunguArray[2];
		sigungu_data.name		= GetSigunguName( sigunguIdx );
					
		return sigungu_data;
	}
	private native long[]	JNI_GetSigunguData( int sigunguIdx );
	
	public native int			GetDongCount( int sigunguIdx );
	public native int			GetSearchDongCount( String name );
	public native String		GetDongName ( int dongIdx );
	public native String		GetDongNameByDCode ( long dcode );
	public DONG_DATA			GetDongData ( int dongIdx )
	{
		long[] dongArray = JNI_GetDongData( dongIdx );
		if( dongArray[0] == -1 ) return null;
		
		DONG_DATA dong_data = new DONG_DATA();
		dong_data.code			= ( int ) dongArray[0];
		dong_data.min_x			= ( int ) dongArray[1];
		dong_data.min_y 		= ( int ) dongArray[2];
		dong_data.ldong_flag	= dongArray[3] == 1;
		dong_data.bunji_cnt		= ( short ) dongArray[4];
		dong_data.offset		= dongArray[5];
			 
		dong_data.name		= GetDongName( dongIdx );
					
		return dong_data;
	}
	private native long[]	JNI_GetDongData( int dongIdx );
	
	public native int			GetBunjiCount( int dongIdx );
	public native int			GetBunjiValue( int bunjiIdx );
	public native int			GetHoCount   ( int bunjiIdx );
	public native int			GetHoValue	 ( int hoIdx );
	public HO_DATA				GetHoData	 ( int hoIdx )
	{
		long[] hoArray = JNI_GetHoData( hoIdx );
		if( hoArray[0] == -1 ) return null;
		
		HO_DATA ho_data = new HO_DATA();
		ho_data.ho = (short)hoArray[0];
		ho_data.x  = hoArray[1];
		ho_data.y  = hoArray[2];
		return ho_data;
	}
	private native long[]	JNI_GetHoData	 ( int hoIdx );
	
	public native int			GetRoadCount( int sigunguIdx );
	public native int			GetSearchRoadCount( String name );
	public native String		GetRoadName ( int roadIdx );
	public ROAD_DATA			GetRoadData ( int roadIdx )
	{
		long[] roadArray = JNI_GetRoadData( roadIdx );
		if( roadArray[0] == -1 ) return null;
		
		ROAD_DATA road_data = new ROAD_DATA();
		road_data.code			= ( int ) roadArray[0];
		road_data.bunji_cnt		= ( short ) roadArray[1];
		road_data.offset		= roadArray[2];
		road_data.name			= GetRoadName( roadIdx );
					
		return road_data;
	}
	private native long[]	JNI_GetRoadData( int dongIdx );
	
	public native int			GetRoadBunjiCount( int dongIdx );
	public native int			GetRoadBunjiValue( int bunjiIdx );
	public native int			GetRoadHoCount   ( int bunjiIdx );
	public native int			GetRoadHoValue	 ( int hoIdx );
	public ROAD_HO_DATA			GetRoadHoData	 ( int hoIdx )
	{
		long[] hoArray = JNI_GetRoadHoData( hoIdx );
		if( hoArray[0] == -1 ) return null;
		
		ROAD_HO_DATA ho_data = new ROAD_HO_DATA();
		ho_data.ho  		= (short)hoArray[0];
		ho_data.old_bunji  	= (short)hoArray[1];
		ho_data.old_ho  	= (short)hoArray[2];
		ho_data.x  			= hoArray[4];
		ho_data.y  			= hoArray[5];
		
		ho_data.old_dongName  	= GetDongNameByDCode( hoArray[3] );
		 
		return ho_data;
	}
	private native long[]	JNI_GetRoadHoData	 ( int hoIdx );
	
	public native int			GetAptCount	( int dongIdx );
	public String				GetAptName	( int aptIdx ){ return GetPoiName( aptIdx ); }
	public POI_DATA				GetAptData	( int aptIdx ){	return GetPoiData( aptIdx ); }
	public POI_DATA				GetAptData	(){ return GetPoiData(); }
			
	public native int			GetCatePoiCount( String code, int categoryType, boolean addData );
	public native int			GetSearchPoiNameCount( int sidoIdx, String name );
	public native int			GetSearchPoiCallCount( String call );
	
	public native boolean		MoveFirstPoiData();
	public native boolean		MoveNextPoiData();
	
	public native boolean		IsCatePoi( int poiIdx );
	public native boolean		IsCatePoi();
	public native String		GetPoiName( int poiIdx );
	public native String		GetPoiName();
	public native String		GetPoiBranchName( int poiIdx );
	public native String		GetPoiBranchName();
	public native String		GetPoiCallNumber( int poiIdx );
	public native String		GetPoiCallNumber();
	public POI_DATA		    	GetPoiData( int poiIdx )
	{ 
		long[] poiArray = JNI_GetPoiData( poiIdx );
		if( poiArray[0] == -1 ) return null;
		
		POI_DATA poiData = new POI_DATA();
		poiData.sidoName 	= GetSidoName(( int ) poiArray[0] );
		poiData.sigunguName	= GetSigunguNameByDCode( poiArray[1] );
		poiData.dongName	= GetDongNameByDCode( poiArray[1] );
		poiData.branchName	= GetPoiBranchName( poiIdx );
		poiData.call_number	= GetPoiCallNumber( poiIdx );
		
		poiData.catePoi		= poiArray[2] == 1;
		poiData.san			= poiArray[3] == 1;
		poiData.parking		= poiArray[4] == 1;
		
		poiData.bunji		= (short) poiArray[5];
		poiData.ho			= (short) poiArray[6];
		
		poiData.x			= poiArray[7];
		poiData.y			= poiArray[8];
		poiData.route_x		= poiArray[9];
		poiData.route_y		= poiArray[10];
		
		return poiData;
	}
	public POI_DATA		    	GetPoiData()
	{
		long[] poiArray = JNI_GetPoiData();
		if( poiArray[0] == -1 ) return null;
		
		POI_DATA poiData = new POI_DATA();
		poiData.sidoName 	= GetSidoName(( int ) poiArray[0] );
		poiData.sigunguName	= GetSigunguNameByDCode( poiArray[1] );
		poiData.dongName	= GetDongNameByDCode( poiArray[1] );
		poiData.branchName	= GetPoiBranchName();
		poiData.call_number	= GetPoiCallNumber();
		
		poiData.catePoi		= poiArray[2] == 1;
		poiData.san			= poiArray[3] == 1;
		poiData.parking		= poiArray[4] == 1;
		 
		poiData.bunji		= (short) poiArray[5];
		poiData.ho			= (short) poiArray[6];
		
		poiData.x			= poiArray[7];
		poiData.y			= poiArray[8];
		poiData.route_x		= poiArray[9];
		poiData.route_y		= poiArray[10];
		
		return poiData;
	}
	private native long[]	JNI_GetPoiData( int poiIdx );
	private native long[]	JNI_GetPoiData();
	 
	public native void 		SetLocationPos( long lon, long lat );
	public long				ChangedLocationCode()
	{
		long [] locInfo = JNI_ChangedLocationCode();
		return locInfo[0];
	}
	private native long[] 	JNI_ChangedLocationCode();
	 
	static
	{
		System.loadLibrary("searchModule");
	}
}
