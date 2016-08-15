package com.ksy.httptest.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {
    private EditText edit_path;
    private Button btn_download1;
    private Button btn_check;
    private String platform = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_path = (EditText)findViewById(R.id.edit_path);
        edit_path.setText("http://ks3-cn-beijing.ksyun.com/ksy.vcloud.sdk/Android/player/KSY_Android_SDK_v1.4.5_all_libs.zip");
        btn_download1 = (Button)findViewById(R.id.btn_download1);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            platform = getSupportedAbiAfterLollip();
        else
            platform = getSupportedAbiBeforeLollip();

        btn_check = (Button)findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                File libsdir = getDir("libs", Context.MODE_PRIVATE);
//                File file = new File(libsdir, "test.so");

                File libdir = getDir("jniLibs",Context.MODE_PRIVATE);
                File file = new File(libdir, "arm64-v8a");
                File[] files = file.listFiles();
                for(File file1:files){
                    Log.e("files",file1.getAbsolutePath());
                }
                //System.load(file.getAbsolutePath());
            }
        });

        btn_download1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadToLibs(getApplicationContext(), "http://ks3-cn-beijing.ksyun.com/ksy.vcloud.sdk/Android/player/KSY_Android_SDK_v1.4.5_all_libs.zip", "SDKlibs.zip");
                    }
                }).start();
            }
        });

    }


    public void downloadToLibs(Context context, String downurl,String filename){
        long start = System.currentTimeMillis();
        FileOutputStream fos = null;
        InputStream is = null;
        File file = null;
        ArrayList<File> filelist = new ArrayList<File>();
        URL url= null;
        HttpURLConnection connection = null;

        ZipInputStream zipIn = null;
        ZipEntry zipEntry = null;

        try{
            url = new URL(downurl);
            connection = (HttpURLConnection) url.openConnection();
        }catch (Exception e){
            e.printStackTrace();
        }

        String mTargetPath = context.getDir("jniLibs", Activity.MODE_PRIVATE).getAbsolutePath();
        try {
            zipIn = new ZipInputStream(connection.getInputStream());
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    entryName = entryName.substring(0, entryName.length() - 1);
                    File folder = new File(mTargetPath + File.separator+ entryName);
                    folder.mkdirs();
                } else {
                    String fileName=mTargetPath + File.separator + entryName;
                    File file2 = new File(fileName);
                    byte[] buf = new byte[1024];
                    file2.createNewFile();
                    fos = new FileOutputStream(file2);
                    int read = 0;
                    while ((read = zipIn.read(buf)) >0) {
                        fos.write(buf, 0, read);
                        //lend+=readedBytes;
                    }
                    fos.close();
                }
                zipIn.closeEntry();
            }
            zipIn.close();
        } catch (IOException e) {
            e.printStackTrace();
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



}
