package com.zlcm.mapgaode;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;

/**
 * 定位服务类
 */
public class LocationService extends Service {
    private AMapLocationClient mLocationClient;
    private double longitude;//经度
    private double latitude;//维度
    private int i = 1;
    private StringBuffer stringBuffer;
    private CountDownTimer countDownTimer;
    private MyBinder mBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //第一次为0.0,0.0, 35000只有5次  40000有6次
        countDownTimer = new CountDownTimer(40000, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (latitude != 0.0 && longitude != 0.0) {
                    LocationInfoBean locationInfoBean = new LocationInfoBean();
                    locationInfoBean.setLatitude(latitude);
                    locationInfoBean.setLongitude(longitude);
                    String s = new Gson().toJson(locationInfoBean);
                    Toast.makeText(getApplicationContext(), "第" + i + "次" + s, Toast.LENGTH_SHORT).show();
                    stringBuffer.append(i + s);
                    i++;
                    if (i == 7) {
                        //6次定位  所有信息
                        String s6 = stringBuffer.toString();
                        Toast.makeText(getApplicationContext(), "6次所有信息:" + s6, Toast.LENGTH_SHORT).show();
                        System.out.println("6次所有信息" + stringBuffer.toString());
                        countDownTimer.cancel();
                    }
                }
            }
            @Override
            public void onFinish() {
            }
        };
    }


    class MyBinder extends Binder {
        /**
         * 开启定位
         */
        public void startLocation() {
            Toast.makeText(LocationService.this, "startLocation", Toast.LENGTH_SHORT).show();
            mLocationClient = new AMapLocationClient(getApplicationContext());
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            //设置定位间隔
            locationOption.setInterval(5000);
            //设置定位模式，其他模式见LocationMode
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            mLocationClient.setLocationOption(locationOption);
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    //纬度
                    latitude = aMapLocation.getLatitude();
                    //经度
                    longitude = aMapLocation.getLongitude();
                    Log.i("location",
                            "纬度" + latitude + "经度"
                                    + longitude
                    );
                }
            });
            //开启定位
            mLocationClient.startLocation();
            stringBuffer = new StringBuffer();
            countDownTimer.start();
        }
    }

    /**
     * 解除绑定
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        stopLocation();
        return super.onUnbind(intent);
    }

    /**
     * 解除绑定  停止服务 关闭CountDownTimer
     */
    public void stopLocation() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }
}