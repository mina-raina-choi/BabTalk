package com.example.cmina.openmeeting.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.example.cmina.openmeeting.fragment.ManchanListFragment;
import com.example.cmina.openmeeting.fragment.MyChatListFragment;
import com.example.cmina.openmeeting.fragment.MyPageFragment;
import com.example.cmina.openmeeting.R;
import com.example.cmina.openmeeting.service.SocketService;
import com.example.cmina.openmeeting.utils.MyDatabaseHelper;
import com.facebook.stetho.Stetho;


/**
 * Created by cmina on 2017-06-09.
 */

public class MainActivity extends AppCompatActivity {

    public SocketService socketService; //연결할 서비스
    public boolean IsBound ; //서비스 연결여부

    public static MyDatabaseHelper myDatabaseHelper;
    public static Cursor cursor;

    PageAdapter pageAdapter;


    //서비스에 바인드하기 위해서, ServiceConnection인터페이스를 구현하는 개체를 생성
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SocketService.LocalBinder binder = (SocketService.LocalBinder) iBinder;
            socketService = binder.getService(); //서비스 받아옴
           // socketService.registerCallback(callback); //콜백 등록

            IsBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //socketService = null;
            IsBound = false;
        }

    };

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
       // unbindService(serviceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();
     //   startService(new Intent(MainActivity.this, SocketService.class));
       // bindService(new Intent(MainActivity.this, SocketService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        pageAdapter.notifyDataSetChanged();
    }


    private void doBindService() {
        if (!IsBound) {
            Intent intent = new Intent(MainActivity.this, SocketService.class);
            //이미지 보낼 때, 바인드된 서비스없어서 서비스 중단되고 다시 소켓연결하는 부분 테스트를 위해 잠시. 주석
            //다시 정상적으로 startService살림
            startService(intent);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            Log.d("Main - onresume", "바인드서비스"+IsBound);
            IsBound = true;
        }

    }

    private void doUnbindService() {
        if (IsBound) {
            unbindService(serviceConnection);
            Log.d("MainActivity onStop", "언바인드서비스" + IsBound);

            IsBound = false;
        }
    }


    private PagerSlidingTabStrip tabStrip;
    private ViewPager viewPager;

    private Fragment manchanFragment;
    private Fragment myChatFragment;
    private Fragment myPageFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main - oncreate", "mainactivity oncreate");

        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

                //sqlite열기
        myDatabaseHelper = new MyDatabaseHelper(MainActivity.this);
        myDatabaseHelper.open();

        setTitle("같이밥먹자");

        manchanFragment = new ManchanListFragment();
        myChatFragment = new MyChatListFragment();
        myPageFragment = new MyPageFragment();

        pageAdapter = new PageAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);


    }

    private String[] pageTitle = {"HOME", "MyChatList", "MyPage"};

    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return manchanFragment;
                //return feedFragment1;
            } else if (position == 1) {
                // return new FeedFragment2();
                return myChatFragment;
            } else {
                // return new FeedFragment3();
                return myPageFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}
