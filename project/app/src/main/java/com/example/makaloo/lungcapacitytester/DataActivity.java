package com.example.makaloo.lungcapacitytester;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {

    private ListView lvShowData;
    ArrayAdapter<String> dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        TextView textView = (TextView) findViewById(R.id.name);
        lvShowData = (ListView) findViewById(R.id.show_data2);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        textView.setText("当前显示的用户为:" + name);
        ArrayList dataList = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(DataActivity.this,"Tester.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + name, null);
            if (cursor.moveToFirst()) {
                do {
                    String time = cursor.getString(cursor.getColumnIndex("test_time"));
                    String capacity = cursor.getString(cursor.getColumnIndex("capacity"));
                    String data2 = time + "  :  " + capacity + "ml";
                    dataList.add(data2);
                } while (cursor.moveToNext());
                cursor.close();
                dataAdapter = new ArrayAdapter<String>(DataActivity.this, android.R.layout.simple_list_item_1, dataList);
                lvShowData.setAdapter(dataAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}