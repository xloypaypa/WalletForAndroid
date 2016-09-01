package com.example.xsu.walletforandroid;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.xsu.walletforandroid.handler.MessageHandler;
import com.example.xsu.walletforandroid.net.NetService;
import com.example.xsu.walletforandroid.net.ProtocolBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import layout.BudgetFragment;
import layout.MoneyFragment;
import model.entity.BudgetEntity;
import model.entity.MoneyEntity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MoneyFragment.OnMoneyFragmentInteractionListener, BudgetFragment.OnBudgetFragmentInteractionListener {

    private ServiceConnection serviceConnection;
    private NetService.NetBinder netBinder;
    private Handler handler;

    private FrameLayout fragment;

    private List<MoneyEntity> moneyEntities;
    private List<BudgetEntity> budgetEntities;

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

        fragment = (FrameLayout) this.findViewById(R.id.fragment);

        this.handler = new MessageHandler.Builder(this)
                .addCommandSolver("getMoney", new MessageHandler.CommandSolver() {
                    @Override
                    public boolean solveCommand(Activity activity, byte[] body) throws JSONException {
                        List<Fragment> all = getSupportFragmentManager().getFragments();
                        List<MoneyEntity> moneyEntities = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(new String(body));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MoneyEntity moneyEntity = new MoneyEntity();
                            moneyEntity.updateValueFromJson(jsonArray.get(i).toString());
                            moneyEntities.add(moneyEntity);
                        }
                        MainActivity.this.moneyEntities = moneyEntities;
                        for (Fragment fragment : all) {
                            if (fragment instanceof MoneyFragment) {
                                ((MoneyFragment) fragment).setMoneyList(moneyEntities);
                            }
                        }
                        return true;
                    }
                })
                .addCommandSolver("getBudget", new MessageHandler.CommandSolver() {
                    @Override
                    public boolean solveCommand(Activity activity, byte[] body) throws JSONException {
                        List<Fragment> all = getSupportFragmentManager().getFragments();
                        List<BudgetEntity> budgetEntities  = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(new String(body));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            BudgetEntity budgetEntity = new BudgetEntity();
                            budgetEntity.updateValueFromJson(jsonArray.get(i).toString());
                            budgetEntities.add(budgetEntity);
                        }
                        MainActivity.this.budgetEntities = budgetEntities;
                        for (Fragment fragment : all) {
                            if (fragment instanceof BudgetFragment) {
                                ((BudgetFragment) fragment).setBudgetList(budgetEntities);
                            }
                        }
                        return true;
                    }
                })
                .addCommandSolver("updateMoneyBroadcast", new MessageHandler.CommandSolver() {
                    @Override
                    public boolean solveCommand(Activity activity, byte[] body) throws JSONException {
                        try {
                            netBinder.sendMessage(ProtocolBuilder.getMoney());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })
                .addCommandSolver("updateBudgetBroadcast", new MessageHandler.CommandSolver() {
                    @Override
                    public boolean solveCommand(Activity activity, byte[] body) throws JSONException {
                        try {
                            netBinder.sendMessage(ProtocolBuilder.getBudget());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).create();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        if (id == R.id.nav_money) {
            MoneyFragment moneyFragment = MoneyFragment.newInstance();
            moneyFragment.setMoneyList(this.moneyEntities);
            fragment = moneyFragment;
        } else if (id == R.id.nav_budget) {
            BudgetFragment budgetFragment = BudgetFragment.newInstance();
            budgetFragment.setBudgetList(this.budgetEntities);
            fragment = budgetFragment;
        }

        List<Fragment> all = getSupportFragmentManager().getFragments();
        for (Fragment now : all) {
            if (now instanceof MoneyFragment) {
                ((MoneyFragment) now).setMoneyList(moneyEntities);
            } else if (now instanceof BudgetFragment) {
                ((BudgetFragment) now).setBudgetList(budgetEntities);
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(this.fragment.getId(), fragment).commit();

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
    public void onAddMoneyFragmentInteraction(String typename, double value) {
        try {
            netBinder.sendMessage(ProtocolBuilder.addMoney(typename, value));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoveMoneyFragmentInteraction(String typename) {
        try {
            netBinder.sendMessage(ProtocolBuilder.removeMoney(typename));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBudgetFragmentInteraction(Uri uri) {

    }
}
