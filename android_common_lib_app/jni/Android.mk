LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := android_common_lib_app
LOCAL_LDLIBS    := -llog -ldl
LOCAL_SRC_FILES := android_common_lib_app.cpp

include $(BUILD_SHARED_LIBRARY)
