package com.yeamy.support.zxing.decode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import com.yeamy.support.zxing.ScanResult;

public class DecodeRequest implements Runnable, ResultPointCallback {
    private final MultiFormatReader multiFormatReader;
    private ResultPointCallback pointCallback;
    private DecodeCallback callback;
    private DecodeBean bean;

    /**
     * width Default Config
     */
    public DecodeRequest() {
        this(DecodeConfig.defaultConfig());
    }

    public DecodeRequest(DecodeConfig config) {
        this.multiFormatReader = new MultiFormatReader();
        config.setCallback(this);
        multiFormatReader.setHints(config.build());
    }

    public void setRequest(DecodeBean bean, DecodeCallback callback, ResultPointCallback pointCallback) {
        this.bean = bean;
        this.callback = callback;
        this.pointCallback = pointCallback;
    }

    @Override
    public void run() {
        ScanResult result = decode(bean);
        if (result != null) {
            callback.onDecodeSuccess(result);
        } else {
            callback.onDecodeFail();
        }
    }

    private ScanResult decode(DecodeBean bean) {
        Result rawResult = null;
//		long start = System.currentTimeMillis();
//		System.out.println("dataWidth = " + bean.dataWidth + //
//				" dataHeight = " + bean.dataHeight + //
//				" left = " + bean.left + //
//				" top = " + bean.top + //
//				" width = " + bean.width + //
//				" height = " + bean.height + //
//				" reverseHorizontal = " + bean.reverseHorizontal);
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(bean.yuvData, bean.dataWidth, bean.dataHeight,
                bean.left, bean.top, bean.width, bean.height, false);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            // continue
        } finally {
            bean.reset();
            multiFormatReader.reset();
        }
//		long end = System.currentTimeMillis();
//		System.out.println("Found barcode in " + (end - start) + " ms");
        if (rawResult != null) {
            return new ScanResult(rawResult);
        }
        return null;
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        if (pointCallback != null) {
            pointCallback.foundPossibleResultPoint(point);
        }
    }

    public interface DecodeCallback {

        void onDecodeSuccess(ScanResult result);

        void onDecodeFail();
    }

}
