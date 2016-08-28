package com.example.xsu.walletforandroid;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.xsu.walletforandroid.net.NetService;
import com.example.xsu.walletforandroid.net.ProtocolBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ServiceConnection serviceConnection;
    private NetService.NetBinder netBinder;
    private Handler handler;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Bundle data = message.getData();
                String command = data.getString("command");
                byte[] body = data.getByteArray("body");

                if (command == null || body == null) {
                    return false;
                }

                if (command.equals("/login")) {
                    try {
                        netBinder.sendMessage(ProtocolBuilder.useApp("wallet"));
                    } catch (InterruptedException | JSONException e) {
                        e.printStackTrace();
                    }
                } else if (command.equals("/useApp")) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(body));
                        String result = jsonObject.getString("result");
                        if (result.equals("ok")) {
                            Intent intentToMain = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(intentToMain);
                            LoginActivity.this.onStop();
                        }
                    } catch (JSONException e) {
                        return false;
                    }
                }
                return true;
            }
        });

        loadComponent();

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String sessionId = getIntent().getStringExtra("sessionId");
                try {
                    netBinder.sendMessage(ProtocolBuilder.login(username, password, sessionId));
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToLogin = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intentToLogin);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindNetService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unbindService(serviceConnection);
    }

    private void loadComponent() {
        this.usernameEditText = (EditText) this.findViewById(R.id.usernameEditText);
        this.passwordEditText = (EditText) this.findViewById(R.id.passwordEditText);
        this.loginButton = (Button) this.findViewById(R.id.loginButton);
        this.registerButton = (Button) this.findViewById(R.id.registerButton);
    }

    private void bindNetService() {
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
