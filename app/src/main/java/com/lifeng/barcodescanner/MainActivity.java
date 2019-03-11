package com.lifeng.barcodescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.lifeng.barcodescanner.scanner.CaptureActivity;
import com.lifeng.barcodescanner.utils.PermissionUtils;

import static com.lifeng.barcodescanner.scanner.config.Config.KEY_DTDA;

public class MainActivity extends Activity {

    private final int REQUEST_DODE = 99;
    private final int RESULT_CODE = 100;
    private TextView tv_result;
    private WebView wv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_result = findViewById(R.id.tv_result);
        wv_result = findViewById(R.id.wv_result);

        //跳转到二维码扫描的页面
        findViewById(R.id.btn_scanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_DODE);
            }
        });
        checkPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DODE && resultCode == RESULT_CODE) {
            String message = data.getStringExtra(KEY_DTDA);
            tv_result.setText(String.valueOf("二维码扫描返回的信息为：" + message));
            wv_result.loadUrl(message);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 检查必要权限
     */
    private void checkPermission() {
        PermissionUtils.getInstance().request(this, PermissionUtils.Type.STORAGE, new PermissionUtils.Callback() {
            @Override
            public void onResult(boolean grant) {
                if (!grant) {
                    showDialog();
                }
            }
        });
        PermissionUtils.getInstance().request(this, PermissionUtils.Type.CAMERA, new PermissionUtils.Callback() {
            @Override
            public void onResult(boolean grant) {
                if (!grant) {
                    showDialog();
                }
            }
        });
    }

    //弹出授权的对话狂
    private void showDialog() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("权限申请对话框");
        localBuilder.setIcon(R.mipmap.ic_launcher);
        localBuilder.setMessage("为了不影响您的正常使用，请赋予应用必要的权限");
        localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        localBuilder.setCancelable(false).create();
        localBuilder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grant = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (!grant) {
            showDialog();
        }
    }
}
