cmake_minimum_required(VERSION 3.4.1)

configure_file(${CMAKE_SOURCE_DIR}/sdl2/${ANDROID_ABI}/lib/libSDL2.a ${Project_BINARY_DIR}/libSDL2.a COPYONLY)
add_library(sdl2-lib STATIC IMPORTED)
set_target_properties(
        sdl2-lib
        PROPERTIES IMPORTED_LOCATION
        libSDL2.a)

# ffmpeg-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/libijkplayer-ffmpeg.so ${Project_BINARY_DIR}/libijkplayer-ffmpeg.so COPYONLY)
add_library(ffmpeg-lib
        SHARED
        IMPORTED)
set_target_properties(ffmpeg-lib
        PROPERTIES IMPORTED_LOCATION
        libijkplayer-ffmpeg.so)

## open-ssl-lib
#configure_file(${CMAKE_SOURCE_DIR}/openssl/${ANDROID_ABI}/lib/libssl.a ${Project_BINARY_DIR}/libssl.a COPYONLY)
#add_library(open-ssl-lib STATIC IMPORTED)
#set_target_properties(
#        open-ssl-lib
#        PROPERTIES IMPORTED_LOCATION
#        libssl.a)
#
## open-crypto-lib
#configure_file(${CMAKE_SOURCE_DIR}/openssl/${ANDROID_ABI}/lib/libcrypto.a ${Project_BINARY_DIR}/libcrypto.a COPYONLY)
#add_library(open-crypto-lib STATIC IMPORTED)
#set_target_properties(
#        open-crypto-lib
#        PROPERTIES IMPORTED_LOCATION
#        libcrypto.a)

## ffmpeg-avcodec-lib
#configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/lib/libavcodec.so ${Project_BINARY_DIR}/libavcodec.so COPYONLY)
#add_library(ffmpeg-avcodec-lib
#        SHARED
#        IMPORTED)
#set_target_properties(ffmpeg-avcodec-lib
#        PROPERTIES IMPORTED_LOCATION
#        libavcodec.so)
#
## ffmpeg-avformat-lib
#configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/lib/libavformat.so ${Project_BINARY_DIR}/libavformat.so COPYONLY)
#add_library(ffmpeg-avformat-lib
#        SHARED
#        IMPORTED)
#set_target_properties(ffmpeg-avformat-lib
#        PROPERTIES IMPORTED_LOCATION
#        libavformat.so)
#
## ffmpeg-avutil-lib
#configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/lib/libavutil.so ${Project_BINARY_DIR}/libavutil.so COPYONLY)
#add_library(ffmpeg-avutil-lib
#        SHARED
#        IMPORTED)
#set_target_properties(ffmpeg-avutil-lib
#        PROPERTIES IMPORTED_LOCATION
#        libavutil.so)
#
## ffmpeg-swresample-lib
#configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/lib/libswresample.so ${Project_BINARY_DIR}/libswresample.so COPYONLY)
#add_library(ffmpeg-swresample-lib
#        SHARED
#        IMPORTED)
#set_target_properties(ffmpeg-swresample-lib
#        PROPERTIES IMPORTED_LOCATION
#        libswresample.so)
#
## ffmpeg-swscale-lib
#configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/lib/libswscale.so ${Project_BINARY_DIR}/libswscale.so COPYONLY)
#add_library(ffmpeg-swscale-lib
#        SHARED
#        IMPORTED)
#set_target_properties(ffmpeg-swscale-lib
#        PROPERTIES IMPORTED_LOCATION
#        libswscale.so)

include_directories(${CMAKE_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/include)
include_directories(${CMAKE_SOURCE_DIR}/openssl/${ANDROID_ABI}/include)
include_directories(${CMAKE_SOURCE_DIR}/sdl2/${ANDROID_ABI}/include)
include_directories(${CMAKE_SOURCE_DIR}/sdl2/${ANDROID_ABI}/include-config-debug)
include_directories(${CMAKE_SOURCE_DIR}/player)


add_library( # Sets the name of the library.
        ffplayer_jni

        SHARED

        ${CMAKE_SOURCE_DIR}/player/ffplayer_api.cpp
        ${CMAKE_SOURCE_DIR}/player/mediaplayer.cpp
        ${CMAKE_SOURCE_DIR}/player/ffmpeg_mediaplayer.c
        ${CMAKE_SOURCE_DIR}/player/audioplayer.c
        ${CMAKE_SOURCE_DIR}/player/videoplayer.c
        ${CMAKE_SOURCE_DIR}/player/ffmpeg_utils.c)

target_link_libraries( # Specifies the target library.
        ffplayer_jni

        sdl2-lib
        ffmpeg-lib

#        open-ssl-lib
#        open-crypto-lib
#        ffmpeg-avcodec-lib
#        ffmpeg-avformat-lib
#        ffmpeg-avutil-lib
#        ffmpeg-swresample-lib
#        ffmpeg-swscale-lib

        m
        z
        dl
        OpenSLES
        GLESv1_CM
        GLESv2
        jnigraphics
        log
        android)