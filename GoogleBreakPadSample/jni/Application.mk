APP_STL := gnustl_static

APP_ABI := all
APP_ABI := armeabi x86 armeabi-v7a 
APP_ABI := armeabi

APP_CPPFLAGS += -std=gnu++11
APP_CPPFLAGS += -fexceptions
APP_CPPFLAGS += -frtti
#APP_CPPFLAGS += -lstdc++