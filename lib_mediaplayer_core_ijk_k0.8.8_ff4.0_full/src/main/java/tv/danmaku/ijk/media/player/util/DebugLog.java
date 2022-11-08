/*
 * Copyright (C) 2013 Bilibili
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>
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

package tv.danmaku.ijk.media.player.util;

import java.util.Locale;

public class DebugLog {

    public static void e(String tag, String msg) {
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        IjkLogUtil.log(tag + " => " + msg, tr);
    }

    public static void efmt(String tag, String fmt, Object... args) {
        String msg = String.format(Locale.US, fmt, args);
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void i(String tag, String msg) {
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        IjkLogUtil.log(tag + " => " + msg, tr);
    }

    public static void ifmt(String tag, String fmt, Object... args) {
        String msg = String.format(Locale.US, fmt, args);
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void w(String tag, String msg) {
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        IjkLogUtil.log(tag + " => " + msg, tr);
    }

    public static void wfmt(String tag, String fmt, Object... args) {
        String msg = String.format(Locale.US, fmt, args);
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void d(String tag, String msg) {
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        IjkLogUtil.log(tag + " => " + msg, tr);
    }

    public static void dfmt(String tag, String fmt, Object... args) {
        String msg = String.format(Locale.US, fmt, args);
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void v(String tag, String msg) {
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        IjkLogUtil.log(tag + " => " + msg, tr);
    }

    public static void vfmt(String tag, String fmt, Object... args) {
        String msg = String.format(Locale.US, fmt, args);
        IjkLogUtil.log(tag + " => " + msg);
    }

    public static void printStackTrace(Throwable e) {
        IjkLogUtil.log(e.getMessage(), e);
    }

    public static void printCause(Throwable e) {
        IjkLogUtil.log(e.getMessage(), e);
    }
}
