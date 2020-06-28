package com.qrcode.sign.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qrcode.sign.util.SharedPreferenceUtil;
import com.smart.elevator.util.SharedPreferenceUtil;

public class SQLiteDbHelper extends SQLiteOpenHelper {

    //数据库名称
    public static final String DB_NAME = "Elevator.db";
    //数据库版本号
    public static int DB_VERSION = 16;
    //用户表
    public static final String TAB_USER = "UserInfo";
    //签到表
    public static final String TAB_SIGN = "Sign";

    Context context;
    public SQLiteDbHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableUser(db);
        createTableSign(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        SharedPreferenceUtil.setFirstTimeUse(true,context);
        db.execSQL("DROP TABLE IF EXISTS "+TAB_USER);
        db.execSQL("DROP TABLE IF EXISTS "+TAB_SIGN);
        onCreate(db);
    }

    //创建人员表
    public void createTableUser(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TAB_USER +
                "(USER_ID varchar(20) primary key, " +
                "USER_NAME varchar(20), " +
                "USER_PASSWORD varchar(20), " +
                "LIFT_PROCESSORPHONE varchar(20), " +
                "USER_MAIL varchar(20), " +
                "USER_CHARCTER varchar(20))");
    }

    //创建签到表
    public void createTableSign(SQLiteDatabase db){
//        db.execSQL("CREATE TABLE IF NOT EXISTS "+TAB_SIGN +
//                "(BudegetTypeId varchar(60) primary key, " +
//                "type varchar(60), " +
//                "note varchar(60))");
    }
}
