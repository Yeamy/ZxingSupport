package com.yeamy.support.zxing.demo;


import android.app.AlertDialog;
import android.content.Context;

/**
 * show the Scan Result
 */
public class ScanResultDialog extends AlertDialog.Builder {

    public ScanResultDialog(Context context, String result) {
        super(context);
        setTitle("The Scan Result is:");
        setMessage(result);
    }

}
