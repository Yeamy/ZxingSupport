package com.yeamy.support.zxing.plugin;

import android.app.Activity;

import com.yeamy.support.zxing.LooperThread;

/**
 * Code come from zxing-android with little modification, can be replay by the original file;
 */
public final class InactivityTimer implements Runnable {

	private static final long INACTIVITY_DELAY_MS = 5 * 60 * 1000L;

	private final Activity activity;
	private final LooperThread thread;

	public InactivityTimer(Activity activity, LooperThread thread) {
		this.activity = activity;
		this.thread = thread;
	}

	public void onResume() {
		thread.postDelayed(this, INACTIVITY_DELAY_MS);
	}

	public void onPause() {
		thread.removeCallbacks(this);
	}

	@Override
	public void run() {
		activity.finish();
	}

}
