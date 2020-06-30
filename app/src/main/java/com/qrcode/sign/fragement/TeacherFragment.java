package com.qrcode.sign.fragement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


import com.amap.api.maps.model.LatLng;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.qrcode.sign.R;
import com.qrcode.sign.data.DBManger;
import com.qrcode.sign.util.MapUtil;
import com.qrcode.sign.util.QRCodeUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/***
 * 老师生成二维码界面
 *
 * */
public class TeacherFragment extends Fragment {
    //课程下拉控件
    private Spinner mCourseSp;
    //生成二维码按钮
    private Button mCreateQrcodeBtn;
    //选择的课程
    private String mSelectCourse="";
    //二维码图片
    private ImageView mQrcodeImg;

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
        mCourseSp = view.findViewById(R.id.select_course);
        mCreateQrcodeBtn = view.findViewById(R.id.create_qrcode_btn);
        mQrcodeImg = view.findViewById(R.id.qrcode_img);
    };


    public void initData(){
        final List<String> mCourse =new ArrayList<>();
        mCourse.add("数学");
        mCourse.add("物理");
        mCourse.add("语文");


        //选择用户角色
        SpinnerAdapter adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,mCourse);
        mCourseSp.setAdapter(adapter);
        mSelectCourse = mCourse.get(0);

        mCourseSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectCourse = mCourse.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCreateQrcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createQrcode();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    //生成二维码
    public void createQrcode(){
        try{
            JSONObject object = new JSONObject();
            object.put("course",mSelectCourse);
            object.put("time",System.currentTimeMillis());

            Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(object.toString(),300,300);
            if (bitmap!=null){
                mQrcodeImg.setImageBitmap(bitmap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
