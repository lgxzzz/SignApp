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

import com.smart.elevator.PlanTaskActivity;
import com.smart.elevator.R;
import com.smart.elevator.adapter.TaskAdapter;
import com.smart.elevator.bean.Task;
import com.smart.elevator.constant.Constant;
import com.smart.elevator.data.DBManger;

import java.util.ArrayList;
import java.util.List;

/***
 *接待员定期维护计划管理界面
 *
 * */
public class PlanFragment extends Fragment {


    List<Task> mTask = new ArrayList<>();

    ListView mTaskListView;

    TaskAdapter mTaskAdapter;

    Button mAddBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_plan, container, false);
        initView(view);
        registerBroadcast();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public static PlanFragment getInstance() {
        return new PlanFragment();
    }

    public void initView(View view){
        mTaskListView = view.findViewById(R.id.task_list);
        mTaskAdapter = new TaskAdapter(getContext());
        mTaskListView.setAdapter(mTaskAdapter);
        mAddBtn = view.findViewById(R.id.add_task_btn);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getContext(), PlanTaskActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("opt","add");
                intent.putExtras(b);
                getContext().startActivity(intent);
            }
        });
    };

    //查询所有状态是定期的任务
    public void initData(){
        String sql = "select * from Task where FORM_STATE =?";
        mTask = DBManger.getInstance(getContext()).getTaskBSql(sql,new String[]{"定期"});
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
