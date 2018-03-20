package com.example.makaloo.lungcapacitytester;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    private ArrayList<String> data;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        listView = (ListView) findViewById(R.id.list_view);
        final SearchView searchView = (SearchView) findViewById(R.id.searchview);
        listView.setTextFilterEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //搜索框内部改变回调，newText就是搜索框里的内容
            @Override
            public boolean onQueryTextChange(String newText) {
                Object[] obj = searchItem(newText);
                updateLayout(obj);
                return true;
            }
        });
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(ShowActivity.this,"Tester.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        data = new ArrayList<String>();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        if (cursor.moveToFirst()) {
            cursor.moveToNext(); //隐藏android_metadata
            cursor.moveToNext(); //隐藏nodata
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

    public Object[] searchItem(String name) {
        ArrayList<String> mSearchList = new ArrayList<String>();
        for (int i = 0; i < data.size(); i++) {
            int index = data.get(i).indexOf(name);
            // 存在匹配的数据
            if (index != -1) {
                mSearchList.add(data.get(i));
            }
        }
        return mSearchList.toArray();
    }

    public void updateLayout(Object[] obj) {
        listView.setAdapter(new ArrayAdapter<Object>(getApplicationContext(),
                android.R.layout.simple_list_item_1, obj));
    }
}