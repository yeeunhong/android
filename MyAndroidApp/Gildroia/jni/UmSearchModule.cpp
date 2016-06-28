#include <stdio.h>
#include "UmSearchModule.h"
#include <math.h>
#include <wchar.h>

#ifndef MAX_PATH
#define MAX_PATH	256
#endif

CUmSearchModule::CUmSearchModule(void)
: m_fpLHCode( NULL )
, m_nLHCodeIdxOfs( 0 )
, m_nLHCodeIdxCnt( 0 )
{
}

CUmSearchModule::~CUmSearchModule(void)
{
	if( m_fpLHCode != NULL ) fclose( m_fpLHCode );
	
	Release();

	for( std::vector<CUmSearchHelper *>::iterator it = m_helpers.begin(); it != m_helpers.end(); ++it ) delete (*it);
}

bool CUmSearchModule::Load( const char * pPath )
{
	int nLen = ( int ) strlen( pPath );

	char m_strPath[ MAX_PATH ];
	strcpy( m_strPath, pPath );

	if( pPath[ nLen - 1 ] != '/') strcat( m_strPath, "/" );

	char strTemp[ MAX_PATH ];
	for( int i = 0; i < MAX_SEARCH_SIDO_CNT; ++i )
	{
		sprintf( strTemp, "%s%s.dat", m_strPath, g_sido_info[i].tail );
		FILE * fp = fopen( strTemp, "rb");
		
		if( fp ) 
		{
			CUmSearchHelper * pHelper = new CUmSearchHelper( fp, &m_Text_mng );
			m_helpers.push_back( pHelper );
		}
	}

	sprintf( strTemp, "%sLHTbl.dat", m_strPath );
	m_fpLHCode = fopen( strTemp, "rb");

	if( m_fpLHCode )
	{
		fseek( m_fpLHCode, -2 * (long)sizeof( u32 ), SEEK_END );
		fread( &m_nLHCodeIdxOfs, sizeof( u32 ), 1, m_fpLHCode );
		fread( &m_nLHCodeIdxCnt, sizeof( u32 ), 1, m_fpLHCode );
	}
	//

	return m_helpers.size() != 0 && m_Text_mng.Load( m_strPath );
}

void CUmSearchModule::Release()
{
	for( std::vector<CUmSearchHelper *>::iterator it = m_helpers.begin(); it != m_helpers.end(); ++it ) (*it)->Release();
	
	m_poiHeaders.clear();		m_poiHeaders.swap( m_poiHeaders );
	m_dongNameResult.clear();	m_dongNameResult.swap( m_dongNameResult );
	m_roadNameResult.clear();	m_roadNameResult.swap( m_roadNameResult );
	m_stLHCodeResult.clear();	m_stLHCodeResult.swap( m_stLHCodeResult );
}

void CUmSearchModule::GetHoPos( int nSidoIdx, HO_DATA * pHo, u32 * pX, u32 * pY )
{
	m_helpers[nSidoIdx]->GetHoPos( pHo, pX, pY );
}

u32 CUmSearchModule::GetNewHoPos ( int nSidoIdx, NEW_HO_DATA * pNewHo, u32 * pX, u32 * pY, HO_DATA * pHo )
{
	DONG_DATA * pDong = m_helpers[nSidoIdx]->GetHo( pNewHo->offset, pHo );
	if( pDong )
	{
		m_helpers[nSidoIdx]->GetHoPos( pHo, pX, pY );

		*pX += pDong->info.min_x;
		*pY += pDong->info.min_y;

		return pDong->info.code;
	}
	else
	{
		DONG_DATA_F dongData;
		m_helpers[nSidoIdx]->GetHo( pNewHo->offset, pHo, &dongData );
		
		m_helpers[nSidoIdx]->GetHoPos( pHo, pX, pY );

		*pX += dongData.min_x;
		*pY += dongData.min_y;

		return dongData.code;
	}

	return 0;
}

std::vector<DONG_DATA> * CUmSearchModule::SearchDongName( u16 * dongName, int nLen )
{
	m_dongNameResult.clear();
	m_dongNameResult.swap( m_dongNameResult );

	u32 nChoCode	= tranChosung( dongName, nLen );
	u32 nCkCmpHan	= checkCompletHangul( dongName, nLen );

	int nCount = ( int ) m_helpers.size();
	for( int nSidoIdx = 0; nSidoIdx < nCount; ++ nSidoIdx )
	{
		m_helpers[nSidoIdx]->SearchDongName( nSidoIdx, dongName, nLen, nChoCode, nCkCmpHan, &m_dongNameResult );
	}
	
	return &m_dongNameResult;
}

std::vector<ROAD_DATA> * CUmSearchModule::SearchRoadName( u16 * roadName, int nLen )
{
	m_roadNameResult.clear();
	m_roadNameResult.swap( m_roadNameResult );

	u32 nChoCode	= tranChosung( roadName, nLen );
	u32 nCkCmpHan	= checkCompletHangul( roadName, nLen );

	int nCount = ( int ) m_helpers.size();
	for( int nSidoIdx = 0; nSidoIdx < nCount; ++ nSidoIdx )
	{
		m_helpers[nSidoIdx]->SearchRoadName( nSidoIdx, roadName, nLen, nChoCode, nCkCmpHan, &m_roadNameResult );
	}

	return &m_roadNameResult;
}

std::vector<BUNJI_DATA> *	CUmSearchModule::GetBunjiData  ( int nSidoIdx, void * pDong, bool bSan, bool bRoad )
{ 
	return bRoad ? m_helpers[nSidoIdx]->GetRoadBunjiData( pDong ) : m_helpers[nSidoIdx]->GetBunjiData( pDong, !bSan ); 
}

std::multimap<tstring,POI_HEADER> *	CUmSearchModule::SearchTelNumber   ( u16 * pText, int nLen )
{
	m_poiHeaders.clear(); m_poiHeaders.swap( m_poiHeaders );

	u8  nTelDDD;
	u32 nTelCode;

	if( tranTelNumber2( pText, &nTelDDD, &nTelCode ))
	{
		u16  nTelNUM = ( u16 )( nTelCode & 0xFFFF );
		u16  nTelKUK = ( u16 )(( nTelCode >> 16 ) & 0xFFFF );

		int nCount = ( int ) m_helpers.size();
		for( int nSidoIdx = 0; nSidoIdx < nCount; ++ nSidoIdx )
		{
			if( nTelDDD == 0 )
				m_helpers[nSidoIdx]->SearchTelNumber( nSidoIdx, nTelKUK, nTelNUM, &m_poiHeaders );
			else
				m_helpers[nSidoIdx]->SearchTelNumber( nSidoIdx, nTelDDD, nTelKUK, nTelNUM, &m_poiHeaders );
		}
	}
	return &m_poiHeaders;
}

std::multimap<tstring,POI_HEADER> *	CUmSearchModule::SearchPoiName     ( u16 * pText, int nLen )
{
	m_poiHeaders.clear(); m_poiHeaders.swap( m_poiHeaders );
	
	u32 nChoCode	= tranChosung( pText, nLen );
	u32 nCkCmpHan	= checkCompletHangul( pText, nLen );

	int nCount = ( int ) m_helpers.size();
	for( int nSidoIdx = 0; nSidoIdx < nCount; ++ nSidoIdx )
	{
		m_helpers[nSidoIdx]->SearchPoiName( nSidoIdx, pText, nLen, nChoCode, nCkCmpHan, &m_poiHeaders );
	}

	return &m_poiHeaders;
}

std::multimap<tstring,POI_HEADER> *	CUmSearchModule::SearchPoiName     ( int nSidoIdx, u16 * pText, int nLen )
{
	m_poiHeaders.clear(); m_poiHeaders.swap( m_poiHeaders );
	
	u32 nChoCode	= tranChosung( pText, nLen );
	u32 nCkCmpHan	= checkCompletHangul( pText, nLen );

	//int nCount = ( int ) m_helpers.size();
	//for( int nSidoIdx = 0; nSidoIdx < nCount; ++ nSidoIdx )
	{
		m_helpers[nSidoIdx]->SearchPoiName( nSidoIdx, pText, nLen, nChoCode, nCkCmpHan, &m_poiHeaders );
	}

	return &m_poiHeaders;
}

