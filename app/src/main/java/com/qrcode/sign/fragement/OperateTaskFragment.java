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
import com.smart.elevator.adapter.TaskAdapter;
import com.smart.elevator.bean.Task;
import com.smart.elevator.constant.Constant;
import com.smart.elevator.data.DBManger;

import java.util.ArrayList;
import java.util.List;

/***
 * 接待员任务管理界面
 *  状态不是已报修的会显示出来
 * */
public class OperateTaskFragment extends Fragment {


    List<Task> mTask = new ArrayList<>();

    ListView mTaskListView;

    TaskAdapter mTaskAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_task, container, false);
        initView(view);
        registerBroadcast();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public static OperateTaskFragment getInstance() {
        return new OperateTaskFragment();
    }

    public void initView(View view){
        mTaskListView = view.findViewById(R.id.task_list);
        mTaskAdapter = new TaskAdapter(getContext());
        mTaskListView.setAdapter(mTaskAdapter);
    };

    public void initData(){
        String sql = "select * from Task where LIFT_CURRENTSTATE !=?";
        mTask = DBManger.getInstance(getContext()).getTaskBSql(sql,new String[]{"已报修"});
        mTaskAdapter.setData(mTask);
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
