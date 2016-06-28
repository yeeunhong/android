package kr.co.uniquantum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import kr.co.uniquantum.interfaces.IFDownloadProgress;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.FloatMath;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Global 
{
	public static final double	POS_RATIO = 1;//60 * 60 * 120;
	public static final double	PIE = 4.0 * Math.atan(1.0);
	public static final double _60x60x120_ = 432000;
	
	static long m_internalAvailableSize = 0;
	static long m_internalTotalSize = 0;
	static long m_externalAvailableSize = 0;
	static long m_externalTotalSize = 0;
	static boolean m_isAvailableExternalMemory = 
		android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	
	static String m_strSDCardPath = "";
	
	// SDCard 유/무
	static public boolean IsSDCardAvailable()
	{
		return m_isAvailableExternalMemory; 
	}
	
	// SDCard Path
	static public String GetSDCardPath()
	{
		if( m_isAvailableExternalMemory && m_strSDCardPath.length() == 0 )
		{
			m_strSDCardPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		
		return m_strSDCardPath;
	}
	
	// Search Files Path
	static public String GetSearchFilePath()
	{
		return GetSDCardPath() + "/MapData/Search";		
	}
	
	// 사용가능한 내부 디스크 용량
	static public long GetAvailableInternalMemorySize()
	{
		if( m_internalAvailableSize == 0 ) 
		{
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			m_internalAvailableSize = availableBlocks * blockSize;
		}
		
		return m_internalAvailableSize; 
	}
	
	// 내부 디스크 용량
	static public long GetTotalInternalMemorySize()
	{
		if( m_internalTotalSize == 0 )
		{
			File path = Environment.getDataDirectory();
	        StatFs stat = new StatFs(path.getPath());
	        long blockSize = stat.getBlockSize();
	        long totalBlocks = stat.getBlockCount();
	        m_internalTotalSize = totalBlocks * blockSize;
		}
		
		return m_internalTotalSize;
	}
	
	// 사용 가능한 외부 디스크(SDCard) 용량
	static public long GetAvailableExternalMemorySize()
	{
		if( !m_isAvailableExternalMemory ) return m_externalAvailableSize;
		if( m_externalAvailableSize == 0 )
		{
			File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            m_externalAvailableSize = availableBlocks * blockSize;
		}
		
		return m_externalAvailableSize;
	}
	
	// 외부 디스크(SDCard) 용량
	static public long GetTotalExternalMemorySize()
	{
		if( !m_isAvailableExternalMemory ) return m_externalTotalSize;
		if( m_externalTotalSize == 0 )
		{
			File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            m_externalTotalSize = totalBlocks * blockSize;
		}
		
		return m_externalTotalSize;
	}
	
	static public String formatSize( long size )
	{
		String suffix = null;
		   
        if (size >= 1024) 
        {
            suffix = "KiB";
            size /= 1024;
            
            if (size >= 1024)
            {
                suffix = "MiB";
                size /= 1024;
            }
        }
   
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
   
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) 
        {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
   
        if (suffix != null)
            resultBuffer.append(suffix);
        
        return resultBuffer.toString();
	}
	
	static public void ShowSoftKeyboard( Context context, EditText et, boolean bShow )
	{
		InputMethodManager imm = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );
		if( bShow ) 
			imm.showSoftInput( et, 0 );
		else
			imm.hideSoftInputFromWindow( et.getWindowToken(), 0 );
	}
	
	////////////////////////////////////////////////////////////////////
	//좌표 변환
	//WGS84 -> TOKYO BESSEL
	//Unit : 0.01 second
	//Moledensky Method
	static public double[] WGS2BSL( double lon, double lat, double alt )
	{
		double	da = 739.845;
		double	df = 0.10037483;
		double	dx = -128.0;
		double	dy = 481;
		double	dz = 664;	df /= 10000.0;
		double	a = 6378137.0 - da;
		double	f = 0.00335281066474 - df;
		double	b = (1.0 - f) * a;
		double	e = Math.sqrt(f * (2.0 - f));
		
		double	sinlat = Math.sin((double)lat * PIE / 180. / POS_RATIO);	//360000L);
		double	sinlon = Math.sin((double)lon * PIE / 180. / POS_RATIO);	//360000L);
		double	coslat = Math.cos((double)lat * PIE / 180. / POS_RATIO);	//360000L);
		double	coslon = Math.cos((double)lon * PIE / 180. / POS_RATIO);	//360000L);
		
		double	rn = a / Math.sqrt(1.0 - Math.pow(e * sinlat, 2));
		double	term1 = 1.0 - Math.pow(e * sinlat, 2);
		double	term2 = Math.pow (term1, 1.5);
		double	rm = (a * (1.0 - (e * e))) / term2;
		
		term1	= -(dx * sinlat * coslon);
		term2	= -(dy * sinlat * sinlon);
		double	term3 = dz * coslat;
		term1	+= term2 + term3;
		term2	= (da * rn * (e * e) * sinlat * coslat) / a;
		term1	+= term2;
		term2	= (rm * (a/b)) + (rn * (b/a));
		term3	= df * term2 * sinlat * coslat;
		term1	+= term3;
		term2	= 1.0 /(rm + alt);
		
		lat		-= (long)((double)(term1 * term2) * 180./PIE * POS_RATIO);	//360000L);
		
		term1	= (-dx * sinlon) + (dy * coslon);
		term2	= 1.0 / ((rn + alt) * coslat);
		
		lon		-= (long)((double)(term1 * term2) * 180./PIE * POS_RATIO);	//360000L);
		
		term1	= dx * coslat * coslon;
		term2	= dy * coslat * sinlon;
		term3	= dz * sinlat;
		term1	+= term2 + term3;
		term2	= -da * (a / rn);
		term3	= df * (b/a) * rn * sinlat * sinlat;
		
		alt		-= (int)((double) term1 + term2 + term3);

		double[] ret = new double[3];
		ret[0] = lon;
		ret[1] = lat;
		ret[2] = alt;
		
		return ret;
	}
	
	// tokyo bessel -> wgs84
	// Unit : 0.01 second
	// Moledensky Method
	static public double[] BSL2WGS( double lon, double lat, double alt )
	{
		// BesselParameters
		final double	a	= 6377397.155;			// Bessel
		final double	f	= 1 / 299.152812800;	// Bessel
		// WGS84 Parameters
		final double	WGS84_a	= 6378137.000;
		final double	WGS84_f	= 1 / 298.257223563;
		double	Lat_in, Lon_in, Hgt_in=0;

		Lon_in	= ((double)lon/POS_RATIO*PIE)/180;
		Lat_in	= ((double)lat/POS_RATIO*PIE)/180;

		double	da		= WGS84_a - a;
		double	df		= WGS84_f - f;
		double	dx		= -148.0;//local->Parameters[0];
		double	dy		= 507.0; //local->Parameters[1];
		double	dz		= 685.0; //local->Parameters[2];
	 
		double	tLon_in;   /* temp longitude                                   */
		double	e2;        /* Intermediate calculations for dp, dl               */
		double	ep2;       /* Intermediate calculations for dp, dl               */
		double	sin_Lat;   /* sin(Latitude_1)                                    */
		double	sin2_Lat;  /* (sin(Latitude_1))^2                                */
		double	sin_Lon;   /* sin(Longitude_1)                                   */
		double	cos_Lat;   /* cos(Latitude_1)                                    */
		double	cos_Lon;   /* cos(Longitude_1)                                   */
		double	w2;        /* Intermediate calculations for dp, dl               */
		double	w;         /* Intermediate calculations for dp, dl               */
		double	w3;        /* Intermediate calculations for dp, dl               */
		double	m;         /* Intermediate calculations for dp, dl               */
		double	n;         /* Intermediate calculations for dp, dl               */
		double	dp;        /* Delta phi                                          */
		double	dp1;       /* Delta phi calculations                             */
		double	dp2;       /* Delta phi calculations                             */
		double	dp3;       /* Delta phi calculations                             */
		double	dl;        /* Delta lambda                                       */
		double	dh;        /* Delta height                                       */
		double	dh1;       /* Delta height calculations                          */
		double	dh2;       /* Delta height calculations                          */

		if(Lon_in > PIE)	tLon_in = Lon_in - (2*PIE);
		else				tLon_in = Lon_in;
		e2		= 2 * f - f * f;
		ep2		= e2 / (1 - e2);
		sin_Lat = Math.sin(Lat_in);
		cos_Lat = Math.cos(Lat_in);
		sin_Lon = Math.sin(tLon_in);
		cos_Lon = Math.cos(tLon_in);
		sin2_Lat	= sin_Lat * sin_Lat;
		w2		= 1.0 - e2 * sin2_Lat;
		w		= Math.sqrt(w2);
		w3		= w * w2;
		m		= (a * (1.0 - e2)) / w3;
		n		= a / w;
		dp1		= cos_Lat * dz - sin_Lat * cos_Lon * dx - sin_Lat * sin_Lon * dy;
		dp2		= ((e2 * sin_Lat * cos_Lat) / w) * da;
		dp3		= sin_Lat * cos_Lat * (2.0 * n + ep2 * m * sin2_Lat) * (1.0 - f) * df;
		dp		= (dp1 + dp2 + dp3) / (m + Hgt_in);
		dl		= (-sin_Lon * dx + cos_Lon * dy) / ((n + Hgt_in) * cos_Lat);
		dh1		= (cos_Lat * cos_Lon * dx) + (cos_Lat * sin_Lon * dy) + (sin_Lat * dz);
		dh2		= -(w * da) + ((a * (1 - f)) / w) * sin2_Lat * df;
		dh		= dh1 + dh2;
		alt		= (double)(Hgt_in + dh);

		Lat_in = Lat_in + dp;
		Lon_in = Lon_in + dl;
		if(Lon_in > (PIE * 2))		Lon_in -= 2*PIE;
		if(Lon_in < (- PIE))		Lon_in += 2*PIE;
		lat		= (double)((Lat_in*180*POS_RATIO)/PIE);
		lon		= (double)((Lon_in*180*POS_RATIO)/PIE);
		
		double[] ret = new double[3];
		ret[0] = lon;
		ret[1] = lat;
		ret[2] = alt;
		
		return ret;
	}
	
	static public String readOneLineStream( InputStream inputStream )
	{
		StringBuffer sb = new StringBuffer();
		int nBuff;
		
		try 
		{
			while(( nBuff = inputStream.read()) != -1 )
			{
				if( nBuff == '\n' ) break;
				sb.append( ( char ) nBuff );
			}
		} catch (IOException e) {e.printStackTrace(); }
		
		return sb.toString();
	}
	
	static public String readOneLineStream_br( InputStream inputStream )
	{
		int  line_idx = 0;
		byte line_buff[] = new byte[ 1024 ];
		byte nReadBuff[] = new byte[1];
		try 
		{
			while( inputStream.read( nReadBuff ) != -1 )
			{
				line_buff[line_idx] = nReadBuff[0];
				if( line_buff[line_idx] == '<' )
				{
					if( inputStream.read( nReadBuff ) == -1 ) break;
					line_buff[++line_idx] = nReadBuff[0];
					if( line_buff[line_idx] != 'b' ){ ++line_idx; continue; }
					
					if( inputStream.read( nReadBuff ) == -1 ) break;
					line_buff[++line_idx] = nReadBuff[0];
					if( line_buff[line_idx] != 'r' ){ ++line_idx; continue; }
					
					if( inputStream.read( nReadBuff ) == -1 ) break;
					line_buff[++line_idx] = nReadBuff[0];
					if( line_buff[line_idx] != '>' ){ ++line_idx; continue; }
					
					return new String( line_buff, 0, line_idx - 3, "utf-8" );					
				}
				
				++ line_idx;
			}
			
			return new String( line_buff, 0, line_idx, "utf-8" );
		} catch (IOException e) {e.printStackTrace(); }
		
		return null;
	}
	
	// Download an HTTP file with progress notification 
	static public void downLoadFileFromWebServer( 
			String serverUrl, String src_filename, 
			String localPath, String dest_filename,
			int arg1, int arg2, IFDownloadProgress dp )
	{
		URL url;
		try 
		{
			url = new URL( serverUrl + src_filename );
			
			HttpURLConnection urlConnection = ( HttpURLConnection ) url.openConnection();
			urlConnection.setRequestMethod( "GET" );
			urlConnection.setDoOutput( true );
			
			urlConnection.connect();
			
			File file = new File( localPath, dest_filename );
			FileOutputStream fileOutput = new FileOutputStream( file );
			InputStream inputStream = urlConnection.getInputStream();
			
			int totalSize = urlConnection.getContentLength();
			int downloadedSize = 0;
			int bufferLength = 0;
			
			byte[] buffer = new byte[ inputStream.available() ];
			
			dp.updateDownloadProgress( src_filename, totalSize, downloadedSize, arg1, arg2 );
			while(( bufferLength = inputStream.read( buffer )) > 0 )
			{
				fileOutput.write( buffer, 0, bufferLength );
				downloadedSize += bufferLength;
				dp.updateDownloadProgress( src_filename, totalSize, downloadedSize, arg1, arg2 );
			}
			
			buffer = null;
			
			inputStream.close();
			fileOutput.close();
			fileOutput = null;
			file = null;
		}
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) {e.printStackTrace();} 
		
		url = null;
	}
	
	// Distance between two GPS coordinates (in meter)
	static public double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) 
	{   
	    float pk = (float) (180/3.14169);   
	    
	    float a1 = lat_a / pk;   
	    float a2 = lng_a / pk;   
	    float b1 = lat_b / pk;   
	    float b2 = lng_b / pk;   
	  
	    float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);   
	    float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);   
	    float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);   
	    double tt = Math.acos(t1 + t2 + t3);   
	      
	    return 6366000*tt;   
	}  

}
