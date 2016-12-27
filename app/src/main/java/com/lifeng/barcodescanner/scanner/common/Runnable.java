package com.lifeng.barcodescanner.scanner.common;

import android.os.AsyncTask;
import android.os.Build;

/**
 * 兼容低版本的子线程开启任务
 */
public class Runnable {
	public static void execAsync(AsyncTask asyncTask) {
		if (Build.VERSION.SDK_INT >= 11) {
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else {
			asyncTask.execute();
		}
	}

}
