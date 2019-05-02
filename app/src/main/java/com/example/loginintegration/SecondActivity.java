package com.example.loginintegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {
    TextView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        tv1=(TextView)findViewById(R.id.tvEmail);

        Intent intent=getIntent();
        String str=intent.getStringExtra(MainActivity.EXTRA_STRING);
        Toast.makeText(SecondActivity.this,str,Toast.LENGTH_SHORT).show();
        tv1.setText(str);
    }
}
