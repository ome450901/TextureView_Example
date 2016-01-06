package com.willy.textureview_example.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.Camera;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Willy on 2015/12/24.
 */
public class CameraUtil {
    public static final String TAG = "CameraUtil";

    private static CameraUtil sCameraUtil = null;

    public static CameraUtil getInstance() {
        if (sCameraUtil == null) {
            sCameraUtil = new CameraUtil();
            return sCameraUtil;
        } else {
            return sCameraUtil;
        }
    }

    public Camera getCameraAndOpen() {
        Camera camera = null;

        int unUseID = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

                try {
                    camera = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    camera = null;
                }

                if (camera != null) {
                    break;
                }
            } else {
                unUseID = camIdx;
            }
        }

        if (camera == null) {
            try {
                camera = Camera.open(unUseID);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        return camera;
    }

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizeList) {
        Camera.Size optimalSize = null;

        for (Camera.Size size : sizeList) {
            if (size.width >= 640 && size.width <= 1280 &&
                    size.height >= 640 && size.height <= 1280) {
                optimalSize = size;
            }
        }

        if (optimalSize == null) {
            optimalSize = sizeList.get(sizeList.size() - 1);
        }

        return optimalSize;
    }

    public Camera.Size getBestPreviewSize(List<Camera.Size> sizeList, int targetWidth, int targetHeight) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) targetWidth / targetHeight;
        if (sizeList == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizeList) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizeList) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {

        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);

        return buffer.array();
    }

    public Bitmap byteArrayToBitmap(Bitmap bmpOriginal, byte[] imageArray) {
        Bitmap.Config configBmp = Bitmap.Config.valueOf(bmpOriginal.getConfig().name());
        Bitmap bitmap_tmp = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), configBmp);
        ByteBuffer buffer = ByteBuffer.wrap(imageArray);
        bitmap_tmp.copyPixelsFromBuffer(buffer);

        return bitmap_tmp;
    }

    public Bitmap convertBitmapToGraySale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);

        return grayBitmap;
    }

    public byte[] convertByteDataToGraySaleImage(byte[] data, int width, int height) {
        int length = width * height;

        byte[] image = new byte[length];
        int counter = 0;

        for (int i = image.length - 1; i >= 0; i--) {
            byte p = (byte) (data[i] & 0xff);
            image[counter] = p;
            counter = (counter + height);
            if (counter >= length) {
                counter -= (length - 1);
            }
        }
        return image;
    }
}
