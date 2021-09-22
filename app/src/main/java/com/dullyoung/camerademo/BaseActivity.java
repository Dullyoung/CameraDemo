package com.dullyoung.camerademo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * Created by @author Dullyoung in  2021/4/6
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {
    protected T mBinding;

    PermissionHelper mPermissionHelper;

    public PermissionHelper getPermissionHelper() {
        return mPermissionHelper;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            mBinding = (T) method.invoke(null, getLayoutInflater());
            setContentView(mBinding.getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPermissionHelper = new PermissionHelper();
        registerLauncher();
        initViews();
    }

    protected void setClick(View view, Runnable runnable) {
        view.setOnClickListener(v -> runnable.run());
    }

    protected void registerLauncher() {

    }


    protected void setClick(@IdRes int id, Runnable runnable) {
        findViewById(id).setOnClickListener(v -> runnable.run());
    }

    protected abstract void initViews();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }
}
