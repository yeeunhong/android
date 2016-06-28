#pragma once

#include <stdio.h>
#include <string.h>
#include <map>

#ifndef U_VAR_TYPE
#define U_VAR_TYPE
typedef unsigned char		u8;
typedef unsigned short		u16;
typedef unsigned long		u32;
typedef unsigned long long	u64;
#endif

#ifndef MAX_PATH
#define MAX_PATH	256
#endif

#pragma pack (push,1)

struct HCODE
{
	HCODE() {sido = 0, gugun = 0, dong = 0;}
	bool isNull() { if (sido == 0 && gugun == 0 && dong == 0) return true; return false; }
	bool operator<(const HCODE & h) const
	{
		if (this->sido < h.sido) return true;
		else if (this->sido == h.sido)
		{
			if (this->gugun < h.gugun) return true;
			else if (this->gugun == h.gugun)
			{
				if (this->dong < h.dong) return true;
			}
		}

		return false;
	}

	u8		sido;
	u32		gugun : 12;
	u32		dong  : 20;
};

// 지구 위경도 기준의 Boundary이다.
// left는 서쪽, right는 동쪽, top은 북쪽, bottom은 남쪽이다.
// bottom < top 이다.
struct BOUND
{
	u32		left;
	u32		top;
	u32		right;
	u32		bottom;

	bool llInBound( u32 & lat,  u32 & lon) const
	{
		if (left <= lon  && lon <= right && bottom <= lat &&  lat <= top)
			return true;
		return false;
	}

	bool operator<(const BOUND & b) const
	{
		if (this->left < b.left)
			return true;
		else if (this->left == b.left)
		{
			if (this->top < b.top)
				return true;
			else if (this->top == b.top)
			{
				if (this->right < b.right)
					return true;
				else if (this->right == b.right)
				{
					if (this->bottom < b.bottom)
						return true;
				}
			}
		}

		return false;
	}
};

struct PARENT_BOUND
{
	BOUND		bound;
	u8			child_num;
};

struct CHILD_BOUND
{
	BOUND		bound;
	u32			offset;
	u16			vtx_num;
	HCODE		hcode;
};

struct BOUND_VTX
{
	BOUND_VTX() { x = y = 0; }
	BOUND_VTX( u32& _x, u32 _y) { x = _x; y = _y; }
	u32		x;
	u32		y;

	bool operator<(const BOUND_VTX & v) const
	{
		if (this->x < v.x)
			return true;
		else if (this->x == v.x)
		{
			if (this->y < v.y)
				return true;
		}

		return false;
	}
};

#pragma pack (pop)

typedef std::map<BOUND, CHILD_BOUND>	DongAdminMap;
typedef std::map<BOUND, DongAdminMap>	GunGuAdminMap;
typedef std::map<BOUND, GunGuAdminMap>	SidoAdminMap;

#define SIDO_BIT		0x01
#define SIGUNGU_BIT		0x02
#define DONG_BIT		0x04

class CAdminBoundManager
{
public :
	CAdminBoundManager(void);
	~CAdminBoundManager(void);

	bool		Create( const char * filepath );
	void		Release();

	bool		findBound( u32 lat, u32 lon );
	u8			if_changed_hcode();
	
	HCODE 		getHCode(){ return m_hCode; }

protected :
	
	bool		findBound(u32 lat, u32 lon, HCODE& hCode); // lat/lon은 1/120sec 정밀도
	bool		llInBoundary(BOUND_VTX* pVertex, const u32 vtx_num, u32 lat, u32 lon);
	BOUND_VTX	getIntersectVertex(BOUND_VTX& a_vtx, BOUND_VTX& b_vtx, u32& lat);

	SidoAdminMap	m_AdminBoundaryMap;
	HCODE			m_hCode, m_tmpHCode;

protected :
	FILE *		m_pFile;

};

typedef struct _UTSD_FILE_HEADER
{
	u8 file_name[32];
	u8 serial[16];
	u8 format_main_version;
	u8 format_sub_version;
	u8 data_main_version;
	u8 data_sub_version;
	u16 date_year;
	u8 date_month;
	u8 date_day;	
	u32 file_size;
} UTSD_FILE_HEADER;
