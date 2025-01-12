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

#ifndef J4A__android_os_Bundle__H
#define J4A__android_os_Bundle__H

#include "ijkj4a/j4a/j4a_base.h"

jobject J4AC_android_os_Bundle__Bundle(JNIEnv *env);
jobject J4AC_android_os_Bundle__Bundle__catchAll(JNIEnv *env);
jobject J4AC_android_os_Bundle__Bundle__asGlobalRef__catchAll(JNIEnv *env);
jint J4AC_android_os_Bundle__getInt(JNIEnv *env, jobject thiz, jstring key, jint defaultValue);
jint J4AC_android_os_Bundle__getInt__catchAll(JNIEnv *env, jobject thiz, jstring key, jint defaultValue);
jint J4AC_android_os_Bundle__getInt__withCString(JNIEnv *env, jobject thiz, const char *key_cstr__, jint defaultValue);
jint J4AC_android_os_Bundle__getInt__withCString__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__, jint defaultValue);
void J4AC_android_os_Bundle__putInt(JNIEnv *env, jobject thiz, jstring key, jint value);
void J4AC_android_os_Bundle__putInt__catchAll(JNIEnv *env, jobject thiz, jstring key, jint value);
void J4AC_android_os_Bundle__putInt__withCString(JNIEnv *env, jobject thiz, const char *key_cstr__, jint value);
void J4AC_android_os_Bundle__putInt__withCString__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__, jint value);
jstring J4AC_android_os_Bundle__getString(JNIEnv *env, jobject thiz, jstring key);
jstring J4AC_android_os_Bundle__getString__catchAll(JNIEnv *env, jobject thiz, jstring key);
jstring J4AC_android_os_Bundle__getString__asGlobalRef__catchAll(JNIEnv *env, jobject thiz, jstring key);
const char *J4AC_android_os_Bundle__getString__asCBuffer(JNIEnv *env, jobject thiz, jstring key, char *out_buf, int out_len);
const char *J4AC_android_os_Bundle__getString__asCBuffer__catchAll(JNIEnv *env, jobject thiz, jstring key, char *out_buf, int out_len);
jstring J4AC_android_os_Bundle__getString__withCString(JNIEnv *env, jobject thiz, const char *key_cstr__);
jstring J4AC_android_os_Bundle__getString__withCString__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__);
jstring J4AC_android_os_Bundle__getString__withCString__asGlobalRef__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__);
const char *J4AC_android_os_Bundle__getString__withCString__asCBuffer(JNIEnv *env, jobject thiz, const char *key_cstr__, char *out_buf, int out_len);
const char *J4AC_android_os_Bundle__getString__withCString__asCBuffer__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__, char *out_buf, int out_len);
void J4AC_android_os_Bundle__putString(JNIEnv *env, jobject thiz, jstring key, jstring value);
void J4AC_android_os_Bundle__putString__catchAll(JNIEnv *env, jobject thiz, jstring key, jstring value);
void J4AC_android_os_Bundle__putString__withCString(JNIEnv *env, jobject thiz, const char *key_cstr__, const char *value_cstr__);
void J4AC_android_os_Bundle__putString__withCString__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__, const char *value_cstr__);
void J4AC_android_os_Bundle__putParcelableArrayList(JNIEnv *env, jobject thiz, jstring key, jobject value);
void J4AC_android_os_Bundle__putParcelableArrayList__catchAll(JNIEnv *env, jobject thiz, jstring key, jobject value);
void J4AC_android_os_Bundle__putParcelableArrayList__withCString(JNIEnv *env, jobject thiz, const char *key_cstr__, jobject value);
void J4AC_android_os_Bundle__putParcelableArrayList__withCString__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__, jobject value);
jlong J4AC_android_os_Bundle__getLong(JNIEnv *env, jobject thiz, jstring key);
jlong J4AC_android_os_Bundle__getLong__catchAll(JNIEnv *env, jobject thiz, jstring key);
jlong J4AC_android_os_Bundle__getLong__withCString(JNIEnv *env, jobject thiz, const char *key_cstr__);
jlong J4AC_android_os_Bundle__getLong__withCString__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__);
void J4AC_android_os_Bundle__putLong(JNIEnv *env, jobject thiz, jstring key, jlong value);
void J4AC_android_os_Bundle__putLong__catchAll(JNIEnv *env, jobject thiz, jstring key, jlong value);
void J4AC_android_os_Bundle__putLong__withCString(JNIEnv *env, jobject thiz, const char *key_cstr__, jlong value);
void J4AC_android_os_Bundle__putLong__withCString__catchAll(JNIEnv *env, jobject thiz, const char *key_cstr__, jlong value);
int J4A_loadClass__J4AC_android_os_Bundle(JNIEnv *env);

