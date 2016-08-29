package com.example.xsu.walletforandroid;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.xsu.walletforandroid.handler.MessageHandler;
import com.example.xsu.walletforandroid.net.NetService;
import com.example.xsu.walletforandroid.net.ProtocolBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ConnectActivity extends AppCompatActivity {

    private Handler handler;
    private ServiceConnection serviceConnection;
    private NetService.NetBinder netBinder;
    private EditText ipEdit;
    private EditText portEdit;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_connect);

        this.handler = new MessageHandler.Builder(this)
                .addCommandSolver("/getSessionID", new MessageHandler.CommandSolver() {
                    @Override
                    public boolean solveCommand(Activity activity, byte[] body) throws JSONException {
                        JSONObject jsonObject = new JSONObject(new String(body));
                        String sessionId = jsonObject.getString("result");
                        Intent intentToLogin = new Intent(activity, LoginActivity.class);
                        intentToLogin.putExtra("sessionId", sessionId);
                        activity.startActivity(intentToLogin);
                        activity.finish();
                        return true;
                    }
                }).create();

        loadComponent();

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
                    netBinder.sendMessage(ProtocolBuilder.getSessionId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        startAndBindNetService();
        Log.d("activity", "create");
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unbindService(serviceConnection);
        Log.d("activity", "pause");
    }

    private void loadComponent() {
        ipEdit = (EditText) this.findViewById(R.id.ipText);
        portEdit = (EditText) this.findViewById(R.id.portText);
        connectButton = (Button) this.findViewById(R.id.connectButton);
    }

    private void startAndBindNetService() {
        Intent intentToNet = new Intent(this, NetService.class);
        this.startService(intentToNet);
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
        this.bindService(intentToNet, serviceConnection, 0);
    }
}
