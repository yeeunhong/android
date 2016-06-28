#include <jni.h>
/* Header for class unquantum_search_SearchModule */

#include "jni_helper.h"
#include "kr_co_uniquantum_search_SearchModule.h"

#include "UmSearchModule.h"
#include "AdminBoundManager.h"

CUmSearchModule * g_pSearchModule = NULL;
CAdminBoundManager * g_pAdminBound = NULL;

jint g_sidoIdx		= -1;
jint g_sigunguIdx	= -1;
jint g_dongIdx		= -1;
jint g_bunjiIdx		= -1;
jint g_roadIdx		= -1;
jint g_roadBunjiIdx	= -1;
jint g_poiHeaderIdx = -1;

std::vector<NEW_HO_DATA> *			m_pNewHoData	= NULL;
std::vector<HO_DATA> *				m_pHoData		= NULL;
std::vector<BUNJI_DATA> *			m_pBunjiData	= NULL;
std::vector<DONG_DATA> *			m_pDongData		= NULL;
std::vector<ROAD_DATA> *			m_pRoadData		= NULL;
std::vector<SIGUNGU_DATA> *			m_pSigunguData	= NULL;
std::multimap<tstring,POI_HEADER> * m_pPoiDatas		= NULL;
POI_HEADER *						m_pPoiHeader	= NULL;
POI_BODY *							m_pPoiBody		= NULL;

std::multimap<tstring,POI_HEADER>::iterator m_pPoiDatasIterator;
bool m_bChangedPoiIterator = true;

/*
 * Class:     unquantum_search_SearchModule
 * Method:    InitModule
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_kr_co_uniquantum_search_SearchModule_InitModule
  (JNIEnv * env, jobject obj, jstring jstrPath )
{
	if( g_pSearchModule != NULL ) 
	{
		g_pSearchModule->Release();
		delete g_pSearchModule;
	}
	g_pSearchModule = new CUmSearchModule();
	
	if( g_pAdminBound != NULL )
	{
		g_pAdminBound->Release();
		delete g_pAdminBound;
	}
	g_pAdminBound = new CAdminBoundManager();

	CString strPath( env, &jstrPath );

	g_pAdminBound->Create( strPath.toString());
	return (jboolean)g_pSearchModule->Load( strPath.toString());
}  

/*
 * Class:     unquantum_search_SearchModule
 * Method:    ExitModule
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_kr_co_uniquantum_search_SearchModule_ExitModule
  (JNIEnv * env, jobject obj )
{
	if( g_pSearchModule != NULL ) 
	{
		g_pSearchModule->Release();
		delete g_pSearchModule;
	}
	g_pSearchModule = NULL;

	if( g_pAdminBound != NULL )
	{
		g_pAdminBound->Release();
		delete g_pAdminBound;
	}
	g_pAdminBound = NULL;
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetSidoCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSidoCount
  (JNIEnv * env, jobject obj )
{
	if( g_pSearchModule == NULL ) return 0; 
	return (jint)g_pSearchModule->GetSidoCount();
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    JNI_GetSidoData
 * Signature: (I)[S
 */
