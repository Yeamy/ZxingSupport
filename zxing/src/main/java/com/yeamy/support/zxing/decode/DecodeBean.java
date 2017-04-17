package com.yeamy.support.zxing.decode;

public class DecodeBean {

	public byte[] yuvData;
	public int dataWidth;
	public int dataHeight;
	public int left;
	public int top;
	public int width;
	public int height;
	private byte[] cropBuff;
	// public boolean portrait = false;

	public void setSource(byte[] yuvData, int dataWidth, int dataHeight) {
		this.yuvData = yuvData;
		this.dataWidth = dataWidth;
		this.dataHeight = dataHeight;
	}

	public void setFrameRectLandspace(int left, int top, int width, int height) {
		// landspace no need to do anything, due to the zxing framework will do it
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public void setFrameRectPortrait(int left, int top, int width, int height) {
		if (cropBuff == null) {
			cropBuff = new byte[width * height * 3 / 2];
		}
		YV12CropAndRotate90(yuvData, dataWidth, dataHeight, //
				cropBuff, top, left, width, height);
		this.left = 0;
		this.top = 0;
		this.width = this.dataWidth = height;
		this.height = this.dataHeight = width;
		yuvData = cropBuff;
	}

	void reset() {
		yuvData = null;
	}

	/**
	 * call when stop scan
	 */
	public void clearBuff() {
		cropBuff = null;
		cropBuff = null;
	}

	/**
	 * @param dst length must equle <b>[width * height * 3 / 2]</b>
	 * @param top The top of {@code src} as the start of {@code dst}
	 * @param left The left of {@code src} as the start of {@code dst}
	 * @param width The width of {@code dst} before rotate
	 * @param height The height of {@code dst} before rotate
	 */
	public static void YV12CropAndRotate90(byte[] src, int srcWidth, int srcHeight, //
			byte[] dst, int top, int left, int width, int height) {
		int c_top = top / 2;
		int c_left = left / 2;
		int i = 0;

		int cr_offset = srcWidth * srcHeight;
		int c_width = width / 2;
		int c_height = height / 2;
		int src_c_width = srcWidth / 2;
		int src_c_size = src_c_width * srcHeight / 2;
		int cb_offset = cr_offset + src_c_size;

		for (int x = 0; x < width; x++) {
			for (int y = height - 1; y >= 0; y--) {
				dst[i++] = src[srcWidth * (top + y) + left + x];
			}
		}
		for (int x = 0; x < c_width; x++) {
			for (int y = c_height - 1; y >= 0; y--) {
				dst[i++] = src[cr_offset + src_c_width * (c_top + y) + c_left + x];
			}
		}
		for (int x = 0; x < c_width; x++) {
			for (int y = c_height - 1; y >= 0; y--) {
				dst[i++] = src[cb_offset + src_c_width * (c_top + y) + c_left + x];
			}
		}
	}

//	public static void YV12Rotate90(byte[] src, byte[] dst, int srcWidth, int srcHeight) {
//		int y_size = srcWidth * srcHeight;
//		int cr_offset = y_size;
//		int c_width = srcWidth / 2;
//		int c_height = srcHeight / 2;
//		int c_size = c_width * c_height;
//		int cb_offset = cr_offset + c_size;
//
//		int i = 0;
//		for (int x = 0; x < srcWidth; x++) {
//			for (int y = srcHeight - 1; y >= 0; y--) {
//				dst[i++] = src[srcWidth * y + x];
//			}
//		}
//		for (int x = 0; x < c_width; x++) {
//			for (int y = c_height - 1; y >= 0; y--) {
//				dst[i++] = src[cr_offset + c_width * y + x];
//			}
//		}
//		for (int x = 0; x < c_width; x++) {
//			for (int y = c_height - 1; y >= 0; y--) {
//				dst[i++] = src[cb_offset + c_width * y + x];
//			}
//		}
//	}

}
