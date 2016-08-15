package com.ksyun.live.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ksyun.live.demo.model.GetList;
import com.ksyun.live.demo.util.Video;

import java.util.ArrayList;


public class LocalFragment extends android.app.Fragment {

    private ListView listView;
    private JieVideoListViewAdapter madapter;
    private ArrayList<Video> listVideos;




    public LocalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listVideos = new ArrayList<Video>();
        GetList getList = new GetList();
        getList.getVideoFile(listVideos, Environment.getExternalStorageDirectory());
        View view = inflater.inflate(com.ksyun.live.demo.R.layout.fragment_local, container, false);
        listView = (ListView)view.findViewById(com.ksyun.live.demo.R.id.list_local_frag);
        madapter = new JieVideoListViewAdapter(getActivity(),listVideos);
        listView.setAdapter(madapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video v = listVideos.get(position);
                Log.e("adasdasd",v.getPath());
                Intent intent = new Intent(getActivity(),VideoPlayerActivity.class);
                intent.putExtra("path", v.getPath());
                startActivity(intent);
            }
        });
        return view;
    }


}

