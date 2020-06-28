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

import com.smart.elevator.R;
import com.smart.elevator.adapter.ReportQrcodeAdapter;
import com.smart.elevator.bean.Elevator;
import com.smart.elevator.constant.Constant;
import com.smart.elevator.data.DBManger;

import java.util.ArrayList;
import java.util.List;

/***
 * 报修人员报修电梯信息管理界面
 *
 * */
public class QrcodeReportFragment extends Fragment {

    List<Elevator> mElevators = new ArrayList<>();

    ListView mEleListView;

    ReportQrcodeAdapter mEleAdapter;

    Button mScanBtn;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragement_qr_report, container, false);
        initView(view);
        registerBroadcast();
        return view;
    }

    public static QrcodeReportFragment getInstance() {
        return new QrcodeReportFragment();
    }

    public void initView(View view){
        mEleListView = view.findViewById(R.id.ele_listview);

        mEleAdapter = new ReportQrcodeAdapter(getContext());
        mEleListView.setAdapter(mEleAdapter);

        mScanBtn = view.findViewById(R.id.scan_qr_btn);
        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    };

    public void initData(){
        mElevators = DBManger.getInstance(getContext()).getAllElevators();
        mEleAdapter.setData(mElevators);
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
