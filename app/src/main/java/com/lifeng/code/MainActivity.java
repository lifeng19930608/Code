package com.lifeng.code;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lifeng.code.create.StringToQRCodeListener;
import com.lifeng.code.create.StringToQRCodeTask;
import com.lifeng.code.utils.PermissionUtils;

import static com.lifeng.code.scanner.config.Config.KEY_DATA;

public class MainActivity extends Activity implements StringToQRCodeListener, View.OnClickListener {

    private final int REQUEST_DODE = 99;
    private final int RESULT_CODE = 100;
    private Button btn_scanner;
    private Button btn_create;
    private TextView tv_result;
    private WebView wv_result;
    private ImageView iv_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scanner = findViewById(R.id.btn_scanner);
        btn_create = findViewById(R.id.btn_create);
        tv_result = findViewById(R.id.tv_result);
        wv_result = findViewById(R.id.wv_result);
        iv_create = findViewById(R.id.iv_create);
        btn_scanner.setOnClickListener(this);
        btn_create.setOnClickListener(this);
        checkPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DODE && resultCode == RESULT_CODE) {
            final String message = data.getStringExtra(KEY_DATA);
            tv_result.setText(String.valueOf("二维码扫描返回的信息为：" + message));
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    wv_result.loadUrl(message);
                }
            }, 1000);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 检查必要权限
     */
    private void checkPermission() {
        PermissionUtils.getInstance().request(this, PermissionUtils.Type.CAMERA, new PermissionUtils.Callback() {
            @Override
            public void onResult(boolean grant) {
                if (!grant) {
                    PermissionUtils.getInstance().showDialog(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grant = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (!grant) {
            PermissionUtils.getInstance().showDialog(MainActivity.this);
        }
    }

    @Override
    public void onResult(Bitmap qrCode) {
        iv_create.setImageBitmap(qrCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scanner:
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_DODE);
                break;
            case R.id.btn_create:
                new StringToQRCodeTask("12345678", 800, MainActivity.this).execute();
                break;
        }
    }
}
