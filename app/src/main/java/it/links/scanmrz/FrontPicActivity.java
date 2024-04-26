package it.links.scanmrz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import it.links.scanmrz.utilities.CheckSession;

public class FrontPicActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_pic);

        CheckSession checkSession = new CheckSession(getApplicationContext());
        checkSession.check();

        Button btnCie = findViewById(R.id.btnCie);
        btnCie.setOnClickListener(this);
        Button btnPe = findViewById(R.id.btnPe);
        btnPe.setOnClickListener(this);

        String message = getIntent().getStringExtra("QRCODE");
        Log.i("RESPONSE", QrCodeScannerActivity.userData.getName());

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        // Perform action on click
        Intent intent;
        switch(view.getId()) {
            case R.id.btnCie:

                MainActivity.isPE = false;
                MainActivity.REQUEST_TYPE = MainActivity.FRONT_PIC_REQUEST;
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btnPe:

                MainActivity.isPE = true;
                MainActivity.REQUEST_TYPE = MainActivity.REAR_PIC_REQUEST;
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