#define J4A_HAVE_SIMPLE__J4AC_android_os_Bundle

#define J4AC_Bundle__Bundle J4AC_android_os_Bundle__Bundle
#define J4AC_Bundle__Bundle__asGlobalRef__catchAll J4AC_android_os_Bundle__Bundle__asGlobalRef__catchAll
#define J4AC_Bundle__Bundle__catchAll J4AC_android_os_Bundle__Bundle__catchAll
#define J4AC_Bundle__getInt J4AC_android_os_Bundle__getInt
#define J4AC_Bundle__getInt__catchAll J4AC_android_os_Bundle__getInt__catchAll
#define J4AC_Bundle__getInt__withCString J4AC_android_os_Bundle__getInt__withCString
#define J4AC_Bundle__getInt__withCString__catchAll J4AC_android_os_Bundle__getInt__withCString__catchAll
#define J4AC_Bundle__putInt J4AC_android_os_Bundle__putInt
#define J4AC_Bundle__putInt__catchAll J4AC_android_os_Bundle__putInt__catchAll
#define J4AC_Bundle__putInt__withCString J4AC_android_os_Bundle__putInt__withCString
#define J4AC_Bundle__putInt__withCString__catchAll J4AC_android_os_Bundle__putInt__withCString__catchAll
#define J4AC_Bundle__getString J4AC_android_os_Bundle__getString
#define J4AC_Bundle__getString__asCBuffer J4AC_android_os_Bundle__getString__asCBuffer
#define J4AC_Bundle__getString__asCBuffer__catchAll J4AC_android_os_Bundle__getString__asCBuffer__catchAll
#define J4AC_Bundle__getString__asGlobalRef__catchAll J4AC_android_os_Bundle__getString__asGlobalRef__catchAll
#define J4AC_Bundle__getString__catchAll J4AC_android_os_Bundle__getString__catchAll
#define J4AC_Bundle__getString__withCString J4AC_android_os_Bundle__getString__withCString
#define J4AC_Bundle__getString__withCString__asCBuffer J4AC_android_os_Bundle__getString__withCString__asCBuffer
#define J4AC_Bundle__getString__withCString__asCBuffer__catchAll J4AC_android_os_Bundle__getString__withCString__asCBuffer__catchAll
#define J4AC_Bundle__getString__withCString__asGlobalRef__catchAll J4AC_android_os_Bundle__getString__withCString__asGlobalRef__catchAll
#define J4AC_Bundle__getString__withCString__catchAll J4AC_android_os_Bundle__getString__withCString__catchAll
#define J4AC_Bundle__putString J4AC_android_os_Bundle__putString
#define J4AC_Bundle__putString__catchAll J4AC_android_os_Bundle__putString__catchAll
#define J4AC_Bundle__putString__withCString J4AC_android_os_Bundle__putString__withCString
#define J4AC_Bundle__putString__withCString__catchAll J4AC_android_os_Bundle__putString__withCString__catchAll
#define J4AC_Bundle__putParcelableArrayList J4AC_android_os_Bundle__putParcelableArrayList
#define J4AC_Bundle__putParcelableArrayList__catchAll J4AC_android_os_Bundle__putParcelableArrayList__catchAll
#define J4AC_Bundle__putParcelableArrayList__withCString J4AC_android_os_Bundle__putParcelableArrayList__withCString
#define J4AC_Bundle__putParcelableArrayList__withCString__catchAll J4AC_android_os_Bundle__putParcelableArrayList__withCString__catchAll
#define J4AC_Bundle__getLong J4AC_android_os_Bundle__getLong
#define J4AC_Bundle__getLong__catchAll J4AC_android_os_Bundle__getLong__catchAll
#define J4AC_Bundle__getLong__withCString J4AC_android_os_Bundle__getLong__withCString
#define J4AC_Bundle__getLong__withCString__catchAll J4AC_android_os_Bundle__getLong__withCString__catchAll
#define J4AC_Bundle__putLong J4AC_android_os_Bundle__putLong
#define J4AC_Bundle__putLong__catchAll J4AC_android_os_Bundle__putLong__catchAll
#define J4AC_Bundle__putLong__withCString J4AC_android_os_Bundle__putLong__withCString
#define J4AC_Bundle__putLong__withCString__catchAll J4AC_android_os_Bundle__putLong__withCString__catchAll
#define J4A_loadClass__J4AC_Bundle J4A_loadClass__J4AC_android_os_Bundle

#endif//J4A__android_os_Bundle__H
