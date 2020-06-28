package com.qrcode.sign.data;

import android.content.Context;


import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.qrcode.sign.bean.Sign;
import com.qrcode.sign.navi.LocationMgr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataFactory {
    private Context mContext;
    public static  DataFactory instance;

    //定位模块
    private LocationMgr mLocationMgr;

    public LatLng mCurrentPosition; //当前地点

    public Double mLongitude;
    public Double mLatitude;
    public String mCurrenCity = "";

    public static DataFactory getInstance(Context mContext){
        if (instance == null){
            instance = new DataFactory(mContext);
        }
        return instance;
    };

    public DataFactory(Context mContext){
        this.mContext = mContext;
        initData();
    }

    public void initData(){
        mLocationMgr  = new LocationMgr(mContext);
        getPosition();
    };

    //获取定位信息
    public void getPosition(){
        mLocationMgr.getLonLat(mContext, new LocationMgr.LonLatListener() {
            @Override
            public void getLonLat(AMapLocation aMapLocation) {
                mLongitude = aMapLocation.getLongitude();
                mLatitude = aMapLocation.getLatitude();
                mCurrentPosition = new LatLng(mLatitude,mLongitude);
                mCurrenCity = aMapLocation.getCity();
                mlistener.onLonLat(mCurrentPosition);
            }
        });
    }

    public IListener mlistener;

    public void setMlistener(IListener mlistener) {
        this.mlistener = mlistener;
    }

    public interface IListener{
        public void onLonLat(LatLng mCurrentPosition);
    }
}
