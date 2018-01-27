package com.example.makaloo.lungcapacitytester;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;


public class DataActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{

    private BottomNavigationBar bottomNavigationBar1;
    private int lastSelectedPosition = 0;
    private DataListFragment mDataListFragment;
    private Chart1Fragment mChart1Fragment;
    private Chart2Fragment mChart2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        TextView textView = (TextView) findViewById(R.id.name);
        Intent intent = getIntent();
        MainActivity.tableName = intent.getStringExtra("name");
        textView.setText("当前显示的用户为:" + MainActivity.tableName);
        /**
         * bottomNavigation 设置
         */

        bottomNavigationBar1 = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar1);

        /** 导航基础设置 包括按钮选中效果 导航栏背景色等 */
        bottomNavigationBar1
                .setTabSelectedListener(this)
                .setMode(BottomNavigationBar.MODE_DEFAULT)
                /**
                 *  setMode() 内的参数有三种模式类型：
                 *  MODE_DEFAULT 自动模式：导航栏Item的个数<=3 用 MODE_FIXED 模式，否则用 MODE_SHIFTING 模式
                 *  MODE_FIXED 固定模式：未选中的Item显示文字，无切换动画效果。
                 *  MODE_SHIFTING 切换模式：未选中的Item不显示文字，选中的显示文字，有切换动画效果。
                 */

                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                /**
                 *  setbackgroundStyle() 内的参数有三种样式
                 *  BACKGROUND_STYLE_DEFAULT: 默认样式 如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC
                 *                                    如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                 *  BACKGROUND_STYLE_STATIC: 静态样式 点击无波纹效果
                 *  BACKGROUND_STYLE_RIPPLE: 波纹样式 点击有波纹效果
                 */
                .setActiveColor("#00FF00") //选中颜色
                .setInActiveColor("#E9E6E6") //未选中颜色
                .setBarBackgroundColor(R.color.purple);//导航栏背景色

        /** 添加导航按钮 */
        bottomNavigationBar1
                .addItem(new BottomNavigationItem(R.drawable.home, "列表"))
                .addItem(new BottomNavigationItem(R.drawable.chart2, "折线图"))
                .addItem(new BottomNavigationItem(R.drawable.chart1, "柱形图"))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise(); //initialise 一定要放在 所有设置的最后一项

        setDefaultFragment();//设置默认导航栏
    }



    /**
     * 设置默认导航栏
     */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mDataListFragment = DataListFragment.newInstance();
        transaction.replace(R.id.tb1, mDataListFragment);
        transaction.commit();
    }

    /**
     * 设置导航选中的事件
     */
    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = this.getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (mDataListFragment == null) {
                    mDataListFragment = DataListFragment.newInstance();
                    transaction.replace(R.id.tb1, mDataListFragment);
                }
                if (mChart1Fragment != null) {
                    transaction.remove(mChart1Fragment);
                    transaction.show(mDataListFragment);
                }
                if (mChart2Fragment != null) {
                    transaction.remove(mChart2Fragment);
                    transaction.show(mDataListFragment);
                }
                break;
            case 1:
                if (mChart1Fragment == null) {
                    mChart1Fragment = mChart1Fragment.newInstance();
                }
                if (mChart2Fragment != null) {
                    transaction.remove(mChart2Fragment);
                }
                transaction.add(R.id.tb1, mChart1Fragment);
                transaction.hide(mDataListFragment);
                break;
            case 2:
                if (mChart2Fragment == null) {
                    mChart2Fragment = mChart2Fragment.newInstance();
                }
                if (mChart1Fragment != null) {
                    transaction.remove(mChart1Fragment);
                }
                transaction.add(R.id.tb1, mChart2Fragment);
                transaction.hide(mDataListFragment);
                break;

            default:
                break;
        }

        transaction.commit();// 事务提交
    }

    /**
     * 设置未选中Fragment 事务
     */
    @Override
    public void onTabUnselected(int position) {

    }

    /**
     * 设置释放Fragment 事务
     */
    @Override
    public void onTabReselected(int position) {

    }
}