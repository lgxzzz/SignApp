package com.qrcode.sign.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import com.smart.elevator.bean.Elevator;
import com.smart.elevator.bean.ElevatorParams;
import com.smart.elevator.bean.Sign;
import com.smart.elevator.bean.Task;
import com.smart.elevator.bean.User;
import com.smart.elevator.constant.Constant;
import com.smart.elevator.util.NotifyState;
import com.smart.elevator.util.SharedPreferenceUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManger {
    private Context mContext;
    private SQLiteDbHelper mDBHelper;
    public User mUser;
    public DataFactory mDataFactory;
    public static  DBManger instance;

    public static DBManger getInstance(Context mContext){
        if (instance == null){
            instance = new DBManger(mContext);
        }
        return instance;
    };

    public DBManger(final Context mContext){
        this.mContext = mContext;
        mDBHelper = new SQLiteDbHelper(mContext);
        mDataFactory = new DataFactory(mContext);
        mDataFactory.setMlistener(new DataFactory.IListener() {
            @Override
            public void onGetDefaultElevators(HashMap<String,Elevator> mElevators, HashMap<String,ElevatorParams> mElevatorParams, HashMap<String,Task> mTasks) {
                //第一次进入初始化五条电梯数据
                if(SharedPreferenceUtil.getFirstTimeUse(mContext)){
                    for(Map.Entry<String, Elevator> entry: mElevators.entrySet()){
                        Elevator elevator = entry.getValue();
                        insertElevator(elevator);
                    }
                    for(Map.Entry<String, ElevatorParams> entry: mElevatorParams.entrySet()){
                        ElevatorParams elevatorParams = entry.getValue();
                        insertElevatorParams(elevatorParams);
                    }
                    for(Map.Entry<String, Task> entry: mTasks.entrySet()){
                        Task task = entry.getValue();
                        insertTask(task);
                    }
                    SharedPreferenceUtil.setFirstTimeUse(false,mContext);
                }
            }
        });
        mHandler.sendEmptyMessageDelayed(HSG_CHCEK_STATE,10*1000);
        mHandler.sendEmptyMessageDelayed(HSG_CHCEK_PLAN_STATE,20*1000);
    }


    //用户登陆
    public void login(String name, String password, IListener listener){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from UserInfo where USER_NAME =? and USER_PASSWORD=?",new String[]{name,password});
            if (cursor.moveToFirst()){
                String USER_ID = cursor.getString(cursor.getColumnIndex("USER_ID"));
                String USER_NAME = cursor.getString(cursor.getColumnIndex("USER_NAME"));
                String USER_MAIL = cursor.getString(cursor.getColumnIndex("USER_MAIL"));
                String LIFT_PROCESSORPHONE = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSORPHONE"));
                String USER_CHARCTER = cursor.getString(cursor.getColumnIndex("USER_CHARCTER"));

                mUser = new User();
                mUser.setUserId(USER_ID);
                mUser.setUserName(USER_NAME);
                mUser.setTelephone(LIFT_PROCESSORPHONE);
                mUser.setMail(USER_MAIL);
                mUser.setRole(USER_CHARCTER);
                listener.onSuccess();
            }else{
                listener.onError("未查询到该用户");
            }
            db.close();
            return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        listener.onError("未查询到该用户");
    }

    //修改用户信息
    public void updateUser(User user,IListener listener){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from UserInfo where USER_NAME=?",new String[]{user.getUserName()});
            if (cursor.moveToFirst()){
                ContentValues values = new ContentValues();
                values.put("USER_NAME",user.getUserName());
                values.put("USER_MAIL",user.getMail());
                values.put("LIFT_PROCESSORPHONE",user.getTelephone());
                values.put("USER_CHARCTER",user.getRole());

                int code = db.update(SQLiteDbHelper.TAB_USER,values,"USER_NAME =?",new String[]{user.getUserName()+""});
                listener.onSuccess();
            }else {
                insertUser(user,listener);
            }
            db.close();
        }catch (Exception e){

        }
    }

    //注册用户
    public void registerUser(User user,IListener listener){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from UserInfo where USER_NAME=?",new String[]{user.getUserName()});
            if (cursor.moveToFirst()){
                listener.onError("用户名已经被注册！");
            }else{
                String userid = getRandomUSER_ID();
                ContentValues values = new ContentValues();
                values.put("USER_ID",userid);
                values.put("USER_NAME",user.getUserName());
                values.put("USER_PASSWORD",user.getPassword());
                values.put("LIFT_PROCESSORPHONE",user.getTelephone());
                values.put("USER_MAIL",user.getMail());
                values.put("USER_CHARCTER",user.getRole());
                mUser = user;
                mUser.setUserId(userid);
                long code = db.insert(SQLiteDbHelper.TAB_USER,null,values);
                listener.onSuccess();
            }
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    };

    //注册用户
    public void insertUser(User user,IListener listener){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from UserInfo where USER_NAME=?",new String[]{user.getUserName()});
            if (cursor.moveToFirst()){
                listener.onError("用户名已经被注册！");
            }else{
                String userid = getRandomUSER_ID();
                ContentValues values = new ContentValues();
                values.put("USER_ID",userid);
                values.put("USER_NAME",user.getUserName());
                values.put("USER_PASSWORD",user.getPassword());
                values.put("LIFT_PROCESSORPHONE",user.getTelephone());
                values.put("USER_MAIL",user.getMail());
                values.put("USER_CHARCTER",user.getRole());
                long code = db.insert(SQLiteDbHelper.TAB_USER,null,values);
                listener.onSuccess();
            }
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    };

    //获取所有用户
    public List<User> getAllUsers(){
        List<User> mUsers = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.query(SQLiteDbHelper.TAB_USER,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                String USER_ID = cursor.getString(cursor.getColumnIndex("USER_ID"));
                String USER_NAME = cursor.getString(cursor.getColumnIndex("USER_NAME"));
                String USER_PASSWORD = cursor.getString(cursor.getColumnIndex("USER_PASSWORD"));
                String LIFT_PROCESSORPHONE = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSORPHONE"));
                String USER_MAIL = cursor.getString(cursor.getColumnIndex("USER_MAIL"));
                String USER_CHARCTER = cursor.getString(cursor.getColumnIndex("USER_CHARCTER"));

                User user = new User();
                user.setUserId(USER_ID);
                user.setUserName(USER_NAME);
                user.setPassword(USER_PASSWORD);
                user.setTelephone(LIFT_PROCESSORPHONE);
                user.setMail(USER_MAIL);
                user.setRole(USER_CHARCTER);
                mUsers.add(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mUsers;
    }

    //获取所有用户
    public List<User> getUsersNameByRole(String role){
        List<User> mUsers = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from UserInfo where USER_CHARCTER=?",new String[]{role});
            while (cursor.moveToNext()){
                String USER_ID = cursor.getString(cursor.getColumnIndex("USER_ID"));
                String USER_NAME = cursor.getString(cursor.getColumnIndex("USER_NAME"));
                String USER_PASSWORD = cursor.getString(cursor.getColumnIndex("USER_PASSWORD"));
                String LIFT_PROCESSORPHONE = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSORPHONE"));
                String USER_MAIL = cursor.getString(cursor.getColumnIndex("USER_MAIL"));
                String USER_CHARCTER = cursor.getString(cursor.getColumnIndex("USER_CHARCTER"));

                User user = new User();
                user.setUserId(USER_ID);
                user.setUserName(USER_NAME);
                user.setPassword(USER_PASSWORD);
                user.setTelephone(LIFT_PROCESSORPHONE);
                user.setMail(USER_MAIL);
                user.setRole(USER_CHARCTER);
                mUsers.add(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mUsers;
    }

    //获取所有用户
    public List<String> getUsersNameNameByRole(String role){
        List<String> mUsers = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from UserInfo where USER_CHARCTER=?",new String[]{role});
            while (cursor.moveToNext()){
                String USER_NAME = cursor.getString(cursor.getColumnIndex("USER_NAME"));

                mUsers.add(USER_NAME);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mUsers;
    }

    //删除用户
    public void deleteUser(User user){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            long code = db.delete(SQLiteDbHelper.TAB_USER,"USER_ID =?",new String[]{user.getUserId()});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取所有用户
    public List<User> QueryUsersByNameKey(String key){
        List<User> mUsers = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM UserInfo WHERE USER_NAME LIKE '%" + key + "%'", null);
            while (cursor.moveToNext()){
                String USER_ID = cursor.getString(cursor.getColumnIndex("USER_ID"));
                String USER_NAME = cursor.getString(cursor.getColumnIndex("USER_NAME"));
                String USER_PASSWORD = cursor.getString(cursor.getColumnIndex("USER_PASSWORD"));
                String LIFT_PROCESSORPHONE = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSORPHONE"));
                String USER_MAIL = cursor.getString(cursor.getColumnIndex("USER_MAIL"));
                String USER_CHARCTER = cursor.getString(cursor.getColumnIndex("USER_CHARCTER"));

                User user = new User();
                user.setUserId(USER_ID);
                user.setUserName(USER_NAME);
                user.setPassword(USER_PASSWORD);
                user.setTelephone(LIFT_PROCESSORPHONE);
                user.setMail(USER_MAIL);
                user.setRole(USER_CHARCTER);
                mUsers.add(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mUsers;
    }

    //注册电梯数据
    public void insertElevator(Elevator elevator){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("LIFT_ID",elevator.getLIFT_ID());
            values.put("LIFT_IDCODE",elevator.getLIFT_IDCODE());
            values.put("LIFT_USER",elevator.getLIFT_USER());
            values.put("LIFT_AREAID",elevator.getLIFT_AREAID());
            values.put("LIFT_ADDRESSID",elevator.getLIFT_ADDRESSID());
            values.put("LIFT_MAINTENANCENAME_ID",elevator.getLIFT_MAINTENANCENAME_ID());
            values.put("LIFT_BRANDID",elevator.getLIFT_BRANDID());
            values.put("LIFT_PRODUCT",elevator.getLIFT_PRODUCT());
            values.put("LIFT_PRODUCTDATE",elevator.getLIFT_PRODUCTDATE());
            values.put("LIFT_STATUS",elevator.getLIFT_STATUS());
            long code = db.insert(SQLiteDbHelper.TAB_ELEVATOR,null,values);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //更新电梯数据
    public void updateElevator(Elevator elevator){
        try{
            if (!isExistElevator(elevator)){
                insertElevator(elevator);
            }else{
                SQLiteDatabase db = mDBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("LIFT_IDCODE",elevator.getLIFT_IDCODE());
                values.put("LIFT_USER",elevator.getLIFT_USER());
                values.put("LIFT_AREAID",elevator.getLIFT_AREAID());
                values.put("LIFT_ADDRESSID",elevator.getLIFT_ADDRESSID());
                values.put("LIFT_MAINTENANCENAME_ID",elevator.getLIFT_MAINTENANCENAME_ID());
                values.put("LIFT_BRANDID",elevator.getLIFT_BRANDID());
                values.put("LIFT_PRODUCT",elevator.getLIFT_PRODUCT());
                values.put("LIFT_PRODUCTDATE",elevator.getLIFT_PRODUCTDATE());
                values.put("LIFT_STATUS",elevator.getLIFT_STATUS());
                long code = db.update(SQLiteDbHelper.TAB_ELEVATOR,values,"LIFT_ID =?",new String[]{elevator.getLIFT_ID()+""});
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //删除电梯数据
    public void delteElevator(Elevator elevator){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            long code = db.delete(SQLiteDbHelper.TAB_ELEVATOR,"LIFT_ID =?",new String[]{elevator.getLIFT_ID()});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //是否存在该电梯
    public boolean isExistElevator(Elevator elevator){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from Elevator where LIFT_ID=?",new String[]{elevator.getLIFT_ID()});
            if (cursor.moveToFirst()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //根据ID获取电梯
    public Elevator getElevatorByID(String LIFT_ID){
        Elevator elevator = null;
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from Elevator where LIFT_ID=?",new String[]{LIFT_ID});
            if (cursor.moveToNext()){
                elevator = new Elevator();
                String LIFT_IDCODE = cursor.getString(cursor.getColumnIndex("LIFT_IDCODE"));
                String LIFT_USER = cursor.getString(cursor.getColumnIndex("LIFT_USER"));
                String LIFT_AREAID = cursor.getString(cursor.getColumnIndex("LIFT_AREAID"));
                String LIFT_ADDRESSID = cursor.getString(cursor.getColumnIndex("LIFT_ADDRESSID"));
                String LIFT_MAINTENANCENAME_ID = cursor.getString(cursor.getColumnIndex("LIFT_MAINTENANCENAME_ID"));
                String LIFT_BRANDID = cursor.getString(cursor.getColumnIndex("LIFT_BRANDID"));
                String LIFT_PRODUCT = cursor.getString(cursor.getColumnIndex("LIFT_PRODUCT"));
                String LIFT_PRODUCTDATE = cursor.getString(cursor.getColumnIndex("LIFT_PRODUCTDATE"));
                String LIFT_STATUS = cursor.getString(cursor.getColumnIndex("LIFT_STATUS"));

                elevator.setLIFT_ID(LIFT_ID);
                elevator.setLIFT_IDCODE(LIFT_IDCODE);
                elevator.setLIFT_USER(LIFT_USER);
                elevator.setLIFT_AREAID(LIFT_AREAID);
                elevator.setLIFT_ADDRESSID(LIFT_ADDRESSID);
                elevator.setLIFT_MAINTENANCENAME_ID(LIFT_MAINTENANCENAME_ID);
                elevator.setLIFT_BRANDID(LIFT_BRANDID);
                elevator.setLIFT_PRODUCT(LIFT_PRODUCT);
                elevator.setLIFT_PRODUCTDATE(LIFT_PRODUCTDATE);
                elevator.setLIFT_STATUS(LIFT_STATUS);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return elevator;
    }

    //获取所有电梯数据
    public List<Elevator> getAllElevators(){
        List<Elevator> mElevators = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.query(SQLiteDbHelper.TAB_ELEVATOR,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                String LIFT_ID = cursor.getString(cursor.getColumnIndex("LIFT_ID"));
                String LIFT_IDCODE = cursor.getString(cursor.getColumnIndex("LIFT_IDCODE"));
                String LIFT_USER = cursor.getString(cursor.getColumnIndex("LIFT_USER"));
                String LIFT_AREAID = cursor.getString(cursor.getColumnIndex("LIFT_AREAID"));
                String LIFT_ADDRESSID = cursor.getString(cursor.getColumnIndex("LIFT_ADDRESSID"));
                String LIFT_MAINTENANCENAME_ID = cursor.getString(cursor.getColumnIndex("LIFT_MAINTENANCENAME_ID"));
                String LIFT_BRANDID = cursor.getString(cursor.getColumnIndex("LIFT_BRANDID"));
                String LIFT_PRODUCT = cursor.getString(cursor.getColumnIndex("LIFT_PRODUCT"));
                String LIFT_PRODUCTDATE = cursor.getString(cursor.getColumnIndex("LIFT_PRODUCTDATE"));
                String LIFT_STATUS = cursor.getString(cursor.getColumnIndex("LIFT_STATUS"));

                Elevator elevator = new Elevator();
                elevator.setLIFT_ID(LIFT_ID);
                elevator.setLIFT_IDCODE(LIFT_IDCODE);
                elevator.setLIFT_USER(LIFT_USER);
                elevator.setLIFT_AREAID(LIFT_AREAID);
                elevator.setLIFT_ADDRESSID(LIFT_ADDRESSID);
                elevator.setLIFT_MAINTENANCENAME_ID(LIFT_MAINTENANCENAME_ID);
                elevator.setLIFT_BRANDID(LIFT_BRANDID);
                elevator.setLIFT_PRODUCT(LIFT_PRODUCT);
                elevator.setLIFT_PRODUCTDATE(LIFT_PRODUCTDATE);
                elevator.setLIFT_STATUS(LIFT_STATUS);
                mElevators.add(elevator);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mElevators;
    }

    //条件查询电梯数据
    public List<Elevator> QueryElevatorsByKey(String keyStr, String value){
        List<Elevator> mElevators = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM Elevator WHERE "+keyStr+" LIKE '%" + value + "%'", null);
            while (cursor.moveToNext()){
                String LIFT_ID = cursor.getString(cursor.getColumnIndex("LIFT_ID"));
                String LIFT_IDCODE = cursor.getString(cursor.getColumnIndex("LIFT_IDCODE"));
                String LIFT_USER = cursor.getString(cursor.getColumnIndex("LIFT_USER"));
                String LIFT_AREAID = cursor.getString(cursor.getColumnIndex("LIFT_AREAID"));
                String LIFT_ADDRESSID = cursor.getString(cursor.getColumnIndex("LIFT_ADDRESSID"));
                String LIFT_MAINTENANCENAME_ID = cursor.getString(cursor.getColumnIndex("LIFT_MAINTENANCENAME_ID"));
                String LIFT_BRANDID = cursor.getString(cursor.getColumnIndex("LIFT_BRANDID"));
                String LIFT_PRODUCT = cursor.getString(cursor.getColumnIndex("LIFT_PRODUCT"));
                String LIFT_PRODUCTDATE = cursor.getString(cursor.getColumnIndex("LIFT_PRODUCTDATE"));
                String LIFT_STATUS = cursor.getString(cursor.getColumnIndex("LIFT_STATUS"));

                Elevator elevator = new Elevator();
                elevator.setLIFT_ID(LIFT_ID);
                elevator.setLIFT_IDCODE(LIFT_IDCODE);
                elevator.setLIFT_USER(LIFT_USER);
                elevator.setLIFT_AREAID(LIFT_AREAID);
                elevator.setLIFT_ADDRESSID(LIFT_ADDRESSID);
                elevator.setLIFT_MAINTENANCENAME_ID(LIFT_MAINTENANCENAME_ID);
                elevator.setLIFT_BRANDID(LIFT_BRANDID);
                elevator.setLIFT_PRODUCT(LIFT_PRODUCT);
                elevator.setLIFT_PRODUCTDATE(LIFT_PRODUCTDATE);
                elevator.setLIFT_STATUS(LIFT_STATUS);
                mElevators.add(elevator);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mElevators;
    }

    //获取所有电梯ID
    public List<String> getAllElevatorID(){
        List<String> mElevators = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.query(SQLiteDbHelper.TAB_ELEVATOR,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                String LIFT_ID = cursor.getString(cursor.getColumnIndex("LIFT_ID"));
                String LIFT_IDCODE = cursor.getString(cursor.getColumnIndex("LIFT_IDCODE"));
                String LIFT_USER = cursor.getString(cursor.getColumnIndex("LIFT_USER"));
                String LIFT_AREAID = cursor.getString(cursor.getColumnIndex("LIFT_AREAID"));
                String LIFT_ADDRESSID = cursor.getString(cursor.getColumnIndex("LIFT_ADDRESSID"));
                String LIFT_MAINTENANCENAME_ID = cursor.getString(cursor.getColumnIndex("LIFT_MAINTENANCENAME_ID"));
                String LIFT_BRANDID = cursor.getString(cursor.getColumnIndex("LIFT_BRANDID"));
                String LIFT_PRODUCTDATE = cursor.getString(cursor.getColumnIndex("LIFT_PRODUCTDATE"));
                String LIFT_STATUS = cursor.getString(cursor.getColumnIndex("LIFT_STATUS"));

                mElevators.add(LIFT_ID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mElevators;
    }

    //注册电梯参数数据
    public void insertElevatorParams(ElevatorParams elevatorParams){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("LIFT_ID",elevatorParams.getLIFT_ID());
            values.put("LIFT_RATEDLOAD",elevatorParams.getLIFT_RATEDLOAD());
            values.put("LIFT_RATEDSPEED",elevatorParams.getLIFT_RATEDSPEED());
            values.put("LIFT_WIDTH",elevatorParams.getLIFT_WIDTH());
            values.put("LIFT_HEIGHT",elevatorParams.getLIFT_HEIGHT());
            values.put("LIFT_VOLTAGE",elevatorParams.getLIFT_VOLTAGE());
            values.put("LIFT_CURRENT",elevatorParams.getLIFT_CURRENT());
            values.put("LIFT_TRACTORMODEL",elevatorParams.getLIFT_TRACTORMODEL());
            values.put("LIFT_TRACTIORWHEELDIAMETER",elevatorParams.getLIFT_TRACTIORWHEELDIAMETER());
            values.put("LIFT_TRACTIORRATIO",elevatorParams.getLIFT_TRACTIORRATIO());
            values.put("LIFT_TRACTIORTYPE",elevatorParams.getLIFT_TRACTIORTYPE());
            values.put("LIFT_BUFFERTYPE",elevatorParams.getLIFT_BUFFERTYPE());
            values.put("LIFT_SAFETYGEARTYPE",elevatorParams.getLIFT_SAFETYGEARTYPE());
            values.put("LIFT_TRACTIORNUMBER",elevatorParams.getLIFT_TRACTIORNUMBER());
            values.put("LIFT_TRACTIORROPENUMBER",elevatorParams.getLIFT_TRACTIORROPENUMBER());
            values.put("LIFT_MOTORTYPE",elevatorParams.getLIFT_MOTORTYPE());
            long code = db.insert(SQLiteDbHelper.TAB_ELEVATOR_PARAMS,null,values);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //更新电梯数据
    public void updateElevatorParams(ElevatorParams elevatorParams){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("LIFT_ID",elevatorParams.getLIFT_ID());
            values.put("LIFT_RATEDLOAD",elevatorParams.getLIFT_RATEDLOAD());
            values.put("LIFT_RATEDSPEED",elevatorParams.getLIFT_RATEDSPEED());
            values.put("LIFT_WIDTH",elevatorParams.getLIFT_WIDTH());
            values.put("LIFT_HEIGHT",elevatorParams.getLIFT_HEIGHT());
            values.put("LIFT_VOLTAGE",elevatorParams.getLIFT_VOLTAGE());
            values.put("LIFT_CURRENT",elevatorParams.getLIFT_CURRENT());
            values.put("LIFT_TRACTORMODEL",elevatorParams.getLIFT_TRACTORMODEL());
            values.put("LIFT_TRACTIORWHEELDIAMETER",elevatorParams.getLIFT_TRACTIORWHEELDIAMETER());
            values.put("LIFT_TRACTIORRATIO",elevatorParams.getLIFT_TRACTIORRATIO());
            values.put("LIFT_TRACTIORTYPE",elevatorParams.getLIFT_TRACTIORTYPE());
            values.put("LIFT_BUFFERTYPE",elevatorParams.getLIFT_BUFFERTYPE());
            values.put("LIFT_SAFETYGEARTYPE",elevatorParams.getLIFT_SAFETYGEARTYPE());
            values.put("LIFT_TRACTIORNUMBER",elevatorParams.getLIFT_TRACTIORNUMBER());
            values.put("LIFT_TRACTIORROPENUMBER",elevatorParams.getLIFT_TRACTIORROPENUMBER());
            values.put("LIFT_MOTORTYPE",elevatorParams.getLIFT_MOTORTYPE());
            long code = db.update(SQLiteDbHelper.TAB_ELEVATOR_PARAMS,values,"LIFT_ID=?",new String[]{elevatorParams.getLIFT_ID()});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //删除电梯数据
    public void deleteElevatorParams(ElevatorParams elevatorParams){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long code = db.delete(SQLiteDbHelper.TAB_ELEVATOR_PARAMS,"LIFT_ID =?",new String[]{elevatorParams.getLIFT_ID()});
    }

    //获取所有电梯参数
    public List<ElevatorParams> getAllElevatorsParams(){
        List<ElevatorParams> mElevatorsParams = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.query(SQLiteDbHelper.TAB_ELEVATOR_PARAMS,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                String LIFT_ID = cursor.getString(cursor.getColumnIndex("LIFT_ID"));
                String LIFT_RATEDLOAD = cursor.getString(cursor.getColumnIndex("LIFT_RATEDLOAD"));
                String LIFT_RATEDSPEED = cursor.getString(cursor.getColumnIndex("LIFT_RATEDSPEED"));
                String LIFT_WIDTH = cursor.getString(cursor.getColumnIndex("LIFT_WIDTH"));
                String LIFT_HEIGHT = cursor.getString(cursor.getColumnIndex("LIFT_HEIGHT"));
                String LIFT_VOLTAGE = cursor.getString(cursor.getColumnIndex("LIFT_VOLTAGE"));
                String LIFT_CURRENT = cursor.getString(cursor.getColumnIndex("LIFT_CURRENT"));
                String LIFT_TRACTORMODEL = cursor.getString(cursor.getColumnIndex("LIFT_TRACTORMODEL"));
                String LIFT_TRACTIORWHEELDIAMETER = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORWHEELDIAMETER"));
                String LIFT_TRACTIORRATIO = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORRATIO"));
                String LIFT_TRACTIORTYPE = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORTYPE"));
                String LIFT_BUFFERTYPE = cursor.getString(cursor.getColumnIndex("LIFT_BUFFERTYPE"));
                String LIFT_SAFETYGEARTYPE = cursor.getString(cursor.getColumnIndex("LIFT_SAFETYGEARTYPE"));
                String LIFT_TRACTIORNUMBER = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORNUMBER"));
                String LIFT_TRACTIORROPENUMBER = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORROPENUMBER"));
                String LIFT_MOTORTYPE = cursor.getString(cursor.getColumnIndex("LIFT_MOTORTYPE"));

                ElevatorParams params = new ElevatorParams();
                params.setLIFT_ID(LIFT_ID);
                params.setLIFT_RATEDLOAD(LIFT_RATEDLOAD);
                params.setLIFT_RATEDSPEED(LIFT_RATEDSPEED);
                params.setLIFT_WIDTH(LIFT_WIDTH);
                params.setLIFT_HEIGHT(LIFT_HEIGHT);
                params.setLIFT_VOLTAGE(LIFT_VOLTAGE);
                params.setLIFT_CURRENT(LIFT_CURRENT);
                params.setLIFT_TRACTORMODEL(LIFT_TRACTORMODEL);
                params.setLIFT_TRACTIORWHEELDIAMETER(LIFT_TRACTIORWHEELDIAMETER);
                params.setLIFT_TRACTIORRATIO(LIFT_TRACTIORRATIO);
                params.setLIFT_TRACTIORTYPE(LIFT_TRACTIORTYPE);
                params.setLIFT_BUFFERTYPE(LIFT_BUFFERTYPE);
                params.setLIFT_SAFETYGEARTYPE(LIFT_SAFETYGEARTYPE);
                params.setLIFT_TRACTIORNUMBER(LIFT_TRACTIORNUMBER);
                params.setLIFT_TRACTIORROPENUMBER(LIFT_TRACTIORROPENUMBER);
                params.setLIFT_MOTORTYPE(LIFT_MOTORTYPE);
                mElevatorsParams.add(params);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mElevatorsParams;
    }

    //获取电梯参数
    public ElevatorParams getElevatorParamsByID(String LIFT_ID){
        ElevatorParams elevatorParams = null;
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from ElevatorParams where LIFT_ID=?",new String[]{LIFT_ID});
            while (cursor.moveToNext()){
                String LIFT_RATEDLOAD = cursor.getString(cursor.getColumnIndex("LIFT_RATEDLOAD"));
                String LIFT_RATEDSPEED = cursor.getString(cursor.getColumnIndex("LIFT_RATEDSPEED"));
                String LIFT_WIDTH = cursor.getString(cursor.getColumnIndex("LIFT_WIDTH"));
                String LIFT_HEIGHT = cursor.getString(cursor.getColumnIndex("LIFT_HEIGHT"));
                String LIFT_VOLTAGE = cursor.getString(cursor.getColumnIndex("LIFT_VOLTAGE"));
                String LIFT_CURRENT = cursor.getString(cursor.getColumnIndex("LIFT_CURRENT"));
                String LIFT_TRACTORMODEL = cursor.getString(cursor.getColumnIndex("LIFT_TRACTORMODEL"));
                String LIFT_TRACTIORWHEELDIAMETER = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORWHEELDIAMETER"));
                String LIFT_TRACTIORRATIO = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORRATIO"));
                String LIFT_TRACTIORTYPE = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORTYPE"));
                String LIFT_BUFFERTYPE = cursor.getString(cursor.getColumnIndex("LIFT_BUFFERTYPE"));
                String LIFT_SAFETYGEARTYPE = cursor.getString(cursor.getColumnIndex("LIFT_SAFETYGEARTYPE"));
                String LIFT_TRACTIORNUMBER = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORNUMBER"));
                String LIFT_TRACTIORROPENUMBER = cursor.getString(cursor.getColumnIndex("LIFT_TRACTIORROPENUMBER"));
                String LIFT_MOTORTYPE = cursor.getString(cursor.getColumnIndex("LIFT_MOTORTYPE"));

                ElevatorParams params = new ElevatorParams();
                params.setLIFT_ID(LIFT_ID);
                params.setLIFT_RATEDLOAD(LIFT_RATEDLOAD);
                params.setLIFT_RATEDSPEED(LIFT_RATEDSPEED);
                params.setLIFT_WIDTH(LIFT_WIDTH);
                params.setLIFT_HEIGHT(LIFT_HEIGHT);
                params.setLIFT_VOLTAGE(LIFT_VOLTAGE);
                params.setLIFT_CURRENT(LIFT_CURRENT);
                params.setLIFT_TRACTORMODEL(LIFT_TRACTORMODEL);
                params.setLIFT_TRACTIORWHEELDIAMETER(LIFT_TRACTIORWHEELDIAMETER);
                params.setLIFT_TRACTIORRATIO(LIFT_TRACTIORRATIO);
                params.setLIFT_TRACTIORTYPE(LIFT_TRACTIORTYPE);
                params.setLIFT_BUFFERTYPE(LIFT_BUFFERTYPE);
                params.setLIFT_SAFETYGEARTYPE(LIFT_SAFETYGEARTYPE);
                params.setLIFT_TRACTIORNUMBER(LIFT_TRACTIORNUMBER);
                params.setLIFT_TRACTIORROPENUMBER(LIFT_TRACTIORROPENUMBER);
                params.setLIFT_MOTORTYPE(LIFT_MOTORTYPE);
                elevatorParams = params;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return elevatorParams;
    }

    //根据状态获取任务
    public List<Task> getTaskByState(String state){
        List<Task> mTasks = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from Task where LIFT_CURRENTSTATE=?",new String[]{state});
            while (cursor.moveToNext()){
                String LIFT_FORMID = cursor.getString(cursor.getColumnIndex("LIFT_FORMID"));
                String LIFT_ID = cursor.getString(cursor.getColumnIndex("LIFT_ID"));
                String LIFT_PROCESSOR = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSOR"));
                String LIFT_FAIULTTIME = cursor.getString(cursor.getColumnIndex("LIFT_FAIULTTIME"));
                String LIFT_SENDTIME = cursor.getString(cursor.getColumnIndex("LIFT_SENDTIME"));
                String LIFT_PROCESSORPHONE = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSORPHONE"));
                String LIFT_CURRENTSTATE = cursor.getString(cursor.getColumnIndex("LIFT_CURRENTSTATE"));
                String LIFT_FAULTTYPE = cursor.getString(cursor.getColumnIndex("LIFT_FAULTTYPE"));
                String FORM_STATE = cursor.getString(cursor.getColumnIndex("FORM_STATE"));
                String FORM_PERIOD = cursor.getString(cursor.getColumnIndex("FORM_PERIOD"));
                Task task = new Task();
                task.setLIFT_FORMID(LIFT_FORMID);
                task.setLIFT_ID(LIFT_ID);
                task.setLIFT_PROCESSOR(LIFT_PROCESSOR);
                task.setLIFT_FAIULTTIME(LIFT_FAIULTTIME);
                task.setLIFT_SENDTIME(LIFT_SENDTIME);
                task.setLIFT_PROCESSORPHONE(LIFT_PROCESSORPHONE);
                task.setLIFT_CURRENTSTATE(LIFT_CURRENTSTATE);
                task.setLIFT_FAULTTYPE(LIFT_FAULTTYPE);
                task.setFORM_STATE(FORM_STATE);
                task.setFORM_PERIOD(FORM_PERIOD);
                Elevator elevator = getElevatorByID(LIFT_ID);
                task.setElevator(elevator);
                mTasks.add(task);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mTasks;
    }

    //根据状态获取任务
    public List<Task> getTaskBSql(String sql, String[] state){
        List<Task> mTasks = new ArrayList<>();
        try{

            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql,state);
            while (cursor.moveToNext()){
                String LIFT_FORMID = cursor.getString(cursor.getColumnIndex("LIFT_FORMID"));
                String LIFT_ID = cursor.getString(cursor.getColumnIndex("LIFT_ID"));
                String LIFT_PROCESSOR = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSOR"));
                String LIFT_FAIULTTIME = cursor.getString(cursor.getColumnIndex("LIFT_FAIULTTIME"));
                String LIFT_SENDTIME = cursor.getString(cursor.getColumnIndex("LIFT_SENDTIME"));
                String LIFT_PROCESSORPHONE = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSORPHONE"));
                String LIFT_CURRENTSTATE = cursor.getString(cursor.getColumnIndex("LIFT_CURRENTSTATE"));
                String LIFT_FAULTTYPE = cursor.getString(cursor.getColumnIndex("LIFT_FAULTTYPE"));
                String FORM_STATE = cursor.getString(cursor.getColumnIndex("FORM_STATE"));
                String FORM_PERIOD = cursor.getString(cursor.getColumnIndex("FORM_PERIOD"));

                Task task = new Task();
                task.setLIFT_FORMID(LIFT_FORMID);
                task.setLIFT_ID(LIFT_ID);
                task.setLIFT_PROCESSOR(LIFT_PROCESSOR);
                task.setLIFT_FAIULTTIME(LIFT_FAIULTTIME);
                task.setLIFT_SENDTIME(LIFT_SENDTIME);
                task.setLIFT_PROCESSORPHONE(LIFT_PROCESSORPHONE);
                task.setLIFT_CURRENTSTATE(LIFT_CURRENTSTATE);
                task.setLIFT_FAULTTYPE(LIFT_FAULTTYPE);
                task.setFORM_STATE(FORM_STATE);
                task.setFORM_PERIOD(FORM_PERIOD);
                Elevator elevator = getElevatorByID(LIFT_ID);
                task.setElevator(elevator);
                mTasks.add(task);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mTasks;
    }


    //添加任务
    public void insertTask(Task task){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("LIFT_FORMID",task.getLIFT_FORMID());
            values.put("LIFT_ID",task.getLIFT_ID());
            values.put("FORM_STATE",task.getFORM_STATE());
            values.put("LIFT_PROCESSOR",task.getLIFT_PROCESSOR());
            values.put("LIFT_FAIULTTIME",task.getLIFT_FAIULTTIME());
            values.put("LIFT_SENDTIME",task.getLIFT_SENDTIME());
            values.put("LIFT_PROCESSORPHONE",task.getLIFT_PROCESSORPHONE());
            values.put("LIFT_CURRENTSTATE",task.getLIFT_CURRENTSTATE());
            values.put("LIFT_FAULTTYPE",task.getLIFT_FAULTTYPE());
            values.put("FORM_STATE",task.getFORM_STATE());
            values.put("FORM_PERIOD",task.getFORM_PERIOD());
            long code = db.insert(SQLiteDbHelper.TAB_TASK,null,values);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reportTask(Elevator elevator){
        String LIFT_FORMID = getRandomLIFT_FORMID();
        Task task = new Task();
        task.setLIFT_FORMID(LIFT_FORMID);//维保工单ID（主键）
        task.setElevator(elevator);//电梯ID
        task.setLIFT_ID(elevator.getLIFT_ID());
        task.setLIFT_PROCESSOR("");//维保人员
        task.setLIFT_FAIULTTIME(getSendTime());//报修时间
        task.setLIFT_SENDTIME(getSendTime());//派单时间
        task.setLIFT_PROCESSORPHONE("");//维保人电话
        task.setLIFT_CURRENTSTATE(Constant.TASK_STATE_REPORT);//当前状态
        task.setLIFT_FAULTTYPE("");//故障类型
        task.setFORM_STATE("报修");
        task.setFORM_PERIOD("1");
        insertTask(task);
    }

    //更新任务
    public void updateTask(Task task){
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("LIFT_FORMID",task.getLIFT_FORMID());
            values.put("LIFT_ID",task.getLIFT_ID());
            values.put("LIFT_PROCESSOR",task.getLIFT_PROCESSOR());
            values.put("LIFT_FAIULTTIME",task.getLIFT_FAIULTTIME());
            values.put("LIFT_SENDTIME",task.getLIFT_SENDTIME());
            values.put("LIFT_PROCESSORPHONE",task.getLIFT_PROCESSORPHONE());
            values.put("LIFT_CURRENTSTATE",task.getLIFT_CURRENTSTATE());
            values.put("LIFT_FAULTTYPE",task.getLIFT_FAULTTYPE());
            values.put("FORM_STATE",task.getFORM_STATE());
            long code = db.update(SQLiteDbHelper.TAB_TASK,values,"LIFT_FORMID =?",new String[]{task.getLIFT_FORMID()});
            long x = code;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void createPlanTask(String LIFT_ID, User mUser, String time){
        String LIFT_FORMID = getRandomLIFT_FORMID();
        Task task = new Task();
        task.setLIFT_FORMID(LIFT_FORMID);//维保工单ID（主键）
        Elevator elevator = getElevatorByID(LIFT_ID);
        task.setElevator(elevator);//电梯ID
        task.setLIFT_ID(LIFT_ID);
        task.setLIFT_PROCESSOR(mUser.getUserName());//维保人员
        task.setLIFT_FAIULTTIME(getSendTime());//报修时间
        task.setLIFT_SENDTIME(getSendTime());//派单时间
        task.setLIFT_PROCESSORPHONE(mUser.getTelephone());//维保人电话
        task.setLIFT_CURRENTSTATE(Constant.TASK_STATE_WAITING);//当前状态
        task.setLIFT_FAULTTYPE("定期检查");//故障类型
        task.setFORM_STATE("定期");
        task.setFORM_PERIOD(time);
        insertTask(task);
    }

    //生成随机LIFT_FORMID
    public String getRandomLIFT_FORMID(){
        String strRand="LF" ;
        for(int i=0;i<10;i++){
            strRand += String.valueOf((int)(Math.random() * 10)) ;
        }
        return strRand;
    }

    //生成保修日期
    public String getSendTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    //生成随机userid
    public String getRandomUSER_ID(){
        String strRand="LF" ;
        for(int i=0;i<10;i++){
            strRand += String.valueOf((int)(Math.random() * 10)) ;
        }
        return strRand;
    }

    //更新维修签到
    public void insertRepairSign(Task task){
        //更新任务状态
//        mDataFactory.updateTask(task);
//        mDataFactory.addRepairSign(task);
        updateTask(task);

    }

    //提交任务
    public void updateTaskFinish(Task task){
//        mDataFactory.updateTask(task);
//        mDataFactory.mRepairSigns.remove(task.getLIFT_FORMID());
        updateTask(task);
    }

    //获取所有任务
    public List<Task> getCurrentTasks(){
        List<Task> mtask = new ArrayList<>();
        for(Map.Entry<String, Task> entry: mDataFactory.mTasks.entrySet()){
            Task task = entry.getValue();
            mtask.add(task);
        }
        return mtask;
    }

    //获取所有任务
    public List<Task> getAllTasks(){
        List<Task> mTasks = new ArrayList<>();
        try{
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor cursor = db.query(SQLiteDbHelper.TAB_TASK,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                String LIFT_FORMID = cursor.getString(cursor.getColumnIndex("LIFT_FORMID"));
                String LIFT_ID = cursor.getString(cursor.getColumnIndex("LIFT_ID"));
                String LIFT_PROCESSOR = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSOR"));
                String LIFT_FAIULTTIME = cursor.getString(cursor.getColumnIndex("LIFT_FAIULTTIME"));
                String LIFT_SENDTIME = cursor.getString(cursor.getColumnIndex("LIFT_SENDTIME"));
                String LIFT_PROCESSORPHONE = cursor.getString(cursor.getColumnIndex("LIFT_PROCESSORPHONE"));
                String LIFT_CURRENTSTATE = cursor.getString(cursor.getColumnIndex("LIFT_CURRENTSTATE"));
                String LIFT_FAULTTYPE = cursor.getString(cursor.getColumnIndex("LIFT_FAULTTYPE"));
                String FORM_STATE = cursor.getString(cursor.getColumnIndex("FORM_STATE"));
                String FORM_PERIOD = cursor.getString(cursor.getColumnIndex("FORM_PERIOD"));
                Task task = new Task();
                task.setLIFT_FORMID(LIFT_FORMID);
                task.setLIFT_ID(LIFT_ID);
                task.setLIFT_PROCESSOR(LIFT_PROCESSOR);
                task.setLIFT_FAIULTTIME(LIFT_FAIULTTIME);
                task.setLIFT_SENDTIME(LIFT_SENDTIME);
                task.setLIFT_PROCESSORPHONE(LIFT_PROCESSORPHONE);
                task.setLIFT_CURRENTSTATE(LIFT_CURRENTSTATE);
                task.setLIFT_FAULTTYPE(LIFT_FAULTTYPE);
                task.setFORM_STATE(FORM_STATE);
                task.setFORM_PERIOD(FORM_PERIOD);
                Elevator elevator = getElevatorByID(LIFT_ID);
                task.setElevator(elevator);
                mTasks.add(task);
            }
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return mTasks;
    }

    //获取所有维修签到
    public List<Sign> getAllRepairSign(){
        List<Sign> mSigns= new ArrayList<>();
        for(Map.Entry<String, Sign> entry: mDataFactory.mRepairSigns.entrySet()){
            Sign sign = entry.getValue();
            mSigns.add(sign);
        }
        return mSigns;
    }

    //更新签到状态和任务状态
    public void updateSign(Sign sign){
        Task task = sign.getTask();
        mDataFactory.mRepairSigns.put(task.getLIFT_FORMID(),sign);
        mDataFactory.mTasks.put(task.getLIFT_FORMID(),task);

    }

    //轮训任务是否超时
    public static final int HSG_CHCEK_STATE = 1;
    public static final int HSG_CHCEK_PLAN_STATE = 2;
    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case HSG_CHCEK_STATE:
                    checkTaskState();
                    NotifyState.notifyRefreshData(mContext);
                    mHandler.sendEmptyMessageDelayed(HSG_CHCEK_STATE,10*1000);
                    break;
                case HSG_CHCEK_PLAN_STATE:
                    checkPlanTaskState();
                    NotifyState.notifyRefreshData(mContext);
                    mHandler.sendEmptyMessageDelayed(HSG_CHCEK_PLAN_STATE,20*1000);
                    break;
            }
            return false;
        }
    });

    public void checkTaskState(){
        List<Task> mAllTask = getAllTasks();
        for (int i=0;i<mAllTask.size();i++){
            Task task = mAllTask.get(i);
            long sendtime = getStringToDate(task.getLIFT_SENDTIME(),pattern);
            long nowtime = System.currentTimeMillis();
            long temp = nowtime - sendtime;
            if ((temp)>Constant.SIGN_TIME_OUT&&(task.getLIFT_CURRENTSTATE().equals("待接受")||task.getLIFT_CURRENTSTATE().equals("已接受待签到"))){
                task.setLIFT_CURRENTSTATE("已超时");
                updateTask(task);
            }
            if (task.getFORM_STATE().equals("定期")&&(task.getLIFT_CURRENTSTATE().equals(Constant.TASK_STATE_FINISH)||task.getLIFT_CURRENTSTATE().equals(Constant.TASK_STATE_TIMEOUT))){
                if (checkIsInPeriod(task)){
                    task.setLIFT_CURRENTSTATE(Constant.TASK_STATE_WAITING);
                    updateTask(task);
                }
            }
        }
    }

    public void checkPlanTaskState(){
        List<Task> mAllTask = getAllTasks();
        for (int i=0;i<mAllTask.size();i++){
            Task task = mAllTask.get(i);
            if (task.getFORM_STATE().equals("定期")&&(task.getLIFT_CURRENTSTATE().equals(Constant.TASK_STATE_FINISH)||task.getLIFT_CURRENTSTATE().equals(Constant.TASK_STATE_TIMEOUT))){
                if (checkIsInPeriod(task)){
                    task.setLIFT_CURRENTSTATE(Constant.TASK_STATE_WAITING);
                    updateTask(task);
                }
            }
        }
    }

    //检查是否超过日期
    public boolean checkIsInPeriod(Task task){
        try{
            int peroid = Integer.parseInt(task.getFORM_PERIOD());
            //设置转换的日期格式
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //当前时间
            Date curDate = new Date(System.currentTimeMillis());
            //上报时间
            Date senddate =simpleDateFormat.parse(task.getLIFT_SENDTIME());

            //得到相差的天数 betweenDate
            long betweenDate = (curDate.getTime() - senddate.getTime())/(60*60*24*1000);

            if (peroid>betweenDate){
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
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


    public interface IListener{
        public void onSuccess();
        public void onError(String error);
    };


}
