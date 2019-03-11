package com.lifeng.barcodescanner.scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

import com.lifeng.barcodescanner.utils.RunnableUtils;

/**
 * Finishes an activity after a period of inactivity if the device is on battery
 * 该活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同
 */
final class InactivityTimer {

	private static final String TAG = InactivityTimer.class.getSimpleName();

	/**
	 * 如果在5min内扫描器没有被使用过，则自动finish掉activity
	 */
	private static final long INACTIVITY_DELAY_MS = 5 * 60 * 1000L;

	/**
	 * 在本app中，此activity即为CaptureActivity
	 */
	private final Activity activity;
	/**
	 * 接受系统广播：手机是否连通电源
	 */
	private final BroadcastReceiver powerStatusReceiver;
	private boolean registered;
	private AsyncTask<?, ?, ?> inactivityTask;

	InactivityTimer(Activity activity) {
		this.activity = activity;
		powerStatusReceiver = new PowerStatusReceiver();
		registered = false;
		onActivity();
	}

	/**
	 * 首先终止之前的监控任务，然后新起一个监控任务
	 */
	synchronized void onActivity() {
		cancel();
		inactivityTask = new InactivityAsyncTask();
		RunnableUtils.execAsync(inactivityTask);
	}

	public synchronized void onPause() {
		cancel();
		if (registered) {
			activity.unregisterReceiver(powerStatusReceiver);
			registered = false;
		}
		else {
			Log.w(TAG, "PowerStatusReceiver was never registered?");
		}
	}

	public synchronized void onResume() {
		if (registered) {
			Log.w(TAG, "PowerStatusReceiver was already registered?");
		}
		else {
			activity.registerReceiver(powerStatusReceiver, 
					new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			registered = true;
		}
		onActivity();
	}

	/**
	 * 取消监控任务
	 */
	private synchronized void cancel() {
		AsyncTask<?, ?, ?> task = inactivityTask;
		if (task != null) {
			task.cancel(true);
			inactivityTask = null;
		}
	}

	void shutdown() {
		cancel();
	}

	/**
	 * 监听是否连通电源的系统广播。如果连通电源，则停止监控任务，否则重启监控任务
	 */
	private final class PowerStatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				// 0 indicates that we're on battery
				boolean onBatteryNow = intent.getIntExtra(
						BatteryManager.EXTRA_PLUGGED, -1) <= 0;
				if (onBatteryNow) {
					InactivityTimer.this.onActivity();
				}
				else {
					InactivityTimer.this.cancel();
				}
			}
		}
	}

	/**
	 * 该任务很简单，就是在INACTIVITY_DELAY_MS时间后终结activity
	 */
	@SuppressLint("StaticFieldLeak")
	private final class InactivityAsyncTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... objects) {
			try {
				Thread.sleep(INACTIVITY_DELAY_MS);
				Log.i(TAG, "Finishing activity due to inactivity");
				activity.finish();
			}
			catch (InterruptedException e) {
				// continue without killing
			}
			return null;
		}
	}

}
