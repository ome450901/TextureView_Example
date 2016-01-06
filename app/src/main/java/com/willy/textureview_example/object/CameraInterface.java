package com.willy.textureview_example.object;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Willy on 2015/12/24.
 */
public class CameraInterface implements Camera.PreviewCallback{

    public static final String TAG = "CameraInterface";

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private static CameraInterface sCameraInterface;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    public interface CameraOpenCallback {
        void cameraHasOpened();
    }

    public static synchronized CameraInterface getInstance() {
        if (sCameraInterface == null) {
            sCameraInterface = new CameraInterface();
        }
        return sCameraInterface;
    }

    public void openCamera(CameraOpenCallback cameraOpenCallback) {
        Log.d(TAG, "Open Camera");
        mCamera = CameraUtil.getInstance().getCameraAndOpen();
        cameraOpenCallback.cameraHasOpened();
    }

    public void startCameraPreview(SurfaceTexture surface) {
        Log.d(TAG, "Start CameraPreview");

        if (isPreviewing) {
            mCamera.stopPreview();
        }

        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initCamera();
        }
    }

    public void stopCameraPreview() {
        Log.d(TAG, "Stop CameraPreview");

        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    private void initCamera() {
        if (mCamera != null) {

            mParams = mCamera.getParameters();

            //設定PreviewSize
            Camera.Size previewSize = CameraUtil.getInstance().getBestPreviewSize(mParams.getSupportedPreviewSizes(), 300, 300);
//            Camera.Size previewSize = CameraUtil.getInstance().getOptimalPreviewSize(mParams.getSupportedPreviewSizes());
            mParams.setPreviewSize(previewSize.width, previewSize.height);
            mParams.setPreviewFormat(ImageFormat.NV21);

            mCamera.setDisplayOrientation(90);

            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

            isPreviewing = true;

            mParams = mCamera.getParameters(); //重新get一次

            Log.d(TAG, "PreviewSize設定：Width = " + mParams.getPreviewSize().width + ", Height = " + mParams.getPreviewSize().height);
        }
    }

    public Camera.Parameters getParams() {
        return mParams;
    }
}
