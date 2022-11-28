#
#### libavffmpeg.so编译源码
```
https://github.com/153437803/ijkplayer-k0.8.8-ff4.0
```

#
#### libexoplayer-ffmpeg.so编译源码
```
https://github.com/kalu-github/exoplayer2-ff4.4.3
```

#
####  exoplayer-extensions
```
- [X] av1 => 视频格式
- [X] cast => DRM支持
- [X] cronet => 网络库
- [✓] ffmpeg
- [X] flac => 音频解码库
- [X] ima => ad
- [X] leanback
- [X] media2
- [X] mediasession => 音频播放器
- [X] okhttp => 网络库
- [X] opus => 音频解码库
- [✓] rtmp
- [X] vp9 => 视频格式
- [X] workmanager
```

#
####  版本
```
exo => r2.18.2
lib_mediaplayer_core_exo_r2.18.2
```
```
vlc => r3.4.9
lib_mediaplayer_core_vlc_r3.4.9
```
```
ijk => k0.8.8、ff4.0--ijk0.8.8--20210426--001
lib_mediaplayer_core_ijk_k0.8.8_ff4.0
```

#
#### 更新
```
2022-11-25
1. 新增：exoplayer-ffmpeg
```
```
2022-11-24
1. exoplayer-2.18.2
```
```
2022-11-09
1. ijk增加log开关
```
```
2022-11-03
1. ijk增加armv7、arm64、x86、x86_64
```
```
2022-10-27
1. 新增cmake编译ijkplayer
```
```
2022-09-19
1. fix bug => exoplayer 子线程获取时长exception【com.google.android.exoplayer2.ExoPlayerImpl -> verifyApplicationThread】
```
```
2022-09-02
1.新增：全屏播放
2.新增：小窗播放
```
```
2022-08-05
1.新增：针对RecyclerView自动回收销毁机制, 增加autoRelease方法
```
```
2022-08-02
1.新增：支持试播，指定开始时间、试播时长
2.新增：支持视频指定配音音频，配音音频和原音音频可以切换
```
```
2022-07-21
1.新增vlc-r3.4.9
```
```
2022-06-29
1.更新exoplayer-r2.18.0
2.编译ijk-so
```
```
2022-06-02
1.解决快进bug
```
```
2022-02-19
1.更新ffmpeg4.0版本, ijk软解
```
```
2022-02-11
1. 新增ijkplayer硬解
```
```
2021-12-19
1. 解决android6.0 crash [指定版本=>'com.google.guava:guava:30.1-android']
```
```
2021-12-15
1. 更新exoplayer2.16.1
```
```
2021-11-23
1. 优化ui
2. 删除冗余模块
3. 添加rtsp支持
```
```
2021-09-29
1. 更新exoplayer-2.15.0
2. 重构lib_mediaplayer_ui模块
```

#
####  计划
```
- [✓] 支持TV
- [✓] 支持exoplayer硬解
- [✓] 支持ijkplayer软解
- [✓] 编译更新ffmpeg4.0
```

#
#### 资料
```
https://github.com/google/ExoPlayer
https://mvnrepository.com/search?q=exoplayer
https://github.com/bilibili/ijkplayer
https://github.com/bilibili/FFmpeg/tags
https://code.videolan.org/videolan/vlc-android
https://mvnrepository.com/artifact/org.videolan.android/libvlc-all
```

#
####  exoplayer-jar
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