#include <stdio.h>
#include <stdarg.h>
#include <jni.h>
#include <android/log.h>
#include "log_base.h"

int mLog = 0;

JNIEXPORT jint JNICALL
Java_tv_danmaku_ijk_media_player_IjkMediaPlayer_native_1setLogger(JNIEnv *env, jclass clazz,
                                                                  jboolean enable) {
    if (enable) {
        mLog = 1;
    } else {
        mLog = 0;
    }
    return 0;
}

int _ijk_vprint_verbose(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_vprint(ANDROID_LOG_VERBOSE, tag, fmt, ap);
    }
    return 0;
}

int _ijk_vprint_debug(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_print(ANDROID_LOG_DEBUG, tag, fmt, ap);
    }
    return 0;
}

int _ijk_vprint_info(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_vprint(ANDROID_LOG_INFO, tag, fmt, ap);
    }
    return 0;
}

int _ijk_vprint_warning(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_vprint(ANDROID_LOG_WARN, tag, fmt, ap);
    }
    return 0;
}

int _ijk_vprint_error(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_vprint(ANDROID_LOG_ERROR, tag, fmt, ap);
    }
    return 0;
}

int _ijk_print_verbose(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_print(ANDROID_LOG_VERBOSE, tag, fmt, ap);
    }
    return 0;
}

int _ijk_print_debug(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_print(ANDROID_LOG_DEBUG, tag, fmt, ap);
    }
    return 0;
}

int _ijk_print_info(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_print(ANDROID_LOG_INFO, tag, fmt, ap);
    }
    return 0;
}

int _ijk_print_warning(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_print(ANDROID_LOG_WARN, tag, fmt, ap);
    }
    return 0;
}

int _ijk_print_error(const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        va_list ap;
        va_start(ap, fmt);
        va_end(ap);
        __android_log_print(ANDROID_LOG_ERROR, tag, fmt, ap);
    }
    return 0;
}