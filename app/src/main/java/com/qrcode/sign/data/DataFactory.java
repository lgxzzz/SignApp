package com.qrcode.sign.data;

import android.content.Context;



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
    public String mCurrenAddress = "";
    private String mKeyWord="电梯";

    //电梯数据
    public HashMap<String,Elevator> mElevators = new HashMap<>();
    //电梯参数列表数据
    public HashMap<String,ElevatorParams> mElevatorParams = new HashMap<>();
    //任务数据
    public HashMap<String,Task> mTasks = new HashMap<>();
    //维修签到数据
    public HashMap<String,Sign> mRepairSigns = new HashMap<>();
    //维保签到数据
    public List<Sign> mMaintainSigns = new ArrayList<>();

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
        mPoiSearchMgr = new PoiSearchMgr(mContext);
        mGeoSearchMgr = new GeoSearchMgr(mContext);

        mGeoSearchMgr.setGeoSearchListener(new GeoSearchMgr.GeoSearchListener() {
            @Override
            public void onSuccess(GeocodeAddress address) {
                mCurrenAddress = address.getDistrict();
            }

            @Override
            public void onSuccess(String address) {
                mCurrenAddress = address;
            }

            @Override
            public void onFail(String error) {

            }
        });


        mPoiSearchMgr.setPoiListener(new PoiSearchMgr.PoiSearchListener() {

            @Override
            public void onSuccess(List<PoiItem> poiItems) {
                mElevators.clear();
                mTasks.clear();
                for (int i = 0;i<poiItems.size();i++){
                    PoiItem item = poiItems.get(i);
                    mCurrenCity = item.getCityName();
                    //生成电梯数据
                    LatLonPoint latLonPoint = item.getLatLonPoint();
                    String LIFT_ID = getRandomLIFT_ID();
                    Elevator elevator = new Elevator();
                    elevator.setLIFT_ID(LIFT_ID);//电梯 ID( 主键 )
                    elevator.setLIFT_IDCODE("ID"+getRandomCode());//电梯识别码
                    elevator.setLIFT_AREAID("AREAID"+getRandomCode());//电梯所属地区
                    elevator.setLIFT_ADDRESSID(latLonPoint.getLongitude()+","+latLonPoint.getLatitude());//电梯地址
                    elevator.setLIFT_USER(item.getSnippet());//电梯使用单位
                    elevator.setLIFT_MAINTENANCENAME_ID("xxx维修公司");//电梯所属维保单位
                    elevator.setLIFT_BRANDID("东芝");//电梯品牌
                    elevator.setLIFT_PRODUCT("上海xxx制造公司");//制造单位
                    elevator.setLIFT_PRODUCTDATE(getProduceTime());//制造日期
                    mElevators.put(LIFT_ID,elevator);


                    //生成电梯参数信息
                    ElevatorParams elevatorParams = new ElevatorParams();
                    elevatorParams.setLIFT_ID(LIFT_ID);//电梯id
                    elevatorParams.setLIFT_RATEDLOAD("3t");//额定载重量
                    elevatorParams.setLIFT_RATEDSPEED("3m/s");//额定速度
                    elevatorParams.setLIFT_WIDTH("3m");//轿厢宽度
                    elevatorParams.setLIFT_HEIGHT("3m");//轿厢高度
                    elevatorParams.setLIFT_VOLTAGE("129v");//电梯电压
                    elevatorParams.setLIFT_CURRENT("3V");//电梯电流
                    elevatorParams.setLIFT_TRACTORMODEL("DX-ddfsf");//曳引机型号
                    elevatorParams.setLIFT_TRACTIORWHEELDIAMETER("XS-2232");//曳引机最大功率
                    elevatorParams.setLIFT_TRACTIORRATIO("200");//曳引比
                    elevatorParams.setLIFT_TRACTIORTYPE("200");//曳引机类型
                    elevatorParams.setLIFT_BUFFERTYPE("类型1");//缓冲器类型
                    elevatorParams.setLIFT_SAFETYGEARTYPE("类型1");//安全齿轮类型
                    elevatorParams.setLIFT_TRACTIORNUMBER("X-113");//曳引机编号
                    elevatorParams.setLIFT_TRACTIORROPENUMBER("3");//曳引绳根数
                    elevatorParams.setLIFT_MOTORTYPE("D-xsdf2111");//电动机型号
                    mElevatorParams.put(LIFT_ID,elevatorParams);

                    //生成任务
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
                    mTasks.put(LIFT_FORMID,task);
                }

                if (mlistener!=null){
                    mlistener.onGetDefaultElevators(mElevators,mElevatorParams,mTasks);
                }

                NotifyState.notifyRefreshData(mContext);
            }

            @Override
            public void onFail(String error) {

            }
        });
        getPosition();
    };

    public void doSearchQueryWithKeyWord(){
        mPoiSearchMgr.doSearchQuery(mKeyWord,mCurrentPosition.latitude,mCurrentPosition.longitude);
    }

    public void doSearchByGps(){
        mGeoSearchMgr.getLocationInfo(new LatLonPoint(mLatitude,mLongitude));
    }

    //获取定位信息并且查询当前的POI点周边
    public void getPosition(){
        mLocationMgr.getLonLat(mContext, new LocationMgr.LonLatListener() {
            @Override
            public void getLonLat(AMapLocation aMapLocation) {
                mLongitude = aMapLocation.getLongitude();
                mLatitude = aMapLocation.getLatitude();
                mCurrentPosition = new LatLng(mLatitude,mLongitude);
                mCurrenCity = aMapLocation.getCity();
                doSearchByGps();
                doSearchQueryWithKeyWord();
            }
        });
    }

    //生成随机LIFT_ID
    public String getRandomLIFT_ID(){
        String strRand="L" ;
        for(int i=0;i<10;i++){
            strRand += String.valueOf((int)(Math.random() * 10)) ;
        }
        return strRand;
    }

    //生成随机LIFT_FORMID
    public String getRandomLIFT_FORMID(){
        String strRand="LF" ;
        for(int i=0;i<10;i++){
            strRand += String.valueOf((int)(Math.random() * 10)) ;
        }
        return strRand;
    }

    //随机生成5位数
    public String getRandomCode(){
        String strRand="" ;
        for(int i=0;i<5;i++){
            strRand += String.valueOf((int)(Math.random() * 10)) ;
        }
        return strRand;
    }

    //随机获取故障类型
    public String getRandomFaultType(){
        String[] fault = new String[]{"故障老旧","线路老化","按键失灵"};
        int index = (int)(Math.random() * 2);
        return fault[index];
    }

    //生成保修日期
    public String getSendTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    //生成制造日期
    public String getProduceTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    //更新任务状态
    public void updateTask(Task task){
        mTasks.put(task.getLIFT_FORMID(),task);
    }

    //添加维修签到已接受待签到
    public void addRepairSign(Task task){
        Sign sign = new Sign();
        sign.setState("待签到");
        sign.setTask(task);
        sign.setType("维修签到");
        mRepairSigns.put(task.getLIFT_FORMID(),sign);
    }

    //生成随机对电梯对象
    public Elevator createRandomElevator(){
        Elevator elevator = new Elevator();
        elevator.setLIFT_ID(getRandomLIFT_ID());
        elevator.setLIFT_IDCODE("ID"+getRandomCode());
        elevator.setLIFT_ADDRESSID(mLongitude+","+mLatitude);
        elevator.setLIFT_USER(mCurrenAddress);
        return elevator;
    }

    public IListener mlistener;

    public void setMlistener(IListener mlistener) {
        this.mlistener = mlistener;
    }

    public interface IListener{
        public void onGetDefaultElevators(HashMap<String, Elevator> mElevators, HashMap<String, ElevatorParams> mElevatorParams, HashMap<String, Task> mTasks);
    }
}
