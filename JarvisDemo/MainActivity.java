package com.example.jarvis;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements EventListener {
    protected EditText txtResult;
    protected Button btnStart;
    protected Button btnEnd;
    protected String command;
    private EventManager asr;

    /**
     * 主要执行过程
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initPermission();

        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);
    }

    /**
     * 动态申请权限
     * */
    private void initPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    /**
     * 组件视图初始化
     * */
    private void initView() {
        txtResult = findViewById(R.id.resText);
        btnStart = findViewById(R.id.btnStart);
        btnEnd = findViewById(R.id.btnEnd);

        btnStart.setOnClickListener(v -> {
            if(isNetworkAvailable(getApplicationContext())) {
                Toast.makeText(MainActivity.this, "请说出命令词!", Toast.LENGTH_SHORT).show();
                asr.send(SpeechConstant.ASR_START, null, null, 0, 0);
            } else {
                Toast.makeText(MainActivity.this, "咦? 你的网络似乎不可用哦!", Toast.LENGTH_SHORT).show();
            }
        });

        btnEnd.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "已结束!", Toast.LENGTH_SHORT).show();
            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        });
    }

    /**
     * 语音识别过程处理
     * */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params == null || params.isEmpty()) {
                return;
            }
            if (params.contains("\"final_result\"")) {
                String regrex = "\\[(.*?),";
                Pattern pattern = Pattern.compile(regrex);
                Matcher matcher = pattern.matcher(params);
                if (matcher.find()) {
                    int a = matcher.group(0).indexOf("[");
                    int b = matcher.group(0).indexOf(",");
                    txtResult.setText(matcher.group(0).substring(a + 2, b - 3));
                    command = matcher.group(0).substring(a + 2, b - 3);
                    commandMatch(command);
                }
            }
        }
    }

    /**
     * 语音识别结束，销毁进程
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        asr.unregisterListener(this);
    }

    /**
     * 工具方法: 判断设备是否有可用网络连接
     * */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {       // 当前网络是连接的
                return info.getState() == NetworkInfo.State.CONNECTED;      // 当前所连接的网络是否可用
            }
        }
        return false;
    }

    /**
     * 开始匹配命令词，执行对应操作
     * 功能列表: {
     *     1.拨打指定号码
     *     2.查看微博热搜
     *     3.查询天气
     *     4.音乐列表
     *     5.电影列表
     *     6.摇色子
     * }
     * */
    public void commandMatch(String command) {
        String phoneCallPattern = ".*[电话呼叫拨号].*";
        String newsPattern = ".*[微博新闻].*";
        String weatherPattern = ".*天气.*";
        String musicPattern = ".*[音乐歌].*";
        String moviePattern = ".*电影.*";
        String dicePattern = ".*色子.*";

        // 拨号
        boolean phoneBillMatch = Pattern.matches(phoneCallPattern, command);
        if (phoneBillMatch) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            String phoneNumber = Pattern.compile("\\D+").matcher(command).replaceAll("");
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
        // 微博新闻
        boolean newsMatch = Pattern.matches(newsPattern, command);
        if (newsMatch) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://s.weibo.com/top/summary?Refer=top_hot&topnav=1&wvr=6"));
            startActivity(intent);
        }
        // 天气查询
        boolean weatherMatch = Pattern.matches(weatherPattern, command);
        if (weatherMatch) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://www.nmc.cn/"));
            startActivity(intent);
        }
        // 音乐
        boolean musicMatch = Pattern.matches(musicPattern, command);
        if (musicMatch) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://y.qq.com/?ADTAG=myqq#type=index"));
            startActivity(intent);
        }
        // 电影
        boolean movieMatch = Pattern.matches(moviePattern, command);
        if (movieMatch) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.douyu.com/directory/subCate/yqk/290"));
            startActivity(intent);
        }

        // 摇色子
        boolean diceMatch = Pattern.matches(dicePattern, command);
        if(diceMatch)
            Toast.makeText(MainActivity.this, "Ok,结果是:"+(new Random().nextInt(6)+1), Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(MainActivity.this, "指令不存在!", Toast.LENGTH_SHORT).show();
        }
    }
}
