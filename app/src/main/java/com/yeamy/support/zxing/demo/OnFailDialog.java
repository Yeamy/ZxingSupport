package com.yeamy.support.zxing.demo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class OnFailDialog extends Builder {

	public OnFailDialog(Activity context) {
		super(context);
		setTitle(context.getString(R.string.app_name));
		setMessage("Sorry, the Android camera encountered a problem. You may need to restart the device.");
		setPositiveButton(android.R.string.ok, new FinishListener(context));
		setOnCancelListener(new FinishListener(context));
	}

	private class FinishListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {

		private final Activity activityToFinish;

		public FinishListener(Activity activityToFinish) {
			this.activityToFinish = activityToFinish;
		}

		@Override
		public void onCancel(DialogInterface dialogInterface) {
			run();
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int i) {
			run();
		}

		private void run() {
			activityToFinish.finish();
		}

	}
}
