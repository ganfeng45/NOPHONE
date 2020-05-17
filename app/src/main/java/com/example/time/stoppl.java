package com.example.time;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class stoppl extends AppCompatActivity {
    private EditText text=null;
    private Button button=null;
    private int limit_time;
    private SQLiteDatabase db;
    private  String TAG="test";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stoppl);
        DatabaseHelper dbHelper = new DatabaseHelper(stoppl.this, "test_db", null, 1);
        db = dbHelper.getWritableDatabase();
        text = (EditText) findViewById(R.id.editText);
        button=(Button) findViewById(R.id.button4);
        if (Settings.canDrawOverlays(stoppl.this))
        {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    limit_time= Integer.parseInt(text.getText().toString());
                    Cursor cursor = db.query("stime", new String[]{"slimit_time"}, null, null, null, null, null);
                    if(cursor.getCount()==0){
                        ContentValues values = new ContentValues();
                        values.put("id",1);
                        values.put("slimit_time",limit_time);
                        db.insert("stime",null,values);
                    }else{
                        ContentValues values2 = new ContentValues();
                        values2.put("slimit_time", limit_time);
                        db.update("stime",values2,"id=?",new String []{"1"});

                    }
                    cursor.close();
                }
            });

            //finish();
        }else
        {
            //若没有权限，提示获取.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Toast.makeText(stoppl.this,"需要取得权限以使用悬浮窗",Toast.LENGTH_SHORT).show();
            startActivity(intent);
            //finish();
        }
    }
}