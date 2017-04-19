package com.yeamy.support.zxing.plugin;

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Code come from zxing-android with little modification, can be replay by the original file;
 */
public final class InactivityTimer {

    public static final long INACTIVITY_DELAY_MS = 15 * 60 * 1000L;

    private final Timer timer = new Timer();
    private TimerTask task;

    public void onResume(final Activity activity, long time) {
        timer.schedule(task = new TimerTask() {
            @Override
            public void run() {
                activity.finish();
            }
        }, time);
    }

    public void onPause() {
        task.cancel();
    }
}
