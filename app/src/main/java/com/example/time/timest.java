package com.example.time;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.time.util.AccessibilityUtil;

import java.lang.reflect.Method;


public class timest extends AppCompatActivity {

    private MenuItem mMenuItem;
    private androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
    private AccessibilityUtil accessibilityUtil = null;
    private static final String PACKAGE_NAME = "com.example.time";
    private static final String SERVICE_NAME = "com.example.time.MyacService";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //statistics
        //StatService.start(this);
        if(!checkAlertWindowsPermission(timest.this)){
            Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
        accessibilityUtil = new AccessibilityUtil(timest.this,
                PACKAGE_NAME, SERVICE_NAME);
        openAccessibilityMet();
        startFragment(new UsageStatisticsFragment(), false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // for miui，miui dev version will leak activity，miui internal is processing fix，Emmmmmm...
        ((ViewGroup) getWindow().getDecorView()).removeAllViews();
    }

    /**
     * When back press in SettingFragment, back to MainFragment
     */

    @Override
   public void onBackPressed() {
        // Fragment fragment = fm.findFragmentByTag(SettingFragment.class.getName());
        //if (fragment != null && fragment.isVisible()) {
        //mMenuItem.setVisible(true);
        //setTitle(R.string.app_name);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // }
        super.onBackPressed();
    }
    protected void startFragment(Fragment fragment, boolean addBackStack) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ui
        transaction.replace(android.R.id.content, fragment, fragment.getClass().getName());
        if (addBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
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
