package com.example.time;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.time.bean.appdata;

import org.litepal.LitePal;

public class setapp extends AppCompatActivity {
    private TextView textView=null;
    private Button button=null;
    private EditText editText=null;
    private TextView pei=null;
    String appname;
    String pckname;
    private String TAG="setapp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LitePal.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setapp);
        textView=findViewById(R.id.setapp_textView);
        button=findViewById(R.id.setapp_button4);
        editText=findViewById(R.id.setapp_editText);
        pei=findViewById(R.id.set_p);
        Intent intent=getIntent();
        appname=intent.getStringExtra("appname");
        pckname=intent.getStringExtra("pckname");
        Log.i(TAG, "pckname:---"+pckname);
        Log.i(TAG, "apname:---"+appname);
        Cursor precursor= LitePal.findBySQL("select * from appdata where appname=?",pckname);
        if(precursor.getCount()==1){
            precursor.moveToFirst();
            precursor.getInt(2);
            pei.setText(precursor.getInt(2)/60+" MIN");
            Log.i(TAG, "设置成功"+precursor.getInt(2));
        }
        textView.setText(appname);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText()!=null){
                    pei.setText(editText.getText()+" MIN");
                    Log.i(TAG, "输入框"+editText.getText());
                    Cursor cursor= LitePal.findBySQL("select * from appdata where appname=?",pckname);
                    Log.i(TAG, "查询结果"+cursor.getCount());
                    if(cursor.getCount()==0){
                        appdata apptem=new appdata();
                        apptem.setAppname(pckname);
                        apptem.setApptime(Integer.parseInt(editText.getText().toString())*60);
                        if(apptem.save()){
                            Log.i(TAG, "存入数据库："+pckname+"---"+editText.getText());
                        }
                    }
                    if(cursor.getCount()==1){
                        appdata apptem=new appdata();
                        apptem.setApptime(Integer.parseInt(editText.getText().toString())*60);
                        apptem.updateAll("appname=?",pckname);
                        Log.i(TAG, "数据库更新："+pckname+"----"+editText.getText().toString());
                    }


                }
            }
        });


    }
}
