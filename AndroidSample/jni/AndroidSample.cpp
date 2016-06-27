#include <android/log.h>
#include <jni.h>

#include <stdio.h>

#define LOG_TAG		""

#define LOGV(...)   __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...)   __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...)   __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...)   __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...)   __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGT()		__android_log_print(ANDROID_LOG_INFO, LOG_TAG, "%s:%d", __FILE__,__LINE__)

JNIEnv * g_env = NULL;
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();

    jint result = vm->GetEnv((void**) &g_env, JNI_VERSION_1_6 );
    if( result != JNI_OK)
    {
        return result;
    }

    return JNI_VERSION_1_6;
}
