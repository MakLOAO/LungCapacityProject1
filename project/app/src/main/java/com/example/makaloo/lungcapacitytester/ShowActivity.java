package com.example.makaloo.lungcapacitytester;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    private ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ListView listView = (ListView) findViewById(R.id.list_view);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(ShowActivity.this,"Tester.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        data = new ArrayList<String>();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        if (cursor.moveToFirst()) {
            cursor.moveToNext(); //隐藏android_metadata
            cursor.moveToNext(); //隐藏sqlite_sequence
            do {
                String tableName = cursor.getString(0);
                data.add(tableName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowActivity.this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = data.get(position);
                Intent intent = new Intent(ShowActivity.this, DataActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }
}
