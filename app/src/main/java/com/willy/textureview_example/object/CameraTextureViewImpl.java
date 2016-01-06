package com.willy.textureview_example.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.nio.ByteBuffer;

/**
 * Created by Willy on 2015/12/22.
 */
public class CameraTextureViewImpl extends TextureView implements TextureView.SurfaceTextureListener,  CameraInterface.CameraOpenCallback{

    public static final String TAG = "CameraTextureViewImpl";

    private Context mContext;

    private Thread openCameraThread = new Thread() {
        @Override
        public void run() {
            CameraInterface.getInstance().openCamera(CameraTextureViewImpl.this);
        }
    };

    public CameraTextureViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    @Override
    public void cameraHasOpened() {
        SurfaceTexture surfaceTexture = getSurfaceTexture();
        CameraInterface.getInstance().startCameraPreview(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");

//        openCameraThread.start();
        CameraInterface.getInstance().openCamera(CameraTextureViewImpl.this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        CameraInterface.getInstance().stopCameraPreview();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Bitmap bitmap = getBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] byteArray = byteBuffer.array();

        // send byteArray here. (Asyntask)

    }
}
