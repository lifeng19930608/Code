package com.lifeng.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.lifeng.barcodescanner.scanner.CaptureActivity;


public class MainActivity extends Activity {

    private final int REQUEST_DODE=99;
    private final int RESULT_CODE=100;
    private TextView tv;
    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv= (TextView) findViewById(R.id.textView);
        wv= (WebView) findViewById(R.id.webView);

        //跳转到二维码扫描的页面
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_DODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_DODE&&resultCode==RESULT_CODE){
            String message=data.getStringExtra("data").toString();
            tv.setText("二维码扫描返回的信息为："+message);
            wv.loadUrl(message);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
