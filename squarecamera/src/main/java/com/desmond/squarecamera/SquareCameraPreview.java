package com.desmond.squarecamera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;

import com.desmond.squarecamera.exception.AutofocusException;
import com.desmond.squarecamera.exception.HandleFocusException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SquareCameraPreview extends SurfaceView {

    public static final String TAG = SquareCameraPreview.class.getSimpleName();
    private static final int INVALID_POINTER_ID = -1;

    private static final int FOCUS_SQR_SIZE = 100;
    private static final int FOCUS_MAX_BOUND = 1000;
    private static final int FOCUS_MIN_BOUND = -FOCUS_MAX_BOUND;

    private Camera mCamera;

    private float mLastTouchX;
    private float mLastTouchY;

    // For scaling
    private int mActivePointerId = INVALID_POINTER_ID;

    // For focus
    private boolean mIsFocus;
    private boolean mIsFocusReady;
    private Camera.Area mFocusArea;
    private ArrayList<Camera.Area> mFocusAreas;
    private EventCallback mEventCallback = null;

    public SquareCameraPreview(Context context) {
        super(context);
        init();
    }

    public SquareCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SquareCameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mFocusArea = new Camera.Area(new Rect(), 1000);
        mFocusAreas = new ArrayList<>();
        mFocusAreas.add(mFocusArea);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mIsFocus = true;

                mLastTouchX = event.getX();
                mLastTouchY = event.getY();

                mActivePointerId = event.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mIsFocus && mIsFocusReady) {
                    try {
                        handleFocus(mCamera.getParameters());
                    }catch(Throwable t) {
                        onError(new HandleFocusException(t));
                    }
                }
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                try {
                    mCamera.cancelAutoFocus();
                }catch (Throwable t){
                    onError(new AutofocusException(t));
                }
                mIsFocus = false;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
        }

        return true;
    }

    private void onError(Throwable t) {
        if(mEventCallback != null) {
            mEventCallback.onError(t);
        }
    }


    private void handleFocus(Camera.Parameters params) {
        float x = mLastTouchX;
        float y = mLastTouchY;

        if (!setFocusBound(x, y)) return;

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null
                && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            Log.d(TAG, mFocusAreas.size() + "");
            params.setFocusAreas(mFocusAreas);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(params);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    // Callback when the auto focus completes
                }
            });
        }
    }

    public void setIsFocusReady(final boolean isFocusReady) {
        mIsFocusReady = isFocusReady;
    }

    private boolean setFocusBound(float x, float y) {
        int left = (int) (x - FOCUS_SQR_SIZE / 2);
        int right = (int) (x + FOCUS_SQR_SIZE / 2);
        int top = (int) (y - FOCUS_SQR_SIZE / 2);
        int bottom = (int) (y + FOCUS_SQR_SIZE / 2);

        if (FOCUS_MIN_BOUND > left || left > FOCUS_MAX_BOUND) return false;
        if (FOCUS_MIN_BOUND > right || right > FOCUS_MAX_BOUND) return false;
        if (FOCUS_MIN_BOUND > top || top > FOCUS_MAX_BOUND) return false;
        if (FOCUS_MIN_BOUND > bottom || bottom > FOCUS_MAX_BOUND) return false;

        mFocusArea.rect.set(left, top, right, bottom);

        return true;
    }

    public void setEventCallback(EventCallback eventCallback) {
        mEventCallback = eventCallback;
    }

    interface EventCallback{
        void onError(Throwable t);
    }
}
