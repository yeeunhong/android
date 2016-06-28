#pragma once

#include <stdio.h>
#include <stdlib.h>

#include <map>
#include <set>
#include <vector>
#include <string>
#include <algorithm>

#include <ctype.h>

#ifndef UNI_VAR_TYPE
#define UNI_VAR_TYPE
typedef unsigned char		u8;
typedef unsigned short		u16;
typedef unsigned long		u32;
typedef unsigned long long	u64;
#endif
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#ifndef TSTRING
#define TSTRING
typedef std::basic_string<u16, std::char_traits<u16>, std::allocator<u16> > tstring;
#endif
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


class CTextMng
{
public:
	CTextMng(void);
	~CTextMng(void);
	
	unsigned int	addText( u16 * pText, u32 & _offset );
	const u16 *	getText( unsigned int textIdx );
	const u16 *	getTextOfs( u32 txt_ofs, int * pLen = NULL, int nBufIdx = 0 );

	void Init( char * path );
	void Save();
	
	bool Load(  char * path );
	bool Load2(  char * path );

	const u16 *	getSidoName		( u8  sidoIdx,		int * pLen = NULL );
	const u16 *	getSigunguName	( u16 sigunguCode,	int * pLen = NULL );
	const u16 *	getDongName		( u32 dongCode,		int * pLen = NULL );
	const u16 *	getRoadName		( u32 roadCode,		int * pLen = NULL );

protected :
	std::vector<tstring>					m_Text;
	std::map<tstring,std::pair<int,u32> >	m_TextIdx;

	u32					m_offset;
	FILE *					m_Text_fp;
	u32					m_Text_idx_ofs;

	u16					m_Text_buff[4][32];

	FILE *					m_name_fp;
	u32					m_sido_ofs;
	u32					m_sigungu_ofs;
	u32					m_dong_ofs;
	u32					m_road_ofs;
};