std::multimap<tstring,POI_HEADER> *	CUmSearchModule::SearchPoiCateSido ( int nSidoIdx, u16 code1, u16 code2, u16 code3, bool clearData )
{
	if( clearData ) { m_poiHeaders.clear(); m_poiHeaders.swap( m_poiHeaders );}

	m_helpers[nSidoIdx]->SearchPoiCateSido( nSidoIdx, code1, code2, code3, &m_poiHeaders );
	return &m_poiHeaders;
}

std::multimap<tstring,POI_HEADER> *	CUmSearchModule::SearchPoiCateDong ( DONG_DATA * pDong, u16 code1, u16 code2, u16 code3, bool clearData )
{
	if( clearData ) { m_poiHeaders.clear(); m_poiHeaders.swap( m_poiHeaders ); }

	u32 DCode = m_helpers[pDong->nSidoIdx]->GetSidoData().idx * 100000000 + pDong->pSigunguF->code * 100000 + pDong->info.code;
	if( pDong->info.ldong_flag )
	{
		std::set<u32> * pHDongList = FindLHDCodeList( DCode );
		for( std::set<u32>::iterator it = (*pHDongList).begin(); it != (*pHDongList).end(); ++it )
		{
			m_helpers[pDong->nSidoIdx]->SearchPoiCateHDong( pDong->nSidoIdx, (*it), code1, code2, code3, &m_poiHeaders );
		}
	}
	else
	{
		m_helpers[pDong->nSidoIdx]->SearchPoiCateHDong( pDong->nSidoIdx, DCode, code1, code2, code3, &m_poiHeaders );
	}
	return &m_poiHeaders;
}

std::multimap<tstring,POI_HEADER> *	CUmSearchModule::SearchPoiCateMesh ( u32 nMeshCode, u16 code1, u16 code2, u16 code3, bool clearData )
{
	if( clearData ){ m_poiHeaders.clear(); m_poiHeaders.swap( m_poiHeaders ); }

	int nCount = ( int ) m_helpers.size();
	for( int nSidoIdx = 0; nSidoIdx < nCount; ++ nSidoIdx )
		m_helpers[nSidoIdx]->SearchPoiCateMesh( nSidoIdx, nMeshCode, code1, code2, code3, &m_poiHeaders );
	return &m_poiHeaders;
}

POI_BODY *							CUmSearchModule::GetSearchPoiResult( POI_HEADER * poiHeader )
{
	return m_helpers[poiHeader->nSidoIdx]->GetSearchPoiResult( &poiHeader->info, &m_poiBody );
}

std::set<u32> * CUmSearchModule::FindLHDCodeList( u32 LDCode )
{
	m_stLHCodeResult.clear(); 
	m_stLHCodeResult.swap( m_stLHCodeResult );

	if( m_fpLHCode )
	{
		if( searchu32KeyValue( m_fpLHCode, LDCode, m_nLHCodeIdxOfs, 3 * sizeof( u32 ), m_nLHCodeIdxCnt ))
		{
			u32 HDCodeOfs, HDCodeCnt, HDCode;
			fread( &HDCodeOfs, sizeof( u32 ), 1, m_fpLHCode );
			fread( &HDCodeCnt, sizeof( u32 ), 1, m_fpLHCode );

			fseek( m_fpLHCode, HDCodeOfs, SEEK_SET );

			for( u32 i = 0; i < HDCodeCnt; ++i )
			{
				fread( &HDCode, sizeof( u32 ), 1, m_fpLHCode );
				m_stLHCodeResult.insert( HDCode );
			}
		}
	}

	return &m_stLHCodeResult;
}
/*
std::set<u32> * CUmSearchModule::FindHLDCodeList( u32 HDCode )
{
	std::map<u32,std::set<u32> >::iterator find_HDCode = m_HLDongList.find( HDCode );
	if( find_HDCode == m_HLDongList.end()) return NULL;
	return &(*find_HDCode).second;
}
*/

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
CUmSearchHelper::CUmSearchHelper( FILE * _fp, CTextMng * _Text_mng )
: fp( _fp )
, m_pTextMng( _Text_mng )
{
	if( fp )
	{
		fseek( fp, -1 * ( long ) sizeof( SEARCH_FILE_HEADER ) , SEEK_END );
		fread( &m_file_header, sizeof( SEARCH_FILE_HEADER ), 1, fp );
		
		fseek( fp, m_file_header.addr_end_ofs - sizeof( ADDR_DATA_HEADER ), SEEK_SET );
		fread( &m_addr_header, sizeof( ADDR_DATA_HEADER ), 1, fp );

		fseek( fp, m_file_header.poi_end_ofs - sizeof( POI_DATA_HEADER ), SEEK_SET );
		fread( &m_poi_header, sizeof( POI_DATA_HEADER ), 1, fp );
		
		fseek( fp, 0, SEEK_SET );
		fread( &m_sidoData, sizeof( SIDO_DATA ), 1, fp );

		LoadSigunguFDatas();
		LoadLDongOffset();
	}
}

CUmSearchHelper::~CUmSearchHelper()
{
	Release();

	if( fp ) fclose( fp );
	fp = NULL;
}
void CUmSearchHelper::Release()
{
	m_sigunguDatas.clear(); m_sigunguDatas.swap( m_sigunguDatas );
	m_dongDatas.clear();	m_dongDatas.swap( m_dongDatas );
	m_dongExDatas.clear();	m_dongExDatas.swap( m_dongExDatas );
	m_roadDatas.clear();	m_roadDatas.swap( m_roadDatas );
	m_bunjiDatas.clear();	m_bunjiDatas.swap( m_bunjiDatas );
	m_hoDatas.clear();		m_hoDatas.swap( m_hoDatas );
	m_newHoDatas.clear();	m_newHoDatas.swap( m_newHoDatas );
	m_LDCodeOffset.clear();		m_LDCodeOffset.swap( m_LDCodeOffset );
}


SIGUNGU_DATA_F *			CUmSearchHelper::GetSigunguData( u32 sigungu_ofs )
{
	for( std::vector<SIGUNGU_DATA_F>::iterator it = m_sigunguDataFs.begin(); it != m_sigunguDataFs.end(); ++it )
	{
		if( (*it).offset == sigungu_ofs ) return &(*it);
	}
	
	return NULL;
}

void CUmSearchHelper::LoadSigunguFDatas()
{
	if( !m_sigunguDataFs.empty()) return;
	m_sigunguDataFs.clear();

	u32 nTemp, nTemp2;

	SIGUNGU_DATA_F	sd_f;
	sd_f.offset = sizeof( SIDO_DATA );

	do
	{
		fseek( fp, sd_f.offset, SEEK_SET );
		
		nTemp = sd_f.offset;
		fread( &sd_f, sizeof( SIGUNGU_DATA_F ), 1, fp );
		
		nTemp2 = sd_f.offset;
		sd_f.offset = nTemp;
		m_sigunguDataFs.push_back( sd_f );

		sd_f.offset = nTemp2;
	}
	while( sd_f.offset != 0 );
}

void CUmSearchHelper::LoadLDongOffset()
{
	fseek( fp, m_addr_header.addr_dong_ofs, SEEK_SET );
	
	u32 nOffset, nDCode, nLen = m_addr_header.addr_dong_cnt;
	for( u32 i = 0; i < nLen; i++ )
	{
		fread( &nOffset,	sizeof( u32 ), 1, fp );
		fread( &nDCode,		sizeof( u32 ), 1, fp );
		
		m_LDCodeOffset.push_back( std::pair<u32,u32>( nOffset, nDCode ));
	}
}

