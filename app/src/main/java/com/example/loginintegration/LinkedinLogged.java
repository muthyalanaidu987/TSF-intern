package com.example.loginintegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LinkedinLogged extends AppCompatActivity {
    TextView tvEmail,tvName;
    Button btnlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin_logged);
        tvEmail=(TextView)findViewById(R.id.tvAdd);
        tvName = (TextView)findViewById(R.id.tvName);
        btnlogout=(Button)findViewById(R.id.btnLogout);
        Intent intent=getIntent();
        String name=intent.getStringExtra(MainActivity.NAME);
        String email=intent.getStringExtra(MainActivity.EMAIL_IN);
        tvEmail.setText(name);
        tvName.setText(email);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LinkedinLogged.this,MainActivity.class));
                finish();
            }
        });
    }
}
