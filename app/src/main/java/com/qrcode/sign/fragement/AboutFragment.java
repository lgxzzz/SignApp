package com.qrcode.sign.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smart.elevator.R;
import com.smart.elevator.SearchElevatorActivity;
import com.smart.elevator.TaskActivity;
import com.smart.elevator.bean.User;
import com.smart.elevator.data.DBManger;

/***
 * 维保人员个人信息界面
 *
 * */
public class AboutFragment extends Fragment {

    //个人信息
    TextView mUserID;
    TextView mUserName;
    TextView mUserTel;
    TextView mUserMail;
    Button mUpdateBtn;
    Button mTaskCurrentBtn;
    Button mTaskHistoryBtn;
    Button mSearchElevatorBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_about, container, false);
        initView(view);

        return view;
    }

    public static AboutFragment getInstance() {
        return new AboutFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public void initView(View view){
        mUserID = view.findViewById(R.id.user_id);
        mUserName = view.findViewById(R.id.user_name);
        mUserTel = view.findViewById(R.id.user_tel);
        mUserMail = view.findViewById(R.id.user_mail);
        mUpdateBtn = view.findViewById(R.id.user_update_btn);
        mTaskCurrentBtn = view.findViewById(R.id.task_current_btn);
        mTaskHistoryBtn = view.findViewById(R.id.task_history_btn);
        mSearchElevatorBtn = view.findViewById(R.id.search_elevator_btn);
    };

    public void initData() {
        User user = DBManger.getInstance(getContext()).mUser;
        mUserID.setText(user.getUserId());
        mUserName.setText(user.getUserName());
        mUserMail.setText(user.getMail());
        mUserTel.setText(user.getTelephone());
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mTaskCurrentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), TaskActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("state","当前任务");
                intent.putExtras(b);
                getContext().startActivity(intent);
            }
        });
        mTaskHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), TaskActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("state","历史任务");
                intent.putExtras(b);
                getContext().startActivity(intent);
            }
        });

        mSearchElevatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), SearchElevatorActivity.class);
                getContext().startActivity(intent);
            }
        });
    }




}
