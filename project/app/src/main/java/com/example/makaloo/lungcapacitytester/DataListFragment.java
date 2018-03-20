package com.example.makaloo.lungcapacitytester;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

/**
 * Created by MakaLoo on 2018/1/22.
 */

public class DataListFragment extends Fragment {

    private ListView lvShowData;
    private ArrayAdapter dataAdapter;

    public static DataListFragment newInstance() {
        DataListFragment fragment = new DataListFragment();
        return fragment;
    }

    public DataListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.datalist_fragment, container, false);
        lvShowData = (ListView) view.findViewById(R.id.show_data2);
        ArrayList dataList = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getActivity(),"Tester.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + MainActivity.tableName, null);
            if (cursor.moveToFirst()) {
                do {
                    String time = cursor.getString(cursor.getColumnIndex("test_time"));
                    String capacity = cursor.getString(cursor.getColumnIndex("capacity"));
                    String data2 = time + "  :  " + capacity + "ml";
                    dataList.add(data2);
                } while (cursor.moveToNext());
                cursor.close();
                dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dataList);
                lvShowData.setAdapter(dataAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}