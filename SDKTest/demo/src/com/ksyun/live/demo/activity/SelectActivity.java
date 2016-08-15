package com.ksyun.live.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ksyun.live.demo.DemoActivity;
import com.ksyun.live.demo.R;

public class SelectActivity extends Activity {
    private Button btn_anchor;
    private Button btn_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        btn_anchor = (Button) findViewById(R.id.btn_anchor);
        btn_player = (Button) findViewById(R.id.btn_player);

        btn_anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, DemoActivity.class);
                startActivity(intent);
            }
        });

        btn_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