JNIEXPORT jshortArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1GetSidoData
  (JNIEnv * env, jobject obj, jint sidoIdx )
{
	return NULL;
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetSidoName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSidoName
  (JNIEnv * env, jobject obj, jint sidoIdx )
{
	if( g_pSearchModule == NULL ) return NULL; 
	
	int nLen = 0;
	const u16 * sidoName = g_pSearchModule->GetSidoName( sidoIdx, &nLen );

	return JWSTR( sidoName, nLen );
}

/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    GetSidoNameByDCode
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSidoNameByDCode
  (JNIEnv * env, jobject obj, jlong dCode )
{
	if( g_pSearchModule == NULL ) return NULL; 
	
	int nLen = 0;
	const u16 * sidoName = g_pSearchModule->GetSidoName( dCode/100000000, &nLen );

	return JWSTR( sidoName, nLen );
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetSigunguCount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSigunguCount
  (JNIEnv * env, jobject obj, jint sidoIdx)
{
	if( g_pSearchModule == NULL ) return NULL;

	g_sidoIdx = sidoIdx;

	m_pSigunguData = g_pSearchModule->GetSigunguData( g_sidoIdx );
	return (jint)(*m_pSigunguData).size();
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetSigunguName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSigunguName
  (JNIEnv * env, jobject obj, jint sigunguIdx)
{
	if( m_pSigunguData == NULL ) return NULL;
	return JWSTR( (*m_pSigunguData)[sigunguIdx].name, (*m_pSigunguData)[sigunguIdx].name_len );
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    GetSigunguNameByDCode
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSigunguNameByDCode
  (JNIEnv * env, jobject obj, jlong DCode )
{
	int nLen;
	const u16 *	pName =	g_pSearchModule->GetSigunguName( DCode, &nLen );
	return JWSTR( pName, nLen );
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    JNI_GetSigunguData
 * Signature: (I)[J
 */
JNIEXPORT jlongArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1GetSigunguData
  (JNIEnv * env, jobject obj, jint sigunguIdx )
{
	if( m_pSigunguData == NULL ) return NULL;
	
	const int ARRAY_SIZE = 3;

	jlong value[ARRAY_SIZE];
	int nCnt = (*m_pSigunguData).size();

	if( nCnt == 0 || nCnt <= sigunguIdx )
	{
		value[0] = value[1] = value[2] = -1;
	}
	else
	{
		value[0] = (*m_pSigunguData)[sigunguIdx].pInfo->code;
		value[1] = (*m_pSigunguData)[sigunguIdx].pInfo->offset;
		value[2] = (*m_pSigunguData)[sigunguIdx].pInfo->road_ofs;
	}

	jlongArray ret = env->NewLongArray( ARRAY_SIZE );

	env->SetLongArrayRegion( ret, 0, ARRAY_SIZE, value );
	
	return ret;
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetDongCount
 * Signature: (I)I
 */

JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetDongCount
  (JNIEnv * env, jobject obj, jint sigunguIdx)
{
	if( m_pSigunguData == NULL ) return 0;

	g_sigunguIdx = sigunguIdx;
	m_pDongData = g_pSearchModule->GetDongData( g_sidoIdx, (*m_pSigunguData)[g_sigunguIdx].pInfo->offset );
	return (jint)(*m_pDongData).size();
}

/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    GetSearchDongCount
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSearchDongCount
  (JNIEnv * env, jobject obj, jstring text )
{
	int nLen = env->GetStringLength( text );
	const jchar * jText = env->GetStringChars( text, 0 );

	m_pDongData =  g_pSearchModule->SearchDongName(( u16 * ) jText, nLen );
	env->ReleaseStringChars( text, jText );

	return (jint)(*m_pDongData).size();
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetDongName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetDongName
  (JNIEnv * env, jobject obj, jint dongIdx )
{
	if( m_pDongData == NULL ) return NULL;
	return JWSTR( (*m_pDongData)[dongIdx].name, (*m_pDongData)[dongIdx].name_len );
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetDongNameByDCode
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetDongNameByDCode
  (JNIEnv * env, jobject obj, jlong DCode )
{
	int nLen;
	const u16 *	pName =	g_pSearchModule->GetDongName( DCode, &nLen );
	return JWSTR( pName, nLen );
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    JNI_GetDongData
 * Signature: (I)[J
 */
JNIEXPORT jlongArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1GetDongData
  (JNIEnv * env, jobject obj, jint dongIdx)
{
	if( m_pDongData == NULL ) return NULL;
	
	const int ARRAY_SIZE = 6;

	jlong value[ARRAY_SIZE];
	int nCnt = (*m_pDongData).size();

	if( nCnt == 0 || nCnt <= dongIdx )
	{
		value[0] = -1;
	}
	else
	{
		value[0] = (*m_pDongData)[dongIdx].info.code;
		value[1] = (*m_pDongData)[dongIdx].info.min_x;
		value[2] = (*m_pDongData)[dongIdx].info.min_y;
		value[3] = (*m_pDongData)[dongIdx].info.ldong_flag;
		value[4] = (*m_pDongData)[dongIdx].info.bunji_cnt;
		value[5] = (*m_pDongData)[dongIdx].info.offset;
	}

	jlongArray ret = env->NewLongArray( ARRAY_SIZE );

	env->SetLongArrayRegion( ret, 0, ARRAY_SIZE, value );
	
	return ret;
}


/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetBunjiCount
 * Signature: (I)I
 */

JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetBunjiCount
  (JNIEnv * env, jobject obj, jint dongIdx ) 
{
	if( m_pDongData == NULL ) return NULL;

	g_dongIdx = dongIdx;

	DONG_DATA * pDong = &(*m_pDongData)[g_dongIdx];
	m_pBunjiData = g_pSearchModule->GetBunjiData( pDong->nSidoIdx, pDong, false );

	return (jint)(*m_pBunjiData).size();
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetBunjiValue
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetBunjiValue
  (JNIEnv * env, jobject obj, jint bunjiIdx )
{
	if( m_pBunjiData == NULL ) return NULL;
	return (jint)(*m_pBunjiData)[bunjiIdx].bunji;
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetHoCount
 * Signature: (I)I
 */

JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetHoCount
  (JNIEnv * env, jobject obj, jint bunjiIdx )
{
	if( m_pBunjiData == NULL ) return 0;

	g_bunjiIdx = bunjiIdx;

	DONG_DATA * pDong = ( DONG_DATA * )(*m_pBunjiData)[g_bunjiIdx].pDong;
	m_pHoData = g_pSearchModule->GetHoData( pDong->nSidoIdx, (*m_pBunjiData)[g_bunjiIdx].offset );
	return (jint)(*m_pHoData).size();
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetHoValue
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetHoValue
  (JNIEnv * env, jobject obj, jint hoIdx )
{
	if( m_pHoData == NULL ) return 0;
	return (jint) g_pSearchModule->GetHoValue( &(*m_pHoData)[hoIdx] );
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    JNI_GetHoData
 * Signature: (I)[J
 */
JNIEXPORT jlongArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1GetHoData
  (JNIEnv * env, jobject obj, jint hoIdx )
{
	if( m_pHoData == NULL ) return 0;
	
	DONG_DATA * pDong = ( DONG_DATA * )(*m_pBunjiData)[g_bunjiIdx].pDong;

	const int ARRAY_SIZE = 3;

	jlong value[ARRAY_SIZE];
	int nCnt = (*m_pHoData).size();

	if( nCnt == 0 || nCnt <= hoIdx )
	{
		value[0] = -1;
	}
	else
	{
		unsigned long x, y;
		g_pSearchModule->GetHoPos( pDong->nSidoIdx, &(*m_pHoData)[hoIdx], &x, &y );
		value[0] = g_pSearchModule->GetHoValue( &(*m_pHoData)[hoIdx] );
		value[1] = x + pDong->info.min_x;
		value[2] = y + pDong->info.min_y;
	}

	jlongArray ret = env->NewLongArray( ARRAY_SIZE );

	env->SetLongArrayRegion( ret, 0, ARRAY_SIZE, value );

	return ret;
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetRoadCount
 * Signature: (I)I
 */

JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetRoadCount
  (JNIEnv * env, jobject obj, jint sigunguIdx )
{
	if( m_pSigunguData == NULL ) return 0;

	m_pRoadData = g_pSearchModule->GetRoadData( g_sidoIdx, (*m_pSigunguData)[g_sigunguIdx].pInfo->offset );
	return (jint)(*m_pRoadData).size();
}

/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    GetSearchRoadCount
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSearchRoadCount
  (JNIEnv * env, jobject obj, jstring text )
{
	int nLen = env->GetStringLength( text );
	const jchar * jText = env->GetStringChars( text, 0 );

	m_pRoadData =  g_pSearchModule->SearchRoadName(( u16 * ) jText, nLen );
	env->ReleaseStringChars( text, jText );

	return (jint)(*m_pRoadData).size();
}


/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetRoadName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetRoadName
  (JNIEnv * env, jobject obj, jint roadIdx)
{
	if( m_pRoadData == NULL ) return NULL;
	return JWSTR( (*m_pRoadData)[roadIdx].name, (*m_pRoadData)[roadIdx].name_len );
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    JNI_GetRoadData
 * Signature: (I)[J
 */
JNIEXPORT jlongArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1GetRoadData
  (JNIEnv * env, jobject obj, jint roadIdx )
{
	if( m_pRoadData == NULL ) return NULL;
	
	const int ARRAY_SIZE = 3;

	jlong value[ARRAY_SIZE];
	int nCnt = (*m_pRoadData).size();

	if( nCnt == 0 || nCnt <= roadIdx )
	{
		value[0] = -1;
	}
	else
	{
		value[0] = (*m_pRoadData)[roadIdx].info.code;
		value[1] = (*m_pRoadData)[roadIdx].info.bunji_cnt;
		value[2] = (*m_pRoadData)[roadIdx].info.offset;
	}

	jlongArray ret = env->NewLongArray( ARRAY_SIZE );

	env->SetLongArrayRegion( ret, 0, ARRAY_SIZE, value );
	
	return ret;
}

/* Class:     unquantum_search_SearchModule
 * Method:    GetRoadBunjiCount
 * Signature: (I)I
 */

JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetRoadBunjiCount
  (JNIEnv * env, jobject obj, jint roadIdx )
{
	if( m_pRoadData == NULL ) return NULL;

	g_roadIdx = roadIdx;

	ROAD_DATA * pRoad = &(*m_pRoadData)[g_roadIdx];
	m_pBunjiData = g_pSearchModule->GetBunjiData( pRoad->nSidoIdx, pRoad, true, true );

	return (jint)(*m_pBunjiData).size();
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetRoadBunjiValue
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetRoadBunjiValue
  (JNIEnv * env, jobject obj, jint roadBunjiIdx )
{
	if( m_pBunjiData == NULL ) return -1;
	return (jint)(*m_pBunjiData)[roadBunjiIdx].bunji;
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetRoadHoCount
 * Signature: (I)I
 */

JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetRoadHoCount
  (JNIEnv * env, jobject obj, jint roadBunjiIdx )
{
	if( m_pBunjiData == NULL ) return 0;
	
	g_roadBunjiIdx = roadBunjiIdx;

	ROAD_DATA * pRoad = &(*m_pRoadData)[g_roadIdx];
	m_pNewHoData = g_pSearchModule->GetNewHoData( pRoad->nSidoIdx, (*m_pBunjiData)[roadBunjiIdx].offset );
	
	return (jint)(*m_pNewHoData).size();
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetRoadHoValue
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetRoadHoValue
  (JNIEnv * env, jobject obj, jint roadHoIdx )
{
	if( m_pNewHoData == NULL ) return -1;
	return (jint)(*m_pNewHoData)[roadHoIdx].ho;
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    JNI_GetRoadHoData
 * Signature: (I)[J
 */
JNIEXPORT jlongArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1GetRoadHoData
  (JNIEnv * env, jobject obj, jint roadHoIdx )
{
	if( m_pNewHoData == NULL ) return NULL;
	
	const int ARRAY_SIZE = 6;

	jlong value[ARRAY_SIZE];
	int nCnt = (*m_pNewHoData).size();

	if( nCnt == 0 || nCnt <= roadHoIdx )
	{
		value[0] = -1;
	}
	else
	{
		ROAD_DATA * pRoad = &(*m_pRoadData)[g_roadIdx];
		HO_DATA ho;
		NEW_HO_DATA * pNewHo = &(*m_pNewHoData)[roadHoIdx];

		unsigned long x, y;
		unsigned long nDCode = g_pSearchModule->GetNewHoPos( pRoad->nSidoIdx, pNewHo, &x, &y, &ho );

		value[0] = pNewHo->ho;
		value[1] = pNewHo->o_bunji;
		value[2] = g_pSearchModule->GetHoValue( &ho );
		value[3] = pRoad->nSidoIdx * 100000000 + pRoad->pSigunguF->code * 100000 + nDCode;
		value[4] = x;
		value[5] = y;

		//LOG( 3, "UI", "sido:%d, sigungu:%d, dong:%d", pRoad->nSidoIdx << 100000000, pRoad->pSigunguF->code, nDCode );
	}

	jlongArray ret = env->NewLongArray( ARRAY_SIZE );

	env->SetLongArrayRegion( ret, 0, ARRAY_SIZE, value );
	
	return ret;
}

/*
 * Class:     unquantum_search_SearchModule
 * Method:    GetAptCount
 * Signature: (I)I
 */

JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetAptCount
  (JNIEnv * env, jobject obj, jint dongIdx )
{
	if( m_pDongData == NULL ) return 0;

	g_dongIdx = dongIdx;

	m_pPoiDatas				= NULL;
	//m_pPoiDatasIterator		= NULL;
	m_bChangedPoiIterator	= true;
	g_poiHeaderIdx			= -1;
	
	DONG_DATA * pDong = &(*m_pDongData)[g_dongIdx];
	m_pPoiDatas = g_pSearchModule->SearchPoiCateDong( pDong, 'P' - 'A', 1, 1 );

	return (jint)(*m_pPoiDatas).size();
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    GetCatePoiCount
 * Signature: (Ljava/lang/String;IZ)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetCatePoiCount
  (JNIEnv * env, jobject obj, jstring code, jint cateType, jboolean addData )
{
	m_pPoiDatas				= NULL;
	//m_pPoiDatasIterator		= NULL;
	m_bChangedPoiIterator	= true;
	g_poiHeaderIdx			= -1;

	int nSidoIdx = -1;
	DONG_DATA * pDong = NULL;
	u32 nMeshCode = 0;

	CString strCode( env, &code );
	char * cateCode = ( char * ) strCode.toString();

	char * pCode1 = strtok( cateCode, "-");
	char * pCode2 = strtok( NULL, "-");
	char * pCode3 = strtok( NULL, "-");

	u16 code1 = pCode1[0] - 'A';
	u16 code2 = (u16) atoi( pCode2 );
	u16 code3 = pCode3 == NULL ? 0 : strlen( pCode3 ) < 1 ? 0 : (u16)atoi( pCode3 );

	switch( cateType )
	{
	case	0 :		// 시도
		// 현재 시도가 어딘지 찾는다.
		nSidoIdx = 0;
		m_pPoiDatas = g_pSearchModule->SearchPoiCateSido( nSidoIdx, code1, code2, code3, !addData );
		break;

	case	1 :		// 동
		// 현재 동이 어딘지 찾는다.
		pDong = NULL;
		m_pPoiDatas = g_pSearchModule->SearchPoiCateDong( pDong, code1, code2, code3, !addData );
		break;

	case	2 :		// 메쉬
		// 현재 메쉬 주변값을 찾는다.
		nMeshCode = 0;
		m_pPoiDatas = g_pSearchModule->SearchPoiCateMesh( nMeshCode, code1, code2, code3, !addData );
		break;
	}
	
	if( m_pPoiDatas == NULL ) return 0;
	return (jint)(*m_pPoiDatas).size();
}


/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    GetSearchPoiNameCount
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSearchPoiNameCount
  (JNIEnv * env, jobject obj, jint sidoIdx, jstring text )
{
	m_pPoiDatas				= NULL;
	m_bChangedPoiIterator	= true;
	g_poiHeaderIdx			= -1;
	
	int nLen = env->GetStringLength( text );
	const jchar * jText = env->GetStringChars( text, 0 );

	if( sidoIdx == -1 )
		m_pPoiDatas = g_pSearchModule->SearchPoiName((u16*)jText, nLen );
	else
		m_pPoiDatas = g_pSearchModule->SearchPoiName( sidoIdx, (u16*)jText, nLen );
	env->ReleaseStringChars( text, jText );

	return (jint)(*m_pPoiDatas).size();
}

/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    GetSearchPoiCallCount
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_kr_co_uniquantum_search_SearchModule_GetSearchPoiCallCount
  (JNIEnv * env, jobject obj, jstring text)
{
	m_pPoiDatas				= NULL;
	m_bChangedPoiIterator	= true;
	g_poiHeaderIdx			= -1;

	int nLen = env->GetStringLength( text );
	const jchar * jText = env->GetStringChars( text, 0 );

	m_pPoiDatas = g_pSearchModule->SearchTelNumber((u16*)jText, nLen );
	env->ReleaseStringChars( text, jText );

	return (jint)(*m_pPoiDatas).size();
}


/*
 * Class:     uniquantum_search_SearchModule
 * Method:    GetPoiName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetPoiName__I
  (JNIEnv * env, jobject obj, jint poiIdx )
{
	if( m_pPoiDatas == NULL ) return NULL;

	std::multimap<tstring,POI_HEADER>::iterator it = m_pPoiDatas->begin();// + nAptIdx;
	for( int i = 0; i < poiIdx; ++i, ++it );
	
	return JWSTR( (*it).first.c_str(), (*it).first.size());	
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    GetPoiName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetPoiName__
  (JNIEnv * env, jobject obj)
{
	//if( m_pPoiDatasIterator == NULL ) return NULL;
	return JWSTR( (*m_pPoiDatasIterator).first.c_str(), (*m_pPoiDatasIterator).first.size());	
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    JNI_GetPoiBranchName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetPoiBranchName__I
  (JNIEnv * env, jobject obj, jint poiIdx )
{
	if( m_pPoiDatas == NULL ) return NULL;
	
	if( m_pPoiBody == NULL || g_poiHeaderIdx != poiIdx )
	{
		std::multimap<tstring,POI_HEADER>::iterator it = m_pPoiDatas->begin();// + nAptIdx;
		for( int i = 0; i < poiIdx; ++i, ++it );

		m_pPoiHeader = &(*it).second;
		m_pPoiBody	 = g_pSearchModule->GetSearchPoiResult( m_pPoiHeader );
		g_poiHeaderIdx = poiIdx;
	}

	return m_pPoiBody->info.branch == 1 ? JWSTR( m_pPoiBody->branchName, u16len( m_pPoiBody->branchName )) : NULL;
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    GetPoiBranchName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetPoiBranchName__
  (JNIEnv * env, jobject obj)
{
	//if( m_pPoiDatasIterator == NULL ) return NULL;
	
	if( m_bChangedPoiIterator )
	{
		m_pPoiHeader = &(*m_pPoiDatasIterator).second;
		m_pPoiBody	 = g_pSearchModule->GetSearchPoiResult( m_pPoiHeader );
		m_bChangedPoiIterator = false;
	}
	
	return m_pPoiBody->info.branch == 1 ? JWSTR( m_pPoiBody->branchName, u16len( m_pPoiBody->branchName )) : NULL;
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    JNI_GetPoiCallNumber
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetPoiCallNumber__I
  (JNIEnv * env, jobject obj, jint poiIdx )
{
	if( m_pPoiBody == NULL || g_poiHeaderIdx != poiIdx )
	{
		std::multimap<tstring,POI_HEADER>::iterator it = m_pPoiDatas->begin();// + nAptIdx;
		for( int i = 0; i < poiIdx; ++i, ++it );

		m_pPoiHeader = &(*it).second;
		m_pPoiBody	 = g_pSearchModule->GetSearchPoiResult( m_pPoiHeader );
		g_poiHeaderIdx = poiIdx;
	}

	return m_pPoiBody->info.tel_cnt > 0 ? JSTR( m_pPoiBody->tel[0] ) : NULL;
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    GetPoiCallNumber
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_kr_co_uniquantum_search_SearchModule_GetPoiCallNumber__
  (JNIEnv * env, jobject obj)
{
	//if( m_pPoiDatasIterator == NULL ) return NULL;
	
	if( m_bChangedPoiIterator )
	{
		m_pPoiHeader = &(*m_pPoiDatasIterator).second;
		m_pPoiBody	 = g_pSearchModule->GetSearchPoiResult( m_pPoiHeader );
		m_bChangedPoiIterator = false;
	}

	return m_pPoiBody->info.tel_cnt > 0 ? JSTR( m_pPoiBody->tel[0] ) : NULL;
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    JNI_GetPoiData
 * Signature: (I)[J
 */
JNIEXPORT jlongArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1GetPoiData__I
  (JNIEnv * env, jobject obj, jint poiIdx )
{
	if( m_pPoiDatas == NULL ) return NULL;

	const int ARRAY_SIZE = 11;

	jlong value[ARRAY_SIZE];
	int nCnt = (*m_pPoiDatas).size();

	if( nCnt == 0 || nCnt <= poiIdx )
	{
		value[0] = -1;
	}
	else
	{
		if( m_pPoiBody == NULL || g_poiHeaderIdx != poiIdx )
		{
			std::multimap<tstring,POI_HEADER>::iterator it = m_pPoiDatas->begin();// + nAptIdx;
			for( int i = 0; i < poiIdx; ++i, ++it );

			m_pPoiHeader = &(*it).second;
			m_pPoiBody	 = g_pSearchModule->GetSearchPoiResult( m_pPoiHeader );
			g_poiHeaderIdx = poiIdx;
		}
		
		value[0] = m_pPoiHeader->nSidoIdx;			// sido idx
		value[1] = m_pPoiBody->info.hDCode;			// DCode
		value[2] = m_pPoiHeader->info.cate_flag;
		value[3] = m_pPoiBody->info.san;
		value[4] = m_pPoiBody->info.parking;
		value[5] = m_pPoiBody->info.bunji;
		value[6] = m_pPoiBody->info.ho;
		value[7] = m_pPoiBody->info.x;
		value[8] = m_pPoiBody->info.y;
		value[9] = m_pPoiBody->x_ex;
		value[10] = m_pPoiBody->y_ex;

	}

	jlongArray ret = env->NewLongArray( ARRAY_SIZE );

	env->SetLongArrayRegion( ret, 0, ARRAY_SIZE, value );
	
	return ret;
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    JNI_GetPoiData
 * Signature: ()[J
 */
JNIEXPORT jlongArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1GetPoiData__
  (JNIEnv * env, jobject obj)
{
	//if( m_pPoiDatasIterator == NULL ) return NULL;
	
	if( m_bChangedPoiIterator )
	{
		m_pPoiHeader = &(*m_pPoiDatasIterator).second;
		m_pPoiBody	 = g_pSearchModule->GetSearchPoiResult( m_pPoiHeader );
		m_bChangedPoiIterator = false;
	}

	const int ARRAY_SIZE = 11;
	jlong value[ARRAY_SIZE];

	value[0] = m_pPoiHeader->nSidoIdx;			// sido idx
	value[1] = m_pPoiBody->info.hDCode;			// DCode
	value[2] = m_pPoiHeader->info.cate_flag;
	value[3] = m_pPoiBody->info.san;
	value[4] = m_pPoiBody->info.parking;
	value[5] = m_pPoiBody->info.bunji;
	value[6] = m_pPoiBody->info.ho;
	value[7] = m_pPoiBody->info.x;
	value[8] = m_pPoiBody->info.y;
	value[9] = m_pPoiBody->x_ex;
	value[10] = m_pPoiBody->y_ex;

	jlongArray ret = env->NewLongArray( ARRAY_SIZE );

	env->SetLongArrayRegion( ret, 0, ARRAY_SIZE, value );
	
	return ret;
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    MoveFirstPoiData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_kr_co_uniquantum_search_SearchModule_MoveFirstPoiData
  (JNIEnv * env, jobject obj)
{
	if( m_pPoiDatas == NULL ) return false;

	m_bChangedPoiIterator = true;

	m_pPoiDatasIterator = m_pPoiDatas->begin();
	if( m_pPoiDatasIterator != m_pPoiDatas->end()) return true;
	return false;
}

/*
 * Class:     uniquantum_search_SearchModule
 * Method:    MoveNextPoiData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_kr_co_uniquantum_search_SearchModule_MoveNextPoiData
  (JNIEnv * env, jobject obj)
{
	if( m_pPoiDatas == NULL /*|| m_pPoiDatasIterator == NULL*/ ) return false;
	++ m_pPoiDatasIterator;

	m_bChangedPoiIterator = true;

	if( m_pPoiDatasIterator != m_pPoiDatas->end()) return true;
	return false;
}

/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    IsCatePoi
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_kr_co_uniquantum_search_SearchModule_IsCatePoi__I
  (JNIEnv * env, jobject obj, jint poiIdx )
{
	if( m_pPoiDatas == NULL ) return false;

	std::multimap<tstring,POI_HEADER>::iterator it = m_pPoiDatas->begin();// + nAptIdx;
	for( int i = 0; i < poiIdx; ++i, ++it );

	if( (*it).second.info.cate_flag == 0 ) return false;
	return true;
}

/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    IsCatePoi
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_kr_co_uniquantum_search_SearchModule_IsCatePoi__
  (JNIEnv * env, jobject obj)
{
	if( m_bChangedPoiIterator )
	{
		m_pPoiHeader = &(*m_pPoiDatasIterator).second;
		m_pPoiBody	 = g_pSearchModule->GetSearchPoiResult( m_pPoiHeader );
		m_bChangedPoiIterator = false;
	}

	if( m_pPoiHeader == NULL ) return false;
	if( m_pPoiHeader->info.cate_flag == 0 ) return false;
	return true;
}

/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    SetLocationPos
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_kr_co_uniquantum_search_SearchModule_SetLocationPos
  (JNIEnv * env, jobject obj, jlong lon, jlong lat )
{
	if( g_pAdminBound == NULL )	return;
	g_pAdminBound->findBound( lat, lon );
}

/*
 * Class:     kr_co_uniquantum_search_SearchModule
 * Method:    JNI_ChangedLocationCode
 * Signature: ()[J
 */
JNIEXPORT jlongArray JNICALL Java_kr_co_uniquantum_search_SearchModule_JNI_1ChangedLocationCode
  (JNIEnv * env, jobject obj)
{
	const int ARRAY_SIZE = 4;
	jlong value[ARRAY_SIZE];

	value[0] = 0;

	if( g_pAdminBound != NULL )
	{
		u8 changed_flag = g_pAdminBound->if_changed_hcode();

		if( changed_flag != 0 )
		{
			HCODE hCode = g_pAdminBound->getHCode();

			int sidoIdx = 0;
			for( sidoIdx = 0; sidoIdx < MAX_SEARCH_SIDO_CNT && g_sido_info[sidoIdx].sidoCode != hCode.sido; ++sidoIdx );

			value[0] = sidoIdx * 100000000 + hCode.gugun * 100000 + hCode.dong;
			value[1] = changed_flag & SIDO_BIT;
			value[2] = changed_flag & SIGUNGU_BIT;
			value[3] = changed_flag & DONG_BIT;
		}
	}
	
	jlongArray ret = env->NewLongArray( ARRAY_SIZE );
	env->SetLongArrayRegion( ret, 0, ARRAY_SIZE, value );
	return ret;
}