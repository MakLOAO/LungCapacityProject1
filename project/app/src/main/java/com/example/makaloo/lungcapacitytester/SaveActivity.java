package com.example.makaloo.lungcapacitytester;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class SaveActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private DrawerLayout mDrawerLayout;

    private BottomNavigationBar bottomNavigationBar;
    int lastSelectedPosition = 0;
    private String TAG = MainActivity.class.getSimpleName();
    private HomeFragment mHomeFragment;
    private Chart1Fragment mChart1Fragment;
    private Chart2Fragment mChart2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view2);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout2);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.menu);
//        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_data:
                        Intent intent = new Intent(SaveActivity.this,ShowActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_exit:
                        finish();
                        break;
                }
                return false;
            }
        });
/**
 * bottomNavigation 设置
 */

        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        /** 导航基础设置 包括按钮选中效果 导航栏背景色等 */
        bottomNavigationBar
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
                 *  setBackgroundStyle() 内的参数有三种样式
                 *  BACKGROUND_STYLE_DEFAULT: 默认样式 如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC
                 *                                    如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                 *  BACKGROUND_STYLE_STATIC: 静态样式 点击无波纹效果
                 *  BACKGROUND_STYLE_RIPPLE: 波纹样式 点击有波纹效果
                 */
                .setActiveColor("#00FF00") //选中颜色
                .setInActiveColor("#E9E6E6") //未选中颜色
                .setBarBackgroundColor(R.color.purple);//导航栏背景色

        /** 添加导航按钮 */
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.home, "首页"))
                .addItem(new BottomNavigationItem(R.drawable.chart1, "柱状图"))
                .addItem(new BottomNavigationItem(R.drawable.chart2, "折线图"))
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
        mHomeFragment = HomeFragment.newInstance();
        transaction.replace(R.id.tb, mHomeFragment);
        transaction.commit();
    }

    /**
     * 设置导航选中的事件
     */
    @Override
    public void onTabSelected(int position) {
        Log.d(TAG, "onTabSelected() called with: " + "position = [" + position + "]");
        FragmentManager fm = this.getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (mHomeFragment == null) {
                    mHomeFragment = HomeFragment.newInstance();
                    transaction.replace(R.id.tb, mHomeFragment);
                }
                if (mChart1Fragment != null) {
                    transaction.remove(mChart1Fragment);
                    transaction.show(mHomeFragment);
                }
                if (mChart2Fragment != null) {
                    transaction.remove(mChart2Fragment);
                    transaction.show(mHomeFragment);
                }
                break;
            case 1:
                if (mChart1Fragment == null) {
                    mChart1Fragment = mChart1Fragment.newInstance();
                }
                if (mChart2Fragment != null) {
                    transaction.remove(mChart2Fragment);
                }
                transaction.add(R.id.tb, mChart1Fragment);
                transaction.hide(mHomeFragment);
                break;
            case 2:
                if (mChart2Fragment == null) {
                    mChart2Fragment = mChart2Fragment.newInstance();
                }
                if (mChart1Fragment != null) {
                    transaction.remove(mChart1Fragment);
                }
                transaction.add(R.id.tb, mChart2Fragment);
                transaction.hide(mHomeFragment);
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


