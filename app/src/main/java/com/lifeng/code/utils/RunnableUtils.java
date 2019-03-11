package com.lifeng.code.utils;

import android.os.AsyncTask;
import android.os.Build;

/**
 * 兼容低版本的子线程开启任务
 */
public class RunnableUtils {
    public static void execAsync(AsyncTask asyncTask) {
        if (Build.VERSION.SDK_INT >= 17) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncTask.execute();
        }
    }

}