std::vector<SIGUNGU_DATA> * CUmSearchHelper::GetSigunguData()
{
	if( !m_sigunguDatas.empty()) return &m_sigunguDatas;

	LoadSigunguFDatas();

	int nLen;
	SIGUNGU_DATA sd;
	for( std::vector<SIGUNGU_DATA_F>::iterator it = m_sigunguDataFs.begin(); it != m_sigunguDataFs.end(); ++it )
	{
		sd.pInfo = &(*it);

		u16cpy( sd.name, m_pTextMng->getSigunguName( m_sidoData.idx * 1000 + sd.pInfo->code, &nLen ));
		
		sd.name_len = (u8)nLen;
		m_sigunguDatas.push_back( sd );
	}

	return &m_sigunguDatas;
}

std::vector<DONG_DATA> *	CUmSearchHelper::GetDongData( int nSidoIdx, u32 sigungu_ofs )
{
	fseek( fp, sigungu_ofs+sizeof( SIGUNGU_DATA_F ), SEEK_SET );

	DONG_DATA_F dd_f;
	DONG_DATA	dd;
	dd.pSigunguF	= FindSigunguOfOffset( sigungu_ofs );
	dd.nSidoIdx		= nSidoIdx;

	u32 next_offset = ( u32 ) ftell( fp );

	m_dongDatas.clear();

	int nLen;
	while( true )
	{
		fread( &dd_f, sizeof( DONG_DATA_F ), 1, fp );
		
		memcpy( &dd.info, &dd_f, sizeof( DONG_DATA_F ));
		dd.info.offset = next_offset;

		u16cpy( dd.name, m_pTextMng->getDongName( m_sidoData.idx * 100000000 + dd.pSigunguF->code * 100000 + dd.info.code, &nLen ));
		dd.name_len = nLen;
		m_dongDatas.push_back( dd );
				
		next_offset = dd_f.offset;
		if( next_offset == 0 ) break;

		fseek( fp, next_offset, SEEK_SET );
	}

	return &m_dongDatas;
}

std::vector<ROAD_DATA> *	CUmSearchHelper::GetRoadData( int nSidoIdx, u32 sigungu_ofs )
{
	ROAD_DATA	rd;
	rd.nSidoIdx		= nSidoIdx;
	rd.pSigunguF	= GetSigunguData( sigungu_ofs );

	m_roadDatas.clear();
	if( rd.pSigunguF->road_ofs == 0 ) return &m_roadDatas;

	fseek( fp, rd.pSigunguF->road_ofs, SEEK_SET );
	u32 nTemp, next_offset = ( u32 ) ftell( fp );
	
	while( true )
	{
		fread( &rd.info, sizeof( ROAD_DATA_F ), 1, fp );
		nTemp = rd.info.offset;

		rd.info.offset = next_offset;

		int nLen;
		const u16 * pRoadName = m_pTextMng->getRoadName( m_sidoData.idx * 100000000 + rd.pSigunguF->code * 100000 + rd.info.code, &nLen );
		if( pRoadName != NULL )
		{
			u16cpy( rd.name, pRoadName );
			rd.name_len = nLen;

			m_roadDatas.push_back( rd );
		}
				
		next_offset = nTemp;
		if( next_offset == 0 ) break;

		fseek( fp, next_offset, SEEK_SET );
	}

	return &m_roadDatas;
}

std::vector<DONG_DATA>::iterator FindDongDataOffset( std::vector<DONG_DATA> * pDongData, u32 offset )
{
	int nSize = ( int ) pDongData->size() / 2;

	std::vector<DONG_DATA>::iterator left	= pDongData->begin();
	std::vector<DONG_DATA>::iterator right	= pDongData->end() - 1;
	std::vector<DONG_DATA>::iterator mid	= left + nSize;
	std::vector<DONG_DATA>::iterator end	= right;
	//while( left < right )
	while( mid != end )
	{
		if( (*mid).info.offset == offset ) return mid;
		else if( (*mid).info.offset < offset ) left = mid + 1;
		else right = mid - 1;

		nSize /= 2;
		mid = left + nSize;
	}
	if( left == right )
	{
		if( (*left).info.offset == offset ) return left;
	}

	return pDongData->end();
}

std::vector<ROAD_DATA>::iterator FindRoadDataOffset( std::vector<ROAD_DATA> * pRoadData, u32 offset )
{
	int nSize = ( int ) pRoadData->size() / 2;

	std::vector<ROAD_DATA>::iterator left	= pRoadData->begin();
	std::vector<ROAD_DATA>::iterator right	= pRoadData->end() - 1;
	std::vector<ROAD_DATA>::iterator mid	= left + nSize;
	
	while( left < right )
	{
		if( (*mid).info.offset == offset ) return mid;
		else if( (*mid).info.offset < offset ) left = mid + 1;
		else right = mid - 1;

		nSize /= 2;
		mid = left + nSize;
	}
	if( left == right )
	{
		if( (*left).info.offset == offset ) return left;
	}

	return pRoadData->end();
}

std::vector<BUNJI_DATA> *	CUmSearchHelper::GetBunjiData( void * pDong, bool bSan, bool bFromHDong )
{	
	u16	bunji, ho_cnt;
	bool	san_bunji;

	DONG_DATA * pDongData = ( DONG_DATA * ) pDong;
	fseek( fp, pDongData->info.offset + sizeof( DONG_DATA_F ), SEEK_SET );
			
	if( !bFromHDong ) m_bunjiDatas.clear();

	int bunji_cnt = pDongData->info.bunji_cnt;
	// *>-- 법정동 이면서, 행정동이여서 하위 법정동을 가지는 동코드가 존재한다.
	// ex) 제주/제주시/애월읍
	// 따라서 행정동으로 부터 들어온 코드는 반드시 법정동이어야 한다.
	if( pDongData->info.ldong_flag == 1 || bFromHDong )
	{
		BUNJI_DATA bunji_data;
		bunji_data.offset = ftell( fp );
		for( int i = 0; i < bunji_cnt; ++i )
		{
			fread( &bunji,	sizeof( u16 ),  1, fp );
			fread( &ho_cnt,	sizeof( u16 ),  1, fp );

			bunji_data.ho_cnt	= ho_cnt;

			san_bunji = ( bunji & 0x8000 ) != 0 ? true : false;
			if( bSan == san_bunji )
			{
				bunji_data.bunji	= bSan ? bunji & 0x7FFF : bunji;
				bunji_data.pDong	= pDong;
				m_bunjiDatas.push_back( bunji_data );
			}

			bunji_data.offset += ( sizeof( HO_DATA ) * bunji_data.ho_cnt + sizeof( u16 ) + sizeof( u16 ));
			fseek( fp, bunji_data.offset, SEEK_SET );
		}	
	}
	else
	{
		u32 offset, offset_bak;
		for( int i = 0; i < bunji_cnt; ++i )
		{
			fread( &offset, sizeof( u32 ), 1, fp );
					
			if( offset != 0 )
			{
				offset_bak = ftell( fp );
				GetBunjiData(( void *) GetDongDataOfOffset( pDongData->nSidoIdx, pDongData->pSigunguF, offset ), bSan, true );
				fseek( fp, offset_bak, SEEK_SET );
			}
		}
	}

	return &m_bunjiDatas;
}

std::vector<BUNJI_DATA> *	CUmSearchHelper::GetRoadBunjiData( void * pRoad )
{
	u16	bunji, ho_cnt;
	
	ROAD_DATA * pRoadData = ( ROAD_DATA * ) pRoad;
	fseek( fp, pRoadData->info.offset + sizeof( ROAD_DATA_F ), SEEK_SET );
			
	m_bunjiDatas.clear();

	int bunji_cnt = pRoadData->info.bunji_cnt;
	
	BUNJI_DATA bunji_data;
	bunji_data.offset = ftell( fp );
	for( int i = 0; i < bunji_cnt; ++i )
	{
		fread( &bunji,	sizeof( u16 ),  1, fp );
		fread( &ho_cnt,	sizeof( u16 ),  1, fp );

		bunji_data.ho_cnt	= ho_cnt;

		bunji_data.bunji	= bunji;
		bunji_data.pDong	= ( void * ) pRoad;
		m_bunjiDatas.push_back( bunji_data );
		
		bunji_data.offset += ( sizeof( NEW_HO_DATA ) * bunji_data.ho_cnt + sizeof( u16 ) + sizeof( u16 ));
		fseek( fp, bunji_data.offset, SEEK_SET );
	}

	return &m_bunjiDatas;
}

