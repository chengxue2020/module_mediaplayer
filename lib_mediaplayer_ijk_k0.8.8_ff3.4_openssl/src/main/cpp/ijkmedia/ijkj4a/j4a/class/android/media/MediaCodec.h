/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * https://github.com/Bilibili/jni4android
 * This file is automatically generated by jni4android, do not modify.
 */

#ifndef J4A__android_media_MediaCodec__H
#define J4A__android_media_MediaCodec__H

#include "ijkj4a/j4a/j4a_base.h"

jint J4AC_android_media_MediaCodec__BufferInfo__flags__get(JNIEnv *env, jobject thiz);
jint J4AC_android_media_MediaCodec__BufferInfo__flags__get__catchAll(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__BufferInfo__flags__set(JNIEnv *env, jobject thiz, jint value);
void J4AC_android_media_MediaCodec__BufferInfo__flags__set__catchAll(JNIEnv *env, jobject thiz, jint value);
jint J4AC_android_media_MediaCodec__BufferInfo__offset__get(JNIEnv *env, jobject thiz);
jint J4AC_android_media_MediaCodec__BufferInfo__offset__get__catchAll(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__BufferInfo__offset__set(JNIEnv *env, jobject thiz, jint value);
void J4AC_android_media_MediaCodec__BufferInfo__offset__set__catchAll(JNIEnv *env, jobject thiz, jint value);
jlong J4AC_android_media_MediaCodec__BufferInfo__presentationTimeUs__get(JNIEnv *env, jobject thiz);
jlong J4AC_android_media_MediaCodec__BufferInfo__presentationTimeUs__get__catchAll(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__BufferInfo__presentationTimeUs__set(JNIEnv *env, jobject thiz, jlong value);
void J4AC_android_media_MediaCodec__BufferInfo__presentationTimeUs__set__catchAll(JNIEnv *env, jobject thiz, jlong value);
jint J4AC_android_media_MediaCodec__BufferInfo__size__get(JNIEnv *env, jobject thiz);
jint J4AC_android_media_MediaCodec__BufferInfo__size__get__catchAll(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__BufferInfo__size__set(JNIEnv *env, jobject thiz, jint value);
void J4AC_android_media_MediaCodec__BufferInfo__size__set__catchAll(JNIEnv *env, jobject thiz, jint value);
jobject J4AC_android_media_MediaCodec__BufferInfo__BufferInfo(JNIEnv *env);
jobject J4AC_android_media_MediaCodec__BufferInfo__BufferInfo__catchAll(JNIEnv *env);
jobject J4AC_android_media_MediaCodec__BufferInfo__BufferInfo__asGlobalRef__catchAll(JNIEnv *env);
jobject J4AC_android_media_MediaCodec__createByCodecName(JNIEnv *env, jstring name);
jobject J4AC_android_media_MediaCodec__createByCodecName__catchAll(JNIEnv *env, jstring name);
jobject J4AC_android_media_MediaCodec__createByCodecName__asGlobalRef__catchAll(JNIEnv *env, jstring name);
jobject J4AC_android_media_MediaCodec__createByCodecName__withCString(JNIEnv *env, const char *name_cstr__);
jobject J4AC_android_media_MediaCodec__createByCodecName__withCString__catchAll(JNIEnv *env, const char *name_cstr__);
jobject J4AC_android_media_MediaCodec__createByCodecName__withCString__asGlobalRef__catchAll(JNIEnv *env, const char *name_cstr__);
void J4AC_android_media_MediaCodec__configure(JNIEnv *env, jobject thiz, jobject format, jobject surface, jobject crypto, jint flags);
void J4AC_android_media_MediaCodec__configure__catchAll(JNIEnv *env, jobject thiz, jobject format, jobject surface, jobject crypto, jint flags);
jobject J4AC_android_media_MediaCodec__getOutputFormat(JNIEnv *env, jobject thiz);
jobject J4AC_android_media_MediaCodec__getOutputFormat__catchAll(JNIEnv *env, jobject thiz);
jobject J4AC_android_media_MediaCodec__getOutputFormat__asGlobalRef__catchAll(JNIEnv *env, jobject thiz);
jobjectArray J4AC_android_media_MediaCodec__getInputBuffers(JNIEnv *env, jobject thiz);
jobjectArray J4AC_android_media_MediaCodec__getInputBuffers__catchAll(JNIEnv *env, jobject thiz);
jobjectArray J4AC_android_media_MediaCodec__getInputBuffers__asGlobalRef__catchAll(JNIEnv *env, jobject thiz);
jint J4AC_android_media_MediaCodec__dequeueInputBuffer(JNIEnv *env, jobject thiz, jlong timeoutUs);
jint J4AC_android_media_MediaCodec__dequeueInputBuffer__catchAll(JNIEnv *env, jobject thiz, jlong timeoutUs);
void J4AC_android_media_MediaCodec__queueInputBuffer(JNIEnv *env, jobject thiz, jint index, jint offset, jint size, jlong presentationTimeUs, jint flags);
void J4AC_android_media_MediaCodec__queueInputBuffer__catchAll(JNIEnv *env, jobject thiz, jint index, jint offset, jint size, jlong presentationTimeUs, jint flags);
jint J4AC_android_media_MediaCodec__dequeueOutputBuffer(JNIEnv *env, jobject thiz, jobject info, jlong timeoutUs);
jint J4AC_android_media_MediaCodec__dequeueOutputBuffer__catchAll(JNIEnv *env, jobject thiz, jobject info, jlong timeoutUs);
void J4AC_android_media_MediaCodec__releaseOutputBuffer(JNIEnv *env, jobject thiz, jint index, jboolean render);
void J4AC_android_media_MediaCodec__releaseOutputBuffer__catchAll(JNIEnv *env, jobject thiz, jint index, jboolean render);
void J4AC_android_media_MediaCodec__start(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__start__catchAll(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__stop(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__stop__catchAll(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__flush(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__flush__catchAll(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__release(JNIEnv *env, jobject thiz);
void J4AC_android_media_MediaCodec__release__catchAll(JNIEnv *env, jobject thiz);
int J4A_loadClass__J4AC_android_media_MediaCodec(JNIEnv *env);

#define J4A_HAVE_SIMPLE__J4AC_android_media_MediaCodec

#define J4AC_MediaCodec__BufferInfo__flags__get J4AC_android_media_MediaCodec__BufferInfo__flags__get
#define J4AC_MediaCodec__BufferInfo__flags__get__catchAll J4AC_android_media_MediaCodec__BufferInfo__flags__get__catchAll
#define J4AC_MediaCodec__BufferInfo__flags__set J4AC_android_media_MediaCodec__BufferInfo__flags__set
#define J4AC_MediaCodec__BufferInfo__flags__set__catchAll J4AC_android_media_MediaCodec__BufferInfo__flags__set__catchAll
#define J4AC_MediaCodec__BufferInfo__offset__get J4AC_android_media_MediaCodec__BufferInfo__offset__get
#define J4AC_MediaCodec__BufferInfo__offset__get__catchAll J4AC_android_media_MediaCodec__BufferInfo__offset__get__catchAll
#define J4AC_MediaCodec__BufferInfo__offset__set J4AC_android_media_MediaCodec__BufferInfo__offset__set
#define J4AC_MediaCodec__BufferInfo__offset__set__catchAll J4AC_android_media_MediaCodec__BufferInfo__offset__set__catchAll
#define J4AC_MediaCodec__BufferInfo__presentationTimeUs__get J4AC_android_media_MediaCodec__BufferInfo__presentationTimeUs__get
#define J4AC_MediaCodec__BufferInfo__presentationTimeUs__get__catchAll J4AC_android_media_MediaCodec__BufferInfo__presentationTimeUs__get__catchAll
#define J4AC_MediaCodec__BufferInfo__presentationTimeUs__set J4AC_android_media_MediaCodec__BufferInfo__presentationTimeUs__set
#define J4AC_MediaCodec__BufferInfo__presentationTimeUs__set__catchAll J4AC_android_media_MediaCodec__BufferInfo__presentationTimeUs__set__catchAll
#define J4AC_MediaCodec__BufferInfo__size__get J4AC_android_media_MediaCodec__BufferInfo__size__get
#define J4AC_MediaCodec__BufferInfo__size__get__catchAll J4AC_android_media_MediaCodec__BufferInfo__size__get__catchAll
#define J4AC_MediaCodec__BufferInfo__size__set J4AC_android_media_MediaCodec__BufferInfo__size__set
#define J4AC_MediaCodec__BufferInfo__size__set__catchAll J4AC_android_media_MediaCodec__BufferInfo__size__set__catchAll
#define J4AC_MediaCodec__BufferInfo__BufferInfo J4AC_android_media_MediaCodec__BufferInfo__BufferInfo
#define J4AC_MediaCodec__BufferInfo__BufferInfo__asGlobalRef__catchAll J4AC_android_media_MediaCodec__BufferInfo__BufferInfo__asGlobalRef__catchAll
#define J4AC_MediaCodec__BufferInfo__BufferInfo__catchAll J4AC_android_media_MediaCodec__BufferInfo__BufferInfo__catchAll
#define J4AC_MediaCodec__createByCodecName J4AC_android_media_MediaCodec__createByCodecName
#define J4AC_MediaCodec__createByCodecName__asGlobalRef__catchAll J4AC_android_media_MediaCodec__createByCodecName__asGlobalRef__catchAll
#define J4AC_MediaCodec__createByCodecName__catchAll J4AC_android_media_MediaCodec__createByCodecName__catchAll
#define J4AC_MediaCodec__createByCodecName__withCString J4AC_android_media_MediaCodec__createByCodecName__withCString
#define J4AC_MediaCodec__createByCodecName__withCString__asGlobalRef__catchAll J4AC_android_media_MediaCodec__createByCodecName__withCString__asGlobalRef__catchAll
#define J4AC_MediaCodec__createByCodecName__withCString__catchAll J4AC_android_media_MediaCodec__createByCodecName__withCString__catchAll
#define J4AC_MediaCodec__configure J4AC_android_media_MediaCodec__configure
#define J4AC_MediaCodec__configure__catchAll J4AC_android_media_MediaCodec__configure__catchAll
#define J4AC_MediaCodec__getOutputFormat J4AC_android_media_MediaCodec__getOutputFormat
#define J4AC_MediaCodec__getOutputFormat__asGlobalRef__catchAll J4AC_android_media_MediaCodec__getOutputFormat__asGlobalRef__catchAll
#define J4AC_MediaCodec__getOutputFormat__catchAll J4AC_android_media_MediaCodec__getOutputFormat__catchAll
#define J4AC_MediaCodec__getInputBuffers J4AC_android_media_MediaCodec__getInputBuffers
#define J4AC_MediaCodec__getInputBuffers__asGlobalRef__catchAll J4AC_android_media_MediaCodec__getInputBuffers__asGlobalRef__catchAll
#define J4AC_MediaCodec__getInputBuffers__catchAll J4AC_android_media_MediaCodec__getInputBuffers__catchAll
#define J4AC_MediaCodec__dequeueInputBuffer J4AC_android_media_MediaCodec__dequeueInputBuffer
#define J4AC_MediaCodec__dequeueInputBuffer__catchAll J4AC_android_media_MediaCodec__dequeueInputBuffer__catchAll
#define J4AC_MediaCodec__queueInputBuffer J4AC_android_media_MediaCodec__queueInputBuffer
#define J4AC_MediaCodec__queueInputBuffer__catchAll J4AC_android_media_MediaCodec__queueInputBuffer__catchAll
#define J4AC_MediaCodec__dequeueOutputBuffer J4AC_android_media_MediaCodec__dequeueOutputBuffer
#define J4AC_MediaCodec__dequeueOutputBuffer__catchAll J4AC_android_media_MediaCodec__dequeueOutputBuffer__catchAll
#define J4AC_MediaCodec__releaseOutputBuffer J4AC_android_media_MediaCodec__releaseOutputBuffer
#define J4AC_MediaCodec__releaseOutputBuffer__catchAll J4AC_android_media_MediaCodec__releaseOutputBuffer__catchAll
#define J4AC_MediaCodec__start J4AC_android_media_MediaCodec__start
#define J4AC_MediaCodec__start__catchAll J4AC_android_media_MediaCodec__start__catchAll
#define J4AC_MediaCodec__stop J4AC_android_media_MediaCodec__stop
#define J4AC_MediaCodec__stop__catchAll J4AC_android_media_MediaCodec__stop__catchAll
#define J4AC_MediaCodec__flush J4AC_android_media_MediaCodec__flush
#define J4AC_MediaCodec__flush__catchAll J4AC_android_media_MediaCodec__flush__catchAll
#define J4AC_MediaCodec__release J4AC_android_media_MediaCodec__release
#define J4AC_MediaCodec__release__catchAll J4AC_android_media_MediaCodec__release__catchAll
#define J4A_loadClass__J4AC_MediaCodec J4A_loadClass__J4AC_android_media_MediaCodec

#endif//J4A__android_media_MediaCodec__H
