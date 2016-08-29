package com.example.xsu.walletforandroid;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.xsu.walletforandroid.handler.MessageHandler;
import com.example.xsu.walletforandroid.net.NetService;
import com.example.xsu.walletforandroid.net.ProtocolBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private ServiceConnection serviceConnection;
    private NetService.NetBinder netBinder;
    private Handler handler;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.handler = new MessageHandler.Builder(this)
                .addCommandSolver("/register", new MessageHandler.CommandSolver() {
                    @Override
                    public boolean solveCommand(Activity activity, byte[] body) throws JSONException {
                        JSONObject jsonObject = new JSONObject(new String(body));
                        if (jsonObject.getString("result").equals("ok")) {
                            activity.finish();
                        }
                        return true;
                    }
                }).create();

        loadComponent();

        this.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.finish();
            }
        });

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                if (password.equals(confirmPassword)) {
                    try {
                        netBinder.sendMessage(ProtocolBuilder.register(username, password));
                    } catch (InterruptedException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("error")
                            .setMessage("please input same issue")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
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
        this.confirmPasswordEditText = (EditText) this.findViewById(R.id.confirmPasswordEditText);
        this.registerButton = (Button) this.findViewById(R.id.registerButton);
        this.backButton = (Button) this.findViewById(R.id.backButton);
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