std::vector<HO_DATA> *		CUmSearchHelper::GetHoData( u32 bunji_ofs )
{
	u16	bunji, ho_cnt;

	fseek( fp, bunji_ofs, SEEK_SET );
	fread( &bunji,	sizeof( u16 ),  1, fp );
	fread( &ho_cnt,	sizeof( u16 ),  1, fp );

	m_hoDatas.clear();

	HO_DATA hoData;
	for( int i = 0; i < ho_cnt; ++i )
	{
		fread( &hoData, sizeof( HO_DATA ), 1, fp );
		m_hoDatas.push_back( hoData );
	}

	return &m_hoDatas;
}

std::vector<NEW_HO_DATA> *		CUmSearchHelper::GetNewHoData( u32 bunji_ofs )
{
	u16	bunji, ho_cnt;

	fseek( fp, bunji_ofs, SEEK_SET );
	fread( &bunji,	sizeof( u16 ),  1, fp );
	fread( &ho_cnt,	sizeof( u16 ),  1, fp );

	m_newHoDatas.clear();

	NEW_HO_DATA hoData;
	for( int i = 0; i < ho_cnt; ++i )
	{
		fread( &hoData, sizeof( NEW_HO_DATA ), 1, fp );
		m_newHoDatas.push_back( hoData );
	}

	return &m_newHoDatas;
}

DONG_DATA *	CUmSearchHelper::GetHo( u32 ho_ofs, HO_DATA * pHo )
{
	fseek( fp, ho_ofs, SEEK_SET );
	fread( pHo, sizeof( HO_DATA ), 1, fp );
	
	if( !m_dongDatas.empty())
	{
		for( std::vector<DONG_DATA>::iterator it = m_dongDatas.begin(); it != m_dongDatas.end(); ++it )
		{
			if( (*it).info.offset > ho_ofs )
			{
				if( (*(it-1)).info.ldong_flag ) return &(*(it-1));
			}
		}

		return &m_dongDatas.back();
	}

	return NULL;
}

void CUmSearchHelper::GetHo	( u32 ho_ofs, HO_DATA * pHo, DONG_DATA_F * ddf )
{
	fseek( fp, ho_ofs, SEEK_SET );
	fread( pHo, sizeof( HO_DATA ), 1, fp );

	FindDongOfOffset( ho_ofs, ddf );
}

void CUmSearchHelper::GetHoPos( HO_DATA * pHo, u32 * pX, u32 * pY )
{
	if( pHo->x_quota == 0x1F && pHo->x == 0xFFFF )
	{
		int nPosIdx = pHo->y_quota * _16BIT_VALUE_ + pHo->y;
		GetPosEx( nPosIdx, pX, pY );
	}
	else
	{
		*pX = pHo->x_quota * _16BIT_VALUE_ + pHo->x;
		*pY = pHo->y_quota * _16BIT_VALUE_ + pHo->y;
	}	
}

void CUmSearchHelper::GetPosEx( int nIndex, u32 * pX, u32 * pY )
{
	fseek( fp, m_addr_header.addr_pos_ext_ofs + sizeof( u32 ) * ( nIndex * 2 ), SEEK_SET );
	fread( pX, sizeof( u32 ), 1, fp );
	fread( pY, sizeof( u32 ), 1, fp );
}

void CUmSearchHelper::SearchPoiCate( int nSidoIdx, u32 end_ofs, u16 c1, u16 c2, u16 c3, 
				   std::multimap<tstring,POI_HEADER> * pPoiHeaders )
{
	std::pair<int,u32> cate_info = searchCateInfo( fp, end_ofs, c1, c2, c3 );
		
	int nCnt = cate_info.first;
	if( nCnt > 0 )
	{
		u32 * pOffsets = new u32[ nCnt ];
			
		fseek( fp, cate_info.second, SEEK_SET );
		fread( pOffsets, sizeof( u32 ), nCnt, fp );

		POI_HEADER ph;
		ph.nSidoIdx = nSidoIdx;
		for( int i = 0; i < nCnt; ++i )
		{
			fseek( fp, pOffsets[i], SEEK_SET );
			fread( &ph.info, sizeof( POI_HEADER_F ), 1, fp );

			if( ph.info.name_ofs & 0x1F )
			{
				pPoiHeaders->insert( std::pair<tstring,POI_HEADER>( 
					tstring( m_pTextMng->getTextOfs( ph.info.name_ofs )), ph ));
			}
		}

		delete [] pOffsets;
	}
}

bool findChosungOffsetPOI( FILE * fp, u32 cho_idx_ofs, u32 cho_idx_cnt, u32 choCode, u32 * offset, int * cnt )
{
	if( searchu32KeyValue( fp, choCode, cho_idx_ofs, (u32)(sizeof(u32)*3), cho_idx_cnt ))
	{
		fread( offset, sizeof( u32 ), 1, fp );
	
		u32 nTemp;

		fread( &nTemp, sizeof( u32 ), 1, fp );
		* cnt = ( int ) nTemp;

		return true;
	}
	return false;
}

bool findChosungOffsetDong( FILE * fp, u32 cho_idx_ofs, u32 cho_idx_cnt, u32 choCode, u32 * offset, int * cnt )
{
	if( searchu32KeyValue( fp, choCode, cho_idx_ofs, (u32)(sizeof(u32)*2), cho_idx_cnt ))
	{
		fread( offset, sizeof( u32 ), 1, fp );
		
		u32 nTemp;
		fread( &nTemp, sizeof( u32 ), 1, fp );
		fread( &nTemp, sizeof( u32 ), 1, fp );
		
		*cnt = ( nTemp - *offset ) / sizeof( u32 );

		return true;
	}
	return false;
}

int	CUmSearchHelper::SearchTelNumber
	( int nSidoIdx, u8 telDDD, u16 telKUK, u16 telNUM, std::multimap<tstring,POI_HEADER> * pPoiHeaders )
{
	// find telDDD
	if( !searchu8KeyValue( 
		fp, telDDD, 
		m_poi_header.tel_poi_idx_ofs, 
		(u32)(sizeof(u32)*2+sizeof(u8)), 
		m_poi_header.tel_poi_idx_cnt )) return 0;

	u32 begin_ofs, end_ofs;

	fread( &begin_ofs,	sizeof( u32 ), 1, fp );
	fread( &end_ofs,	sizeof( u32 ), 1, fp );

	fseek( fp, end_ofs - ( sizeof( u32 ) * 2 ), SEEK_SET );

	u32 offset, count;

	fread( &count,	sizeof( u32 ), 1, fp );
	fread( &offset,	sizeof( u32 ), 1, fp );

	// find telKUK
	if( !searchu16KeyValue( fp, telKUK, offset, (u32)(sizeof(u32)*2+sizeof(u16)), count )) 
		return 0;

	fread( &count,	sizeof( u32 ), 1, fp );
	fread( &offset,	sizeof( u32 ), 1, fp );

	// find telNUM
	if( !searchu16KeyValue( fp, telNUM, offset, (u32)(sizeof(u32)+sizeof(u16)), count )) 
		return 0;

	fread( &offset,	sizeof( u32 ), 1, fp );

	POI_HEADER ph;
	ph.nSidoIdx = nSidoIdx;

	fseek( fp, offset, SEEK_SET );
	fread( &ph.info, sizeof( POI_HEADER_F ), 1, fp );

	pPoiHeaders->insert( std::pair<tstring,POI_HEADER>( tstring( m_pTextMng->getTextOfs( ph.info.name_ofs ) ), ph ));
	
	return 1;
}

