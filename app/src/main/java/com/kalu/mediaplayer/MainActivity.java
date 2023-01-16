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
        init(type);
    }

    private void init(@PlayerType.KernelType.Value int type) {
        PlayerBuilder build = new PlayerBuilder.Builder()
                .setLog(true)
                .setKernel(type)
                .setRender(PlayerType.RenderType.SURFACE_VIEW)
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
        return s;
    }
}
