/*****************************************************************************
 * ijksdl_log.h
 *****************************************************************************
 *
 * Copyright (c) 2015 Bilibili
 * copyright (c) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * This file is part of ijkPlayer.
 *
 * ijkPlayer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * ijkPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ijkPlayer; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#define IJKSDL__IJKSDL_LOG_H

#include <stdio.h>

#include <android/log.h>
#include "ijksdl_extra_log.h"
#include "log_base.h"

#define IJK_LOG_UNKNOWN     ANDROID_LOG_UNKNOWN
#define IJK_LOG_DEFAULT     ANDROID_LOG_DEFAULT

#define IJK_LOG_VERBOSE     ANDROID_LOG_VERBOSE
#define IJK_LOG_DEBUG       ANDROID_LOG_DEBUG
#define IJK_LOG_INFO        ANDROID_LOG_INFO
#define IJK_LOG_WARN        ANDROID_LOG_WARN
#define IJK_LOG_ERROR       ANDROID_LOG_ERROR
#define IJK_LOG_FATAL       ANDROID_LOG_FATAL
#define IJK_LOG_SILENT      ANDROID_LOG_SILENT

#define IJK_LOG_TAG "MP_IJK_SDL"

#define VLOGV(...)  VLOG(IJK_LOG_VERBOSE,   IJK_LOG_TAG, __VA_ARGS__)
#define VLOGD(...)  VLOG(IJK_LOG_DEBUG,     IJK_LOG_TAG, __VA_ARGS__)
#define VLOGI(...)  VLOG(IJK_LOG_INFO,      IJK_LOG_TAG, __VA_ARGS__)
#define VLOGW(...)  VLOG(IJK_LOG_WARN,      IJK_LOG_TAG, __VA_ARGS__)
#define VLOGE(...)  VLOG(IJK_LOG_ERROR,     IJK_LOG_TAG, __VA_ARGS__)
#define ALOGV(...)  ALOG(IJK_LOG_VERBOSE,   IJK_LOG_TAG, __VA_ARGS__)
#define ALOGD(...)  ALOG(IJK_LOG_DEBUG,     IJK_LOG_TAG, __VA_ARGS__)
#define ALOGI(...)  ALOG(IJK_LOG_INFO,      IJK_LOG_TAG, __VA_ARGS__)
#define ALOGW(...)  ALOG(IJK_LOG_WARN,      IJK_LOG_TAG, __VA_ARGS__)
#define ALOGE(...)  ALOG(IJK_LOG_ERROR,     IJK_LOG_TAG, __VA_ARGS__)
#define VLOG(level, TAG, ...)    ((void)_ijk_vprint_error(level, TAG, __VA_ARGS__))
#define ALOG(level, TAG, ...)    ((void)_ijk_print_error(level, TAG, __VA_ARGS__))
#define LOG_ALWAYS_FATAL(...)   do { ALOGE(__VA_ARGS__); exit(1); } while (0)