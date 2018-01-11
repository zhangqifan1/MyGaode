package com.zlcm.mapgaode;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * ----------------------
 * 开启定位
 * ----------------
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button butBindService;
    private Button butUnbindStopService;

    private LocationService.MyBinder myBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (LocationService.MyBinder) service;
            myBinder.startLocation();
        }
    };
    private boolean isbind;
    private Intent bindIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        butBindService = (Button) findViewById(R.id.butBindService);
        butUnbindStopService = (Button) findViewById(R.id.butUnbindStopService);

        butBindService.setOnClickListener(this);
        butUnbindStopService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butBindService://绑定开启服务
                bindIntent = new Intent(this, LocationService.class);
                isbind = bindService(bindIntent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.butUnbindStopService://解除绑定服务
                if(isbind){
                    unbindService(connection);
                    isbind=false;
                }
                break;
        }
    }
}