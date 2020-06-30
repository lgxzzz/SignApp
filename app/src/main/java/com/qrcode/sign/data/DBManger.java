package com.qrcode.sign.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import com.amap.api.maps.model.LatLng;
import com.qrcode.sign.bean.User;

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
    public LatLng mPosition;

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
            public void onLonLat(LatLng mCurrentPosition) {
                mPosition = mCurrentPosition;
            }
        });
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

    //生成随机userid
    public String getRandomUSER_ID(){
        String strRand="LF" ;
        for(int i=0;i<10;i++){
            strRand += String.valueOf((int)(Math.random() * 10)) ;
        }
        return strRand;
    }

    public interface IListener{
        public void onSuccess();
        public void onError(String error);
    };


}
