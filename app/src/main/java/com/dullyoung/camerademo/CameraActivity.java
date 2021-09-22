package com.dullyoung.camerademo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.annotation.CameraExecutor;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dullyoung.camerademo.databinding.ActivityCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class CameraActivity extends BaseActivity<ActivityCameraBinding> {

    Camera mCamera;
    PreviewView mPreviewView;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void initViews() {
        mPreviewView = mBinding.pv;
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "相机预览创建失败", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        ImageCapture imageCapture = new ImageCapture.Builder()
                .setTargetRotation(mPreviewView.getDisplay().getRotation())
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

        mCamera = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, imageAnalysis, preview);

        setClick(mBinding.btnTakePic, () -> {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath(),
                    "Dullyoung" + System.currentTimeMillis() + ".png");
            ImageCapture.OutputFileOptions outputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(file).build();

            ThreadPoolExecutor executor = ThreadPoolExecutorImpl.getImpl();

            imageCapture.takePicture(outputFileOptions, executor,
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                            // insert your code here.
                            Log.i("aaaa", "图片保存成功 保存在: " + file.getAbsolutePath());
                            UIKit.post(() -> {
                                Intent intent = new Intent();
                                intent.putExtra("filePath", file.getAbsolutePath());
                                setResult(RESULT_OK, intent);
                                finish();
                            });
                        }

                        @Override
                        public void onError(ImageCaptureException error) {
                            // insert your code here.
                            UIKit.post(() -> {
                                Toast.makeText(CameraActivity.this, "保存失败" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
            );
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}