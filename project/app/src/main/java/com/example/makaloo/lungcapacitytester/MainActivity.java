package com.example.makaloo.lungcapacitytester;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static String tableName;

    //蓝牙管理类
    private BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;
    private BluetoothSocket btSocket;
    static OutputStream outputStream;
    static InputStream inputStream;

    //蓝牙扫描广播
    private BroadcastReceiver mReceiver;
    private BroadcastReceiver mReceiver1;

    //扫描开始与结束标志
    private ProgressBar progressBar;

    //显示信息的TextView
    TextView textView;

    //显示扫描到的蓝牙设备的ListView
    ListView lvShowDevice;

    //listBTDevicey用于识别device,listShowBTDevice用于lvShowDevice的显示
    private List<BluetoothDevice> listBTDevice = new ArrayList<BluetoothDevice>();
    private List<String> listShowBTDevie = new ArrayList<String>();
    private ArrayAdapter<String> btDeviceArrayAdapter = null;

    //创建Rfcomm通道的UUID码
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    //蓝牙连接与断开的广播和标志
    private BroadcastReceiver btConnectReceiver;
    private BroadcastReceiver btDisconnectReceiver;
    private IntentFilter connectIntentFilter;
    private IntentFilter disconnectIntentFilter;
    private boolean isConnect = false;

    private String deviceName;

    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvShowDevice = (ListView) findViewById(R.id.lv_device);
        textView = (TextView) findViewById(R.id.text_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar(); //得到ActionBar实例，即ToolBar
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        setSupportActionBar(toolbar);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); //那个按钮就是HomeAsUp
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh); //下拉刷新
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (btAdapter.isEnabled()) {
                    btAdapter.startDiscovery();
                    Toast.makeText(MainActivity.this,"开始扫描",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,"请开启蓝牙",Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
        //navView.setCheckedItem(R.id.nav_search); //默认选项
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_search:
                        mDrawerLayout.closeDrawers();
                        if (btAdapter.isEnabled()) {
                            btAdapter.startDiscovery();
                            Toast.makeText(MainActivity.this,"开始扫描",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(ProgressBar.VISIBLE);
                        } else {
                            Toast.makeText(MainActivity.this,"请开启蓝牙",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_test:
                        mDrawerLayout.closeDrawers();
                        if (isConnect) {
                            Intent intent = new Intent(MainActivity.this, SaveActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "请先连接蓝牙", Toast.LENGTH_LONG)
                                    .show();
                        }
                        break;
                    case R.id.nav_data:
                        mDrawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_exit:
                        mDrawerLayout.closeDrawers();
                        finish();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        //如果扫描到有蓝牙设备，将其MAC地址与设备名称添加到ListView上
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                textView.setText("扫描到的蓝牙设备：");
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // 通过这个意图来获得device
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listBTDevice.add(device);
                    listShowBTDevie.add(device.getAddress() + " " + device.getName());
                    btDeviceArrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,listShowBTDevie);
                    lvShowDevice.setAdapter(btDeviceArrayAdapter);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver,intentFilter);

        //扫描结束时ProgressBar不可见
        mReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                swipeRefresh.setRefreshing(false);
                if (listBTDevice.size() == 0) {
                    textView.setText("没有扫描到有蓝牙设备");
                }
            }
        };

        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver1,intentFilter1);

        //蓝牙连接时把标志位isConnect设为true
        btConnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isConnect = true;
                textView.setText("连接至" + deviceName +  ",请进入测试界面");
            }
        };

        connectIntentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(btConnectReceiver, connectIntentFilter);

        //蓝牙断开连接时尝试启动TryToConnect线程重连
        btDisconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isConnect = false;
                if (!isConnect) {
                    textView.setText("连接断开，请检查蓝牙设备");
                }
                new TryToConnect().start();
            }
        };

        disconnectIntentFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(btDisconnectReceiver, disconnectIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //当点击到一个扫描的Item时，发起连接
        lvShowDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deviceName = listBTDevice.get(position).getName();
                btAdapter.cancelDiscovery();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                swipeRefresh.setRefreshing(false);
                btDevice = btAdapter.getRemoteDevice(listBTDevice.get(position).getAddress());

                //点击产生一个Dialog提示是否连接，连接成功后通过textView显示连接的设备名称
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("连接到此设备？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new TryToConnect().start();
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnect) {
            try {
                outputStream.close();
                inputStream.close();
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //取消广播注册
        unregisterReceiver(btConnectReceiver);
        unregisterReceiver(btDisconnectReceiver);
        unregisterReceiver(mReceiver1);
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth_request:
                if (!btAdapter.isEnabled()) {
                    btAdapter.enable();
                    item.setIcon(R.drawable.bluetooth_enabled);
                }
                if (btAdapter.isEnabled()) {
                    btAdapter.disable();
                    item.setIcon(R.drawable.bluetooth_disabled);
                }
                break;
            case R.id.search_bluetooth:
                if (btAdapter.isEnabled()) {
                    btAdapter.startDiscovery();
                    Toast.makeText(MainActivity.this,"开始扫描",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                } else {
                    Toast.makeText(MainActivity.this,"请开启蓝牙",Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                // HomeAsUp的id为android.R.id.home
                mDrawerLayout.openDrawer(GravityCompat.START);
                //保证和xml文件中的行为一致,start与xml文件中layout_gravity中的start一致，用于滑动菜单在屏幕左边还是右边，start为随语言而定
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TryToConnect extends Thread {
        public void run() {
            try {
                btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream = btSocket.getOutputStream();
                inputStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                //一直尝试连接直到isConnect==true
                try {
                    btSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isConnect) {
                    Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}