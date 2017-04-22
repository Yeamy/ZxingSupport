package com.yeamy.support.zxing.plugin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.google.zxing.ResultPoint;
import com.yeamy.support.zxing.R;
import com.yeamy.support.zxing.Viewfinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * The best way to instance Viewfinder is to create a view implements it<br>
 * invoke {@link #setForeground(Drawable)} to change cover frame
 */
public class ViewfinderView extends FrameLayout implements Viewfinder {
    private int frameTop, frameLeft, frameRight, frameBottom;
    private Point viewRect;
    private Rect frameRect;
    private boolean created;
    protected TextureView textureView;
    protected LayoutParams textureParams;
    private PreviewListener listener;

    public ViewfinderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView, 0, 0);
        frameTop = a.getDimensionPixelSize(R.styleable.ViewfinderView_frameTop, -1);
        frameLeft = a.getDimensionPixelSize(R.styleable.ViewfinderView_frameLeft, 0);
        frameRight = a.getDimensionPixelSize(R.styleable.ViewfinderView_frameRight, 0);
        frameBottom = a.getDimensionPixelSize(R.styleable.ViewfinderView_frameBottom, -1);
        a.recycle();

        LayoutParams params = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        TextureView view = new TextureView(context);
        addView(view, 0, params);
        this.textureView = view;
        this.textureParams = params;
        this.viewRect = new Point();
        this.frameRect = new Rect();

        setForeground(new SimpleFrameDrawable());
    }

    @Override
    public void setForeground(Drawable drawable) {
        if (drawable != null && drawable instanceof FrameDrawable) {
            FrameDrawable draw = (FrameDrawable) drawable;
            draw.setFrameRect(frameRect);
        }
        super.setForeground(drawable);
    }

    /**
     * @return the real display view
     */
    public TextureView getTextureView() {
        return textureView;
    }

    public void setPreviewListener(PreviewListener listener) {
        this.listener = listener;
        if (created) {
            listener.onViewCreated();
        }
    }

    /**
     * get the size of this view at the first time here
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            reSizeRect(left, top, right, bottom);
            if (listener != null) {
                listener.onViewCreated();
            }
            created = true;
        }
    }

    private void reSizeRect(int left, int top, int right, int bottom) {
        viewRect.x = right - left;
        viewRect.y = bottom - top;

        frameRect.left = left + frameLeft;
        frameRect.right = right - frameRight;
        if (frameTop == -1) {
            if (frameBottom == -1) {
                frameRect.top = top;
                frameRect.bottom = frameRect.top + frameRect.width();
            } else {
                frameRect.bottom = bottom - frameBottom;
                frameRect.top = frameRect.bottom - frameRect.width();
            }
        } else {
            if (frameBottom == -1) {
                frameRect.top = top + frameTop;
                frameRect.bottom = frameRect.top + frameRect.width();
            } else {
                frameRect.top = top + frameTop;
                frameRect.bottom = bottom - frameBottom;
            }
        }
        if (frameRect.top < 0) {
            frameRect.top = 0;
        }
        if (frameRect.bottom > bottom) {
            frameRect.bottom = bottom;
        }
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
    }

    @Override
    public Point getPreviewSize() {
        return viewRect;
    }

    @Override
    public Rect getFrameSize() {
        return frameRect;
    }

    @Override
    public int getOrientation() {
        return 90;
    }

    /**
     * resize the preview-View
     */
    @Override
    public void onStartPreview(TextureView view, int pw, int ph) {
        // resize
        int width = getWidth();
        int height = getHeight();
        int surfaceWidth, surfaceHeight;
        if (width * ph > height * pw) {// 16:9
            surfaceWidth = pw * height / ph;
            surfaceHeight = height;
        } else {// 4:3
            surfaceWidth = width;
            surfaceHeight = ph * width / pw;
        }
        LayoutParams params = textureParams;
        params.width = surfaceWidth;
        params.height = surfaceHeight;
        textureView.setLayoutParams(params);

        int x = (width - surfaceWidth) / 2;
        reSizeRect(getLeft() + x, //
                getTop(), //
                getRight() - x, //
                getTop() + surfaceHeight);
        if (listener != null) {
            listener.onPreviewStart();
        }
    }

    /**
     * the frame rect of viewfinder width four corner
     */
    public static abstract class FrameDrawable extends Drawable {
        private Paint paint = new Paint();
        private Rect frameRect;

        private void setFrameRect(Rect frameRect) {
            this.frameRect = frameRect;
        }

        public Rect getFrameRect() {
            return frameRect;
        }

        public Paint getPaint() {
            return paint;
        }
    }

    public static class SimpleFrameDrawable extends FrameDrawable {
        private int laseColor = 0x90000000;
        private int frameColor = 0x60ffffff;
        private int cornerColor = 0xc0ffffff;

        public void setLaseColor(int laseColor) {
            this.laseColor = laseColor;
        }

        public void setFrameColor(int frameColor) {
            this.frameColor = frameColor;
        }

        public void setCornerColor(int cornerColor) {
            this.cornerColor = cornerColor;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            Rect frameRect = getFrameRect();
            int top = frameRect.top;
            int left = frameRect.left;
            int right = frameRect.right;
            int bottom = frameRect.bottom;
            Paint paint = getPaint();
            // lase
            paint.setColor(laseColor);
            canvas.drawRect(0, 0, width, top, paint);// top
            canvas.drawRect(0, top, left, bottom, paint);// bottom
            canvas.drawRect(0, bottom, width, height, paint);// left
            canvas.drawRect(right, top, width, bottom, paint);// right
            // frame
            paint.setColor(frameColor);
            float[] pts = new float[]{left, top, right, top, // top
                    left, bottom, right, bottom, // bottom
                    left, top, left, bottom, // left
                    right, top, right, bottom,// right;
            };
            canvas.drawLines(pts, paint);
            // corners
            paint.setColor(cornerColor);
            int xlength = frameRect.width() / 20;
            int ylength = frameRect.height() / 20;
            int dm = frameRect.width() / 27;
            top += dm;
            left += dm;
            right -= dm;
            bottom -= dm;
            float[] corners = new float[]{ //
                    left, top, left + xlength, top, // left-top-h
                    left, top, left, top + ylength, // left-top-v
                    right, top, right - xlength, top, // right-top-h
                    right, top, right, top + ylength, // right-top-v
                    left, bottom, left + xlength, bottom, // left-top-h
                    left, bottom, left, bottom - ylength, // left-top-v
                    right, bottom, right - xlength, bottom, // right-top-h
                    right, bottom, right, bottom - ylength, // right-top-v
            };
            canvas.drawLines(corners, paint);
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

    }

    public interface PreviewListener {
        void onViewCreated();

        void onPreviewStart();
    }
}
