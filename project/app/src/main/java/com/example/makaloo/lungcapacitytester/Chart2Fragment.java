package com.example.makaloo.lungcapacitytester;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;


public class Chart2Fragment extends Fragment {

    private BarChart mBarChart;
    private BarDataSet set1;

    public static Chart2Fragment newInstance() {
        Chart2Fragment fragment = new Chart2Fragment();
        return fragment;
    }

    public Chart2Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart2_fragment, container, false);
//        Bundle bundle = getArguments();
//        String agrs1 = bundle.getString("agrs1");
//        TextView tv = (TextView)view.findViewById(R.id.container);
//        tv.setText(agrs1);
        mBarChart = (BarChart) view.findViewById(R.id.chart2);
        mBarChart.getAxisRight().setEnabled(true);
        mBarChart.setBackgroundColor(Color.WHITE);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.setNoDataText("no data!");
        Legend legend = mBarChart.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(10);
        //legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mBarChart, getContext());
        xAxis.setValueFormatter(xAxisFormatter); //设置Formatter
        ArrayList<String> xTimeList = new ArrayList<>();
        ArrayList<BarEntry> yDataList = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext(),"Tester.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int entryNum = 0;
            Cursor cursor = db.rawQuery("SELECT * FROM "+ MainActivity.tableName, null);
            if (cursor.moveToFirst()) {
                do {
                    String time = cursor.getString(cursor.getColumnIndex("test_time"));
                    time = time.substring(0, 9);
                    Log.d("time:", time);
                    Float capacity = Float.parseFloat(cursor.getString(cursor.getColumnIndex("capacity")));
                    yDataList.add(new BarEntry(entryNum, capacity)); //Entry(x, y)绘制坐标点
                    xTimeList.add(time);
                    entryNum++;
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setData(yDataList);
        return view;
    }

    //设置数据
    private void setData(ArrayList<BarEntry> values) {
        if (mBarChart.getData() != null && mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            // 创建一个数据集,并给它一个类型
            set1 = new BarDataSet(values, "肺活量统计");
            // 在这里设置线
//            set1.enableDashedLine(10f, 5f, 0f); //线设置为虚线
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//            set1.setColor(R.color.red); //暂时不知道有什么用
//            set1.setCircleColor(R.color.yellow); //暂时不知道有什么用
            set1.setDrawValues(true); // 是否在点上绘制Value
            set1.setValueTextColor(Color.GREEN); //Value的颜色
            set1.setValueTextSize(12f); //Value的字体大小
            set1.setHighLightColor(Color.RED); // 设置点击某个点时，横竖两条线的颜色
            set1.setColors(ColorTemplate.MATERIAL_COLORS); //设置4种颜色
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            //LineDataSet装载yDataList,ArrayList<ILineDataSet>装载LineDataSet,LineData装载LineDataSets(类似于Adapter),图表设置数据为LineData
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            //添加数据集
            dataSets.add(set1);

            //创建一个数据集的数据对象
            BarData data = new BarData(dataSets);

            //设置数据
            mBarChart.setData(data);
        }
    }
}