package it.links.scanmrz;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class TextRecognition extends AsyncTask<Map<String, Object>, Void, Void> {
    private static final int CIE_NRIGHE = 3;
    private static final int PE_NCHAR = 44;
    private static final int PE_NRIGHE = 2;
    private static final String TAG = "e.brienza";
    public static String recognizedText;
    Mat finalMat;
    Mat finalMatBIN;

    /* access modifiers changed from: protected */
    public void onPostExecute(String str) {
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
    }

    /* access modifiers changed from: protected */
    public void onProgressUpdate() {
    }

    /* access modifiers changed from: protected */
    public Void doInBackground(Map<String, Object>... mapArr) {
        try {
            if (mapArr.length > 0) {
                Mat mat = (Mat) mapArr[0].get("matrice");
                Rect rect = (Rect) mapArr[0].get("rect");
                Boolean bool = (Boolean) mapArr[0].get("containsChar");
                this.finalMat = new Mat(mat, rect);

                Mat tmp = finalMat; //preproc1(this.finalMat);

                detectAndCheckMrz(tmp);
                if (!AnalisiOcr.trovato) {
                    this.finalMatBIN = new Mat();
                    this.finalMat.convertTo(this.finalMatBIN, -1, 3.0d, 0.0d);
                    detectAndCheckMrz(this.finalMatBIN);
                }
                this.finalMat.release();
                this.finalMatBIN.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // CREA IL BITMAP DA DARE IN PASTO A TESSERACT
    public void detectAndCheckMrz(Mat mat) {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(mat.width(), mat.height(), Config.ARGB_8888);
            Utils.matToBitmap(mat, createBitmap);
            AnalisiOcr.baseApi.setImage(createBitmap);
            recognizedText = AnalisiOcr.baseApi.getUTF8Text().trim();
            Log.i("RECGNIZEDTEXT", recognizedText);
            AnalisiOcr.setTestoTradotto(recognizedText);
        } catch (Exception e) {
            e.printStackTrace();
            cancel(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onCancelled() {
        super.onCancelled();
        Log.i(TAG, "oncancelled");
        try {
            AnalisiOcr.endOrc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Mat preproc1(Mat rgbaInnerWindow) {
        Mat mIntermediateMat = new Mat();
        Imgproc.GaussianBlur(rgbaInnerWindow, mIntermediateMat, new org.opencv.core.Size(5.0d, 5.0d), 0.0d);
        Mat mat4 = new Mat();
        Imgproc.GaussianBlur(mIntermediateMat, mat4, new org.opencv.core.Size(0.0d, 0.0d), 9.0d);
        Core.addWeighted(rgbaInnerWindow, 2.0d, mat4, -0.6d, -5.0d, mat4);
        mat4.convertTo(mIntermediateMat, -1, 1.1d, 3.0d);
        Imgproc.cvtColor(mIntermediateMat, mat4, 11);
        Imgproc.threshold(mat4, rgbaInnerWindow, 0.0d, 255.0d, 8);
        return rgbaInnerWindow;
    }

    public Mat deglare(Mat roi) {
        Mat yuv = new Mat();
        Imgproc.cvtColor(roi, yuv, Imgproc.COLOR_BGR2YUV);
        List<Mat> bgr = new ArrayList<>();
        Core.split(yuv, bgr);
        Imgproc.equalizeHist(bgr.get(0), bgr.get(0));
        Core.merge(bgr, yuv);
        Imgproc.cvtColor(yuv, roi, Imgproc.COLOR_YUV2BGR);
        return roi;
    }


}
