package com.kalu.mediaplayer.m3u8;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kalu.mediaplayer.newPlayer.activity.NormalActivity;
import lib.kalu.mediaplayer.videom3u8.bean.M3u8;
import lib.kalu.mediaplayer.videom3u8.inter.OnDownloadListener;
import lib.kalu.mediaplayer.videom3u8.inter.OnM3u8InfoListener;
import lib.kalu.mediaplayer.videom3u8.manager.M3u8InfoManger;
import lib.kalu.mediaplayer.videom3u8.manager.M3u8LiveManger;
import lib.kalu.mediaplayer.videom3u8.task.M3u8DownloadTask;
import lib.kalu.mediaplayer.videom3u8.utils.M3u8FileUtils;
import lib.kalu.mediaplayer.videom3u8.utils.NetSpeedUtils;

import com.kalu.mediaplayer.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.kalu.mediaplayer.videokernel.utils.VideoLogUtils;


public class M3u8Activity extends AppCompatActivity {
    //url随时可能失效
//    private String url = "https://yunqivedio.alicdn.com/2017yq/v2/0x0/96d79d3f5400514a6883869399708e11/96d79d3f5400514a6883869399708e11.m3u8";
    private String url = "https://vod.iartschool.com/793497282635890688_low.m3u8?sign=52a09ee9f0f03b0270339dab57712f9a&t=60990a84";
    private TextView tvSpeed1;
    private EditText etUrl;
    private TextView tvConsole;
    private TextView tvSaveFilePathTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3u8_view);
        tvSpeed1 = (TextView) findViewById(R.id.tv_speed1);
        etUrl = (EditText) findViewById(R.id.et_url);
        etUrl.setText(url);
        tvConsole = (TextView) findViewById(R.id.tv_console);
        tvSaveFilePathTip= (TextView) findViewById(R.id.tv_savepath_tip);
    }


    public void onGetInfo(View view) {
        String url = etUrl.getText().toString().trim();
        M3u8InfoManger.getInstance().getM3U8Info(url, new OnM3u8InfoListener() {
            @Override
            public void onSuccess(M3u8 m3U8) {
                tvConsole.append("\n\n获取成功了" + m3U8);
                VideoLogUtils.e("获取成功了" + m3U8);
            }

            @Override
            public void onStart() {
                tvConsole.append("\n\n开始获取信息");
                VideoLogUtils.e("开始获取信息");
            }

            @Override
            public void onError(Throwable errorMsg) {
                tvConsole.append("\n\n出错了" + errorMsg);
                VideoLogUtils.e("出错了" + errorMsg);
            }
        });
    }

    //上一秒的大小
    private long lastLength = 0;
    M3u8DownloadTask task1 = new M3u8DownloadTask("1001");

    public void onDownload(View view) {
        String url = etUrl.getText().toString().trim();
//        url = etUrl.getText().toString();
        task1.setSaveFilePath("/sdcard/111/" + System.currentTimeMillis() + ".ts");
        tvSaveFilePathTip.setText("文件保存在：/sdcard/111/" + System.currentTimeMillis() + ".ts");
        task1.download(url, new OnDownloadListener() {
            @Override
            public void onDownloading(final long itemFileSize, final int totalTs, final int curTs) {
                VideoLogUtils.e(task1.getTaskId() + "下载中.....itemFileSize=" + itemFileSize + "\ttotalTs=" + totalTs + "\tcurTs=" + curTs);
                tvConsole.append("\n\n下载中....." + itemFileSize + "\t" + totalTs + "\t" + curTs);
            }

            /**
             * 下载成功
             */
            @Override
            public void onSuccess() {
                VideoLogUtils.e(task1.getTaskId() + "下载完成了");
                tvConsole.append("\n\n下载完成");
            }

            /**
             * 当前的进度回调
             *
             * @param curLenght
             */
            @Override
            public void onProgress(final long curLenght) {
                if (curLenght - lastLength > 0) {
                    final String speed = NetSpeedUtils.getInstance().displayFileSize(curLenght - lastLength) + "/s";
                    VideoLogUtils.e(task1.getTaskId() + "speed = " + speed);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            VideoLogUtils.e("更新了");
                            tvSpeed1.setText(speed);
                            VideoLogUtils.e(tvSpeed1.getText().toString());
                        }
                    });
                    lastLength = curLenght;

                }
            }

            @Override
            public void onStart() {
                VideoLogUtils.e(task1.getTaskId() + "开始下载了");
                tvConsole.append("\n\n开始下载");
            }

            @Override
            public void onError(Throwable errorMsg) {
                tvConsole.append("\n\n出错了" + errorMsg);
                VideoLogUtils.e(task1.getTaskId() + "出错了" + errorMsg);
            }
        });
    }

    public void onStopTask1(View view) {
        task1.stop();
        M3u8LiveManger.getInstance().stop();
    }

    /**
     * 当前正在下载的视频
     */
    private int curTsIndex;

    public void onLiveDownload(View view) {
//        String url = "http://tvbilive7-i.akamaihd.net/hls/live/494651/CJHK4/CJHK4-06.m3u8";
        String url = etUrl.getText().toString().trim();
        String toFile="/sdcard/" + System.currentTimeMillis() + ".ts";
        tvSaveFilePathTip.setText("缓存目录在：/sdcard/11m3u8/\n最终导出的缓存文件在："+toFile);
        M3u8LiveManger.getInstance()
                .setTempDir("/sdcard/11m3u8/")
                .setSaveFile(toFile)//（设置导出缓存文件）必须以.ts结尾
                .caching(url, new OnDownloadListener() {
                    @Override
                    public void onDownloading(long itemFileSize, int totalTs, int curTs) {
                        curTsIndex = curTs;
                        tvConsole.append(String.format("\n\n下载中.....开始下载第 %s 个视频了", curTs));
//                        tvConsole.setText("第 " + curTs + " 个视频下载中\n\n" + tvConsole.getText().toString());
                    }

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onProgress(long curLength) {
                        if (curLength - lastLength > 0) {
                            final String speed = NetSpeedUtils.getInstance().displayFileSize(curLength - lastLength) + "/s";
                            VideoLogUtils.e(task1.getTaskId() + "speed = " + speed);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    VideoLogUtils.e("更新了");
                                    tvSpeed1.setText(speed + "( 第" + (curTsIndex + 1) + "个视频 )");
                                    VideoLogUtils.e(tvSpeed1.getText().toString());
                                }
                            });
                            lastLength = curLength;
                        }
                    }

                    @Override
                    public void onStart() {
                        tvConsole.append("\n\n开始缓存");
                    }

                    @Override
                    public void onError(Throwable errorMsg) {
                        tvConsole.append("\n\n缓存出错了" + errorMsg);
                    }
                });
    }

    public void onGetLiveCache(View view) {
        String currentTs = M3u8LiveManger.getInstance().getCurrentTs();
        tvConsole.append("\n\n缓存完成了，已经存至：" + currentTs);
        Log.e("hdltag", "onGetLiveCache(Main2Activity.java:151): currentTs = " + currentTs);
    }

    public void onPlay(View view){
        String url = etUrl.getText().toString().trim();
        Intent intent = new Intent(this, NormalActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);
    }

    public void onMergin(View view) {
        File dir=new File("/sdcard/11m3u8/11");
        File[] files = dir.listFiles();
        List<File> fileList=new ArrayList<>();
        for (File file : files) {
            fileList.add(file);
        }
        try {
            M3u8FileUtils.merge(fileList,"/sdcard/1123/"+ System.currentTimeMillis()+".ts");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
