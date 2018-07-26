package com.yeamy.support.zxing;

import android.graphics.Rect;
import android.view.TextureView;

import com.google.zxing.ResultPointCallback;

public interface Viewfinder extends ResultPointCallback {
	public static final int MIN_FRAME_WIDTH = 260;
	public static final int MIN_FRAME_HEIGHT = 260;
	public static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
	public static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080
	public static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen

	/**
	 * the size of the preview surface plan to layout
	 * 
	 * @return can not be null,
	 */
	Size getPreviewSize();

	/**
	 * preview code has been done, to change the surface size here
	 * 
	 * @param pw the width of camera preview
	 * @param ph the height of camera preview
	 */
	void onStartPreview(TextureView view, int pw, int ph);

	/**
	 * the viewfinder's size
	 * 
	 * @return {@link #MIN_FRAME_WIDTH} <= width <= {@link #MAX_FRAME_WIDTH}<br>
	 *         {@link #MIN_FRAME_HEIGHT} <= height <= {@link #MAX_FRAME_HEIGHT}
	 */
	Rect getFrameRect();

	/**
	 * orientation to display camera preview, only support 0 (landspace) or 90 (portrait) so far
	 * 
	 * @return 0 or 90
	 */
	int getOrientation();
}
