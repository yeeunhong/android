LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := AndroidSample
LOCAL_SRC_FILES := AndroidSample.cpp

LOCAL_LDLIBS 	:= -llog

include $(BUILD_SHARED_LIBRARY)