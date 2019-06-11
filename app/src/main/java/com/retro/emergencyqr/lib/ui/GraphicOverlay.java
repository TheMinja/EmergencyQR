package com.retro.emergencyqr.lib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.retro.emergencyqr.utils.DisplayUtil;
import com.retro.emergencyqr.utils.NotificationUtil;

/**
 * Created by tommy on 10/June/2019.
 */
public class GraphicOverlay extends View {

    protected static final int DEFAULT_FRAME_OPACITY = 0xA0; // Range: 0-255
    protected static final int DEFAULT_RESULT_BOX_OPACITY = 0xA0; // Range: 0-255
    protected static final float SCAN_FRAME_STROKE_WIDTH = 4.0f;
    private static final int SCAN_FRAME_MARGIN = 80; // Pixels
    protected static final int RESULT_BOX_HEIGHT = 40;
    protected static final float RESULT_TEXT_SIZE = 16.0f;
    private static final int DEFAULT_VIBRATE_TIME = 50; // Milliseconds
    public static final float TEXT_TRUNCATE_WIDTH = 60;

    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mXOffset;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    private int mYOffset;
    private int mFacing = CameraSource.CAMERA_FACING_BACK;
    private Rect mRectScanner;
    private Rect mRectResultBox;
    private Paint mRectPaint;
    private Paint mRectResultBoxPaint;
    private TextPaint mTextPaint;
    private Paint mRectFramePaint;
    private volatile Barcode mBarcode;
    private volatile String mLastBarCodeRawValue;

    private OnScannerCallback mScannerCallback;

    public interface OnScannerCallback {
        void onNew(Barcode barcode);
    }

    public void setOnScannerCallback(OnScannerCallback onScannerCallback) {
        mScannerCallback = onScannerCallback;
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRectScanner();
        initRectText();
        initRectFrame();
        mLastBarCodeRawValue = "No QR Code detected";
    }

    private void initRectScanner() {
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(SCAN_FRAME_STROKE_WIDTH);
    }

    private void initRectText() {
        mRectResultBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectResultBoxPaint.setColor(Color.WHITE);
        mRectResultBoxPaint.setStyle(Paint.Style.FILL);
        mRectResultBoxPaint.setAlpha(DEFAULT_RESULT_BOX_OPACITY);
        mRectResultBoxPaint.setAntiAlias(true);

        Paint.FontMetrics fm = new Paint.FontMetrics();
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTypeface(FontOverride.getRegularFont(getContext().getApplicationContext()));
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(DisplayUtil.spToPx(getContext(), RESULT_TEXT_SIZE));
        mTextPaint.setAntiAlias(true);
        mTextPaint.getFontMetrics(fm);
    }

    private void initRectFrame() {
        mRectFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectFramePaint.setColor(Color.BLACK);
        mRectFramePaint.setAlpha(DEFAULT_FRAME_OPACITY);
    }

    /**
     * Removes all graphics from the overlay.
     */
    public void clear() {
        synchronized (mLock) {
            mBarcode = null;
        }
        postInvalidate();
    }

    /**
     * Adds a graphic to the overlay.
     */
    public void add(Barcode barcode) {
        synchronized (mLock) {
            if (mBarcode == barcode ||
                    ((mBarcode != null && barcode != null) && TextUtils.equals(mBarcode.rawValue, barcode.rawValue))) {
                return;
            }
            mBarcode = barcode;
        }
        postInvalidate();
    }

    /**
     * Removes a graphic from the overlay.
     */
    public void remove() {
        synchronized (mLock) {
            mBarcode = null;
        }
        postInvalidate();
    }

    /**
     * Returns the horizontal scale factor.
     */
    public float getWidthScaleFactor() {
        return mWidthScaleFactor;
    }

