package com.qrcode.sign;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;

import com.qrcode.sign.bean.User;
import com.qrcode.sign.data.DBManger;
import com.qrcode.sign.fragement.AboutFragment;
import com.qrcode.sign.fragement.StudentFragment;
import com.qrcode.sign.fragement.TeacherFragment;
import com.qrcode.sign.util.FragmentUtils;


/***
 * 主页activity
 * 根据登录角色显示对应的不同底部tab 执行不同的操作
 *
 * */
public class MainActivity extends BaseActivtiy {

    private BottomNavigationView mTeacherBottomMenu;
    private BottomNavigationView mStudentBottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


    }

    public void init(){
        User mUser = DBManger.getInstance(this).mUser;
        mTeacherBottomMenu = findViewById(R.id.teacher_bottom_menu);
        mStudentBottomMenu = findViewById(R.id.student_bottom_menu);

        mTeacherBottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showFragment(item.getItemId());
                return true;
            }
        });


        mStudentBottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                showFragment(item.getItemId());
                return true;
            }
        });

        if (mUser!=null){
            String role = mUser.getRole();
            if (role.equals("老师")){
                mStudentBottomMenu.setVisibility(View.GONE);
                mTeacherBottomMenu.setVisibility(View.VISIBLE);
                mTeacherBottomMenu.setSelectedItemId(R.id.bottom_menu_teacher_sign);
            }else if(role.equals("学生")){
                mStudentBottomMenu.setVisibility(View.VISIBLE);
                mTeacherBottomMenu.setVisibility(View.GONE);
                mStudentBottomMenu.setSelectedItemId(R.id.bottom_menu_stu_sign);
            }
        }
    }


    /**
     * 根据id显示相应的页面
     * @param menu_id
     */
    private void showFragment(int menu_id) {
        switch (menu_id){
            case R.id.bottom_menu_teacher_sign:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, TeacherFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_teacher_about:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, AboutFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_stu_sign:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, StudentFragment.getInstance(),R.id.main_frame);
                break;
            case R.id.bottom_menu_stu_about:
                FragmentUtils.replaceFragmentToActivity(fragmentManager, AboutFragment.getInstance(),R.id.main_frame);
                break;
        }
    }

}
