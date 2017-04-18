package com.yeamy.support.zxing.plugin;

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Code come from zxing-android with little modification, can be replay by the original file;
 */
public final class InactivityTimer {

    private static final long INACTIVITY_DELAY_MS = 5 * 60 * 1000L;

    private final Activity activity;
    private final Timer timer = new Timer();
    private final TimerTask task = new TimerTask() {
        @Override
        public void run() {
            activity.finish();
        }
    };

    public InactivityTimer(Activity activity) {
        this.activity = activity;
    }

    public void onResume() {
        timer.schedule(task, INACTIVITY_DELAY_MS);
    }

    public void onPause() {
        task.cancel();
    }

}
