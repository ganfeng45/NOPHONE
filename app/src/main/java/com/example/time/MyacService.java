package com.example.time;

import android.accessibilityservice.AccessibilityService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.time.bean.MyUsageStats;
import com.example.time.bean.appdata;
import com.example.time.util.AppsUtil;
import com.example.time.util.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyacService extends AccessibilityService {
    private static final String TAG = "tes";
    private static final String TAG2 = "ACservice";
    public static boolean isStarted = false;
    public static boolean isremove = true;
    public int code=-1;
    private int limit_time=-1;
    private Handler changeImageHandler;
    private SQLiteDatabase db;



    //设置全局变量
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View displayView;
    private int[] images;
    private int imageIndex = 0;
    private String lastpck="com.android.systemui";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        //监听心跳
        Log.i(TAG, "悬浮窗监听ing ");
        String appname=event.getPackageName().toString();
        //lastpck=appname;
        Log.i(TAG, "复制结果"+lastpck);
        Log.i(TAG, "前台应用: "+appname);
        //定时
        Cursor cursor = db.rawQuery("SELECT * FROM stime",null);
        if(limit_time<0&&cursor.getCount()==1)
        {
            cursor.moveToFirst();
            limit_time=cursor.getInt(1);
            if(limit_time>0){
                ContentValues values2 = new ContentValues();
                values2.put("slimit_time", -1);
                db.update("stime",values2,"id=?",new String []{"1"});
                Log.i(TAG, "数据库service更新为-1");
            }
        }
        cursor.close();
        if(isremove&&limit_time>0){
            Log.i(TAG2, "准备开启悬浮窗");
            code=202;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG2, "删除悬浮窗 ");
                    limit_time=-1;
                    code=201;
                }
            },limit_time*1000*60);
            Log.i(TAG2, "悬浮窗定时结束");
        }
        if (appname.compareTo(lastpck)!=0)
        {
            lastpck=appname;
            Log.i(TAG, "-----app切换了-----");
            Cursor cursor2= LitePal.findBySQL("select * from appdata where appname=?",appname);
            if(cursor2.getCount()==1){
                cursor2.moveToFirst();
                // Log.i(TAG, "控制开始："+cursor2.getInt(1));
                Log.i(TAG, "控制开始："+cursor2.getInt(2));
                List<MyUsageStats> list = AppsUtil.updateAppsUsageData(getApplicationContext(),1);
                for (MyUsageStats a:list){
                    //Log.i(TAG, "onAccessibilityEvent:"+a.usageStats.getPackageName());
                    if (a.usageStats.getPackageName().compareTo(appname)==0){
                        Log.i(TAG, "------AMD!------");
                        if((a.usageStats.getTotalTimeInForeground()/1000)-cursor2.getInt(2)>0){
                            Log.i(TAG, "注意！！超时！！！");
                            Log.i(TAG, "系统-用户"+a.usageStats.getTotalTimeInForeground()+"-"+cursor2.getInt(2));
                            Log.i(TAG, "具体："+ TimeUtil.timeToString(a.usageStats.getTotalTimeInForeground()));
                            Toast.makeText(MyacService.this, "AMD!!YES!!~",
                                    Toast.LENGTH_SHORT).show();
                            layoutParams.width = 1080;
                            layoutParams.height = 1920;
                            windowManager.updateViewLayout(displayView,layoutParams);
                        }
                    }
                }
            }else {
                //code=201;
                layoutParams.width = 0;
                layoutParams.height = 0;
                windowManager.updateViewLayout(displayView,layoutParams);
            }
        }
        //sendRequestWithOkHttp();
        //开关控制
        if(code==201&&!isremove){
            Log.i(TAG, "删除 ");
            isremove=true;
            layoutParams.width = 0;
            layoutParams.height = 0;
            windowManager.updateViewLayout(displayView,layoutParams);

        }
        if(code==202&&isremove){
            Log.i(TAG, "添加 ");
            isremove=false;
            //获取资源对象
            Resources resources = getResources();
            //获取屏幕数据
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            float density = displayMetrics.density;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams.width = displayMetrics.widthPixels;
            layoutParams.height = displayMetrics.heightPixels;
            windowManager.updateViewLayout(displayView,layoutParams);
        }


    }

    @Override
    public void onInterrupt()
    {
        //服务断开
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onServiceConnected()
    {
        super.onServiceConnected();
        LitePal.initialize(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this, "test_db", null, 1);
        db = dbHelper.getWritableDatabase();
        wininit();
        showFloatingWindow();
        Toast.makeText(MyacService.this, "  time无障碍开启服务成功~",
                Toast.LENGTH_SHORT).show();
        //stopSelf();
    }


    public  void wininit(){
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        layoutParams.width = 300;
        layoutParams.height =300;
        layoutParams.x = 0;
        layoutParams.y = 0;

        images = new int[] {
                R.drawable.image_01,
                R.drawable.image_02,
                R.drawable.image_01,
                R.drawable.image_02,
                R.drawable.image_01,
        };

        //changeImageHandler = new Handler(this.getMainLooper(), changeImageCallback);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            displayView = layoutInflater.inflate(R.layout.image_display, null);
            //displayView.setOnTouchListener(new FloatingOnTouchListener());
            ImageView imageView = displayView.findViewById(R.id.image_display_imageview);
            imageView.setImageResource(images[imageIndex]);
            windowManager.addView(displayView, layoutParams);
        }
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                try {
                    // 创建一个OkHttpClient的实例
                    OkHttpClient client = new OkHttpClient();
                    // 如果要发送一条HTTP请求，就需要创建一个Request对象
                    // 可在最终的build()方法之前连缀很多其他方法来丰富这个Request对象
                    Request request = new Request.Builder()
                            .url("https://www.auster.fun/sy/public/api/door/redata?token=api2020")
                            .build();
                    // 调用OkHttpClient的newCall()方法来创建一个Call对象，并调用execute()方法来发送请求并获取服务器的返回数据
                    Response response = client.newCall(request).execute();
                    // 其中Response对象就是服务器返回的数据，将数据转换成字符串
                    String responseData = response.body().string();
                    // 将获取到的字符串传入showResponse()方法中进行UI显示
                    JSONObject myjson= new JSONObject(responseData);
                    code=myjson.optInt("code");
                    Log.i(TAG, String.valueOf(code));
                    Log.i(TAG, responseData);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



}
