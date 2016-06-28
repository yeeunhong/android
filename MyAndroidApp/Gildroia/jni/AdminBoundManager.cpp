#include "AdminBoundManager.h"
#include <set>

#ifdef ANDROID
#include <android/log.h>
#define LOG			__android_log_print
#endif

CAdminBoundManager::CAdminBoundManager(void)
: m_pFile( NULL )
{
}

CAdminBoundManager::~CAdminBoundManager(void)
{
}

bool CAdminBoundManager::Create( const char * filepath )
{
	m_AdminBoundaryMap.clear();

	char m_strPath[ MAX_PATH ];
	strcpy( m_strPath, filepath );

	int nLen = strlen( filepath );

	if( filepath[ nLen - 1 ] != '/') strcat( m_strPath, "/" );

	char strTemp[ MAX_PATH ];
	sprintf( strTemp, "%sadmin_bound.dat", m_strPath );
		
	m_pFile = fopen( strTemp, "rb" );
	if( NULL == m_pFile ) return false;
	
	UTSD_FILE_HEADER file_hdr;
	
	fseek( m_pFile, 0, SEEK_SET );
	if( 0 >= fread( &file_hdr, sizeof(UTSD_FILE_HEADER), 1, m_pFile )) return false;

	u32 sido_num;
	if( 0 >= fread( &sido_num, sizeof(u32), 1, m_pFile )) return false;

	PARENT_BOUND sido_bound;
	PARENT_BOUND gugun_bound;
	CHILD_BOUND dong_bound;

	GunGuAdminMap gugun_map;
	DongAdminMap dong_map;

	for ( u32 s = 0; s < sido_num; ++s)
	{
		if( 0 >= fread( &sido_bound, sizeof(PARENT_BOUND), 1, m_pFile )) return false;
		gugun_map.clear();

		for ( u32 g = 0; g < sido_bound.child_num; ++g)
		{
			if( 0 >= fread( &gugun_bound, sizeof(PARENT_BOUND), 1, m_pFile )) return false;
			
			for ( u32 d = 0; d < gugun_bound.child_num; ++d)
			{
				if( 0 >= fread( &dong_bound, sizeof(CHILD_BOUND), 1, m_pFile )) return false;

				dong_map.insert(std::make_pair(dong_bound.bound, dong_bound));
			}

			gugun_map.insert(make_pair(gugun_bound.bound, dong_map));
			dong_map.clear();
		}

		m_AdminBoundaryMap.insert(make_pair(sido_bound.bound, gugun_map));
		gugun_map.clear();
	}

	return true;
}

void CAdminBoundManager::Release()
{
	m_AdminBoundaryMap.clear();
	m_AdminBoundaryMap.swap( m_AdminBoundaryMap );

	if( NULL != m_pFile ) fclose( m_pFile );
	m_pFile = NULL;
}

bool CAdminBoundManager::findBound( u32 lat, u32 lon, HCODE& hcode)
{
	if( NULL == m_pFile )
		return false;

	BOUND_VTX* pVertex = NULL;
	long nVertexSize = 0;

	memset( &hcode, 0, sizeof( HCODE ));
	fseek( m_pFile, 0, SEEK_SET );
	
	SidoAdminMap::iterator iter_sido = m_AdminBoundaryMap.begin();
	for (; iter_sido != m_AdminBoundaryMap.end(); ++iter_sido)
	{
		if (iter_sido->first.llInBound(lat, lon))
		{
			GunGuAdminMap::iterator iter_gugun = iter_sido->second.begin();
			for (; iter_gugun != iter_sido->second.end(); ++iter_gugun)
			{
				if (iter_gugun->first.llInBound(lat, lon))
				{
					DongAdminMap::iterator iter_dong = iter_gugun->second.begin();
					for (; iter_dong != iter_gugun->second.end(); ++iter_dong)
					{
						if (iter_dong->first.llInBound(lat, lon))
						{
							if( pVertex == NULL )
							{
								pVertex = new BOUND_VTX[iter_dong->second.vtx_num];
								nVertexSize = iter_dong->second.vtx_num;
							}
							else if( nVertexSize < iter_dong->second.vtx_num )
							{
								delete [] pVertex;
								pVertex = new BOUND_VTX[iter_dong->second.vtx_num];
								nVertexSize = iter_dong->second.vtx_num;
							}
														
							fseek( m_pFile, iter_dong->second.offset - ftell( m_pFile ), SEEK_CUR );
							if( 0 >= fread( pVertex, sizeof(BOUND_VTX), iter_dong->second.vtx_num, m_pFile ))
							{
								goto func_exit;
							}
							
							if (llInBoundary(pVertex, iter_dong->second.vtx_num, lat, lon) == true)
							{
								hcode = iter_dong->second.hcode;

								goto func_exit;
							}						
						}
					}
				}
				if (hcode.isNull() == false)
					break;
			}
		}

		if (hcode.isNull() == false)
			break;
	}

func_exit :

	if( pVertex != NULL ) delete [] pVertex;
	
	return !hcode.isNull();
}

