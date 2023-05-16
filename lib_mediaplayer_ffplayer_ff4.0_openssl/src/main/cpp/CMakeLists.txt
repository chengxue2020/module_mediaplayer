cmake_minimum_required(VERSION 3.4.1)

configure_file(${CMAKE_SOURCE_DIR}/sdl2/${ANDROID_ABI}/lib/libSDL2.a ${Project_BINARY_DIR}/libSDL2.a COPYONLY)
add_library(sdl2-lib STATIC IMPORTED)
set_target_properties(
        sdl2-lib
        PROPERTIES IMPORTED_LOCATION
        libSDL2.a)

# ffmpeg-avcodec-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libavcodec.a ${Project_BINARY_DIR}/libavcodec.a COPYONLY)
add_library(ffmpeg-avcodec-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avcodec-lib
        PROPERTIES IMPORTED_LOCATION
        libavcodec.a)

# ffmpeg-avformat-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libavformat.a ${Project_BINARY_DIR}/libavformat.a COPYONLY)
add_library(ffmpeg-avformat-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avformat-lib
        PROPERTIES IMPORTED_LOCATION
        libavformat.a)

# ffmpeg-avutil-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libavutil.a ${Project_BINARY_DIR}/libavutil.a COPYONLY)
add_library(ffmpeg-avutil-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avutil-lib
        PROPERTIES IMPORTED_LOCATION
        libavutil.a)

# ffmpeg-swresample-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libswresample.a ${Project_BINARY_DIR}/libswresample.a COPYONLY)
add_library(ffmpeg-swresample-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-swresample-lib
        PROPERTIES IMPORTED_LOCATION
        libswresample.a)

# ffmpeg-swscale-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libswscale.a ${Project_BINARY_DIR}/libswscale.a COPYONLY)
add_library(ffmpeg-swscale-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-swscale-lib
        PROPERTIES IMPORTED_LOCATION
        libswscale.a)

# open-ssl-lib
configure_file(${CMAKE_SOURCE_DIR}/openssl/${ANDROID_ABI}/lib/libssl.a ${Project_BINARY_DIR}/libssl.a COPYONLY)
add_library(open-ssl-lib STATIC IMPORTED)
set_target_properties(
        open-ssl-lib
        PROPERTIES IMPORTED_LOCATION
        libssl.a)

# open-crypto-lib
configure_file(${CMAKE_SOURCE_DIR}/openssl/${ANDROID_ABI}/lib/libcrypto.a ${Project_BINARY_DIR}/libcrypto.a COPYONLY)
add_library(open-crypto-lib STATIC IMPORTED)
set_target_properties(
        open-crypto-lib
        PROPERTIES IMPORTED_LOCATION
        libcrypto.a)

include_directories(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/include)
include_directories(${CMAKE_SOURCE_DIR}/openssl/${ANDROID_ABI}/include)
include_directories(${CMAKE_SOURCE_DIR}/sdl2/${ANDROID_ABI}/include)
include_directories(${CMAKE_SOURCE_DIR}/sdl2/${ANDROID_ABI}/include-config-debug)
include_directories(${CMAKE_SOURCE_DIR}/player)


add_library( # Sets the name of the library.
        ffplayer

        SHARED

        ${CMAKE_SOURCE_DIR}/player/ffplayer_api.cpp
        ${CMAKE_SOURCE_DIR}/player/mediaplayer.cpp
        ${CMAKE_SOURCE_DIR}/player/ffmpeg_mediaplayer.c
        ${CMAKE_SOURCE_DIR}/player/audioplayer.c
        ${CMAKE_SOURCE_DIR}/player/videoplayer.c
        ${CMAKE_SOURCE_DIR}/player/ffmpeg_utils.c)

target_link_libraries( # Specifies the target library.
        ffplayer

        # sdl2
        sdl2-lib

        # openssl
        open-ssl-lib
        open-crypto-lib

        # ffmpeg
        ffmpeg-avcodec-lib
        ffmpeg-avformat-lib
        ffmpeg-avutil-lib
        ffmpeg-swresample-lib
        ffmpeg-swscale-lib

        m
        z
        dl
        OpenSLES
        GLESv1_CM
        GLESv2
        jnigraphics
        log
        android)