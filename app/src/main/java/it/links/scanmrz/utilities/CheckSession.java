package it.links.scanmrz.utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import it.links.scanmrz.FrontPicActivity;
import it.links.scanmrz.QrCodeScannerActivity;

public class CheckSession {

    String url = "http://10.0.5.132:85/api/Login/checkSession";
    private Context context;
    private String token = "";
    //save the context recievied via constructor in a local variable
    public CheckSession(Context context){
        this.context=context;
        token = "Bearer " + QrCodeScannerActivity.userData.getBearerToken();
    }

    public void check() {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    Log.e("RESPONSE", response);
                } else {
                    Log.e("RESPONSE", "Data Null");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE", "" + error);

                Intent intent = new Intent(context, QrCodeScannerActivity.class);
                context.startActivity(intent);
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization ", token);
                return params;
            }

        };
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
