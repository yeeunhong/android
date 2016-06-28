#include <jni.h>

#include <stdio.h>
#include <string.h>
#include <stdarg.h>

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>

#include <algorithm>
#include <cctype>
#include <functional>

#include <android/log.h>

void Log( const char *szFormat, ... );

// ����Ƽ�� �Լ��� ����� �ڹ� Ŭ����
static const char *classPathName = "app/inka/android/AndroidCommonLibAppSo";

static void	test_function(JNIEnv *env, jobject obj )
{
	Log( "11" );
	Log( "22" );
}


// jni.h ���� �����ϴ� JNINativeMethod ����ü �迭�� ����� JNI �޼��带 ����
static JNINativeMethod methods[] = {
	{"test_function", "()V", (void*)test_function },
};

/*
* Register several native methods for one class.
*/
static int registerNativeMethods(JNIEnv* env, const char* className,
	JNINativeMethod* gMethods, int numMethods)
{
	jclass clazz;
	clazz = env->FindClass(className);
	if (clazz == NULL) {
		Log("Native registration unable to find class '%s'", className);
		return JNI_FALSE;
	}

	//methods[] ���� ������ JNI �޼��带 ���� �Լ� ����� �ϴ� �����̴�.
	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		Log("RegisterNatives failed for '%s'", className);
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

/*
* Register native methods for all classes we know about.
*
* returns JNI_TRUE on success.
*/
static int registerNatives(JNIEnv* env)
{
	if (!registerNativeMethods(env, classPathName,
		methods, sizeof(methods) / sizeof(methods[0]))) {
			return JNI_FALSE;
	}

	return JNI_TRUE;
}

/*
* This is called by the VM when the shared library is first loaded.
*/
typedef union {
	JNIEnv* env;
	void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	UnionJNIEnvToVoid uenv;
	uenv.venv = NULL;
	jint result = -1;
	JNIEnv* env = NULL;

	Log("JNI_OnLoad");

	if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
		Log("ERROR: GetEnv failed");
		goto bail;
	}
	env = uenv.env;

	if (registerNatives(env) != JNI_TRUE) {
		Log("ERROR: registerNatives failed");
		goto bail;
	}

	result = JNI_VERSION_1_4;

bail:
	return result;
}


void Log( const char *szFormat, ... )
{
	char szLog[4096];

	va_list	args;
	va_start(args, szFormat);

	strcat( szLog, szFormat );
	__android_log_vprint(ANDROID_LOG_DEBUG, "_TEST_", szLog, args );

	va_end( args );
}
