//package com.qrcode.sign.view;
//
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.amap.api.maps.model.LatLng;
//import com.google.zxing.BinaryBitmap;
//import com.google.zxing.ChecksumException;
//import com.google.zxing.FormatException;
//import com.google.zxing.NotFoundException;
//import com.google.zxing.RGBLuminanceSource;
//import com.google.zxing.Result;
//import com.google.zxing.common.HybridBinarizer;
//import com.google.zxing.qrcode.QRCodeReader;
//
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///***
// * 老师选择的条件生成的二维码弹窗
// * */
//public class SignDialog extends Dialog {
//
//    private boolean iscancelable;//控制点击dialog外部是否dismiss
//    private boolean isBackCancelable;//控制返回键是否dismiss
//    private View view;
//    private Context context;
//    private Button mSignDecodeBtn;
//    private Button mSignSureBtn;
//    private ImageView mQrcode;
//    private TextView mQrcodeTv;
//    private TextView mUserTv;
//    private TextView mCaluteDistanceTv;
//    private TextView mCaluteTimeTv;
//
//    private String mDecode = "";
//    private String mDistance = "";
//    //是否正确签到地点
//    private boolean isRightPlace = false;
//    //是否正确签到时间
//    private boolean isRightTime = false;
//
//    public SignDialog(Context context, int layoutid, boolean isCancelable, boolean isBackCancelable) {
//        super(context, R.style.MyDialog);
//
//        this.context = context;
//        this.view = LayoutInflater.from(context).inflate(layoutid, null);
//        this.iscancelable = isCancelable;
//        this.isBackCancelable = isBackCancelable;
//
//        initView();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(view);//这行一定要写在前面
//        setCancelable(iscancelable);//点击外部不可dismiss
//        setCanceledOnTouchOutside(isBackCancelable);
//
//
//    }
//
//    public void setData(Task task){
//        this.mTask = task;
//        Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(mTask.getElevator().getLIFT_ADDRESSID(),300,300);
//        if (bitmap!=null){
//            mQrcode.setImageBitmap(bitmap);
//            //长按，通过zxing读取图片，判断是否有二维码
//            mQrcode.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View viewm) {
//
//
//                    return false;
//                }
//            });
//        }
//    }
//
//    public void initView() {
//        mSignDecodeBtn = view.findViewById(R.id.sign_decode_btn);
//        mSignSureBtn = view.findViewById(R.id.sign_sure_btn);
//        mQrcode = view.findViewById(R.id.sign_qrcode);
//        mQrcodeTv = view.findViewById(R.id.qrcode_place);
//        mUserTv = view.findViewById(R.id.user_place);
//        mCaluteDistanceTv = view.findViewById(R.id.calute_distance);
//        mCaluteTimeTv = view.findViewById(R.id.calute_time);
//
//        mSignDecodeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                decodeBitmap();
//                Toast.makeText(getContext(),"识别成功！", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        mSignSureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isRightPlace){
//                    Toast.makeText(getContext(),"签到失败，不在目标范围内！", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if (!isRightTime){
//                    Toast.makeText(getContext(),"签到失败，不在规定时间范围内！", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                dismiss();
//                Toast.makeText(getContext(),"签到成功！", Toast.LENGTH_LONG).show();
//                mTask.setLIFT_CURRENTSTATE(Constant.TASK_STATE_SIGN);
//                DBManger.getInstance(getContext()).updateTask(mTask);
//                isRightTime = false;
//                isRightPlace = false;
//            }
//        });
//    }
//
//    public void decodeBitmap(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap obmp = ((BitmapDrawable) (mQrcode).getDrawable()).getBitmap();
//                int width = obmp.getWidth();
//                int height = obmp.getHeight();
//                int[] data = new int[width * height];
//                obmp.getPixels(data, 0, width, 0, 0, width, height);
//                RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
//                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
//                QRCodeReader reader = new QRCodeReader();
//                Result re = null;
//                try {
//                    re = reader.decode(bitmap1);
//                    mDecode = re.getText();
//                    LatLng latLng = DBManger.getInstance(getContext()).mDataFactory.mCurrentPosition;
//                    String user_palce = latLng.longitude+","+latLng.latitude;
//                    mDistance = MapUtil.distance(mDecode,user_palce);
//                    mHandler.sendEmptyMessage(1);
//                } catch (NotFoundException e) {
//                    e.printStackTrace();
//                } catch (ChecksumException e) {
//                    e.printStackTrace();
//                } catch (FormatException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//
//    }
//
//    public Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what){
//                case 1:
//                    mQrcodeTv.setText("二维码地址："+mDecode);
//                    LatLng latLng = DBManger.getInstance(getContext()).mDataFactory.mCurrentPosition;
//                    mUserTv.setText("用户地址："+latLng.longitude+","+latLng.latitude);
//                    mCaluteDistanceTv.setText(caluteDistance());
//                    mCaluteTimeTv.setText(caluteTime());
//                    break;
//            }
//            return false;
//        }
//    });
//
//    public String caluteDistance(){
//        String des = "";
//        int dis = Integer.parseInt(mDistance);
//        if (dis<1000){
//            des = "1000米范围内，可进行签到！";
//            isRightPlace = true;
//        }else{
//            des = "您当前距离签到地点"+mDistance+"米，不能签到";
//            isRightPlace = false;
//        }
//        return des;
//    }
//
//    public String caluteTime(){
//        String des = "";
//        long sendtime = getStringToDate(mTask.getLIFT_SENDTIME(),pattern);
//        long nowtime = System.currentTimeMillis();
//        long temp = nowtime - sendtime;
//        if ((temp)>Constant.SIGN_TIME_OUT){
//            des = "签到已超时！";
//            isRightTime = false;
//        }else {
//            des = "请在"+getDateTime(sendtime+ Constant.SIGN_TIME_OUT)+"前打卡签到！";
//            isRightTime = true;
//        }
//        return des;
//    }
//
//    String pattern = "yyyy-MM-dd HH:mm:ss";
//    public static long getStringToDate(String dateString, String pattern) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
//        Date date = new Date();
//        try{
//            date = dateFormat.parse(dateString);
//        } catch(ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return date.getTime();
//    }
//
//
//    public String getDateTime(long time){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date(time);
//        String dateStr = simpleDateFormat.format(date);
//        return dateStr;
//    }
//}