int CUmSearchHelper::SearchTelNumber( int nSidoIdx, u16 telKUK, u16 telNUM, std::multimap<tstring,POI_HEADER> * pPoiHeaders )
{
	u8  telDDD[32];
	u32 begin_ofs[32], end_ofs[32];

	fseek( fp, m_poi_header.tel_poi_idx_ofs, SEEK_SET );
	
	int nLen = m_poi_header.tel_poi_idx_cnt;
	for( int i = 0; i < nLen; ++i )
	{
		fread( &telDDD[i],		sizeof( u8 ),  1, fp );
		fread( &begin_ofs[i],	sizeof( u32 ), 1, fp );
		fread( &end_ofs[i],		sizeof( u32 ), 1, fp );
	}

	POI_HEADER ph;
	ph.nSidoIdx = nSidoIdx;

	u32 offset, count;
	for( int i = 0; i < nLen; ++i )
	{
		fseek( fp, end_ofs[i] - ( sizeof( u32 ) * 2 ), SEEK_SET );

		fread( &count,	sizeof( u32 ), 1, fp );
		fread( &offset,	sizeof( u32 ), 1, fp );

		// find telKUK
		if( !searchu16KeyValue( fp, telKUK, offset, (u32)(sizeof(u32)*2+sizeof(u16)), count )) 
			continue;

		fread( &count,	sizeof( u32 ), 1, fp );
		fread( &offset,	sizeof( u32 ), 1, fp );

		// find telNUM
		if( !searchu16KeyValue( fp, telNUM, offset, (u32)(sizeof(u32)+sizeof(u16)), count )) 
			continue;

		fread( &offset,	sizeof( u32 ), 1, fp );

		fseek( fp, offset, SEEK_SET );
		fread( &ph.info, sizeof( POI_HEADER_F ), 1, fp );

		pPoiHeaders->insert( std::pair<tstring,POI_HEADER>( tstring( m_pTextMng->getTextOfs( ph.info.name_ofs ) ), ph ));
	}
	
	return 1;
}

int CUmSearchHelper::SearchPoiName     
	( int nSidoIdx, u16 * pText, int nLen, u32 choCode, u32 ckCmpHan, std::multimap<tstring,POI_HEADER> * pPoiHeaders  )
{
	u32 choOfs;
	int choCnt = 0;

	if( findChosungOffsetPOI( fp, m_poi_header.poi_cho_idx_ofs, m_poi_header.poi_cho_idx_cnt, choCode, &choOfs, &choCnt ))
	{
#if 1
		fseek( fp, choOfs, SEEK_SET );
		
		bool bAddData = true;
		u32 checkHangulBit = ckCmpHan;

		u16 strName[ 36 ];

		POI_HEADER ph;
		ph.nSidoIdx = nSidoIdx;
		for( int i = 0; i < choCnt; ++i )
		{
			fread( &ph.info, sizeof( POI_HEADER_F ), 1, fp );
			
			u16cpy( strName, m_pTextMng->getTextOfs( ph.info.name_ofs ));
			
			if( ckCmpHan != 0 || nLen > MAX_CHOSUNG_LEN )
			{
				bAddData = true;
				checkHangulBit = ckCmpHan;

				for( int nStrIdx = 0; nStrIdx < nLen; ++ nStrIdx )
				{
					if( nStrIdx > MAX_CHOSUNG_LEN - 1 )
					{
						if( isChosung( pText[nStrIdx] ))
						{
							if( tranChosung( pText[nStrIdx] ) != tranChosung( strName[nStrIdx] )) { bAddData = false; break; }
						}
						else
						{
							if( pText[nStrIdx] != strName[nStrIdx] ) { bAddData = false; break; }
						}
					}
					else
					{
						if( checkHangulBit & 0x01 && pText[nStrIdx] != strName[nStrIdx] ) { bAddData = false; break; }
						checkHangulBit = checkHangulBit >> 1;
					}
				}
			}
			
			if( bAddData ) 
				pPoiHeaders->insert( std::pair<tstring,POI_HEADER>( tstring( strName ), ph ));
		}

#else
		u32 * offsets = new u32[ choCnt ];

		fseek( fp, choOfs, SEEK_SET );
		fread( offsets, sizeof( u32 ), choCnt, fp );

		bool bAddData = true;
		u32 checkHangulBit = ckCmpHan;

		u16 strName[ 36 ];

		POI_HEADER ph;
		ph.nSidoIdx = nSidoIdx;
		for( int i = 0; i < choCnt; ++i )
		{
			fseek( fp, offsets[i], SEEK_SET );
			fread( &ph.info, sizeof( POI_HEADER_F ), 1, fp );
			
			u16cpy( strName, m_pTextMng->getTextOfs( ph.info.name_ofs ));
			
			if( ckCmpHan != 0 || nLen > MAX_CHOSUNG_LEN )
			{
				bAddData = true;
				checkHangulBit = ckCmpHan;

				for( int nStrIdx = 0; nStrIdx < nLen; ++ nStrIdx )
				{
					if( nStrIdx > MAX_CHOSUNG_LEN - 1 )
					{
						if( isChosung( pText[nStrIdx] ))
						{
							if( tranChosung( pText[nStrIdx] ) != tranChosung( strName[nStrIdx] )) { bAddData = false; break; }
						}
						else
						{
							if( pText[nStrIdx] != strName[nStrIdx] ) { bAddData = false; break; }
						}
					}
					else
					{
						if( checkHangulBit & 0x01 && pText[nStrIdx] != strName[nStrIdx] ) { bAddData = false; break; }
						checkHangulBit = checkHangulBit >> 1;
					}
				}
			}
			
			if( bAddData ) 
				pPoiHeaders->insert( std::pair<tstring,POI_HEADER>( tstring( strName ), ph ));
		}

		delete [] offsets;
#endif
	}
	return choCnt;
}

void CUmSearchHelper::SearchPoiCateSido  
	( int nSidoIdx, u16 code1, u16 code2, u16 code3, std::multimap<tstring,POI_HEADER> * pPoiHeaders  )
{
	SearchPoiCate( nSidoIdx, m_poi_header.cate_poi_ofs, code1, code2, code3, pPoiHeaders );
}

void CUmSearchHelper::SearchPoiCateHDong 
	( int nSidoIdx, u32 nHCode, u16 code1, u16 code2, u16 code3, std::multimap<tstring,POI_HEADER> * pPoiHeaders )
{
	fseek( fp, m_poi_header.dong_cate_poi_idx_ofs, SEEK_SET );
	
	if( searchu32KeyValue( fp, nHCode, ftell( fp ), (u32)( sizeof( u32 ) * 3 ), m_poi_header.dong_cate_poi_idx_cnt ))
	{
		u32 begin_ofs, end_ofs;

		fread( &begin_ofs,	sizeof( u32 ), 1, fp );
		fread( &end_ofs,	sizeof( u32 ), 1, fp );

		SearchPoiCate( nSidoIdx, end_ofs, code1, code2, code3, pPoiHeaders );
	}
}

void CUmSearchHelper::SearchPoiCateMesh  
	( int nSidoIdx, u32 nMeshCode, u16 code1, u16 code2, u16 code3, std::multimap<tstring,POI_HEADER> * pPoiHeaders  )
{
	u16 mesh_x = ( u16 )( nMeshCode & 0xFFFF );
	u16 mesh_y = ( u16 )(( nMeshCode >> 16 ) & 0xFFFF );
	
	if( mesh_x < m_sidoData.mesh_x_min ||m_sidoData.mesh_x_max < mesh_x ) return;
	if( mesh_y < m_sidoData.mesh_y_min ||m_sidoData.mesh_y_max < mesh_y ) return;

	fseek( fp, m_poi_header.mesh_poi_idx_cnt, SEEK_SET );
	
	if( searchu32KeyValue( fp, nMeshCode, ftell( fp ), (u32)( sizeof( u32 ) * 3 ), m_poi_header.mesh_poi_idx_cnt ))
	{
		u32 begin_ofs, end_ofs;

		fread( &begin_ofs,	sizeof( u32 ), 1, fp );
		fread( &end_ofs,	sizeof( u32 ), 1, fp );

		SearchPoiCate( nSidoIdx, end_ofs, code1, code2, code3, pPoiHeaders );
	}
}

