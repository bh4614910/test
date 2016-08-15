package com.ksyun.vrplayer.demo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.ksyun.vrplayer.demo.R;
import com.ksyun.vrplayer.demo.util.Settings;

public class SettingActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    public static final int GET_JSONDATA = 0x1001;
    public static final int PLAY_VIDEO = 0x1002;


    private SharedPreferences settings ;
    private SharedPreferences.Editor editor;
    private RadioButton radiosurface;
    private RadioButton radiotexture;
    private RadioButton radiosoft;
    private RadioButton radiohard;
    private Switch debugswitch;
    private Switch vrswitch;

    private RadioGroup mChooseSurface;
    private RadioGroup mChooseCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settings = getSharedPreferences("SETTINGS",Context.MODE_PRIVATE);
        editor = settings.edit();
        String chooseview = settings.getString("choose_view","信息为空");
        String choosedecode = settings.getString("choose_decode","信息为空");
        String choosedebug = settings.getString("choose_debug","信息为空");
        String choosevr = settings.getString("choose_vr","信息为空");


        mChooseSurface = (RadioGroup) findViewById(R.id.choose_surface);
        mChooseCodec = (RadioGroup) findViewById(R.id.choose_codec);

        mChooseSurface.setOnCheckedChangeListener(this);
        mChooseCodec.setOnCheckedChangeListener(this);

        debugswitch = (Switch)findViewById(R.id.switch_set);
        vrswitch = (Switch)findViewById(R.id.switch_vr);

        initSetting(choosedecode,chooseview,choosedebug,choosevr);

        vrswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    editor.putString("choose_vr",Settings.VRON);
                    Toast.makeText(SettingActivity.this, "VR被打开", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString("choose_vr",Settings.VROFF);
                    Toast.makeText(SettingActivity.this, "VR被关闭", Toast.LENGTH_SHORT).show();
                }
                editor.commit();
            }
        });

        debugswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    editor.putString("choose_debug",Settings.DEBUGON);
                    Toast.makeText(SettingActivity.this, "Debug被打开", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString("choose_debug",Settings.DEBUGOFF);
                    Toast.makeText(SettingActivity.this, "Debug被关闭", Toast.LENGTH_SHORT).show();
                }
                editor.commit();
            }
        });
    }

    private void initSetting(String choosedecode, String chooseview,String choosedebug,String choosevr) {
        radiosoft = (RadioButton)findViewById(R.id.use_sw);
        radiohard = (RadioButton)findViewById(R.id.use_hw);
        radiosurface = (RadioButton)findViewById(R.id.use_surfaceview);
        radiotexture = (RadioButton)findViewById(R.id.use_textureview);

        switch (choosedecode){
            case Settings.USEHARD:
                mChooseCodec.check(radiohard.getId());
                break;
            case Settings.USESOFT:
                mChooseCodec.check(radiosoft.getId());
                break;
            default:
                mChooseCodec.check(radiosoft.getId());
                editor.putString("choose_decode",Settings.USESOFT);
                break;
        }
        switch (chooseview){
            case Settings.USESUFACE:
                mChooseSurface.check(radiosurface.getId());
                break;
            case Settings.USETEXTURE:
                mChooseSurface.check(radiotexture.getId());
                break;
            default:
                mChooseSurface.check(radiosurface.getId());
                editor.putString("choose_view",Settings.USESUFACE);
                break;
        }
        switch (choosedebug){
            case Settings.DEBUGOFF:
                debugswitch.setChecked(false);
                break;
            case Settings.DEBUGON:
                debugswitch.setChecked(true);
                break;
            default:
                debugswitch.setChecked(false);
                editor.putString("choose_debug",Settings.DEBUGOFF);
                break;
        }
        switch (choosevr){
            case Settings.VROFF:
                vrswitch.setChecked(false);
                break;
            case Settings.VRON:
                vrswitch.setChecked(true);
                break;
            default:
                vrswitch.setChecked(false);
                editor.putString("choose_vr",Settings.VROFF);
                break;
        }
        editor.commit();

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(i){
            case R.id.use_surfaceview:
                editor.putString("choose_view",Settings.USESUFACE);
                break;
            case R.id.use_textureview:
                editor.putString("choose_view",Settings.USETEXTURE);
                break;
            case R.id.use_hw:
                mChooseSurface.check(radiotexture.getId());
                //radiosurface.setEnabled(false);
                Toast.makeText(SettingActivity.this, "硬解请使用TextureView", Toast.LENGTH_SHORT).show();
                editor.putString("choose_decode",Settings.USEHARD);
                editor.putString("choose_view",Settings.USETEXTURE);
                break;
            case  R.id.use_sw:
                //radiosurface.setEnabled(true);
                editor.putString("choose_decode",Settings.USESOFT);
                break;
            default:
                break;
        }
        editor.commit();
    }
}
