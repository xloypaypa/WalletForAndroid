package com.example.xsu.walletforandroid;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.xsu.walletforandroid.net.NetService;

import java.util.Locale;

public class MainActiviy extends AppCompatActivity {

    private Handler handler;
    private ServiceConnection serviceConnection;
    private NetService.NetBinder netBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activiy);

        this.handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                return true;
            }
        });

        Intent intent = new Intent(this, NetService.class);
        this.startService(intent);

        final EditText ipEdit = (EditText) this.findViewById(R.id.ipText);
        final EditText portEdit = (EditText) this.findViewById(R.id.portText);
        Button connectButton = (Button) this.findViewById(R.id.connectButton);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getString("ip", null) != null) {
            ipEdit.setText(sharedPreferences.getString("ip", null));
        }
        if (sharedPreferences.getInt("port", -1) != -1) {
            portEdit.setText(String.format(Locale.getDefault(), "%d", sharedPreferences.getInt("port", -1)));
        }

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipEdit.getText().toString();
                int port = Integer.parseInt(portEdit.getText().toString());

                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("ip", ip);
                edit.putInt("port", port);
                edit.apply();

                netBinder.connect(ip, port);
                try {
                    netBinder.sendMessage("/getSessionID#{}".getBytes());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                netBinder = (NetService.NetBinder) iBinder;
                netBinder.setHandler(handler);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        this.bindService(intent, serviceConnection, 0);
        Log.d("activity", "create");
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unbindService(serviceConnection);
        Log.d("activity", "pause");
    }
}
