#include <jni.h>
#include <android/log.h>

int mLog = 0;

const int get_log_status() {
    return mLog;
}

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