// lat/lon 기준으로 오른쪽으로 이동하며 만나는 점의 수를 저장한다.
bool CAdminBoundManager::llInBoundary(BOUND_VTX* pVertex, const u32 vtx_num, u32 lat, u32 lon)
{
	bool check = false;
	double dbLat = (double)lat;
	double dbLon = (double)lon;
	double Xi, Yi, Xj, Yj;

	u32 i, j;
	for (i = 0, j = vtx_num-1; i < vtx_num; j = i++)
	{
		Xi = (double)pVertex[i].x;
		Yi = (double)pVertex[i].y;
		Xj = (double)pVertex[j].x;
		Yj = (double)pVertex[j].y;
		
		if ( (((Yi <= dbLat) && (dbLat < Yj)) || ((Yj <= dbLat) && (dbLat < Yi))) && (dbLon < (Xj - Xi) * (dbLat - Yi) / (Yj - Yi) + Xi) )
			check = !check;
	}
	return check;
}

BOUND_VTX CAdminBoundManager::getIntersectVertex(BOUND_VTX& a_vtx, BOUND_VTX& b_vtx, u32 & lat)
{
	BOUND_VTX bound_vtx;
	bound_vtx.y = lat;

	if (lat == a_vtx.y)
		bound_vtx.x = a_vtx.x;
	else if (lat == b_vtx.y)
		bound_vtx.x = b_vtx.x;
	else
	{
		int a = lat - a_vtx.y;
		int b = b_vtx.x - a_vtx.x;
		float c = float(int(b_vtx.y) - int(a_vtx.y));
		bound_vtx.x = (u32)(a_vtx.x + (a * b) / c + 0.5);
	}

	return bound_vtx;
}

bool CAdminBoundManager::findBound( u32 lat, u32 lon )
{
	if( !findBound( lat, lon, m_tmpHCode )) return false;
	return false;
}

u8 CAdminBoundManager::if_changed_hcode()
{
	if( m_hCode.isNull()) 
	{
		m_hCode.sido  = m_tmpHCode.sido;
		m_hCode.gugun = m_tmpHCode.gugun;
		m_hCode.dong  = m_tmpHCode.dong;
		
		return SIDO_BIT | SIGUNGU_BIT | DONG_BIT;
	}

	u8 ret = 0;

	if( m_hCode.sido  != m_tmpHCode.sido )  ret |= SIDO_BIT;
	if( m_hCode.gugun != m_tmpHCode.gugun ) ret |= SIGUNGU_BIT;
	if( m_hCode.dong  != m_tmpHCode.dong )  ret |= DONG_BIT;

	if( ret != 0 ) 
	{
		m_hCode.sido  = m_tmpHCode.sido;
		m_hCode.gugun = m_tmpHCode.gugun;
		m_hCode.dong  = m_tmpHCode.dong;
	}

	return ret;
}