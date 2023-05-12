package com.kalu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.UdpMulticastUtil;

/**
 * description:
 * created by kalu on 2021/11/23
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        ((RadioGroup) findViewById(R.id.main_kernel)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                init();
            }
        });

        ((RadioGroup) findViewById(R.id.main_exo_ffmpeg)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                init();
            }
        });

        findViewById(R.id.main_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, getUrl());
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, isLive());
//                intent.putExtra(TestActivity.INTENT_SEEK, 4000L);
                startActivity(intent);
            }
        });
        findViewById(R.id.main_button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeekActivity.class);
                intent.putExtra(SeekActivity.INTENT_URL, getUrl());
                startActivity(intent);
            }
        });
    }

    private final boolean isLive() {
        String url = getUrl();
        return "http://39.134.19.248:6610/yinhe/2/ch00000090990000001335/index.m3u8?virtualDomain=yinhe.live_hls.zte.com".equals(url);
    }

    private final String getUrl() {
        String s = null;
        try {
            EditText editText = findViewById(R.id.main_edit);
            s = editText.getText().toString();
            if (null == s || s.length() <= 0) {
                RadioGroup radioGroup = findViewById(R.id.main_radio);
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroup.findViewById(id);
                s = radioButton.getTag().toString();
            }
        } catch (Exception e) {
        }

        if ("video-h265.mkv".equals(s) || "video-test.rmvb".equals(s)) {
            s = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + s;
        } else if ("video-h264-adts.m3u8".equals(s)) {
            s = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + s;
        } else if ("video-sxgd.mpeg".equals(s)) {
            s = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + s;
        }

        if (s.startsWith("udp")) {
            boolean checkUdpJoinGroup = UdpMulticastUtil.checkUdpJoinGroup(s);
            Toast.makeText(getApplicationContext(), "checkUdpJoinGroup = " + checkUdpJoinGroup + ", udp = " + s, Toast.LENGTH_SHORT).show();
        }

        return s;
//        return "http://zteres.sn.chinamobile.com:6060/ystxds/32/movie62ff2023041019270000?AuthInfo=XtytY6od2CoxL3Ece34qrDut5VCPsz5XztCLvxBRpErVaX%2F0PpXSHHk8ZrK18wSwUcPUBpKvvT33aM%2FbcRBNJw%3D%3D&version=v1.0&BreakPoint=0&virtualDomain=ystxds.vod_hpd.zte.com&mescid=00000050280009590769&programid=&contentid=movie62ff2023041019270000&videoid=00000050280009590769&recommendtype=0&userid=A089E4CA0921&boid=&stbid=&terminalflag=1&profilecode=&usersessionid=755219691";
    }

    private void init() {

        // 1
        int type;
        try {
            RadioGroup radioGroup = findViewById(R.id.main_kernel);
            int id = radioGroup.getCheckedRadioButtonId();
            switch (id) {
                case R.id.main_kernel_button1:
                    type = PlayerType.KernelType.IJK;
                    Toast.makeText(getApplicationContext(), "ijk init succ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.main_kernel_button2:
                    type = PlayerType.KernelType.EXO_V1;
                    Toast.makeText(getApplicationContext(), "exo init succ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.main_kernel_button3:
                    type = PlayerType.KernelType.EXO_V2;
                    Toast.makeText(getApplicationContext(), "exo init succ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.main_kernel_button4:
                    type = PlayerType.KernelType.VLC;
                    Toast.makeText(getApplicationContext(), "vlc init succ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.main_kernel_button5:
                    type = PlayerType.KernelType.ANDROID;
                    Toast.makeText(getApplicationContext(), "android init succ", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            type = PlayerType.KernelType.IJK;
            Toast.makeText(getApplicationContext(), "default ijk init succ", Toast.LENGTH_SHORT).show();
        }

        // 2
        List<String> list = Arrays.asList("video-h265.mkv", "video-test.rmvb", "video-h264-adts.m3u8", "video-h264-adts-0000.ts", "video-h264-adts-0001.ts", "video-sxgd.mpeg");
        for (int i = 0; i < list.size(); i++) {
            String fromPath = list.get(i);
            String savePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fromPath;
            try {
                InputStream is = getApplicationContext().getAssets().open(fromPath);
                FileOutputStream fos = new FileOutputStream(savePath);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "初始化资源文件 => 错误", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        Toast.makeText(getApplicationContext(), "初始化资源文件 => 成功", Toast.LENGTH_SHORT).show();

        // 3
        init(type);
    }

    private void init(@PlayerType.KernelType.Value int type) {

        int exoFFmpeg;
        RadioGroup radioGroup = findViewById(R.id.main_exo_ffmpeg);
        int buttonId = radioGroup.getCheckedRadioButtonId();
        switch (buttonId) {
            case R.id.main_exo_ffmpeg_yes_audio:
                exoFFmpeg = PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_ON_HIGH_AUDIO;
                break;
            default:
                exoFFmpeg = PlayerType.FFmpegType.EXO_EXTENSION_RENDERER_OFF;
                break;
        }
        PlayerBuilder build = new PlayerBuilder.Builder()
                .setLog(true)
                .setKernel(type)
                .setRender(PlayerType.RenderType.TEXTURE_VIEW)
                .setExoFFmpeg(exoFFmpeg)
                .setBuriedEvent(new LogBuriedEvent())
                .build();
        PlayerManager.getInstance().setConfig(build);
    }
}