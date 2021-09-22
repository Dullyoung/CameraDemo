package com.dullyoung.camerademo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dullyoung.camerademo.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileFilter;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    @Override
    protected void registerLauncher() {
        ActivityResultContract<Intent, ActivityResult> resultLauncher = new ActivityResultContracts.StartActivityForResult();
        mActivityResultLauncher = registerForActivityResult(resultLauncher, result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent != null) {
                    String path = intent.getStringExtra("filePath");
                    Glide.with(this).load(path)
                            .into(mBinding.ivPicPre);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThreadPoolExecutorImpl.getImpl().execute(() -> {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath());
            File[] files = file.listFiles(pathname -> pathname.getName().endsWith(".png"));

            if (files == null) {
                return;
            }

            List<FileInfo> fileInfoList = new ArrayList<>();
            for (File file1 : files) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(file1.getName());
                fileInfo.setPath(file1.getAbsolutePath());
                fileInfoList.add(fileInfo);
            }
            UIKit.post(() -> {
                if (mBinding.rvPics.getAdapter() instanceof FileAdapter) {
                    FileAdapter fileAdapter = (FileAdapter) mBinding.rvPics.getAdapter();
                    fileAdapter.setNewInstance(fileInfoList);
                }
            });
        });
    }

    @Override
    protected void initViews() {
        setClick(mBinding.btnTakePic, this::checkPermission);
        FileAdapter fileAdapter = new FileAdapter(null);
        fileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btn_delete) {
                File file = new File(fileAdapter.getData().get(position).getPath());
                if (file.delete()) {
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                    fileAdapter.getData().remove(position);
                    fileAdapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fileAdapter.setOnItemClickListener((adapter, view, position) -> {
            FileInfo fileInfo = fileAdapter.getData().get(position);
            Glide.with(this).load(fileInfo.getPath())
                    .into(mBinding.ivPicPre);
        });

        mBinding.rvPics.setAdapter(fileAdapter);
        mBinding.rvPics.setLayoutManager(new LinearLayoutManager(this));

    }

    private void checkPermission() {
        getPermissionHelper().setMustPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA});
        getPermissionHelper().checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                mActivityResultLauncher.launch(new Intent(MainActivity.this, CameraActivity.class));
            }

            @Override
            public void onRequestPermissionError() {
                Toast.makeText(MainActivity.this, "未获取到权限", Toast.LENGTH_SHORT).show();
            }
        });
    }

}