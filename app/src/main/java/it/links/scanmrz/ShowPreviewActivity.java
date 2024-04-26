package it.links.scanmrz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import it.links.scanmrz.utilities.CheckSession;

public class ShowPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static Bitmap fotoRetro;
    public static Bitmap fotoFronte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_preview);

        CheckSession checkSession = new CheckSession(getApplicationContext());
        checkSession.check();

        Button btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        Button bntRetake = findViewById(R.id.btnRetake);
        bntRetake.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView fotoPreview = findViewById(R.id.imagePreview);
        if (MainActivity.REQUEST_TYPE == MainActivity.REAR_PIC_REQUEST){
            fotoPreview.setImageBitmap(fotoRetro);
            //MainActivity.mrz.setFotoRetro(fotoRetro);
        } else {
            fotoPreview.setImageBitmap(fotoFronte);
        }
    }


    @Override
    public void onClick(View view) {
        // Perform action on click
        Intent intent;
        switch(view.getId()) {
            case R.id.btnOk:
                if (MainActivity.REQUEST_TYPE == MainActivity.FRONT_PIC_REQUEST){
                    MainActivity.REQUEST_TYPE = MainActivity.REAR_PIC_REQUEST;
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else if (AnalisiOcr.trovato && MainActivity.REQUEST_TYPE == MainActivity.REAR_PIC_REQUEST){
                    intent = new Intent(this, nfcReader.class);
                    startActivity(intent);
                } else if (!AnalisiOcr.trovato && MainActivity.REQUEST_TYPE == MainActivity.REAR_PIC_REQUEST){
                    Log.e("ERROREMRZ", "MRZ NON TROVATO");
                }
                Log.e("ERROREMRZ", "CHE SUCCEDE QUI?");
                break;
            case R.id.btnRetake:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
