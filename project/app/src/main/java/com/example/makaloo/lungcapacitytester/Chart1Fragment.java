package com.example.makaloo.lungcapacitytester;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MakaLoo on 2017/11/17.
 */

public class Chart1Fragment extends Fragment {

    private LineChart mLineChart;
    private LineDataSet set1;

    public static Chart1Fragment newInstance() {
        Chart1Fragment fragment = new Chart1Fragment();
//        Bundle args = new Bundle();
//        args.putString("agrs1", param1); //newInstance(String param1)
//        fragment.setArguments(args);
        return fragment;
    }

    public Chart1Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart1_fragment, container, false);
        mLineChart = (LineChart) view.findViewById(R.id.chart1);
        mLineChart.getAxisRight().setEnabled(false); //禁止右侧显示坐标轴
        mLineChart.setNoDataText("no data!");
        //mLineChart.setGridBackgroundColor(R.color.white); //设置背景颜色都要设置为类似0xff00ff00才行不然会自动变为0xff0000，R.color.white不是
        //mLineChart.setBackgroundColor(0xffffffff); //0xff表示透明度100，然后后面表示红绿蓝
        mLineChart.setBackgroundColor(Color.WHITE);
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //设置X轴在底部
        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter2(mLineChart, getContext());
        xAxis.setValueFormatter(xAxisFormatter); //设置Formatter
//        xAxis.enableGridDashedLine(10f, 10f, 0f);
//        enableGridDashedLine(float lineLength, float spaceLength, float phase) : 启用网格线的虚线模式中得出，比如像这样“ - - - - - - ”。
//            “lineLength”控制虚线段的长度
//            “spaceLength”控制线之间的空间
//            “phase”controls the starting point.
//        YAxis leftAxis = mLineChart.getAxisLeft();
//        //y轴最大
//        leftAxis.setAxisMaximum(200f);
//        //y轴最小
//        leftAxis.setAxisMinimum(0f);
        ArrayList<String> xTimeList = new ArrayList<>();
        ArrayList<Entry> yDataList = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext(),"Tester.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int entryNum = 0;
            Cursor cursor = db.rawQuery("SELECT * FROM " + HomeFragment.tableName, null);
            if (cursor.moveToFirst()) {
                do {
                    String time = cursor.getString(cursor.getColumnIndex("test_time"));
                    time = time.substring(0, 9);
                    Float capacity = Float.parseFloat(cursor.getString(cursor.getColumnIndex("capacity")));
                    yDataList.add(new Entry(entryNum, capacity)); //Entry(x, y)绘制坐标点
                    xTimeList.add(time);
                    entryNum++;
                } while (cursor.moveToNext());
                cursor.close();
                setData(yDataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void setData(ArrayList<Entry> values) {
        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            // 创建一个数据集,并给它一个类型
            set1 = new LineDataSet(values, "肺活量统计");
            // 在这里设置线
//            set1.enableDashedLine(10f, 5f, 0f); //线设置为虚线
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//            set1.setColor(R.color.red); //暂时不知道有什么用
//            set1.setCircleColor(R.color.yellow); //暂时不知道有什么用
            set1.setDrawValues(true); // 是否在点上绘制Value
            set1.setValueTextColor(Color.GREEN); //Value的颜色
            set1.setValueTextSize(12f); //Value的字体大小
            set1.setHighLightColor(Color.RED); // 设置点击某个点时，横竖两条线的颜色
            set1.setLineWidth(1f); //线宽
            set1.setDrawCircles(true); //显示圆点，默认显示
            set1.setCircleRadius(3f); //圆点的大小
            set1.setDrawCircleHole(false); //空心还是实心
            set1.setDrawFilled(false); //设置x轴到坐标点是否填充,大概是用setFillColor()来设置填充颜色的
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // 填充背景只支持18以上
                //Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
                //set1.setFillDrawable(drawable);
                set1.setFillColor(Color.YELLOW);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            //LineDataSet装载yDataList,ArrayList<ILineDataSet>装载LineDataSet,LineData装载LineDataSets(类似于Adapter),图表设置数据为LineData
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            //添加数据集
            dataSets.add(set1);

            //创建一个数据集的数据对象
            LineData data = new LineData(dataSets);

            //设置数据
            mLineChart.setData(data);
        }
    }
}
