#include "TextMng.h"
#include "UmSearchModule.h"

#ifndef MAX_PATH
#define MAX_PATH	256
#endif

CTextMng::CTextMng(void)
{
	m_Text.push_back( (u16*)L"");	// text index에서 0은 값이 없는 문자열로 사용한다.
	m_offset	= 0;
	m_Text_fp	= NULL;
	m_name_fp	= NULL;

	m_Text_idx_ofs	= 0;

	m_sido_ofs		= 0;
	m_sigungu_ofs	= 0;
	m_dong_ofs		= 0;
	m_road_ofs		= 0;
}

CTextMng::~CTextMng(void)
{
	if( m_Text_fp ) fclose( m_Text_fp );
	if( m_name_fp ) fclose( m_name_fp );
}

unsigned int	CTextMng::addText( u16 * pText, u32 & _offset )
{
	tstring text( Trim( pText ));
	std::map<tstring,std::pair<int,u32> >::iterator find_Text = m_TextIdx.find( text );
	if( find_Text == m_TextIdx.end() ) 
	{
		int nLen = ( int ) text.size();
		unsigned int nTextIdx = ( unsigned int ) m_Text.size();
		
		m_offset = ( u32 ) ftell( m_Text_fp );
		fwrite( text.c_str(), sizeof( u16 ), nLen, m_Text_fp );
			
		_offset = m_offset;
		_offset = _offset << 5;
		_offset = ( _offset & ~0x1F ) + nLen;

		m_Text.push_back( text );
		m_TextIdx.insert( std::pair<tstring,std::pair<int,u32> >( text, std::pair<int,long>( nTextIdx, _offset )));
		
		return nTextIdx;
	}
	else
	{
		_offset = (*find_Text).second.second;
		return (*find_Text).second.first;
	}

	return 0;
}

const u16 *	CTextMng::getText( unsigned int textIdx )
{
	if( m_Text_fp )
	{
		long offset = textIdx * ( long ) sizeof( u32 );
		fseek( m_Text_fp, m_Text_idx_ofs + offset, SEEK_SET );
		
		u32 txt_ofs;
		fread( &txt_ofs, sizeof( u32 ), 1, m_Text_fp );
		
		return getTextOfs( txt_ofs );
	}
	else
	{
		if( textIdx >= ( unsigned int ) m_Text.size()) return NULL;
		return m_Text[textIdx].c_str();
	}
	return NULL;
}

const u16 *	CTextMng::getTextOfs( u32 txt_ofs, int * pLen, int nBufIdx )
{
	int nLen;
	if( pLen == NULL ) pLen = &nLen;

	* pLen = ( int )( txt_ofs & 0x1F );
	txt_ofs = txt_ofs >> 5;
	nLen = * pLen;

	fseek( m_Text_fp, txt_ofs, SEEK_SET );
	fread( m_Text_buff[nBufIdx], sizeof( u16 ), nLen, m_Text_fp );

	m_Text_buff[nBufIdx][nLen] = 0;

	return m_Text_buff[nBufIdx];
}

void CTextMng::Init( char * path )
{
	char strTemp[ MAX_PATH ];
	strcpy( strTemp, path );
	strcat( strTemp, "text.dat");
	m_Text_fp = fopen( strTemp, "wb" );
}

void CTextMng::Save()
{
	u32 text_idx_ofs = ftell( m_Text_fp );
	for( std::map<tstring,std::pair<int,u32> >::iterator it = m_TextIdx.begin(); it != m_TextIdx.end(); ++it )
		fwrite( &(*it).second.second, sizeof( u32 ), 1, m_Text_fp );
	fwrite( &text_idx_ofs, sizeof( u32 ), 1, m_Text_fp );
	fclose( m_Text_fp );
}

bool CTextMng::Load( char * path )
{
	char strTemp[ MAX_PATH ];

	if( m_Text_fp ) fclose( m_Text_fp );
	
	strcpy( strTemp, path );
	strcat( strTemp, "text.dat");
	m_Text_fp = fopen( strTemp, "rb");
	
	fseek( m_Text_fp, - 1 * (long)sizeof( u32 ), SEEK_END );
	fread( &m_Text_idx_ofs, sizeof( u32 ), 1, m_Text_fp );

	strcpy( strTemp, path );
	strcat( strTemp, "name.dat");
	m_name_fp = fopen( strTemp, "rb");

	//m_sido_ofs		= 0;
	if( m_name_fp )
	{
		fseek( m_name_fp,		( long ) sizeof( u32 ) * -3, SEEK_END );
		fread( &m_sigungu_ofs,	sizeof( u32 ), 1, m_name_fp );
		fread( &m_dong_ofs,		sizeof( u32 ), 1, m_name_fp );
		fread( &m_road_ofs,		sizeof( u32 ), 1, m_name_fp );
	}

	return m_Text_fp != NULL && m_name_fp != NULL;
}

