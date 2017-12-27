package com.example.makaloo.lungcapacitytester;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.makaloo.lungcapacitytester.MyDatabaseHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

/**
 * Created by MakaLoo on 2017/12/3.
 */
//创建类继承自接口IAxisValueFormatter
public class DayAxisValueFormatter2 implements IAxisValueFormatter {

    private LineChart mLineChart;
    private Context context;
    private int count;

    public DayAxisValueFormatter2(LineChart mLineChart, Context context) {
        this.mLineChart = mLineChart;
        this.context = context;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        count = (int) value;
        ArrayList<String> xTimeList = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context,"Tester.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM tester", null);
            if (cursor.moveToFirst()) {
                do {
                    String time = cursor.getString(cursor.getColumnIndex("test_time"));
                    time = time.substring(0, 9);
                    xTimeList.add(time);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xTimeList.get(count);
    }
}
