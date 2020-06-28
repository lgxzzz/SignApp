package com.qrcode.sign;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;

import com.smart.elevator.bean.User;
import com.smart.elevator.data.DBManger;
import com.smart.elevator.fragement.AboutFragment;
import com.smart.elevator.fragement.ElevotarMgrFragment;
import com.smart.elevator.fragement.ElevotarParamsMgrFragment;
import com.smart.elevator.fragement.OperateTaskFragment;
import com.smart.elevator.fragement.PersonMgrFragment;
import com.smart.elevator.fragement.PlanFragment;
import com.smart.elevator.fragement.QrcodeReportFragment;
import com.smart.elevator.fragement.ReportFragment;
import com.smart.elevator.fragement.SignFragment;
import com.smart.elevator.fragement.TaskFragment;
import com.smart.elevator.util.FragmentUtils;

/***
 * 主页activity
 * 根据登录角色显示对应的不同底部tab 执行不同的操作
 *
 * */
public class MainActivity extends BaseActivtiy {

    private BottomNavigationView mSysPersonBottomMenu;
    private BottomNavigationView mReceptPersonBottomMenu;
    private BottomNavigationView mRepairPersonBottomMenu;
    private BottomNavigationView mReportPersonBottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


    }

    public void init(){
        User mUser = DBManger.getInstance(this).mUser;
        mSysPersonBottomMenu = findViewById(R.id.sys_person_bottom_menu);
        mReceptPersonBottomMenu = findViewById(R.id.recept_person_bottom_menu);
        mRepairPersonBottomMenu = findViewById(R.id.repair_person_bottom_menu);
        mReportPersonBottomMenu = findViewById(R.id.report_person_bottom_menu);


        mSysPersonBottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showFragment(item.getItemId());
                return true;
            }
        });


        mReceptPersonBottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showFragment(item.getItemId());
                return true;
            }
        });


        mRepairPersonBottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showFragment(item.getItemId());
                return true;
            }
        });

        mReportPersonBottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showFragment(item.getItemId());
                return true;
            }
        });

        if (mUser!=null){
            String role = mUser.getRole();
            if (role.equals("维保人员")){
                mSysPersonBottomMenu.setVisibility(View.GONE);
                mReceptPersonBottomMenu.setVisibility(View.GONE);
                mReportPersonBottomMenu.setVisibility(View.GONE);
                mRepairPersonBottomMenu.setVisibility(View.VISIBLE);
                mRepairPersonBottomMenu.setSelectedItemId(R.id.bottom_menu_task);
            }else if(role.equals("维保接待员")){
                mSysPersonBottomMenu.setVisibility(View.GONE);
                mReceptPersonBottomMenu.setVisibility(View.VISIBLE);
                mRepairPersonBottomMenu.setVisibility(View.GONE);
                mReportPersonBottomMenu.setVisibility(View.GONE);
                mReceptPersonBottomMenu.setSelectedItemId(R.id.bottom_menu_report);
            }else if(role.equals("维保系统管理员")){
                mSysPersonBottomMenu.setVisibility(View.VISIBLE);
                mReceptPersonBottomMenu.setVisibility(View.GONE);
                mRepairPersonBottomMenu.setVisibility(View.GONE);
                mReportPersonBottomMenu.setVisibility(View.GONE);
                mSysPersonBottomMenu.setSelectedItemId(R.id.bottom_menu_elevotar);
            } else if(role.equals("报修人员")){
                mSysPersonBottomMenu.setVisibility(View.GONE);
                mReceptPersonBottomMenu.setVisibility(View.GONE);
                mRepairPersonBottomMenu.setVisibility(View.GONE);
                mReportPersonBottomMenu.setVisibility(View.VISIBLE);
                mReportPersonBottomMenu.setSelectedItemId(R.id.bottom_menu_qr_report);
            }
        }
    }


    /**
     * 根据id显示相应的页面
     * @param menu_id
     */
    private void showFragment(int menu_id) {
        switch (menu_id){
            case R.id.bottom_menu_sign:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, SignFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_task:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, TaskFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_about:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, AboutFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_elevotar:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, ElevotarMgrFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_elevotar_params:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, ElevotarParamsMgrFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_person_manager:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, PersonMgrFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_report:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, ReportFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_operate_task:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, OperateTaskFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_plan:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, PlanFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_qr_report:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, QrcodeReportFragment.getInstance(),R.id.main_frame);
                break;
        }
    }

}
