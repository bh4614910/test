package com.ksyun.live.demo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.ksyun.live.demo.model.NetDbAdapter;

import java.util.ArrayList;


public class NetMediaActivty extends Activity implements View.OnClickListener{
    private Button net_setting;
    private Button net_history;
    private Button net_scan;
    private Button net_startvedio;
    private EditText texturl;
    private ListView netlist;

    private ArrayList<String> listurl;

    private Cursor cursor;
    private NetDbAdapter NetDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ksyun.live.demo.R.layout.activity_net);

        texturl = (EditText)findViewById(com.ksyun.live.demo.R.id.search_net);
        net_startvedio = (Button)findViewById(com.ksyun.live.demo.R.id.btn_net_vedio);
        netlist = (ListView)findViewById(com.ksyun.live.demo.R.id.list_net);

        final String[] myurl = {"rtmp://live.hkstv.hk.lxdns.com/live/hks",
                "http://playback.ks.zb.mi.com/record/live/107578_1467605748/hls/107578_1467605748.m3u8",
                "http://hdl2.plu.cn/longzhu/6e0c2880a5cf4740a2ec21a17c8c67f2.flv",
                "http://cxy.kssws.ks-cdn.com/h265_56c26b7a7dc5f6043.mp4"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,myurl);
        netlist.setAdapter(adapter);

        netlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                texturl.setText(myurl[i]);
            }
        });

        net_startvedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = texturl.getText().toString();
                NetDb = new NetDbAdapter(NetMediaActivty.this);
                NetDb.open();

                if(NetDb.getData(path)){
                    NetDb.updateData(path);
                }else{
                    NetDb.createDate(path);
                }
                NetDb.close();
                Intent intent = new Intent(NetMediaActivty.this,VideoPlayerActivity.class);
                intent.putExtra("path",path);
                startActivity(intent);

            }
        });
        setActionBarLayout(com.ksyun.live.demo.R.layout.net_actionbar,this);
    }

    public void setActionBarLayout(int layoutId, Context mContext) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(com.ksyun.live.demo.R.layout.net_actionbar);
            net_history = (Button)findViewById(com.ksyun.live.demo.R.id.net_history);
            net_setting = (Button)findViewById(com.ksyun.live.demo.R.id.net_setting);
            net_scan = (Button)findViewById(com.ksyun.live.demo.R.id.net_scan);
            net_scan.setOnClickListener(this);
            net_history.setOnClickListener(this);
            net_setting.setOnClickListener(this);

        }else{
            Toast.makeText(NetMediaActivty.this, "ActionBar不存在", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case com.ksyun.live.demo.R.id.net_history:
                listurl = new ArrayList<String>();
                NetDb = new NetDbAdapter(NetMediaActivty.this);
                NetDb.open();
                cursor = NetDb.getAllData();
                cursor.moveToFirst();
                if(cursor.getCount()>0){
                    listurl.add( cursor.getString(cursor.getColumnIndex(NetDbAdapter.KEY_PATH)));
                }
                while(cursor.moveToNext()){
                    listurl.add( cursor.getString(cursor.getColumnIndex(NetDbAdapter.KEY_PATH)));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listurl);
                netlist.setAdapter(adapter);
                netlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        texturl.setText(listurl.get(i));
                    }
                });

                break;
            case com.ksyun.live.demo.R.id.net_setting:
                Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
                break;
            case com.ksyun.live.demo.R.id.net_scan:
                Intent intent1 = new Intent(this,CaptureActivity.class);
                startActivityForResult(intent1,0);
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            texturl.setText(scanResult);
        }
    }
}
