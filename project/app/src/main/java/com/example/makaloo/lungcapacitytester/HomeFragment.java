package com.example.makaloo.lungcapacitytester;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by MakaLoo on 2017/11/17.
 */

public class HomeFragment extends Fragment {

    private Button btSaveData;
    private Button btEnter;
    private Button btCancelData;
    private TextView tvShowData;
    private TextView tvShowName;
    private EditText etTableName;
    private StringBuilder sb;
    private MyDatabaseHelper dbHelper;
    private String[] clockNum;

    //static String tableName;

    private Context context;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString("agrs1", param1);
//        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity(); //getActivity()获得当前上下文，即当前Fragment的Activity，用于dbHelper的参数
        View view = inflater.inflate(R.layout.home_fragment, container, false);
//        Bundle bundle = getArguments();
//        String agrs1 = bundle.getString("agrs1");
//        TextView tv = (TextView)view.findViewById(R.id.container);
//        tv.setText(agrs1);

        tvShowData = (TextView)  view.findViewById(R.id.show_data);
        tvShowName = (TextView) view.findViewById(R.id.show_name);
        etTableName = (EditText) view.findViewById(R.id.table_name);
        sb = new StringBuilder();
        btSaveData = (Button) view.findViewById(R.id.save_data);
        btEnter = (Button) view.findViewById(R.id.enter);
        btCancelData = (Button) view.findViewById(R.id.cancel_data);
        dbHelper = new MyDatabaseHelper(context,"Tester.db",null,1);
        /*
         *以下为测试专用的数据添加
          *        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String createSQL1 = "CREATE TABLE "
                + "tester"
                + " (id integer primary key autoincrement,"
                + "test_time TIMESTAMP default CURRENT_TIMESTAMP,"
                + "capacity text)";
        String addSQL1 = "INSERT INTO tester (capacity) values ('1234')";
        String addSQL2 = "INSERT INTO tester (capacity) values ('2345')";
        String addSQL3 = "INSERT INTO tester (capacity) values ('3456')";
        String addSQL4 = "INSERT INTO tester (capacity) values ('4567')";
        String addSQL5 = "INSERT INTO tester (capacity) values ('1421')";
        String addSQL6 = "INSERT INTO tester (capacity) values ('3521')";
        db.execSQL(createSQL1);
        db.execSQL(addSQL1);
        db.execSQL(addSQL2);
        db.execSQL(addSQL3);
        db.execSQL(addSQL4);
        db.execSQL(addSQL5);
        db.execSQL(addSQL6); */

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
//        setSupportToolbar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.menu);
//        }
        ReadThread mreadThread = new ReadThread();
        mreadThread.start();

        btEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.tableName = etTableName.getText().toString();
                new AlertDialog.Builder(context)
                        .setTitle("确定受测者姓名：" + MainActivity.tableName)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvShowName.setText(MainActivity.tableName);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                try {
                                    String createSQL = "CREATE TABLE "
                                            + MainActivity.tableName
                                            + " (id integer primary key autoincrement,"
                                            + "test_time TIMESTAMP default CURRENT_TIMESTAMP,"
                                            + "capacity text)";
                                    db.execSQL(createSQL);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        btSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    //String addSQL = "INSERT INTO " + MainActivity.tableName + " (capacity) values ('aha')";
                    String addSQL = "INSERT INTO " + MainActivity.tableName + " (capacity) values ('" + tvShowData.getText().toString() + "')";
                    db.execSQL(addSQL);
                    Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
                    tvShowData.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btCancelData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("确定取消吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvShowData.setText("");
                            }
                        })
                        .setNegativeButton("不", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
        return view;
    }

    public void show(String msg) {
        sb.append(msg);
        clockNum = sb.toString().split("A");
        //|是转义字符，要加上\\防转义
        //要求发送过来的字符串要加个|作为发送结束的标志
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                tvShowData.setText(dataList[dataList.length - 1]);
//                tvShowData.setText(sb);
                tvShowData.setText(String.valueOf(calData()));
            }
        });
    }

    private double calData() {
        double[] clockNumDouble = new double[clockNum.length];
        double[] flow = new double[clockNum.length];
        double s = Math.PI * Math.pow(18.5/2, 2);
        double t = 503.677;
        double flowAll = 0;
        for (int i = 0; i < clockNum.length; i++) {
            clockNumDouble[i] = Double.parseDouble(clockNum[i]);
            if (clockNumDouble[i] > 18000)
                clockNumDouble[i] = 36418 - clockNumDouble[i];
            flow[i] = (72 * Math.pow(10, 10))/(17525.7-clockNumDouble[i]) * s * t;
            flowAll += flow[i];
        }
        return flowAll;
    }

    private class ReadThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                InputStream inputStream = MainActivity.inputStream;
                try {
                    if ((bytes = inputStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        show(s);
                    }
                } catch (IOException e) {
                    try {
                        inputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toobar2, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.data_show:
//                Intent intent = new Intent(SaveActivity.this,ShowActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.back:
//                finish();
//                break;
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}