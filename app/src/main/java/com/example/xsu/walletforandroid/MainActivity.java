package com.example.xsu.walletforandroid;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.xsu.walletforandroid.net.NetService;
import com.example.xsu.walletforandroid.net.ProtocolBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import layout.MoneyFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MoneyFragment.OnFragmentInteractionListener {

    private ServiceConnection serviceConnection;
    private NetService.NetBinder netBinder;
    private Handler handler;

    private MoneyFragment moneyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        moneyFragment = (MoneyFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        this.handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Bundle data = message.getData();
                String command = data.getString("command");
                byte[] body = data.getByteArray("body");

                if (command == null || body == null) {
                    return false;
                }

                if (command.equals("getMoney")) {
                    try {
                        JSONArray jsonArray = new JSONArray(new String(body));
                        moneyFragment.setMoneyList(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        bindNetService();
    }

    private void bindNetService() {
        Intent intentToNet = new Intent(this, NetService.class);
        this.startService(intentToNet);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                netBinder = (NetService.NetBinder) iBinder;
                netBinder.setHandler(handler);
                try {
                    netBinder.sendMessage(ProtocolBuilder.getMoney());
                    netBinder.sendMessage(ProtocolBuilder.getBudget());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        this.bindService(intentToNet, serviceConnection, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        if (id == R.id.nav_money) {
            fragment = this.moneyFragment;
        } else if (id == R.id.nav_budget) {

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unbindService(serviceConnection);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
