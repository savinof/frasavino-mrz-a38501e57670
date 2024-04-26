package it.links.scanmrz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.links.scanmrz.data.LoginResponse;
import it.links.scanmrz.utilities.MySingleton;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    public static LoginResponse userData = new LoginResponse();;

    // Creating Progress dialog.
    ProgressDialog progressDialog;

    // Storing server url into String variable.
    String HttpUrl = "http://10.0.5.132:85/api/Login/obtainaccesstoken";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        // Set the scanner view as the content view
        setContentView(mScannerView);


        // Assigning Activity this to progress dialog.
        progressDialog = new ProgressDialog(QrCodeScannerActivity.this);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }


    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Prints scan results
        Log.i("qrcode", rawResult.getText());
        // Prints the scan format (qrcode, pdf417 etc.)
        Log.i("qrcode", rawResult.getBarcodeFormat().toString());
        //If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);

/*        Intent intent = new Intent(this, FrontPicActivity.class);
        intent.putExtra("QRCODE", rawResult.getText());
        setResult(RESULT_OK, intent);
        startActivity(intent);*/

        UserLogin(rawResult.getText());

        //finish();
    }


    // Creating user login function.
    public void UserLogin(final String qrCode) {

        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Matching server responce message to our text.
                        if(!ServerResponse.isEmpty()/*.equalsIgnoreCase("Data Matched")*/) {

                            // If response matched then show the toast.
                            Toast.makeText(QrCodeScannerActivity.this, "Logged In Successfully", Toast.LENGTH_LONG).show();
                            Log.i("RESPONSE", ServerResponse);

                            try {

                                JSONObject obj = new JSONObject(ServerResponse);

                                userData.setBearerToken(obj.getString("bearerToken"));
                                userData.setExpires(obj.getString(".expires"));
                                userData.setExpires_in(obj.getString("expires_in"));
                                userData.setIssued(obj.getString(".issued"));
                                userData.setName(obj.getString("name"));
                                userData.setToken_type(obj.getString("token_type"));
                                userData.setUserName(obj.getString("userName"));


                            } catch (Throwable t) {
                                Log.e("RESPONSE", "Could not parse malformed JSON: \"" + ServerResponse + "\"");
                            }

                            // Finish the current Login activity.
                            //finish();

                            // Opening the user profile activity using intent.
                            Intent intent = new Intent(QrCodeScannerActivity.this, FrontPicActivity.class);

                            startActivity(intent);
                        }
                        else {

                            // Showing Echo Response Message Coming From Server.
                            Toast.makeText(QrCodeScannerActivity.this, ServerResponse, Toast.LENGTH_LONG).show();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Showing error message if something goes wrong.
                        Toast.makeText(QrCodeScannerActivity.this, "QR-CODE NON VALIDO", Toast.LENGTH_LONG).show();
                        Log.i("RESPONSE", volleyError.toString());
                        NetworkResponse networkResponse = volleyError.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            Log.i("RESPONSE", jsonError);
                        }
                        onResume();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("qrCode", qrCode);
                return params;
            }
            /*            *//** Passing some request headers* *//*
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                //headers.put("apiKey", "xxxxxxxxxxxxxxx");
                return headers;
            }*/


        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}