package com.example.time;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.time.util.AccessibilityUtil;

import java.lang.reflect.Method;


public class  MainActivity extends AppCompatActivity {
    //包名和服务名称
    private static final String PACKAGE_NAME = "com.example.time";
    private static final String SERVICE_NAME = "com.example.time.MyacService";
    private AccessibilityUtil accessibilityUtil = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(com.example.time.MainActivity.this, timest.class);
                startActivity(intent);
                Intent intent2 = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                //startActivity(intent2);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(com.example.time.MainActivity.this, timelm.class);
                startActivity(intent);
                Intent  intent3=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent3);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(com.example.time.MainActivity.this, stoppl.class);
                startActivity(intent);
            }
        });
        if(!checkAlertWindowsPermission(MainActivity.this)){
            Intent  intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
        accessibilityUtil = new AccessibilityUtil(MainActivity.this,
                PACKAGE_NAME, SERVICE_NAME);
        openAccessibilityMet();

    }
    public void openAccessibilityMet()
    {
        //服务状态
        boolean status = AccessibilityUtil.isAccessibilitySettingsOn(this, SERVICE_NAME);
        if (!status)
        {
            accessibilityUtil.openSettingDialog();
        }
    }
    /**
     * 判断 悬浮窗口权限是否打开
     * @param context
     * @return true 允许  false禁止
     */
    public boolean checkAlertWindowsPermission(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }



}
