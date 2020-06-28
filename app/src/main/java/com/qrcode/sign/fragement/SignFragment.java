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
import com.smart.elevator.adapter.SignAdapter;
import com.smart.elevator.bean.Task;
import com.smart.elevator.constant.Constant;
import com.smart.elevator.data.DBManger;

import java.util.ArrayList;
import java.util.List;

/***
 * 维护人员的签到界面
 *
 * */
public class SignFragment extends Fragment {

    List<Task> mTask = new ArrayList<>();

    SignAdapter mTaskAdapter;

//    List<Sign> mSign = new ArrayList<>();

    ListView mSignListView;

//    SignAdapter mSignAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_sign, container, false);
        initView(view);
        registerBroadcast();
        return view;
    }

    public static SignFragment getInstance() {
        return new SignFragment();
    }

    public void initView(View view){
        mSignListView = view.findViewById(R.id.sign_listview);

        mTaskAdapter = new SignAdapter(getContext());
        mSignListView.setAdapter(mTaskAdapter);
    };
    //查询当前任务状态不是已报修和待接受的任务
    public void initData(){
        String sql = "select * from Task where LIFT_CURRENTSTATE != ? and LIFT_CURRENTSTATE != ?";
        mTask = DBManger.getInstance(getContext()).getTaskBSql(sql,new String[]{Constant.TASK_STATE_REPORT,Constant.TASK_STATE_WAITING});
        mTaskAdapter.setData(mTask);
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
