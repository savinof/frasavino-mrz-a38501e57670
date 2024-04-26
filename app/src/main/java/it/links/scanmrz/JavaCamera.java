package it.links.scanmrz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Rect;

public class JavaCamera extends JavaCameraView implements PictureCallback, PreviewCallback {
    private static final String TAG = "e.brienza";
    private String mPictureFileName;

    public JavaCamera(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setAutoFocusMode() {
        Parameters parameters = this.mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains("auto")) {
            Log.d(TAG, "Autofocus");
            parameters.setFocusMode("auto");
        }
        this.mCamera.setParameters(parameters);
    }

    public void setMaxFPS(int i) {
        Parameters parameters = this.mCamera.getParameters();
        parameters.setPreviewFpsRange(1000, i * 1000);
        this.mCamera.setParameters(parameters);
    }

    public Size getMyBestPreviewSize(List<Size> list, int i, int i2) {
        int i3 = i2;
        double d = ((double) i) / ((double) i3);
        Size size = null;
        if (list == null) {
            return null;
        }
        double d2 = Double.MAX_VALUE;
        double d3 = Double.MAX_VALUE;
        for (Size size2 : list) {
            double d4 = (((double) size2.width) / ((double) size2.height)) - d;
            Math.abs(d4);
            if (Math.abs(d4) <= 0.12d && ((double) Math.abs(size2.height - i3)) < d3) {
                d3 = (double) Math.abs(size2.height - i3);
                size = size2;
            }
        }
        if (size == null) {
            for (Size size3 : list) {
                if (((double) Math.abs(size3.height - i3)) < d2) {
                    size = size3;
                    d2 = (double) Math.abs(size3.height - i3);
                }
            }
        }
        return size;
    }

    public void setCamera() {
        Parameters parameters = this.mCamera.getParameters();
        this.mCamera.stopPreview();
        this.mCamera.setParameters(parameters);
        Log.i(TAG, "m.recupero disconnect");
        disconnectCamera();
        connectCamera(this.mFrameWidth, this.mFrameHeight);
        this.mCamera.startPreview();
    }

    private void getScreenRotationOnTablet() {
        switch (((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 0:
                Log.i(TAG, "SCREEN_ORIENTATION_LANDSCAPE");
                try {
                    this.mCamera.setPreviewDisplay(getHolder());
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            case 1:
                Log.i(TAG, "SCREEN_ORIENTATION_REVERSE_PORTRAIT");
                return;
            case 2:
                Log.i(TAG, "SCREEN_ORIENTATION_REVERSE_LANDSCAPE");
                return;
            case 3:
                Log.i(TAG, "SCREEN_ORIENTATION_PORTRAIT");
                return;
            default:
                return;
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public void turnOnFlash(boolean z) {
        Parameters parameters = this.mCamera.getParameters();
        if (z) {
            parameters.setFlashMode("torch");
        } else {
            parameters.setFlashMode("off");
        }
        this.mCamera.setParameters(parameters);
    }

    public void submitFocusAreaRect(Rect rect, int i, int i2) {
        Parameters parameters = this.mCamera.getParameters();
        if (parameters.getMaxNumFocusAreas() != 0) {
            android.graphics.Rect rect2 = new android.graphics.Rect(((rect.x * 2000) / i) - 1000, ((rect.y * 2000) / i2) - 1000, (((rect.x + rect.width) * 2000) / i) - 1000, (((rect.y + rect.height) * 2000) / i2) - 1000);
            ArrayList arrayList = new ArrayList();
            arrayList.add(new Area(rect2, 1000));
            try {
                parameters.setFocusAreas(arrayList);
                this.mCamera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setResolution(Size size) {
        if (this.mCamera != null) {
            disconnectCamera();
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("OPTIMAL_WIDTH: ");
        sb.append(size.width);
        sb.append("OPTIMAL_HEIGHT: ");
        sb.append(size.height);
        Log.d(str, sb.toString());
        connectCamera(size.width, size.height);
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("OPTIMAL_WIDTH: ");
        sb2.append(size.width);
        sb2.append("OPTIMAL_HEIGHT: ");
        sb2.append(size.height);
        Log.d(str2, sb2.toString());
    }

    public List<String> getEffectList() {
        return this.mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return this.mCamera.getParameters().getColorEffect() != null;
    }

    public String getEffect() {
        return this.mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String str) {
        Parameters parameters = this.mCamera.getParameters();
        parameters.setColorEffect(str);
        this.mCamera.setParameters(parameters);
    }

    public List<Size> getResolutionList() {
        return this.mCamera.getParameters().getSupportedPreviewSizes();
    }

    public Size getResolution() {
        return this.mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(String str) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = str;
        mCamera.setPreviewCallback(null);
        mCamera.takePicture(null, null, this);

    }
    @Override
    public void onPictureTaken(byte[] bArr, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        this.mCamera.startPreview();
        this.mCamera.setPreviewCallback(this);
        //ShowPreviewActivity.fotoByte = bArr;
        //ShowPreviewActivity.fotoRetro = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(this.mPictureFileName);
            fileOutputStream.write(bArr);
            fileOutputStream.close();

        } catch (IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        Intent intent = new Intent(MainActivity.activity, ShowPreviewActivity.class);
        MainActivity.activity.startActivityForResult(intent, 1);
    }
}
