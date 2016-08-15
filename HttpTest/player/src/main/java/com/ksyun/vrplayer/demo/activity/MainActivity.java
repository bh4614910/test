package com.ksyun.vrplayer.demo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ksyun.media.player.KSYLibraryManager;
import com.ksyun.vrplayer.demo.R;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private LocalFragment localFragment;
    private Button media_net;
    private Button media_setting;
    private Button media_history;
    private String platform = null;

    private Handler mHandler;
    private ProgressDialog dialog;
    private int length;
    private int num = 0;
    private int dialogmax = 0;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        CrashReport.initCrashReport(getApplicationContext(), "900040861", true);

        setActionBarLayout(R.layout.media_actionbar,this);

        setDefaultFragment();


        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        dialogmax = (int) (((float)msg.arg1/length)*100/84*100);
                        break;
                    case 2:
                        if(num<=dialogmax){
                            dialog.setProgress(num++);
                            if(dialog.getProgress()==100){
                                dialog.dismiss();
                                timer.cancel();
                            }
                        }
                        break;
                }
            }
        };


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            platform = getSupportedAbiAfterLollip();
        else
            platform = getSupportedAbiBeforeLollip();
        String mTargetPath = getApplicationContext().getDir("jniLibs", Activity.MODE_PRIVATE).getAbsolutePath();
        File file2 = new File(mTargetPath,"libksyplayer.so");
        if(!file2.exists()){
            AlertDialog alert = new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("为了正常使用,请下载播放必须的"+platform+"库")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadToLibs(getApplicationContext(), "http://ks3-cn-beijing.ksyun.com/ksy.vcloud.sdk/Android/player/KSY_Android_SDK_v1.4.5_all_libs.zip", "SDKlibs.zip");
                                    File libdir = getDir("jniLibs",Context.MODE_PRIVATE);

                                    // 需将下载的动态库放至该文件夹下
                                    String mTargetPath = getApplicationContext().getDir("jniLibs", Activity.MODE_PRIVATE).getAbsolutePath();
                                    // 动态库的加载路径
                                    String libPath = mTargetPath + File.separator;
                                    KSYLibraryManager.setLocalLibraryPath(libPath);
                                }
                            }).start();

                            TimerTask timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        Message message = new Message();
                                        message.what = 2;
                                        if (message != null) {
                                            mHandler.sendMessage(message);
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                           timer = new Timer(true);
                            timer.schedule(timerTask,0,50);
                            dialog.show();
                        }
                    }).setCancelable(false).show();
        }



        dialog = new ProgressDialog(this);
        dialog.setTitle("下载");
        dialog.setMessage("下载中");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);
        dialog.setProgress(100);
        dialog.setCancelable(false);

    }


    public void setActionBarLayout(int layoutId, Context mContext) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            LayoutInflater inflator = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(layoutId, new LinearLayout(mContext), false);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                    android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT, android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(v, layout);
            media_net = (Button)findViewById(R.id.media_network);
            media_history = (Button)findViewById(R.id.media_history);
            media_setting = (Button)findViewById(R.id.media_setting);
            media_net.setOnClickListener(this);
            media_setting.setOnClickListener(this);
            media_history.setOnClickListener(this);
        }else{
            Toast.makeText(MainActivity.this, "ActionBar不存在", Toast.LENGTH_SHORT).show();
        }

    }

    private void setDefaultFragment() {

        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        localFragment = new LocalFragment();
        transaction.replace(R.id.contentFrame,localFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()){
            case R.id.media_network:
                Toast.makeText(MainActivity.this, "media_net", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this,NetMediaActivty.class);
                startActivity(intent);
                break;
            case R.id.media_history:
                Intent intent2 = new Intent(this,HistoryActivity.class);
                startActivity(intent2);
                Toast.makeText(MainActivity.this, "media_history", Toast.LENGTH_SHORT).show();
                break;
            case R.id.media_setting:
                intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "media_setting", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getSupportedAbiAfterLollip() {
        String ret = null;
        String[] support = Build.SUPPORTED_ABIS;
        for(int i = 0; i < support.length; i++)
        {
            switch (support[i])
            {
                case "armeabi":
                case "armeabi-v7a":
                    ret = "armeabi-v7a";
                    break;
                case "arm64-v8a":
                    ret = "arm64-v8a";
                    return ret;
                case "x86":
                    ret = "x86";
                    return ret;
            }
        }
        return ret;
    }

    private String getSupportedAbiBeforeLollip()
    {
        String ret = null;
        switch(Build.CPU_ABI2)
        {
            case "armeabi":
            case "armeabi-v7a":
                ret = "armeabi-v7a";
                break;
            case "arm64-v8a":
                ret = "arm64-v8a";
                return ret;
            case "x86":
                ret = "x86";
                break;
        }
        return ret;
    }

    public void downloadToLibs(Context context, String downurl,String filename){
        FileOutputStream fos = null;
        URL url= null;
        HttpURLConnection connection = null;
        length = 0;

        ZipInputStream zipIn = null;
        ZipEntry zipEntry = null;

        try{
            url = new URL(downurl);
            connection = (HttpURLConnection) url.openConnection();

        }catch (Exception e){
            e.printStackTrace();
        }

        length = connection.getContentLength();
        Log.e("lenth",length+"");
        String mPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Test";
        File file = new File(mPath);
        if(!file.exists()){
            file.mkdir();
        }
        try {
            zipIn = new ZipInputStream(connection.getInputStream());
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    entryName = entryName.substring(0, entryName.length() - 1);
                    File folder = new File(mPath + File.separator+ entryName);
                    folder.mkdirs();
                } else {
                    String fileName=mPath + File.separator + entryName;
                    File file2 = new File(fileName);
                    byte[] buf = new byte[1024];
                    file2.createNewFile();
                    fos = new FileOutputStream(file2);
                    int read = 0;
                    int lenth = 0;
                    while ((read = zipIn.read(buf))!=-1) {
                        fos.write(buf, 0, read);
                        lenth+=read;
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = lenth;
                        mHandler.sendMessage(msg);
                    }
                    fos.close();
                }
                zipIn.closeEntry();
            }
            zipIn.close();

            String mTargetPath = getApplicationContext().getDir("jniLibs", Activity.MODE_PRIVATE).getAbsolutePath();

            File file2 = new File(mTargetPath,"libksyplayer.so");
            if(!file2.exists()){
                file2.createNewFile();
            }
            FileOutputStream fos1 = new FileOutputStream(file2);
            Log.e("oldpath",mPath+"/"+platform+"/libksyplayer.so");
            FileInputStream fin = new FileInputStream(mPath+"/"+platform+"/libksyplayer.so");


            byte[] buffer = new byte[1444];
            int i;
            while((i= fin.read(buffer))!=-1){
                fos1.write(buffer,0,i);
            }
            fos1.close();
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