    /**
     * Returns the vertical scale factor.
     */
    public float getHeightScaleFactor() {
        return mHeightScaleFactor;
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int xOffset, int yOffset, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mXOffset = xOffset;
            mYOffset = yOffset;
            mFacing = facing;
        }
        postInvalidate();
    }

    /**
     * Draws the overlay with its associated graphic objects.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mLock) {
            final int canvasW = getWidth();
            final int canvasH = getHeight();

            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvasW / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvasH / (float) mPreviewHeight;
            }

            final int cameraFrameW = canvasW;
            final int cameraFrameH = canvasH - (int) DisplayUtil.dpToPx(getContext(), RESULT_BOX_HEIGHT);

            if (mBarcode != null && !TextUtils.equals(mLastBarCodeRawValue, mBarcode.rawValue)) {
                mLastBarCodeRawValue = mBarcode.rawValue;
                NotificationUtil.vibrate(getContext().getApplicationContext(), DEFAULT_VIBRATE_TIME);
                if (mScannerCallback != null) {
                    mScannerCallback.onNew(mBarcode);
                }
            }

            // Draw result box text
            drawResultBoxText(canvas, cameraFrameW, cameraFrameH, mLastBarCodeRawValue);

            // Draw scan frame with border
            drawScannerFrame(canvas, cameraFrameW, cameraFrameH);

        }
    }

    /**
     *
     * @param canvas Canvas
     * @param cameraFrameW Camera Frame Width
     * @param cameraFrameH Camera Frame Height
     */
    private void drawScannerFrame(Canvas canvas, int cameraFrameW, int cameraFrameH) {
        int canvasW = getWidth();
        int childXOffset = mXOffset / 2;
        int childYOffset = mYOffset / 2;

        if (mRectScanner == null) {
            int scanFrameSize = cameraFrameW - (int) DisplayUtil.dpToPx(getContext(), SCAN_FRAME_MARGIN);
            Point centerFrame = new Point(cameraFrameW / 2, cameraFrameH / 2);
            int left = centerFrame.x - (scanFrameSize / 2);
            int top = centerFrame.y - (scanFrameSize / 2);
            int right = centerFrame.x + (scanFrameSize / 2);
            int bottom = centerFrame.y + (scanFrameSize / 2);
            mRectScanner = new Rect(left, top, right, bottom);
        }
        canvas.drawRect(mRectScanner, mRectPaint);

        // Draw darker background wrap scan frame
        canvas.drawRect(childXOffset, childYOffset, canvasW - (childXOffset), mRectScanner.top, mRectFramePaint); // Top
        canvas.drawRect(childXOffset, mRectScanner.top, mRectScanner.left, mRectScanner.bottom, mRectFramePaint); // Left
        canvas.drawRect(mRectScanner.right, mRectScanner.top, canvasW - childXOffset, mRectScanner.bottom, mRectFramePaint); // Right
        canvas.drawRect(childXOffset, mRectScanner.bottom, canvasW - (childXOffset), mRectResultBox.top, mRectFramePaint); // Bottom
    }

    /**
     *
     * @param canvas Canvas
     * @param cameraFrameW Camera Frame Width
     * @param cameraFrameH Camera Frame Height
     * @param resultText Display text
     */
    private void drawResultBoxText(Canvas canvas, int cameraFrameW, int cameraFrameH, String resultText) {
        int canvasW = getWidth();
        int canvasH = getHeight();
        int childXOffset = mXOffset / 2;
        int childYOffset = mYOffset / 2;
        // Draw rect display result text at bottom of frame
        if (mRectResultBox == null) {
            mRectResultBox = new Rect(childXOffset, cameraFrameH - mYOffset, canvasW - childXOffset, canvasH - childYOffset);
        }
        canvas.drawRect(mRectResultBox, mRectResultBoxPaint);

        // Set text ellipse if too long
        String displayText = TextUtils.ellipsize(resultText, mTextPaint, canvasW - TEXT_TRUNCATE_WIDTH, TextUtils.TruncateAt.END).toString();

        // Calculate x, y coordinate position at center horizontal width and bottom of frame
        float x = (canvasW / 2) - (mTextPaint.measureText(displayText) / 2);

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = Math.abs(fm.ascent) - Math.abs(fm.descent);

        float y = mRectResultBox.centerY() + Math.abs(textHeight / 2);

        // Draw result text
        canvas.drawText(displayText, x, y, mTextPaint);
    }

}

