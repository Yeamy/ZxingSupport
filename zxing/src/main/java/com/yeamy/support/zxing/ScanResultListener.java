package com.yeamy.support.zxing;

public interface ScanResultListener {

	/**
	 * 扫描完成
	 * @param result 扫描结果
	 */
	void onScanSuccess(ScanResult result);

}