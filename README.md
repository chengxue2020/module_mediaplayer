#
#### 模块

![image](https://github.com/153437803/module_mediaplayer/blob/master/image/exoplayer.png )

#
####  版本
```
exo => 2.16.1
ijk => k0.8.8
ffmpeg => ff3.4--ijk0.8.7--20180103--001
```

#
####  计划
```
1. 支持TV
2. 支持exoplayer硬解
3. 支持ijkplayer软解
```

#
#### 更新
```
----------------------------------------

2022-02-11

1. 新增ijkplayer硬解

----------------------------------------

2021-12-19

1. 解决android6.0 crash [指定版本=>'com.google.guava:guava:30.1-android']

----------------------------------------

2021-12-15

1. 更新exoplayer2.16.1

----------------------------------------

2021-11-23

1. 优化ui
2. 删除冗余模块
3. 添加rtsp支持

---------------------------------------

2021-09-29

1. 更新exoplayer-2.15.0
2. 重构lib_mediaplayer_ui模块
```

#
#### Jar
```
files('libs/androidx-annotation-1.2.0.jar')
files('libs/commons-io-2.5.jar')
files('libs/guava-30.1-android.jar')
files('libs/failureaccess-1.0.1.jar')
files('libs/checker-qual-2.5.0.jar')
files('libs/checker-compat-qual-2.5.0.jar')
files('libs/j2objc-annotations-1.3.jar')
files('libs/error_prone_annotations-2.10.0.jar')
files('libs/annotations-3.0.1.jar')
```

#
#### 资料
```
https://github.com/google/ExoPlayer
https://mvnrepository.com/search?q=exoplayer
https://github.com/bilibili/ijkplayer
https://github.com/bilibili/FFmpeg/tags
https://github.com/yangchong211/YCVideoPlayer
```

#
#### 记录
```
1. android 6.0 crash[指定版本=>'com.google.guava:guava:30.1-android']
```

#
#### ffmpeg
```
libavutil：核心工具库，该模块是最基本的模块之一，其它这么多模块会依赖此模块做一些音视频处理操作。
libavformat： 文件格式和协议库，该模块是最重要的模块之一，封装了Protocol层、Demuxer层、muxer层，使用协议和格式对于开发者是透明的。
libavcodec: 编解码库，该模块也是最重要模块之一，封装了Codec层，但是有一些Codec是具备自己的License的，FFmpeg是不会默认添加，例如libx264,FDK-AAC, lame等库，但FFmpeg就像一个平台一样，可以将其它的第三方的Codec以插件的方式添加进来，然后为开发者提供统一的接口。
libswrsample：音频重采样库，可以对数字音频进行声道数、数据格式、采样率等多种基本信息的转换。
libswscale：视频压缩和格式转换库，可以进行视频分辨率修改、YUV格式数据与RGB格式数据互换。
libavdevice：输入输出设备库，编译ffplay就需要确保该模块是打开的，时时也需要libSDL预先编译，因为该设备播放声音和播放视频使用的都是libSDL库。
libavfilter:音视频滤镜库，该模块包含了音频特效和视频特效的处理，在使用FFmpeg的API进行编解码的过程中，直接使用该模块为音视频数据做特效物理非常方便同时也非常高效的一种方式。
libpostproc:音视频后期处理库，当使用libavfilter的时候需要打开该模块开关，因为Filter中会使用该库中的一些基础函数。
```
![image](https://github.com/153437803/module_mediaplayer/blob/master/image/exoplayer.png )
