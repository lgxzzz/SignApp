package com.qrcode.sign.fragement;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.qrcode.sign.R;
import com.qrcode.sign.data.DBManger;
import com.qrcode.sign.util.MapUtil;
import com.qrcode.sign.zxing.Constants;
import com.qrcode.sign.zxing.activity.CaptureActivity;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/***
 * 学生签到界面
 *
 * */
public class StudentFragment extends Fragment {

    private TextView mDecodeTv;
    private TextView mSignPlaceTv;
    private TextView mSignTimeTv;
    private TextView mSignReslutTv;
    private Button mScanBtn;

    //是否正确签到地点
    private boolean isRightPlace = false;
    //是否正确签到时间
    private boolean isRightTime = false;

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
        mDecodeTv = view.findViewById(R.id.decode_tv);
        mSignPlaceTv = view.findViewById(R.id.sign_place_tv);
        mSignTimeTv = view.findViewById(R.id.sign_time_tv);
        mScanBtn = view.findViewById(R.id.scan_btn);
        mSignReslutTv = view.findViewById(R.id.sign_result_tv);

        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQrCode();
            }
        });
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    private void startQrCode() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // android 6.0以上需要动态申请权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constants.REQ_PERM_CAMERA);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent, Constants.REQ_QR_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        Toast.makeText(getActivity(),"扫描成功", Toast.LENGTH_SHORT).show();
        if (requestCode == Constants.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constants.INTENT_EXTRA_KEY_QR_SCAN);

            try {
                //将扫描出的信息显示出来
                mDecodeTv.setText(scanResult);
                LatLng latLng = DBManger.getInstance(getContext()).mDataFactory.mCurrentPosition;
                String user_palce = latLng.longitude+","+latLng.latitude;
                JSONObject object = new JSONObject(scanResult);
                String tec_location = object.getString("location");
                String sign_time = object.getString("time");
                String mDistance = MapUtil.distance(tec_location,user_palce);

                mSignPlaceTv.setText(caluteDistance(mDistance));
                mSignTimeTv.setText(caluteTime(sign_time));

                if (isRightPlace&&isRightTime){
                    mSignReslutTv.setText("签到成功！");
                }else{
                    mSignReslutTv.setText("签到失败！");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(getActivity(), "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public String caluteDistance(String mDistance){
        String des = "";
        int dis = Integer.parseInt(mDistance);
        if (dis<1000){
            des = "1000米范围内，可进行签到！";
            isRightPlace = true;
        }else{
            des = "您当前距离签到地点"+mDistance+"米，不能签到";
            isRightPlace = false;
        }
        return des;
    }

    public long SIGN_TIME_OUT = 60*1000;
    public String caluteTime(String time){
        String des = "";
        long sendtime = getStringToDate(time,pattern);
        long nowtime = System.currentTimeMillis();
        long temp = nowtime - sendtime;
        if ((temp)>SIGN_TIME_OUT){
            des = "签到已超时！";
            isRightTime = false;
        }else {
            des = "请在"+getDateTime(sendtime+ SIGN_TIME_OUT)+"前打卡签到！";
            isRightTime = true;
        }
        return des;
    }

    String pattern = "yyyy-MM-dd HH:mm:ss";
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }


    public String getDateTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }
}