POI_BODY *	CUmSearchHelper::GetSearchPoiResult( POI_HEADER_F * poiHeader, POI_BODY * poiBody )
{
	u8  nu8;
	u32 nu32, ho_ofs;

	memset( poiBody, 0, sizeof( POI_BODY ));
	fseek( fp, poiHeader->body_ofs, SEEK_SET );
	if( poiHeader->use_ho_ofs )
	{
		POI_BODY_USE_HO_F body;
		fread( &body, sizeof( POI_BODY_USE_HO_F ), 1, fp );

		poiBody->info.branch	= body.branch;
		poiBody->info.bunji		= body.bunji;
		poiBody->info.hDCode	= FindDongOfOffset( body.ho_ofs, NULL );
		poiBody->info.parking	= body.parking;
		poiBody->info.san		= body.san;
		poiBody->info.tel_cnt	= body.tel_cnt;

		ho_ofs = body.ho_ofs;
	}
	else
	{
		fread( &poiBody->info, sizeof( POI_BODY_F ), 1, fp );
	}
		
	if( poiBody->info.branch != 0 ) 
	{
		fread( &nu32, sizeof( u32 ), 1, fp );
		u16cpy( poiBody->branchName, m_pTextMng->getTextOfs( nu32 ));
	}

	char telDDD[5], telKUK[5], telNUM[5];
	int nLen = (int)poiBody->info.tel_cnt;
	for( int i = 0; i < nLen; ++i )
	{
		fread( &nu8,  sizeof( u8 ),  1, fp );
		fread( &nu32, sizeof( u32 ), 1, fp );
			
		strcpy( telDDD, tranTelNumber2((u16)nu8));
		strcpy( telKUK, tranTelNumber2((u16)( nu32 >> 16 ) & 0xFFFF ));
		strcpy( telNUM, tranTelNumber2((u16)( nu32 & 0xFFFF ), false ));

		sprintf( poiBody->tel[i], "0%s) %s-%s", telDDD, telKUK, telNUM );
	}
	
	if( poiHeader->pos_cnt != 0 )	// 1개만 읽어 온다.
	{
		fread( &poiBody->x_ex, sizeof( u32 ), 1, fp );
		fread( &poiBody->y_ex, sizeof( u32 ), 1, fp );
	}

	if( poiHeader->use_ho_ofs )
	{
		HO_DATA hoData;
		DONG_DATA_F dongF;
		GetHo( ho_ofs, &hoData, &dongF );

		poiBody->info.ho = (u16)( hoData.ho_quota * _8BIT_VALUE_ + hoData.ho );

		u32 x;
		GetHoPos( &hoData, &x, (u32*)&poiBody->info.y );
		poiBody->info.x = x + dongF.min_x;
		poiBody->info.y += dongF.min_y;
	}
	return poiBody;
}

u32 CUmSearchHelper::FindDongOfOffset( u32 ofs, DONG_DATA_F * dongDataF )
{
#if 1
	int nSize = ( int ) m_LDCodeOffset.size() / 2;

	std::vector<std::pair<u32,u32> >::iterator left		= m_LDCodeOffset.begin();
	std::vector<std::pair<u32,u32> >::iterator right	= m_LDCodeOffset.end() - 1;
	std::vector<std::pair<u32,u32> >::iterator mid		= left + nSize;
	std::vector<std::pair<u32,u32> >::iterator end		= right;
	
	while( left < right )
	{
		if( ofs > (*mid).first ) left = mid;
		else right = mid;

		nSize /= 2;
		mid = left + nSize;

		if( mid == left ) break;
	}
	
	if( dongDataF != NULL )
	{
		fseek( fp, (*left).first, SEEK_SET );
		fread( dongDataF, sizeof( DONG_DATA_F ), 1, fp );
	}

	return (*left).second;
	
#else
	u32 left	= 0;
	u32 right	= m_addr_header.addr_dong_cnt;
	u32 mid	= ( left + right ) / 2;

	u32 _offset		= m_addr_header.addr_dong_ofs;
	u32 _record_size	= sizeof( u32 ) * 2;

	u32 nKeyValue, nDCode;
	while ( left < right )
	{
		fseek( fp, _offset + mid * _record_size, SEEK_SET );
		fread( &nKeyValue, sizeof( u32 ), 1, fp );
		
		if( ofs > nKeyValue ) left = mid + 1;
		else right = mid - 1;

		if( left + 1 == right ) break;

		mid = ( left + right ) / 2;
	}

	fseek( fp, _offset + left * _record_size, SEEK_SET );
	fread( &nKeyValue,	sizeof( u32 ), 1, fp );
	fread( &nDCode,		sizeof( u32 ), 1, fp );
		
	fseek( fp, nKeyValue, SEEK_SET );
	fread( dongDataF, sizeof( DONG_DATA_F ), 1, fp );

	return nDCode;
#endif
}

SIGUNGU_DATA_F * CUmSearchHelper::FindSigunguOfOffset( u32 ofs, bool bRoad )
{
	for( std::vector<SIGUNGU_DATA_F>::iterator it = m_sigunguDataFs.begin(); it != m_sigunguDataFs.end(); ++it )
	{
		if( bRoad )
		{
			if( (*it).road_ofs > ofs ) return &(*(--it));
		}
		else
		{
			if( (*it).offset > ofs ) return &(*(--it));
		}
	}

	return &m_sigunguDataFs.back();
}

DONG_DATA * 	CUmSearchHelper::GetDongDataOfOffset( int nSidoIdx, SIGUNGU_DATA_F * pSigunguF, u32 dong_ofs )
{
	for( std::vector<DONG_DATA>::iterator it = m_dongDatas.begin(); it != m_dongDatas.end(); ++it )
	{
		if( (*it).info.offset == dong_ofs ) return &(*it);
	}

	for( std::vector<DONG_DATA>::iterator it = m_dongExDatas.begin(); it != m_dongExDatas.end(); ++it )
	{
		if( (*it).info.offset == dong_ofs ) return &(*it);
	}

	DONG_DATA	dd;
	dd.nSidoIdx		= nSidoIdx;
	dd.pSigunguF	= pSigunguF;
	
	fseek( fp, dong_ofs, SEEK_SET );
	fread( &dd.info, sizeof( DONG_DATA_F ), 1, fp );
	
	dd.info.offset = dong_ofs;

	u16cpy( dd.name, m_pTextMng->getDongName( m_sidoData.idx * 100000000 + dd.pSigunguF->code * 100000 + dd.info.code ));
	m_dongExDatas.push_back( dd );

	return &m_dongExDatas.back();
}

