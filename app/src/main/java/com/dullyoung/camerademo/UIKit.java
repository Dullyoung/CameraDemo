package com.dullyoung.camerademo;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Dullyoung   2021/9/22
 */
public class UIKit {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void post(Runnable runnable) {
        postDelay(0, runnable);
    }

    public static void postDelay(long delay, Runnable runnable) {
        handler.postDelayed(runnable, delay);
    }
}
