package com.ruanke.easypermissiondemo;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final int RC_CAMERA_AND_RECORD_AUDIO = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_requst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 去申请权限
     */
    @AfterPermissionGranted(RC_CAMERA_AND_RECORD_AUDIO)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        //判断有没有权限
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 如果有权限了, 就做你该做的事情
            // doing something
            openCamera();
        } else {
            // 如果没有权限, 就去申请权限
            // this: 上下文
            // Dialog显示的正文
            // RC_CAMERA_AND_RECORD_AUDIO 请求码, 用于回调的时候判断是哪次申请
            // perms 就是你要申请的权限
            EasyPermissions.requestPermissions(this, "写上你需要用权限的理由, 是给用户看的", RC_CAMERA_AND_RECORD_AUDIO, perms);
        }
    }

    /**
     * 权限申请成功的回调
     *
     * @param requestCode 申请权限时的请求码
     * @param perms       申请成功的权限集合
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsGranted: ");
        if (requestCode != RC_CAMERA_AND_RECORD_AUDIO) {
            return;
        }
        for (int i = 0; i < perms.size(); i++) {
            if (perms.get(i).equals(Manifest.permission.CAMERA)) {
                Log.i(TAG, "onPermissionsGranted: " + "相机权限成功");
                openCamera();

            } else if (perms.get(i).equals(Manifest.permission.RECORD_AUDIO)) {
                Log.i(TAG, "onPermissionsGranted: " + "录制音频权限成功");
            }
        }
    }

    /**
     * 权限申请拒绝的回调
     *
     * @param requestCode 申请权限时的请求码
     * @param perms       申请拒绝的权限集合
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsDenied: ");

        if (requestCode != RC_CAMERA_AND_RECORD_AUDIO) {
            return;
        }

        for (int i = 0; i < perms.size(); i++) {
            if (perms.get(i).equals(Manifest.permission.CAMERA)) {
                Log.i(TAG, "onPermissionsDenied: " + "相机权限拒绝");
            } else if (perms.get(i).equals(Manifest.permission.RECORD_AUDIO)) {
                Log.i(TAG, "onPermissionsDenied: " + "录制音频权限拒绝");
            }
        }

        //如果有一些权限被永久的拒绝, 就需要转跳到 设置-->应用-->对应的App下去开启权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限已经被您拒绝")
                    .setRationale("如果不打开权限则无法使用该功能,点击确定去打开权限")
                    .setRequestCode(10001)//用于onActivityResult回调做其它对应相关的操作
                    .build()
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            Toast.makeText(this, "从开启权限的页面转跳回来", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        Toast.makeText(this, "打开相机 ~~~", Toast.LENGTH_LONG).show();
    }
}
