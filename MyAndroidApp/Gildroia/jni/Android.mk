# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH  := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := searchModule

LOCAL_DEFAULT_CPP_EXTENSION := cpp
LOCAL_CFLAGS += -I$(LOCAL_PATH)/include -DLINUX -D__NEW__ -D__SGI_STL_INTERNAL_PAIR_H -DANDROID -DOS_ANDROID
LOCAL_LDLIBS += -llog

# SEARCH_SRCS := SearchModuleHelper.cpp TextMng.cpp UmSearchModule.cpp 
SEARCH_SRCS := AdminBoundManager.cpp TextMng.cpp UmSearchModule.cpp SearchModule.cpp

LOCAL_SRC_FILES := $(SEARCH_SRCS)

include $(BUILD_SHARED_LIBRARY)
