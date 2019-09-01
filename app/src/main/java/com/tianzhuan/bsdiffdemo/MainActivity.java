package com.tianzhuan.bsdiffdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_version;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initPermission();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        tv_version.setText("当前版本："+BuildConfig.VERSION_NAME);
    }

    /**
     * 初始化android6.0+权限申请
     */
    private void initPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String[] perms={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.REQUEST_INSTALL_PACKAGES,Manifest.permission.READ_EXTERNAL_STORAGE};
            if(checkSelfPermission(perms[0])== PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(perms[1])== PackageManager  .PERMISSION_GRANTED
                    || checkSelfPermission(perms[2])== PackageManager  .PERMISSION_GRANTED){
                     requestPermissions(perms,200);
            }
        }

    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_version=findViewById(R.id.tv_version);
        findViewById(R.id.btn_upadte).setOnClickListener(this);
    }



    public native void doPatchNative(String oldApk,String newApk,String patch);

    /**
     * 增量更新点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        //网络请求下载差分包（省略，直接拷贝差分包到sd卡）
        initPermission();
        new AsyncTask<Void,Void, File>(){
            @Override
            protected File doInBackground(Void... voids) {
                //bspatch做合成，得到新版本的apk文件
                 String patch=new File(Environment.getExternalStorageDirectory(),"diff.patch").getAbsolutePath();
                 String oldApk=getApplicationInfo().sourceDir;
                 File newApk=new File(Environment.getExternalStorageDirectory(),"new.apk");
                 doPatchNative(oldApk,newApk.getAbsolutePath(),patch);
                return newApk;
            }

            @Override
            protected void onPostExecute(File apkFile) {
////                //安装
                if(!apkFile.exists()){
                    return;
                }
                Log.e("====ss=========","333");
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(Build.VERSION.SDK_INT >=24){
                    Uri apkUri= FileProvider.getUriForFile(MainActivity.this,
                            "com.tianzhuan.bsdiffdemo.fileprovider",apkFile);
                    //对目标应用临时授权改Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri,"application/vnd.android.package-archive");
                }else {
                    intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
                }
                startActivity(intent);
            }
        }.execute();


    }
}
