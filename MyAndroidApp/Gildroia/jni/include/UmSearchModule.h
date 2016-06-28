#pragma once

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#ifndef _60x60x120_
#define _60x60x120_			432000
#endif
#ifndef _16BIT_VALUE_
#define _16BIT_VALUE_		65536
#endif
#ifndef _8BIT_VALUE_
#define _8BIT_VALUE_		256
#endif

#include "TextMng.h"
#include <set>

#ifdef ANDROID
#include <android/log.h>
#define LOG			__android_log_print
#endif

#pragma pack (push,1)
typedef struct _search_file_header_
{
	u32 addr_begin_ofs;
	u32 addr_end_ofs;
	u32 poi_begin_ofs;
	u32 poi_end_ofs;
} SEARCH_FILE_HEADER;
typedef struct _addr_header_st_
{
	u32 addr_cho_ofs;
	u32 addr_cho_idx_ofs;
	u32 addr_cho_idx_cnt;
	u32 road_cho_ofs;
	u32 road_cho_idx_ofs;
	u32 road_cho_idx_cnt;
	u32 addr_pos_ext_ofs;
	u32 addr_dong_ofs;
	u32 addr_dong_cnt;
} ADDR_DATA_HEADER;
typedef struct _poi_header_st_
{
	u32 poi_body_ofs;
	u32 poi_info_ofs;
	u32 poi_cho_ofs;
	u32 poi_cho_idx_ofs;
	u32 poi_cho_idx_cnt;
	u32 cate_poi_ofs;
	u32 dong_cate_poi_idx_ofs;
	u32 dong_cate_poi_idx_cnt;
	u32 tel_poi_idx_ofs;
	u32 tel_poi_idx_cnt;
	u32 mesh_poi_idx_ofs;
	u32 mesh_poi_idx_cnt;
} POI_DATA_HEADER;
typedef struct _sido_data_file_format_st_
{
	u8 idx;
	u8 code;
	u16 mesh_x_min, mesh_x_max;
	u16 mesh_y_min, mesh_y_max;
} SIDO_DATA_F;
#define SIDO_DATA	SIDO_DATA_F
typedef struct _sigungu_data_file_format_st_
{
	u16	code;
	u32	road_ofs;
	u32	offset;
} SIGUNGU_DATA_F;
typedef struct _sigungu_data_st_
{
	u16	name[32];
	u8  name_len;
	SIGUNGU_DATA_F * pInfo;
} SIGUNGU_DATA;
typedef struct _dong_data_file_format_st_
{
	u32	code;
	u32	min_x, min_y;
	u16	ldong_flag	: 1;
	u16	road_flag	: 1;
	u16	bunji_cnt	: 14;
	u32	offset;
} DONG_DATA_F;
typedef struct _dong_data_st_ 
{
	int		nSidoIdx;
	SIGUNGU_DATA_F * pSigunguF;

	u16	name[32];
	u8  name_len;
	DONG_DATA_F info;
} DONG_DATA;
typedef struct _road_data_file_format_st_
{
	u32	code;
	u16	bunji_cnt;
	u32	offset;
} ROAD_DATA_F;
typedef struct _road_data_st_ 
{
	int		nSidoIdx;
	SIGUNGU_DATA_F * pSigunguF;

	u16	name[32];
	u8  name_len;
	ROAD_DATA_F info;
} ROAD_DATA;
typedef struct _bunji_data_st_ 
{
	void	* pDong;

	u16	bunji;
	u16	ho_cnt;
	u32	offset;
}BUNJI_DATA;
typedef union
{
	struct
	{
		u8	ho;
		u16	ho_quota		: 6;
		u16	x_quota			: 5;
		u16	y_quota			: 5;
		u16	x;
		u16	y;
	};
	struct
	{
		u16	n_ho;
		u32	offset;
	};
} HO_DATA;
typedef struct _addr_new_ho_info_st_
{
	u16	ho;
	u16	o_bunji;
	u32	offset;
} NEW_HO_DATA;
typedef struct _poi_info_file_format_st_
{
	u32	name_ofs	;
	u32	body_ofs	:	29;
	u32	pos_cnt		:	 1;
	u32 cate_flag	:	 1;
	u32	use_ho_ofs	:	 1;
} POI_HEADER_F;
typedef struct _poi_info_st_
{
	int nSidoIdx;
	POI_HEADER_F info;
} POI_HEADER;
typedef struct _poi_body_file_format_st_
{
	u32	hDCode;
		
	u16	ho;
	u16	bunji	:	14;
	u16	san		:	 1;
	u16	parking	:	 1;

	u32	x		:	28;
	u32	tel_cnt	:	 3;
	u32	branch	:	 1;

	u32	y;
} POI_BODY_F;
typedef struct _poi_body_file_format_use_ho_st_
{
	//u32	hDCode;
	
	u16	san		:	 1;
	u16	bunji	:	14;
	u16	branch	:	 1;
	
	u32	ho_ofs	:	28;
	u32	tel_cnt	:	 3;
	u32	parking	:	 1;
} POI_BODY_USE_HO_F;
typedef struct _poi_body_st_
{
	u16 branchName[ 35 ];
	char tel[8][16];
	
	u32 x_ex, y_ex;	// ?�Ωª�?¡¬«??
	POI_BODY_F info;
} POI_BODY;
typedef union _chosung_code_
{
	u32	nChoCode;
	struct
	{
		u32		len  : 8;
		u32		cho1 : 6;
		u32		cho2 : 6;
		u32		cho3 : 6;
		u32		cho4 : 6;
	};
	struct
	{
		u32		len_1  : 8;
		u32		cho1_2 : 12;
		u32		cho3_4 : 12;
	};
	struct
	{
		u32		len_2  : 8;
		u32		cho1_3 : 18;
		u32		cho4_4 : 6;
	};
	struct
	{
		u32		len_3  : 8;
		u32		cho1_4 : 24;
	};
	struct
	{
		u32		len_4	: 4;
		u32		tel1	: 4;
		u32		tel2	: 4;
		u32		tel3	: 4;
		u32		tel4	: 4;
		u32		tel5	: 4;
		u32		tel6	: 4;
		u32		tel7	: 4;
	};
} CHOSUNG_CODE;

