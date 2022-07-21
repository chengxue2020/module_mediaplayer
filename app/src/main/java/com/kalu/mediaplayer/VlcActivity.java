package com.kalu.mediaplayer;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.util.ArrayList;

//  // "https://cdn.qupeiyin.cn/2021-02-28/1614507215953md525nwz.mp4"

public class VlcActivity extends AppCompatActivity {

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    private SurfaceView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vlc);

        textureView = findViewById(R.id.texture_video);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    protected void onStart() {
        super.onStart();

        final ArrayList args = new ArrayList<>();//VLC参数

        args.add("--rtsp-tcp");//强制rtsp-tcp，加快加载视频速度

        args.add("--aout=opensles");

        args.add("--audio-time-stretch");

        //args.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的

        args.add("-vvv");

        mLibVLC = new LibVLC(this, args);

        mMediaPlayer = new MediaPlayer(mLibVLC);

        Rect surfaceFrame = textureView.getHolder().getSurfaceFrame();

        //设置vlc视频铺满布局
        //
        //mMediaPlayer.getVLCVout().setWindowSize(layout_video.getWidth(), layout_video.getHeight());//宽，高  播放窗口的大小
        //
        //mMediaPlayer.setAspectRatio(layout_video.getWidth()+":"+layout_video.getHeight());//宽，高  画面大小

        mMediaPlayer.setScale(0);//这行必须加，为了让视图填满布局

        //添加视图

        IVLCVout vout = mMediaPlayer.getVLCVout();

        vout.setVideoView(textureView);

        vout.attachViews();

//        Uri uri = Uri.parse("https://cdn.qupeiyin.cn/2021-02-28/1614507215953md525nwz.mp4");//rtsp流地址或其他流地址//"https://media.w3.org/2010/05/sintel/trailer.mp4"
        Uri uri = Uri.parse("http://39.134.19.248:6610/yinhe/2/ch00000090990000001335/index.m3u8?virtualDomain=yinhe.live_hls.zte.com");//rtsp流地址或其他流地址//"https://media.w3.org/2010/05/sintel/trailer.mp4"

        final Media media = new Media(mLibVLC, uri);

        int cache = 10;

        media.addOption(":network-caching=" + cache);

        media.addOption(":file-caching=" + cache);

        media.addOption(":live-cacheing=" + cache);

        media.addOption(":sout-mux-caching=" + cache);

        media.addOption(":codec=mediacodec,iomx,all");

        mMediaPlayer.setMedia(media);//

        media.setHWDecoderEnabled(false, false);//设置后才可以录制和截屏,这行必须放在mMediaPlayer.setMedia(media)后面，因为setMedia会设置setHWDecoderEnabled为true

        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                if (event.type == MediaPlayer.Event.Playing) {
                    Toast.makeText(getApplicationContext(), "正在播放", Toast.LENGTH_SHORT).show();
                } else {

                }

            }
        });
        mMediaPlayer.play();

    }

    @Override
    protected void onStop() {
        super.onStop();

        mMediaPlayer.stop();
//        mMediaPlayer.release();
    }
}