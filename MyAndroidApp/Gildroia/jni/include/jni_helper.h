#ifndef _Included_unquantum_search_JNI_Helper
#define _Included_unquantum_search_JNI_Helper

#include <jni.h>
#include <android/log.h>

#define LOG			__android_log_print

#ifndef JSTR
#define JSTR(x)		env->NewStringUTF(x)
#endif
#ifndef JWSTR
#define JWSTR(x,n)		env->NewString(x,n)
#endif

class CString 
{
public :
	CString( JNIEnv * _env, jstring * _jstr )
		: env( _env )
		, jstr( _jstr )
	{ cstr	= env->GetStringUTFChars( *jstr, 0 );}
	~CString(){	env->ReleaseStringUTFChars( *jstr, cstr );}
	
	const char * toString(){ return cstr; }
	
protected :
	JNIEnv * env;
	jstring	* jstr;
	const char * cstr;
};

class JIntField
{
public :
	JIntField( JNIEnv * _env, jclass _cls, const char * int_field_name )
		: env( _env )
		, cls( _cls )
	{
		fid = env->GetFieldID( cls, int_field_name, "I" );
	}
	
	int 	GetInt(){ return env->GetIntField( cls, fid );}
	void	SetInt( int nValue ){ env->SetIntField( cls, fid, nValue ); }
protected :
	JNIEnv * 	env;
	jclass 		cls;
	jfieldID	fid;
	
};

class JStaticIntField
{
public :
	JStaticIntField( JNIEnv * _env, jclass _cls, const char * int_field_name )
		: env( _env )
		, cls( _cls )
	{
		fid = env->GetStaticFieldID( cls, int_field_name, "I" );
	}
	
	int 	GetStaticInt(){ return env->GetStaticIntField( cls, fid );}
	void	SetStaticInt( int nValue ){ env->SetStaticIntField( cls, fid, nValue ); }
protected :
	JNIEnv * 	env;
	jclass 		cls;
	jfieldID	fid;
};

#endif
