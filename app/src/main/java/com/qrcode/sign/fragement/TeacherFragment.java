package com.qrcode.sign.fragement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


import com.qrcode.sign.R;

import java.util.ArrayList;
import java.util.List;

/***
 * 老师生成二维码界面
 *
 * */
public class TeacherFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_teacher, container, false);
        initView(view);
        return view;
    }

    public static TeacherFragment getInstance() {
        return new TeacherFragment();
    }

    public void initView(View view){

    };


    public void initData(){

    }



    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

}
