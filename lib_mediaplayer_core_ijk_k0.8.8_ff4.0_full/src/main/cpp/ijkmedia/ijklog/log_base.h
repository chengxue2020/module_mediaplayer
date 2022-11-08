/*
 * copyright (c) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * This file is part of jni4android.
 *
 * jni4android is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * jni4android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with jni4android; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#ifndef LOG_BASE_H
#define LOG_BASE_H

#define  LOGOPEN  0 //日志开关，1为开，其它为关

int _ijk_vprint_verbose(const char *tag, const char *fmt, ...);

int _ijk_vprint_debug(const char *tag, const char *fmt, ...);

int _ijk_vprint_info(const char *tag, const char *fmt, ...);

int _ijk_vprint_warning(const char *tag, const char *fmt, ...);

int _ijk_vprint_error(const char *tag, const char *fmt, ...);

int _ijk_print_verbose(const char *tag, const char *fmt, ...);

int _ijk_print_debug(const char *tag, const char *fmt, ...);

int _ijk_print_info(const char *tag, const char *fmt, ...);

int _ijk_print_warning(const char *tag, const char *fmt, ...);

int _ijk_print_error(const char *tag, const char *fmt, ...);

#endif//LOG_BASE_INTERNAL_H