int CUmSearchHelper::SearchDongName( int nSidoIdx, u16 * dongName, int nLen, u32 choCode, u32 ckCmpHan, std::vector<DONG_DATA> * pResult )
{
	u32 choOfs;
	int choCnt = 0;

	if( findChosungOffsetDong( fp, m_addr_header.addr_cho_idx_ofs, m_addr_header.addr_cho_idx_cnt, choCode, &choOfs, &choCnt ))
	{
		u32 * offsets = new u32[ choCnt ];

		fseek( fp, choOfs, SEEK_SET );
		fread( offsets, sizeof( u32 ), choCnt, fp );

		bool bAddData = true;
		u32 checkHangulBit = ckCmpHan;

		int nLen = 0;

		DONG_DATA dd;
		dd.nSidoIdx = nSidoIdx;
		for( int i = 0; i < choCnt; ++i )
		{
			fseek( fp, offsets[i], SEEK_SET );
			fread( &dd.info, sizeof( DONG_DATA_F ), 1, fp );
			dd.info.offset = offsets[i];

			dd.pSigunguF = FindSigunguOfOffset( offsets[i] );
			u16cpy( dd.name, m_pTextMng->getDongName( m_sidoData.idx * 100000000 + dd.pSigunguF->code * 100000 + dd.info.code, &nLen ));
			dd.name_len = nLen;

			if( ckCmpHan != 0 || nLen > MAX_CHOSUNG_LEN )
			{
				bAddData = true;
				checkHangulBit = ckCmpHan;

				for( int nStrIdx = 0; nStrIdx < nLen; ++ nStrIdx )
				{
					if( nStrIdx > MAX_CHOSUNG_LEN - 1 )
					{
						if( isChosung( dongName[nStrIdx] ))
						{
							if( tranChosung( dongName[nStrIdx] ) != tranChosung( dd.name[nStrIdx] )) { bAddData = false; break; }
						}
						else
						{
							if( dongName[nStrIdx] != dd.name[nStrIdx] ) { bAddData = false; break; }
						}
					}
					else
					{
						if( checkHangulBit & 0x01 && dongName[nStrIdx] != dd.name[nStrIdx] ) { bAddData = false; break; }
						checkHangulBit = checkHangulBit >> 1;
					}
				}
			}
			
			if( bAddData ) pResult->push_back( dd );
		}

		delete [] offsets;
	}

	return choCnt;
}

