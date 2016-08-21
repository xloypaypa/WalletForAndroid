package com.example.xsu.walletforandroid;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.xsu.walletforandroid.net.NetService;

public class MainActiviy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activiy);

        Intent intent = new Intent(this, NetService.class);
        this.startService(intent);

        this.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                NetService.NetBinder netBinder = (NetService.NetBinder) iBinder;
                netBinder.connect("192.241.130.88", 10200);
                try {
                    netBinder.sendMessage("/getSessionID#{}".getBytes());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, 0);
    }
}