typedef struct _chosung_index_st_
{
	u32  choCode;

	u32 idx			: 24;		// 16777215 ?�≥
	u32 cnt_idx		:  8;		// cnt?��?65536¿?�∑Œ ?�™¥´ ?��?
	u16  cnt;					// cnt?��?65536¿?�∑Œ ?�™¥´ ?�™?�”¡�?
} CHOSUNG_INDEX;

typedef struct _chosung_data_st_
{
	u32	offset		: 29;		// 511M ±Ó¡? µ 
	u32	dataType	:  3;
} CHOSUNG_DATA;

#pragma pack (pop)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#define MAX_SEARCH_SIDO_CNT				16
typedef struct _sido_info_st_
{
	u8	sidoCode;
	const char * ddd;
	u8	dddLen;
	const char * tail;
	const char * name;
}SIDO_INFO_ST;

const static SIDO_INFO_ST g_sido_info[MAX_SEARCH_SIDO_CNT] = {
	{11, "02",  2, "su", "Seoul"},	
	{26, "051", 3, "bs", "Busna"},	
	{27, "053", 3, "dg", "Daegu"},	
	{28, "032", 3, "ic", "Incheon"},
	{29, "062", 3, "kj", "Gwangju"},
	{30, "042", 3, "dj", "Daejeon"},
	{31, "052", 3, "us", "Ulsan"},
	{41, "031", 3, "kg", "Gyeonggi-do"},
	{42, "033", 3, "kw", "Gangwon-do"},
	{43, "043", 3, "cb", "Chungcheong buk-do"},
	{44, "041", 3, "cn", "Chungcheong nam-do"},
	{45, "063", 3, "jb", "Jeolla buk-do"},
	{46, "061", 3, "jn", "Jeolla nam-do"},
	{47, "054", 3, "kb", "Gyeongsan buk-do"},
	{48, "055", 3, "kn", "Gyeongsan nam-do"},
	{50, "064", 3, "jj", "Jeju-do"}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void  u16cpy( u16 * dest, const u16 * src );
void  u16cat( u16 * dest, const u16 * src );
u16 * u16chr( u16 * src,  const u16 ch );
int   u16len( u16 * text );

typedef u16 * u16TokenValue[128];
u16 * Trim_Left	( u16 * str );
u16 * Trim_Right	( u16 * str );
u16 * Trim		( u16 * str );
int	  u16Tokens		( u16TokenValue	Tokens, u16 * str, u16 ch );

u32	dongCode( u64 dCode );

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#define MAX_HAN_CHO_CNT			19
#define MAX_HAN_JUNG_CNT		21
#define MAX_HAN_JONG_CNT		28
#define chHangulBegin			0xAC00
#define chHangulEnd				0xD7AF
#define chHangulChosungBegin	0x3131
#define chHangulChosungEnd		0x314E

const static int HAN_JUNG_MUL_JONG	= MAX_HAN_JUNG_CNT * MAX_HAN_JONG_CNT;

const static u16 chHangulCho[]		=
{
	12593, // '§°',
	12594, // '§¢',
	12596, // '§§',
	12599, // '§ß', 
	12600, // '§®',
	12601, // '§©',
	12609, // '§±',
	12610, // '§??, 
	12611, // '§??,
	12613, // '§µ',
	12614, // '§??,
	12615, // '§??, 
	12616, // '§??,
	12617, // '§?',
	12618, // '§??,
	12619, // '§ª', 
	12620, // '§º',
	12621, // '§Ω',
	12622,  // '§æ'
	(u16)L'0',(u16)L'1',(u16)L'2',(u16)L'3',(u16)L'4',(u16)L'5',(u16)L'6',(u16)L'7',(u16)L'8',(u16)L'9',
	(u16)L'A',(u16)L'B',(u16)L'C',(u16)L'D',(u16)L'E',(u16)L'F',(u16)L'G',(u16)L'H',(u16)L'I',(u16)L'J',(u16)L'K',(u16)L'L',(u16)L'M',
	(u16)L'N',(u16)L'O',(u16)L'P',(u16)L'Q',(u16)L'R',(u16)L'S',(u16)L'T',(u16)L'U',(u16)L'V',(u16)L'W',(u16)L'X',(u16)L'Y',(u16)L'Z'
}; 

//#define MAX_CHOSUNG_LEN		4
#define MAX_CHOSUNG_LEN		2

bool	isChosung( u16 ch );
u16		tranChosung( u16 ch );
u32		tranChosung( u16 * pText, size_t nLen );
u16 *	tranChosung( u32 nChoCode );
u32		checkCompletHangul( u16 * pText, size_t nLen );
u32		tranTelNumber( int kuk, int num );
u32		tranTelNumber( u16 * pTel );
u16		tranTelNumber2( u16 * pTel, size_t nLen );
char *	tranTelNumber2( u16 nTelCode, bool bLeftTrim = true );
bool	tranTelNumber2( u16 * pTel, u8 * _nTelDDD, u32 * _nTelCODE );
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
bool searchu32KeyValue( FILE * fp, u32 _key, u32 _offset, u32 _record_size, u32 _record_cnt );
bool searchu16KeyValue( FILE * fp, u16 _key, u32 _offset, u32 _record_size, u32 _record_cnt );
bool searchu8KeyValue( FILE * fp, u8 _key, u32 _offset, u32 _record_size, u32 _record_cnt );

std::pair<int,u32> searchCateInfo ( FILE * fp, u32 e_offset, u16 code1, u16 code2, u16 code3 );
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


class CUmSearchHelper
{
public :
	CUmSearchHelper( FILE * _fp, CTextMng * _Text_mng );
	~CUmSearchHelper();
	
	void Release();
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	SIDO_DATA					GetSidoData(){ return m_sidoData; }
	SIGUNGU_DATA_F *			GetSigunguData( u32 sigungu_ofs );
	std::vector<SIGUNGU_DATA> *	GetSigunguData();
	std::vector<DONG_DATA> *	GetDongData( int nSidoIdx, u32 sigungu_ofs );
	DONG_DATA *					GetDongDataOfOffset( int nSigoIdx, SIGUNGU_DATA_F * pSigunguF, u32 dong_ofs );
	std::vector<ROAD_DATA> *	GetRoadData( int nSidoIdx, u32 sigungu_ofs );
	std::vector<BUNJI_DATA> *	GetBunjiData( void * pDong, bool bSan, bool bFromHDong = false );
	std::vector<BUNJI_DATA> *	GetRoadBunjiData( void * pRoad );
	std::vector<HO_DATA> *		GetHoData	( u32 bunji_ofs );
	std::vector<NEW_HO_DATA> *	GetNewHoData( u32 bunji_ofs );
	
	DONG_DATA *					GetHo	( u32 ho_ofs, HO_DATA * pHo );
	void						GetHo	( u32 ho_ofs, HO_DATA * pHo, DONG_DATA_F * ddf );
	void						GetHoPos( HO_DATA * pHo, u32 * pX, u32 * pY );
	void						GetPosEx( int nIndex, u32 * pX, u32 * pY );

	int SearchDongName( int nSidoIdx, u16 * dongName, int nLen, u32 choCode, u32 ckCmpHan, std::vector<DONG_DATA> * pResult );
	int SearchRoadName( int nSidoIdx, u16 * roadName, int nLen, u32 choCode, u32 ckCmpHan, std::vector<ROAD_DATA> * pResult );

protected :
	SEARCH_FILE_HEADER			m_file_header;
	ADDR_DATA_HEADER			m_addr_header;
	POI_DATA_HEADER				m_poi_header;
	CTextMng *					m_pTextMng;
	FILE *						fp;
	SIDO_DATA					m_sidoData;
	std::vector<SIGUNGU_DATA>	m_sigunguDatas;
	std::vector<SIGUNGU_DATA_F>	m_sigunguDataFs;
	std::vector<DONG_DATA>		m_dongDatas;
	std::vector<DONG_DATA>		m_dongExDatas;
	std::vector<ROAD_DATA>		m_roadDatas;
	std::vector<BUNJI_DATA>		m_bunjiDatas;
	std::vector<HO_DATA>		m_hoDatas;
	std::vector<NEW_HO_DATA>	m_newHoDatas;

	std::vector<std::pair<u32,u32> > m_LDCodeOffset;

	void						LoadSigunguFDatas();
	void						LoadLDongOffset();
	SIGUNGU_DATA_F *			FindSigunguOfOffset( u32 ofs, bool bRoad = false );
	u32							FindDongOfOffset( u32 ofs, DONG_DATA_F * dongDataF );

	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public :
	int			SearchTelNumber   ( int nSidoIdx, u8 telDDD, u16 telKUK, u16 telNUM, std::multimap<tstring,POI_HEADER> * pPoiHeaders );
	int			SearchTelNumber   ( int nSidoIdx,			   u16 telKUK, u16 telNUM, std::multimap<tstring,POI_HEADER> * pPoiHeaders );

	int 		SearchPoiName      ( int nSidoIdx, u16 * pText, int nLen, u32 choCode, u32 ckCmpHan, std::multimap<tstring,POI_HEADER> * pPoiHeaders  );

	void		SearchPoiCateSido  ( int nSidoIdx, u16 code1, u16 code2, u16 code3, std::multimap<tstring,POI_HEADER> * pPoiHeaders  );
	void		SearchPoiCateHDong ( int nSidoIdx, u32 nHCode, u16 code1, u16 code2, u16 code3, std::multimap<tstring,POI_HEADER> * pPoiHeaders  );
	void		SearchPoiCateMesh  ( int nSidoIdx, u32 nMeshCode, u16 code1, u16 code2, u16 code3, std::multimap<tstring,POI_HEADER> * pPoiHeaders  );
	POI_BODY *	GetSearchPoiResult( POI_HEADER_F * poiHeader, POI_BODY * poiBody );
protected :
	void		SearchPoiCate( int nSidoIdx, u32 end_ofs, u16 c1, u16 c2, u16 c3, 
				   std::multimap<tstring,POI_HEADER> * pPoiHeaders );
};

class CUmSearchModule
{
public:
	CUmSearchModule(void);
	~CUmSearchModule(void);

	bool Load( const char * pPath );
	void Release();
	
	int				GetSidoCount(){ return ( int ) m_helpers.size(); }
	const u16 *		GetSidoName( int nIndex, int * pLen = NULL ){ return m_Text_mng.getSidoName( m_helpers[nIndex]->GetSidoData().idx, pLen ); }
	const u16 *		GetSigunguName( int nSidoIdx, u16 sigunguCode, int * pLen = NULL ){ return m_Text_mng.getSigunguName( m_helpers[nSidoIdx]->GetSidoData().idx * 1000 + sigunguCode, pLen );}
	const u16 *		GetSigunguName( u32 DCode, int * pLen = NULL ){ return m_Text_mng.getSigunguName(( u16 )( DCode / 100000 ), pLen );}
	const u16 *		GetDongName( int nSidoIdx, u16 sigunguCode, u32 DCode, int * pLen = NULL ){ return m_Text_mng.getDongName( m_helpers[nSidoIdx]->GetSidoData().idx * 100000000 + sigunguCode * 100000 + DCode, pLen );}
	const u16 *		GetDongName( u32 DCode, int * pLen = NULL ){ return m_Text_mng.getDongName( DCode, pLen );}

	std::vector<SIGUNGU_DATA> * GetSigunguData( int nSidoIdx ){ return m_helpers[nSidoIdx]->GetSigunguData(); }
	std::vector<DONG_DATA> *	GetDongData   ( int nSidoIdx, u32 sigungu_ofs ){ return m_helpers[nSidoIdx]->GetDongData( nSidoIdx, sigungu_ofs ); }
	std::vector<ROAD_DATA> *	GetRoadData   ( int nSidoIdx, u32 sigungu_ofs ){ return m_helpers[nSidoIdx]->GetRoadData( nSidoIdx, sigungu_ofs ); }
	std::vector<BUNJI_DATA> *	GetBunjiData  ( int nSidoIdx, void * pDong, bool bSan, bool bRoad = false );	
	std::vector<HO_DATA> *		GetHoData	  ( int nSidoIdx, u32 bunji_ofs ){ return m_helpers[nSidoIdx]->GetHoData( bunji_ofs ); }
	std::vector<NEW_HO_DATA> *	GetNewHoData  ( int nSidoIdx, u32 bunji_ofs ){ return m_helpers[nSidoIdx]->GetNewHoData( bunji_ofs ); }

	u16				GetHoValue( HO_DATA * pHo ){ u16 ret = pHo->ho_quota * _8BIT_VALUE_ + pHo->ho; return ret; }
	void			GetHoPos	( int nSidoIdx, HO_DATA     * pHo,    u32 * pX, u32 * pY );
	u32				GetNewHoPos ( int nSidoIdx, NEW_HO_DATA * pNewHo, u32 * pX, u32 * pY, HO_DATA * pHo );
	
	std::vector<DONG_DATA> * SearchDongName( u16 * dongName, int nLen );
	std::vector<ROAD_DATA> * SearchRoadName( u16 * roadName, int nLen );

protected :
	std::vector<CUmSearchHelper *>		m_helpers;
	CTextMng							m_Text_mng;

	std::vector<DONG_DATA>		m_dongNameResult;
	std::vector<ROAD_DATA>		m_roadNameResult;

	const u16 *		GetText( int nIndex ){ return m_Text_mng.getText( nIndex ); }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////  POI  //////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public :

	std::multimap<tstring,POI_HEADER> *	SearchTelNumber   ( u16 * pText, int nLen );

	std::multimap<tstring,POI_HEADER> *	SearchPoiName     ( u16 * pText, int nLen );
	std::multimap<tstring,POI_HEADER> *	SearchPoiName     ( int nSidoIdx, u16 * pText, int nLen );
	std::multimap<tstring,POI_HEADER> *	SearchPoiCateSido ( int nSidoIdx, u16 code1, u16 code2, u16 code3, bool clearData = true );
	std::multimap<tstring,POI_HEADER> *	SearchPoiCateDong ( DONG_DATA * pDong, u16 code1, u16 code2, u16 code3, bool clearData = true );
	std::multimap<tstring,POI_HEADER> *	SearchPoiCateMesh ( u32 nMeshCode, u16 code1, u16 code2, u16 code3, bool clearData = true );
	
	POI_BODY *							GetSearchPoiResult( POI_HEADER * poiHeader );
	
protected :

	std::multimap<tstring,POI_HEADER>	m_poiHeaders;	// POI ?�Àª? ?�·∞?
	POI_BODY							m_poiBody;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////  L->H DONG TABLE  //////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public :
	std::set<u32> * FindLHDCodeList( u32 LDCode );
		
protected :	
	FILE *			m_fpLHCode;
	u32				m_nLHCodeIdxOfs;
	u32				m_nLHCodeIdxCnt;
	std::set<u32>	m_stLHCodeResult;
};

