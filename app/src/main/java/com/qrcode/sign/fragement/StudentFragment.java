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

import com.smart.elevator.R;
import com.smart.elevator.adapter.ElevatorParamsAdapter;
import com.smart.elevator.bean.ElevatorParams;
import com.smart.elevator.constant.Constant;
import com.smart.elevator.data.DBManger;

import java.util.ArrayList;
import java.util.List;

/***
 * 管理员电梯信息管理界面
 *
 * */
public class StudentFragment extends Fragment {

    List<ElevatorParams> mEleParams = new ArrayList<>();

    ListView mListView;

    ElevatorParamsAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_elevator_params, container, false);
        initView(view);
        registerBroadcast();
        return view;
    }

    public static StudentFragment getInstance() {
        return new StudentFragment();
    }

    public void initView(View view){
        mListView = view.findViewById(R.id.ele_params_list);

        mAdapter = new ElevatorParamsAdapter(getContext());
        mListView.setAdapter(mAdapter);

    };

    public void initData(){
        mEleParams = DBManger.getInstance(getContext()).getAllElevatorsParams();
        mAdapter.setData(mEleParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.INTENT_REFRESH_DATA);
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Constant.INTENT_REFRESH_DATA)){
                    initData();
                }
            }
        },filter);
    }
}
