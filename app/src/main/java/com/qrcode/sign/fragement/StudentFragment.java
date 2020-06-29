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
import android.widget.ListView;

import com.qrcode.sign.R;

import java.util.ArrayList;
import java.util.List;

/***
 * 学生签到界面
 *
 * */
public class StudentFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_student, container, false);
        initView(view);
        return view;
    }

    public static StudentFragment getInstance() {
        return new StudentFragment();
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
