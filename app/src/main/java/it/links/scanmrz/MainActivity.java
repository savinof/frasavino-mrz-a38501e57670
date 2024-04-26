package it.links.scanmrz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.AsyncTask.Status;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import it.links.scanmrz.utilities.CheckSession;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2, OnTouchListener{

    public static final float RATIO_CIE = 1.58f;
    public static final float RATIO_PE = 1.42f;
    public static final int FRONT_PIC_REQUEST = 1;
    public static final int REAR_PIC_REQUEST = 2;
    public static int REQUEST_TYPE = FRONT_PIC_REQUEST;
    private static final String TAG = "e.brienza";
    protected static File fDirCache = null;
    public static boolean isFocusAreaInitialized = false;
    public static boolean isPE = false;
    public static Activity activity;
    private AlertDialog alert = null;
    private Mat mGray;
    private JavaCamera mOpenCvCameraView;
    private Mat mRgba;
    public int maxSsecondiAttesa = 999;
    TextRecognition myTextRecognition = null;
    public static Rect rectMirino;
    public int secondiPassati = 0;
    private Timer timerSecPassati = null;
    private int xMirino = 0;
    private int yMirino = 0;
    public static Rect cropRect;

    public Mat frame;

    public static Mrz mrz = new Mrz();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        setContentView(R.layout.activity_main);
        CheckSession checkSession = new CheckSession(getApplicationContext());
        checkSession.check();
        Log.d("verify",String.valueOf(OpenCVLoader.initDebug()));
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        //getWindow().setFlags(1024, 1024);
        this.mOpenCvCameraView = (JavaCamera) findViewById(R.id.mrz_activity_java_surface_view);
        this.mOpenCvCameraView.setCvCameraViewListener((CvCameraViewListener2) this);
        //this.mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.mOpenCvCameraView.setOnTouchListener(this);
        try {
            checkAndDownloadFile();
            AnalisiOcr.trovato = false;
            mrz = null;
            AnalisiOcr.initOrc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (this.timerSecPassati != null) {
            this.timerSecPassati.cancel();
        }
        if (this.mOpenCvCameraView != null) {
            this.mOpenCvCameraView.disableView();
        }
    }
    @Override
    public void onResume() {
        Log.i(TAG, "onResume MRZPE");
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        AnalisiOcr.mrz = null;
        mrz = null;
        AnalisiOcr.trovato = false;
        Bundle extras = getIntent().getExtras();



        this.secondiPassati = 0;
        try {
            this.maxSsecondiAttesa = 1000;//Integer.parseInt(PreferenzeFragment.getPref(PreferenzeFragment.SECONDI_ATTESA_INS_MANUALE_KEY, "String", getApplicationContext()).toString());
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("max secondi d'attesa: ");
            sb.append(this.maxSsecondiAttesa);
            Log.i(str, sb.toString());
        } catch (Exception unused) {
            this.maxSsecondiAttesa = 30;
        }
        this.mOpenCvCameraView.setOnTouchListener(this);
        if (checkOrientation() == 180) {
            this.mOpenCvCameraView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        } else if (checkOrientation() == 0) {
            this.mOpenCvCameraView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        this.mOpenCvCameraView.enableView();
        avviaTimerSecPassati();
        System.gc();
    }
    /* access modifiers changed from: private */
    public void avviaTimerSecPassati() {
        this.timerSecPassati = new Timer();
        this.timerSecPassati.schedule(new TimerTask() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.this.secondiPassati = MainActivity.this.secondiPassati + 1;
                        if (MainActivity.this.secondiPassati == MainActivity.this.maxSsecondiAttesa) {
                            //MainActivity.this.passaAInsManualeSiNo();
                            Log.i(TAG,"PASSA A INSERIMENTO MANUALE");
                        }
                        if (AnalisiOcr.trovato == true) {
                            Toast.makeText(activity, mrz.getIdCarta(), Toast.LENGTH_SHORT).show();
                            /*Intent intent = new Intent(activity, nfcReader.class);
                            startActivity(intent);*/
                        }
                    }
                });
            }
        }, 0, 1000);
    }
    /*
    public void passaAInsManualeSiNo() {
        this.timerSecPassati.cancel();
        if (this.alert == null || !this.alert.isShowing()) {
            this.alert = new Builder(this).setTitle(R.string.app_name).setMessage("Non sono riuscito a leggere il documento. Vuoi passare all'inserimento manuale?").setIcon(17301543).setPositiveButton(17039379, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.beepAndExit(false, "inserimentoManuale");
                }
            }).setNegativeButton(17039369, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.secondiPassati = 0;
                    MainActivity.this.avviaTimerSecPassati();
                }
            }).show();
        }
    }
    */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.timerSecPassati != null) {
            this.timerSecPassati.cancel();
        }
        if (this.mOpenCvCameraView != null) {
            this.mOpenCvCameraView.disableView();
        }
        try {
            if (this.myTextRecognition.getStatus() != Status.FINISHED) {
                this.myTextRecognition.cancel(true);
            }
            this.mRgba.release();
            this.mGray.release();

            if (this.alert != null && this.alert.isShowing()) {
                this.alert.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    @Override
    public void onClick(View view) {
        if (view.getId() == this.scan_passaporto.getId()) {
            isPE = true;
            this.scan_passaporto.setVisibility(4);
            this.scan_card.setVisibility(0);
            riavviaCamera();
        } else if (view.getId() == this.scan_card.getId()) {
            isPE = false;
            this.scan_passaporto.setVisibility(0);
            this.scan_card.setVisibility(4);
            riavviaCamera();
        } else if (view.getId() == this.flash_icon.getId()) {
            this.status_flash = !this.status_flash;
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Status Flash > ");
            sb.append(this.status_flash);
            Log.v(str, sb.toString());
            if (this.status_flash) {
                this.flash_icon.setImageResource(R.drawable.icon_flash_on);
            } else {
                this.flash_icon.setImageResource(R.drawable.icon_flash_off);
            }
            this.mOpenCvCameraView.turnOnFlash(this.status_flash);
        } else if (view.getId() == R.id.btn_ok_esempio_scansione || view.getId() == R.id.btn_ok_esempio_scansionePiu) {
            if (view.getId() == R.id.btn_ok_esempio_scansionePiu) {
                PreferenzeFragment.setPref(PreferenzeFragment.ESEMPIO_MIRINO_KEY, Boolean.valueOf(false), getApplicationContext());
            }
            this.dialog_esempio_scansione.setVisibility(4);
            this.mOpenCvCameraView.enableView();
            avviaTimerSecPassati();
        } else if (view.getId() == this.imgInsManuale.getId()) {
            beepAndExit(false, "inserimentoManuale");
        } else if (view.getId() == this.parametri_view.getId()) {
            this.status_parametri = !this.status_parametri;
            if (this.status_parametri) {
                this.parametri.startAnimation(this.slide_parametri_visibile);
            } else {
                this.parametri.startAnimation(this.slide_parametri_invisibile);
            }
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("start_animation, status_parametri vale: ");
            sb2.append(this.status_parametri);
            Log.i(str2, sb2.toString());
        }
    }
*/
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent){
        Log.i(TAG,"onTouch event");
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/sample_picture_" + currentDateandTime + ".jpg";*/

        Mat mat = new Mat(frame,cropRect);
        if(REQUEST_TYPE == REAR_PIC_REQUEST){
            ShowPreviewActivity.fotoRetro = Bitmap.createBitmap(mat.cols(),  mat.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, ShowPreviewActivity.fotoRetro);
        } else {
            ShowPreviewActivity.fotoFronte = Bitmap.createBitmap(mat.cols(),  mat.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, ShowPreviewActivity.fotoFronte);
        }

        Intent intent = new Intent(this, ShowPreviewActivity.class);
        startActivity(intent);

        //mOpenCvCameraView.takePicture(fileName);
        //Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onCameraViewStarted(int i, int i2) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Larg: ");
        sb.append(i);
        sb.append("Alt: ");
        sb.append(i2);
        Log.d(str, sb.toString());
        this.mRgba = new Mat(i, i2, CvType.CV_8UC3);
        isFocusAreaInitialized = false;
        List resolutionList = this.mOpenCvCameraView.getResolutionList();
        if (resolutionList.size() > 0) {
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            Size myBestPreviewSize = this.mOpenCvCameraView.getMyBestPreviewSize(resolutionList, point.x, point.y);
            if (!(myBestPreviewSize == null || (myBestPreviewSize.width == i && myBestPreviewSize.height == i2))) {
                this.mOpenCvCameraView.setResolution(myBestPreviewSize);
            }
        }
        try {
            this.myTextRecognition = new TextRecognition();
            this.myTextRecognition.execute(new Map[0]);
        } catch (Exception e) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(" errore catturato ");
            sb2.append(e.getStackTrace()[0]);
            Log.e(str2, sb2.toString());
        }
    }

    @Override
    public void onCameraViewStopped() {
        this.mRgba.release();
        this.mGray.release();
        isFocusAreaInitialized = false;
    }


    @Override
    public Mat onCameraFrame(CvCameraViewFrame cvCameraViewFrame) {
        Rect rect;

        frame = cvCameraViewFrame.rgba().clone();

        this.mRgba = cvCameraViewFrame.rgba();

        /*if (!AnalisiOcr.trovato) {*/
            this.mGray = cvCameraViewFrame.gray();
            setMirino();
            HashMap hashMap = new HashMap();
            if (!isPE) {
                rect = new Rect(this.rectMirino.x + ((this.rectMirino.width - ((this.rectMirino.width * 19) / 20)) / 2), this.rectMirino.y + ((this.rectMirino.height - ((this.rectMirino.height * 19) / 20)) / 2), (this.rectMirino.width * 19) / 20, (this.rectMirino.height * 19) / 20);
            } else {
                rect = new Rect(this.rectMirino.x + ((this.rectMirino.width - ((this.rectMirino.width * 29) / 30)) / 2), this.rectMirino.y + ((this.rectMirino.height - ((this.rectMirino.height * 9) / 10)) / 2), (this.rectMirino.width * 29) / 30, (this.rectMirino.height * 9) / 10);
            }
            if (REQUEST_TYPE == REAR_PIC_REQUEST) {
                if (AnalisiOcr.containsChar) {
                    hashMap.put("matrice", this.mRgba);
                    hashMap.put("rect", rect);
                } else {
                    Rect rect2 = new Rect(rect.width / 2, rect.y, rect.width / 7, rect.height);
                    hashMap.put("matrice", this.mRgba);
                    hashMap.put("rect", rect2);
                }
                try {
                    if (this.myTextRecognition.getStatus() == Status.FINISHED) {
                        this.myTextRecognition = (TextRecognition) new TextRecognition().execute(new Map[]{hashMap});
                    }
                } catch (Exception e) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("  errore catturato ");
                    sb.append(e.getStackTrace()[0]);
                    Log.e(str, sb.toString());
                }

                if (AnalisiOcr.trovato && REQUEST_TYPE == REAR_PIC_REQUEST){
                    Mat mat = new Mat(frame,cropRect);
                    ShowPreviewActivity.fotoRetro = Bitmap.createBitmap(mat.cols(),  mat.rows(),Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(mat, ShowPreviewActivity.fotoRetro);

                    Intent intent = new Intent(this, ShowPreviewActivity.class);
                    startActivity(intent);
                }
            }


/*        } else {
            //beepAndExit(true, "avviaProgressione");
            Log.i(TAG, "BEEP avviaProgressione");
        }*/
        this.rectMirino = null;
        return this.mRgba;
    }
    public void setMirino() {

        if (!isPE) {
            int height = (this.mGray.height() * 6) / 10;
            int i = (int) (((float) height) * RATIO_CIE);
            int width = (this.mGray.width() / 2) - (i / 2);
            int height2 = (this.mGray.height() / 2) - (height / 2);
            Rect rect = new Rect(width, height2, i, height);
            Mat mat = this.mRgba;
            cropRect = rect;
            org.opencv.core.Point tl = rect.tl();
            org.opencv.core.Point br = rect.br();
            Scalar scalar = new Scalar(0.0d, 0.0d, 255.0d);
            Imgproc.rectangle(mat, tl, br, scalar, 2, 8, 0);
            int i2 = (int) (((double) height) / 2.7d);
            this.xMirino = width;
            this.yMirino = (height2 + height) - i2;
            this.rectMirino = new Rect(this.xMirino, this.yMirino, i, i2);
            Mat mat2 = this.mRgba;
            org.opencv.core.Point tl2 = this.rectMirino.tl();
            org.opencv.core.Point br2 = this.rectMirino.br();
            Scalar scalar2 = new Scalar(0.0d, 0.0d, 255.0d);
            Imgproc.rectangle(mat2, tl2, br2, scalar2, 2, 8, 0);

        } else if (isPE) {
            int height3 = (this.mGray.height() * 7) / 10;
            int i3 = (int) (((float) height3) * RATIO_PE);
            int width2 = (this.mGray.width() / 2) - (i3 / 2);
            int height4 = (this.mGray.height() / 2) - (height3 / 2);
            Rect rect2 = new Rect(width2, height4, i3, height3);
            Mat mat3 = this.mRgba;
            cropRect=rect2;
            org.opencv.core.Point tl3 = rect2.tl();
            org.opencv.core.Point br3 = rect2.br();
            Scalar scalar3 = new Scalar(0.0d, 0.0d, 255.0d);
            Imgproc.rectangle(mat3, tl3, br3, scalar3, 2, 8, 0);
            int i4 = (int) (((double) height3) / 3.79d);
            this.xMirino = width2;
            this.yMirino = (height4 + height3) - i4;
            this.rectMirino = new Rect(this.xMirino, this.yMirino, i3, i4);
            Mat mat4 = this.mRgba;
            org.opencv.core.Point tl4 = this.rectMirino.tl();
            org.opencv.core.Point br4 = this.rectMirino.br();
            Scalar scalar4 = new Scalar(0.0d, 0.0d, 255.0d);
            Imgproc.rectangle(mat4, tl4, br4, scalar4, 2, 8, 0);

        }
    }
    public void setFocusArea(Rect rect) {
        if (!isFocusAreaInitialized) {
            this.mOpenCvCameraView.submitFocusAreaRect(rect, this.mOpenCvCameraView.getResolution().width, this.mOpenCvCameraView.getResolution().height);
            isFocusAreaInitialized = true;
        }
    }
    public void checkAndDownloadFile() {
        fDirCache = getApplicationContext().getFilesDir();
        File file = new File(getApplicationContext().getFilesDir(), "tesseract/tessdata/");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, "ocrb.traineddata");
        try {
            if (!file2.exists()) {
                file2.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = null;
        try {
            inputStream = getApplicationContext().getAssets().open("ocrb.traineddata", 2);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        if (inputStream != null) {
            copyInputStreamToFile(inputStream, file2);
        }
    }
    private void copyInputStreamToFile(InputStream inputStream, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr);
                if (read > 0) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileOutputStream.close();
                    inputStream.close();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void riavviaCamera() {
        this.mOpenCvCameraView.disableView();
        isFocusAreaInitialized = false;
        this.mOpenCvCameraView.enableView();
    }
    public int checkOrientation() {
        int i;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        CameraInfo cameraInfo = new CameraInfo();
        int i2 = 0;
        Camera.getCameraInfo(0, cameraInfo);
        switch (rotation) {
            case 1:
                i2 = 90;
                break;
            case 2:
                i2 = 180;
                break;
            case 3:
                i2 = 270;
                break;
        }
        if (cameraInfo.facing == 1) {
            i = (360 - ((cameraInfo.orientation + i2) % 360)) % 360;
        } else {
            i = ((cameraInfo.orientation - i2) + 360) % 360;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onConfigurationChanged mrzscan: resultAngle: ");
        sb.append(i);
        Log.i(str, sb.toString());
        return i;
    }

}