bool CTextMng::Load2(  char * path )
{
	char strTemp[ MAX_PATH ];

	if( m_Text_fp ) fclose( m_Text_fp );
	
	strcpy( strTemp, path );
	strcat( strTemp, "text.dat");
	m_Text_fp = fopen( strTemp, "rb");

	fseek( m_Text_fp, -1 * ( long ) sizeof( u32 ), SEEK_END );

	u32 text_idx_ofs;
	fread( &text_idx_ofs, sizeof( u32 ), 1, m_Text_fp );
	fseek( m_Text_fp, text_idx_ofs, SEEK_SET );

	u32 text_ofs;
	std::vector<u32> offsets;
	
	while( !feof( m_Text_fp ))
	{
		fread( &text_ofs, sizeof( u32 ), 1, m_Text_fp );
		offsets.push_back( text_ofs );
	}

	for( std::vector<u32>::iterator it = offsets.begin(); it != offsets.end(); ++it )
	{
		tstring strText( getTextOfs( (*it) ));
		m_TextIdx.insert( std::pair<tstring,std::pair<int,u32> >( strText, std::pair<int,u32>(0,(*it))));
	}

	return true;
}

const u16 *	CTextMng::getSidoName		( u8  sidoIdx, int * pLen )
{
	if( m_name_fp == NULL || m_Text_fp == NULL ) return NULL;
	
	u32 nu32;

	fseek( m_name_fp, m_sido_ofs, SEEK_SET );
	fread( &nu32, sizeof( u32 ), 1, m_name_fp );

	if( searchu8KeyValue( m_name_fp, sidoIdx, (u32) ftell( m_name_fp ), (u32)( sizeof(u8) + sizeof(u32)), nu32 ))
	{
		fread( &nu32, sizeof( u32 ), 1, m_name_fp );
		return getTextOfs( nu32, pLen );
	}

	return NULL;
}

const u16 *	CTextMng::getSigunguName	( u16  sigunguCode, int * pLen )
{
	if( m_name_fp == NULL || m_Text_fp == NULL ) return NULL;

	u32 nu32;

	fseek( m_name_fp, m_sigungu_ofs, SEEK_SET );
	fread( &nu32, sizeof( u32 ), 1, m_name_fp );

	if( searchu16KeyValue( m_name_fp, sigunguCode, (u32) ftell( m_name_fp ), (u32)( sizeof(u16) + sizeof(u32)), nu32 ))
	{
		fread( &nu32, sizeof( u32 ), 1, m_name_fp );
		return getTextOfs( nu32, pLen, 1 );
	}

	return NULL;
}

const u16 *	CTextMng::getDongName		( u32 dongCode, int * pLen )
{
	if( m_name_fp == NULL || m_Text_fp == NULL ) return NULL;

	u32 nu32;

	fseek( m_name_fp, m_dong_ofs, SEEK_SET );
	fread( &nu32, sizeof( u32 ), 1, m_name_fp );

	if( searchu32KeyValue( m_name_fp, dongCode, (u32) ftell( m_name_fp ), (u32)( sizeof(u32) + sizeof(u32)), nu32 ))
	{
		fread( &nu32, sizeof( u32 ), 1, m_name_fp );
		return getTextOfs( nu32, pLen, 2 );
	}

	return NULL;
}

const u16 *	CTextMng::getRoadName		( u32 roadCode, int * pLen )
{
	if( m_name_fp == NULL || m_Text_fp == NULL ) return NULL;

	u32 nu32;

	fseek( m_name_fp, m_road_ofs, SEEK_SET );
	fread( &nu32, sizeof( u32 ), 1, m_name_fp );

	if( searchu32KeyValue( m_name_fp, roadCode, (u32) ftell( m_name_fp ), (u32)( sizeof(u32) + sizeof(u32)), nu32 ))
	{
		fread( &nu32, sizeof( u32 ), 1, m_name_fp );
		return getTextOfs( nu32, pLen, 3 );
	}

	return NULL;
}