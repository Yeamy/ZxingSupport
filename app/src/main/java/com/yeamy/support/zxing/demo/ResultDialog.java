package com.yeamy.support.zxing.demo;


import android.app.AlertDialog;
import android.content.Context;

public class ResultDialog extends AlertDialog.Builder {

    public ResultDialog(Context context, String result) {
        super(context);
        setTitle("The Scan Result is:");
        setMessage(result);
    }

}
