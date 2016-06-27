LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := GoogleBreakPadSample
LOCAL_SRC_FILES := GoogleBreakPadSample.cpp

BREAKPAD_PATH	:= jni/google-breakpad
LOCAL_CFLAGS	:= -I$(BREAKPAD_PATH)/src/client/linux/handler
LOCAL_LDLIBS 	:= -llog

LOCAL_STATIC_LIBRARIES += breakpad_client

include $(BUILD_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE    := breakpad_client
#BREAKPAD_PATH	:= jni/google-breakpad
include $(BREAKPAD_PATH)/android/google_breakpad/Android.mk