int CUmSearchHelper::SearchRoadName( int nSidoIdx, u16 * roadName, int nLen, u32 choCode, u32 ckCmpHan, std::vector<ROAD_DATA> * pResult )
{
	u32 choOfs;
	int choCnt = 0;
	
	if( findChosungOffsetDong( fp, m_addr_header.road_cho_idx_ofs, m_addr_header.road_cho_idx_cnt, choCode, &choOfs, &choCnt ))
	{
		u32 * offsets = new u32[ choCnt ];

		fseek( fp, choOfs, SEEK_SET );
		fread( offsets, sizeof( u32 ), choCnt, fp );

		bool bAddData = true;
		u32 checkHangulBit = ckCmpHan;

		int nLen = 0;
		ROAD_DATA rd;
		rd.nSidoIdx = nSidoIdx;
		for( int i = 0; i < choCnt; ++i )
		{
			fseek( fp, offsets[i], SEEK_SET );
			fread( &rd.info, sizeof( ROAD_DATA_F ), 1, fp );
			rd.info.offset = offsets[i];

			rd.pSigunguF = FindSigunguOfOffset( offsets[i], true );
			const u16 * pRoadName = m_pTextMng->getRoadName( m_sidoData.idx * 100000000 + rd.pSigunguF->code * 100000 + rd.info.code, &nLen );
			
			if( pRoadName == NULL ) continue;
			
			u16cpy( rd.name, pRoadName );
			rd.name_len = nLen;

			if( ckCmpHan != 0 || nLen > MAX_CHOSUNG_LEN )
			{
				bAddData = true;
				checkHangulBit = ckCmpHan;

				for( int nStrIdx = 0; nStrIdx < nLen; ++ nStrIdx )
				{
					if( nStrIdx > MAX_CHOSUNG_LEN - 1 )
					{
						if( isChosung( roadName[nStrIdx] ))
						{
							if( tranChosung( roadName[nStrIdx] ) != tranChosung( rd.name[nStrIdx] )) { bAddData = false; break; }
						}
						else
						{
							if( roadName[nStrIdx] != rd.name[nStrIdx] ) { bAddData = false; break; }
						}
					}
					else
					{
						if( checkHangulBit & 0x01 && roadName[nStrIdx] != rd.name[nStrIdx] ) { bAddData = false; break; }
						checkHangulBit = checkHangulBit >> 1;
					}
				}
			}

			if( bAddData ) pResult->push_back( rd );
		}

		delete [] offsets;
	}

	return choCnt;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
u16 *	Trim_Left ( u16 * str )
{
	u16 *			ret;

	ret		= str;
	while( *ret == (u16)L' ')	ret++;
	return	ret;
}

u16 * Trim_Right	( u16 * str )
{
	u16 * ret = str;
	int nLen = ( int ) u16len( str ) - 1;

	while( *(ret+nLen) == (u16)L' ' || *(ret+nLen) == (u16)L'\r' || *(ret+nLen) == (u16)L'\n' )	nLen--;
	
	ret[nLen+1] = (u16)L'\0';
	return	ret;
}
u16 * Trim		( u16 * str )
{
	return Trim_Left( Trim_Right( str ));
}
int u16Tokens	(u16TokenValue	Tokens, u16 * str, u16 ch )
{
	int			i;
	int			n;
	u16 *		line;

	memset(Tokens, 0, sizeof(u16TokenValue));

	n		= 0;
	line	= Trim_Left(str);
	if(*line)
	{
		for(i=0; *line; i++)
		{
			n++;
			Tokens[i]	= line;
			line		= u16chr( line, ch );
			if(!*line)	return n;
			*line		= 0;
			line++;
			line		= Trim_Left(line);
			
			if( !*line ) 
			{
				Tokens[i+1]	= line;
				n++;
			}
		}
	}

	return n;
}

u32	dongCode( u64 dCode )
{
	u32 nSido = ( u32 )( dCode / 100000000 );
	for( u32 i = 0; i < MAX_SEARCH_SIDO_CNT; ++i )
	{
		if( g_sido_info[i].sidoCode == nSido )
		{
			return ( u32 )( i * 100000000 + dCode % 100000000 );
		}
	}

	return 0;
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
bool	isChosung( u16 ch )
{
	if( chHangulChosungBegin <= ch && ch <= chHangulChosungEnd ) return true;
	return false;
}

u16	tranChosung( u16 ch )
{
	if( chHangulBegin <= ch && ch <= chHangulEnd )
	{		
		return ( u16 ) chHangulCho[( ch - chHangulBegin ) / HAN_JUNG_MUL_JONG ];
	}
	
	return ch;
}

u32	tranChosung( u16 * pText, size_t nLen )
{
	nLen = nLen > MAX_CHOSUNG_LEN ? MAX_CHOSUNG_LEN : nLen;

	u32 nRet = ( u32 ) nLen;
	u32 choIdx;
	for( size_t i = 0; i < nLen; ++i )
	{
		if( chHangulBegin <= pText[i] && pText[i] <= chHangulEnd )
		{		
			choIdx = 1 + ( pText[i] - chHangulBegin ) / HAN_JUNG_MUL_JONG;			
		}
		else if( chHangulChosungBegin <= pText[i] && pText[i] <= chHangulChosungEnd )
		{
			for( choIdx = 0; choIdx < MAX_HAN_CHO_CNT && pText[i] != chHangulCho[choIdx]; ++choIdx ) ;
			++choIdx;
		}
		else if( iswalpha ( pText[i] ))
		{
			choIdx = 1 + 19 + pText[i] - (u16)L'0';
		}
		else if( iswalpha( pText[i] ))
		{
			u16 ch = (u16)towupper( pText[i] );
			choIdx = 1 + 29 + ch - (u16)L'A';
		}
		else choIdx = 0;

		choIdx = choIdx << 8;
		for( size_t j = 0; j < i; ++j ) choIdx = choIdx << 6;
		nRet |= choIdx;
	}
	
	return nRet;
}

u16 *	tranChosung( u32 nChoCode )
{
	static u16 g_TranChosung[ 6 ];
	memset( g_TranChosung, 0, sizeof( g_TranChosung ));

	int nLen = ( int )( nChoCode & 0xFF );
	int nIndex = 0;
	int nChoIndex = -1;

	nChoCode = nChoCode >> 8;
	for( nIndex = 0; nIndex < nLen; ++nIndex )
	{
		nChoIndex = ( int ) ( nChoCode & 0x3F ) - 1;
		
		if( nChoIndex < 0 ) g_TranChosung[nIndex] = (u16)L' ';
		else				g_TranChosung[nIndex] = chHangulCho[nChoIndex];
		
		nChoCode = nChoCode >> 6;
	}

	g_TranChosung[nIndex] = (u16)L'\0';
	return g_TranChosung;
}

u32	checkCompletHangul( u16 * pText, size_t nLen )
{
	u32	nRet = 0;
	for( int i = ( int ) nLen - 1; i >= 0; --i )
	{
		if( chHangulChosungBegin <= pText[i] && pText[i] <= chHangulChosungEnd )
		{
			nRet |= 0;
		}
		else nRet |= 1;

		nRet = nRet << 1;
	}

	return nRet >> 1;
}

u16	tranTelNumber2( u16 * pTel, size_t nLen )
{
	u16 nRet = 0;

	for( size_t i = 0; i < nLen; ++i ) 
	{
		nRet = nRet << 4;
		nRet += pTel[i] - (u16)L'0';
	}
	return nRet;
}

char *	tranTelNumber2( u16 nTelCode, bool bLeftTrim )
{
	static char g_tranTel2[ 5 ];
	
	bool enable = !bLeftTrim;
	u16 nTmp, nFormat = 0xF000;
	
	int idx = 0;
	for( int i = 0; i < 4; ++i )
	{
		nTmp = ( nTelCode & nFormat ) >> ( 4 * ( 3 - i ));
		if( nTmp || enable ) 
		{
			g_tranTel2[idx++] = nTmp + '0';
			enable = true;
		}
		nFormat = nFormat >> 4;
	}
	
	g_tranTel2[idx] = '\0';

	return g_tranTel2;
}

bool	tranTelNumber2( u16 * pTel, u8 * _nTelDDD, u32 * _nTelCODE )
{
	u16  nTelDDD, nTelKUK, nTelNUM;

	u16TokenValue token;
	int nTokCnt = u16Tokens( token, pTel, (u16)L'-');

	if( nTokCnt == 3 )
	{
		nTelDDD	= tranTelNumber2( token[0], ( int ) u16len( token[0] ));
		nTelKUK	= tranTelNumber2( token[1], ( int ) u16len( token[1] ));
		nTelNUM	= tranTelNumber2( token[2], ( int ) u16len( token[2] ));
	
	}	
	else if( nTokCnt == 2 )
	{
		nTelDDD = 0;
		nTelKUK	= tranTelNumber2( token[0], ( int ) u16len( token[0] ));
		nTelNUM	= tranTelNumber2( token[1], ( int ) u16len( token[1] ));
	}
	else return false;
		
	* _nTelDDD	 = ( u8 ) nTelDDD;
	* _nTelCODE  = ( u32 )(( u32 ) nTelKUK << 16 ) + nTelNUM;

	return true;
}

u32	tranTelNumber( u16 * pTel )
{
	int nLen = ( int )u16len( pTel );

	int nCnt = 0;
	u32	nRet = 0, nTel = 0;
	for( int i = nLen - 1; i >= 0; --i )
	{
		if( pTel[i] == (u16)L'-' || pTel[i] == (u16)L' ') continue;

		nTel = pTel[i] - (u16)L'0';
		for( int j = 0; j < i; ++j ) nTel = nTel << 4;

		nRet |= nTel;

		++ nCnt;
	}

	return ( nRet | nCnt );
}

bool searchu8KeyValue( FILE * fp, u8 _key, u32 _offset, u32 _record_size, u32 _record_cnt )
{
	u32 left	= 0;
	u32 right	= _record_cnt;
	u32 mid	= ( left + right ) / 2;

	u8 nKeyValue;
	while ( left < right )
	{
		fseek( fp, _offset + mid * _record_size, SEEK_SET );
		fread( &nKeyValue, sizeof( u8 ), 1, fp );
		
		if( _key == nKeyValue ) return true;
		else if( _key > nKeyValue ) left = mid + 1;
		else right = mid - 1;

		mid = ( left + right ) / 2;
	}

	if( left == right )
	{
		fseek( fp, _offset + left * _record_size, SEEK_SET );
		fread( &nKeyValue, sizeof( u8 ), 1, fp );

		if( _key == nKeyValue ) return true;
	}
	
	return false;
}

bool searchu16KeyValue( FILE * fp, u16 _key, u32 _offset, u32 _record_size, u32 _record_cnt )
{
	u32 left	= 0;
	u32 right	= _record_cnt;
	u32 mid	= ( left + right ) / 2;

	u16 nKeyValue;
	while ( left < right )
	{
		fseek( fp, _offset + mid * _record_size, SEEK_SET );
		fread( &nKeyValue, sizeof( u16 ), 1, fp );
		
		if( _key == nKeyValue ) return true;
		else if( _key > nKeyValue ) left = mid + 1;
		else right = mid - 1;

		mid = ( left + right ) / 2;
	}

	if( left == right )
	{
		fseek( fp, _offset + left * _record_size, SEEK_SET );
		fread( &nKeyValue, sizeof( u16 ), 1, fp );

		if( _key == nKeyValue ) return true;
	}
	
	return false;
}

bool searchu32KeyValue( FILE * fp, u32 _key, u32 _offset, u32 _record_size, u32 _record_cnt )
{
	u32 left	= 0;
	u32 right	= _record_cnt;
	u32 mid	= ( left + right ) / 2;

	u32 nKeyValue;
	while ( left < right )
	{
		fseek( fp, _offset + mid * _record_size, SEEK_SET );
		fread( &nKeyValue, sizeof( u32 ), 1, fp );
		
		if( _key == nKeyValue ) return true;
		else if( _key > nKeyValue ) left = mid + 1;
		else right = mid - 1;

		mid = ( left + right ) / 2;
	}

	if( left == right )
	{
		fseek( fp, _offset + left * _record_size, SEEK_SET );
		fread( &nKeyValue, sizeof( u32 ), 1, fp );

		if( _key == nKeyValue ) return true;
	}
	
	return false;
}

std::pair<int,u32> searchCateInfo ( FILE * fp, u32 e_offset, u16 code1, u16 code2, u16 code3 )
{
	u32 count, offset;

	fseek( fp, e_offset - sizeof( u32 ), SEEK_SET );
	fread( &offset,	  sizeof( u32 ), 1, fp );
	fseek( fp, offset, SEEK_SET );

	fread( &count, sizeof( u32 ), 1, fp );
	
	// read code1 info
	if( !searchu16KeyValue( fp, code1, ftell( fp ), (u32)( sizeof(u32)+sizeof(u32)+sizeof(u16) ), count ))
	{
		return std::pair<int,u32>(0,0);
	}

	fread( &count,  sizeof( u32 ), 1, fp );
	fread( &offset, sizeof( u32 ), 1, fp );

	// read code2 info
	if( !searchu16KeyValue( fp, code2, offset, (u32)( sizeof(u32)+sizeof(u32)+sizeof(u16) ), count ))
	{
		return std::pair<int,u32>(0,0);
	}
	
	fread( &count,  sizeof( u32 ), 1, fp );
	fread( &offset, sizeof( u32 ), 1, fp );
	
	// read code3 info
	if( !searchu16KeyValue( fp, code3, offset, (u32)( sizeof(u32)+sizeof(u32)+sizeof(u16) ), count ))
	{
		return std::pair<int,u32>(0,0);
	}

	fread( &count,  sizeof( u32 ), 1, fp );
	fread( &offset, sizeof( u32 ), 1, fp );

	return std::pair<int,u32>( count, offset );
}

void u16cpy( u16 * dest, const u16 * src )
{
	while( *(src) != 0 ) *(dest++)=*(src++); (*dest)=0;
}

void u16cat( u16 * dest, const u16 * src )
{
	int len = u16len( dest );
	for( int i = 0; i < len; ++i ) ++dest;
	u16cpy( dest, src );
}

u16 * u16chr( u16 * src,  const u16 ch )
{
	if( src == 0 ) 
		return 0;

	u16 * pRet = src;
	while( *(pRet) != 0 && *(pRet) != ch ) pRet++;
	return pRet;
}

int u16len( u16 * text )
{
	int len = 0;
	while(true)
	{
		if( *(text+len) == 0 ) return len;
		++ len;
	}
	return len;
}