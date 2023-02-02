package com.kalu.mediaplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import lib.kalu.mediaplayer.TestActivity;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;

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
        RadioGroup radioGroup = findViewById(R.id.main_kernel);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        findViewById(R.id.main_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
                intent.putExtra(FullActivity.INTENT_URL, getUrl());
//                intent.putExtra(FullActivity.INTENT_LIVE, true);
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

    private void init() {

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
                    type = PlayerType.KernelType.EXO;
                    Toast.makeText(getApplicationContext(), "exo init succ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.main_kernel_button3:
                    type = PlayerType.KernelType.VLC;
                    Toast.makeText(getApplicationContext(), "vlc init succ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.main_kernel_button4:
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

        copy();
        init(type);
    }

    private void copy() {
        String absolutePath = getApplicationContext().getCacheDir().getAbsolutePath();
        List<String> list = Arrays.asList("video-h265.mkv", "video-test.rmvb");
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            boolean copyFile = copyFile(getApplicationContext(), s, absolutePath + "/" + s);
            if (!copyFile) {
                Toast.makeText(getApplicationContext(), "初始化资源文件 => 错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private boolean copyFile(Context context, String assetsPath, String savePath) {
        // assetsPath 为空时即 /assets
        try {
            InputStream is = context.getAssets().open(assetsPath);
            FileOutputStream fos = new FileOutputStream(new File(savePath));
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
            return false;
        }
        return true;
    }

    private void init(@PlayerType.KernelType.Value int type) {
        PlayerBuilder build = new PlayerBuilder.Builder()
                .setLog(true)
                .setKernel(type)
                .setRender(PlayerType.RenderType.SURFACE_VIEW)
                .setExoFFmpeg(PlayerType.FFmpegType.EXO_EXT_FFPEMG_NULL)
                .setBuriedEvent(new Event())
                .build();
        PlayerManager.getInstance().setConfig(build);
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
            s = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + s;
        }
        return s;
    }
}
