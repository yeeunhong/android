#include <jni.h>
#include <android/log.h>

#include <stdio.h>

#include "exception_handler.h"
#include "minidump_descriptor.h"

#define LOG_TAG		"BreakPad"
#define LOGD(...)   __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGT()		__android_log_print(ANDROID_LOG_INFO, LOG_TAG, "%s:%d", __FILE__,__LINE__)

void Crash() {
	LOGT();

	char * p = NULL;
	memset( p, 0, 10 );
}

bool DumpCallback(const google_breakpad::MinidumpDescriptor& descriptor,
                  void* context,
                  bool succeeded) {
	LOGT();
	LOGD("Dump path: %s\n", descriptor.path());

	return succeeded;
}


jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();

	//google_breakpad::MinidumpDescriptor descriptor("/mnt/sdcard/download");
	//google_breakpad::ExceptionHandler eh(descriptor, NULL, DumpCallback, NULL, true, -1);


	//Crash();
    return JNI_VERSION_1_4;
}


