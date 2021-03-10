package com.example.demo;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Register extends AppCompatActivity {
    private EditText textacc;
    private EditText textpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textacc = findViewById(R.id.textAcc);
        textpwd = findViewById(R.id.textPwd);
        Button backLog = findViewById(R.id.backLogin);
        Button register = findViewById(R.id.register);

        backLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,MainActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,MainActivity.class);
                intent.putExtra("acc",textacc.getText().toString());
                intent.putExtra("pwd",textpwd.getText().toString());
                startActivity(intent);
                Toast.makeText(Register.this, "注册成功!", Toast.LENGTH_SHORT).show();
                textacc.setText("");
                textpwd.setText("");
            }
        });
    }
}