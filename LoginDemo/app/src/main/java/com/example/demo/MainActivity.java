package com.example.demo;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    public static Hashtable<String, String> microDb = new Hashtable<>();
    private EditText account;
    private EditText passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account = findViewById(R.id.textAccount);
        passwd = findViewById(R.id.textPasswd);
        Button login = findViewById(R.id.Login);
        Button register = findViewById(R.id.register);
        Intent intent = getIntent();
        String acc = intent.getStringExtra("acc");
        String pwd = intent.getStringExtra("pwd");
        if(acc != null && pwd != null) {
            microDb.put(acc,pwd);
        }



        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String stracc = account.getText().toString();
                String strpwd = passwd.getText().toString();
                String Info = checkAccount(stracc,strpwd);
                Toast.makeText(MainActivity.this,Info,Toast.LENGTH_SHORT).show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });

    }
    public String checkAccount(String stracc, String strpwd) {
        String Info = "";
        if (microDb.containsKey(stracc) && strpwd.equals(microDb.get(stracc))) {
            Info = "登录成功";
        }
        if (microDb.containsKey(stracc) && !strpwd.equals(microDb.get(stracc))) {
            Info = "密码错误";
        }
        if (!microDb.containsKey(stracc)) {
            Info = "用户未注册";
        }
        return Info;
    }
}