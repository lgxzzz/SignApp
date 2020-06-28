package com.qrcode.sign.navi;

import android.content.Context;
import android.util.Log;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

public class GeoSearchMgr implements GeocodeSearch.OnGeocodeSearchListener {

    private GeoSearchListener mListener;
    private Context mContext;
    private GeocodeSearch geocodeSearch;

    public GeoSearchMgr(Context mContext){
        this.mContext = mContext;
        geocodeSearch = new GeocodeSearch(mContext);
        geocodeSearch.setOnGeocodeSearchListener(this);
    }

    public void setGeoSearchListener(GeoSearchListener listener){
        this.mListener = listener;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        Log.e("lgx","");
        String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
        mListener.onSuccess(address);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);

                if(address != null) {
                   mListener.onSuccess(address);
                }
            } else {
                mListener.onFail("未搜索到该地址");
            }
        } else {
            mListener.onFail("搜索失败");
        }
    }


    public interface GeoSearchListener{
        public void onSuccess(GeocodeAddress address);
        public void onSuccess(String address);
        public void onFail(String error);
    }

    public void getGeoInfo(String keyword, String city){
        GeocodeQuery query = new GeocodeQuery(keyword, city);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocodeSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    public void getLocationInfo(LatLonPoint latLonPoint){
        // 第一个参数表示一个Latlng(经纬度)，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }
}
