package it.links.scanmrz.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.links.scanmrz.FrontPicActivity;
import it.links.scanmrz.MainActivity;
import it.links.scanmrz.QrCodeScannerActivity;
import it.links.scanmrz.ShowPreviewActivity;

public class SendData {
    String url = "http://10.0.5.132:85/api/Censimento/censimento";
    //String url = "https://prova.free.beeceptor.com/prova";
    private Context context;
    private String token = "";
    //save the context recievied via constructor in a local variable

    public SendData(Context context){
        this.context=context;
        token = "Bearer " + QrCodeScannerActivity.userData.getBearerToken();
        MainActivity.mrz.setFotoFronte(ShowPreviewActivity.fotoFronte);
        MainActivity.mrz.setFotoRetro(ShowPreviewActivity.fotoRetro);
    }
    public void prova() {
        JSONObject params = new JSONObject();
        SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


        try {
            params.put("nome", MainActivity.mrz.getNome());
            params.put("cognome", MainActivity.mrz.getCognome());
            params.put("cf", MainActivity.mrz.getCf());
            params.put("numDoc", MainActivity.mrz.getIdCarta());

            params.put("dataDiNascita", dateFormat.format(formatter1.parse(MainActivity.mrz.getDataNascita())));
            params.put("dataDiScadenza", dateFormat.format(formatter1.parse(MainActivity.mrz.getDataScadenza())));

            params.put("cittadinanza", MainActivity.mrz.getCountry());
            params.put("tipoDocumento", MainActivity.mrz.getDocType());
            params.put("sesso", MainActivity.mrz.getSesso());
            params.put("luogoDiNascita", MainActivity.mrz.getLuogoDiNascita());
            params.put("residenza", MainActivity.mrz.getResidenza());
            params.put("authority", MainActivity.mrz.getAuthority());

            params.put("foto", getStringFromBitmap(MainActivity.mrz.getFoto()));
            params.put("fotoRetro", getStringFromBitmap(MainActivity.mrz.getFotoRetro()));
            if (!MainActivity.isPE) {
                params.put("fotoFronte", getStringFromBitmap(MainActivity.mrz.getFotoFronte()));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
           Log.e("RESPONSE", e.getMessage());
        }
//

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE", response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("RESPONSE", "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization ", token);
                return params;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }


    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 50;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public void send() {

        Gson gson = new Gson();
        final String json = gson.toJson(MainActivity.mrz);
        //Log.i("RESPONSE", json);
        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String  response) {
                if (response.length() !=0) {
                    Log.e("RESPONSE", response.toString());
                } else {
                    Log.e("RESPONSE", "Data Null");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE", "" + error);
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.i("RESPONSE", jsonError);
                }
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
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("nome", MainActivity.mrz.getNome());
                params.put("cognome", MainActivity.mrz.getCognome());
                params.put("cf", MainActivity.mrz.getCf());
                params.put("numDoc", MainActivity.mrz.getIdCarta());
                params.put("dataDiNascita", MainActivity.mrz.getDataNascita());
                params.put("dataDiScadenza", MainActivity.mrz.getDataScadenza());
                params.put("cittadinanza", MainActivity.mrz.getCountry());
                params.put("tipoDocumento", MainActivity.mrz.getDocType());
                params.put("sesso", MainActivity.mrz.getSesso());
                params.put("luogoDiNascita", MainActivity.mrz.getLuogoDiNascita());
                params.put("residenza", MainActivity.mrz.getResidenza());
                params.put("authority", MainActivity.mrz.getAuthority());
                //params.put("fotoFronte", getStringFromBitmap(MainActivity.mrz.getFotoFronte()));
                //params.put("fotoRetro", getStringFromBitmap(MainActivity.mrz.getFotoRetro()));
                return params;
            }
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
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
    private ArrayList<HashMap<String, String>> arraylist;

    public void uploadImage(final Bitmap bitmap){

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.d("ressssssoo",new String(response.data));
                        //rQueue.getCache().clear();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            jsonObject.toString().replace("\\\\","");

                            if (jsonObject.getString("status").equals("true")) {

                                arraylist = new ArrayList<HashMap<String, String>>();
                                JSONArray dataArray = jsonObject.getJSONArray("data");

                                String url = "";
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    url = dataobj.optString("pathToFile");
                                }
                                //Picasso.get().load(url).into(imageView);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // params.put("tags", "ccccc");  add string parameters
                return params;
            }

            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("filename", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public Bitmap compress(Bitmap bitmap) {

        Log.i("RESPONSE", " "+bitmap.getByteCount());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                /*
                    public boolean compress (Bitmap.CompressFormat format, int quality, OutputStream stream)
                        Write a compressed version of the bitmap to the specified outputstream.
                        If this returns true, the bitmap can be reconstructed by passing a
                        corresponding inputstream to BitmapFactory.decodeStream().

                        Note: not all Formats support all bitmap configs directly, so it is possible
                        that the returned bitmap from BitmapFactory could be in a different bitdepth,
                        and/or may have lost per-pixel alpha (e.g. JPEG only supports opaque pixels).

                        Parameters
                        format : The format of the compressed image
                        quality : Hint to the compressor, 0-100. 0 meaning compress for small size,
                            100 meaning compress for max quality. Some formats,
                            like PNG which is lossless, will ignore the quality setting
                        stream: The outputstream to write the compressed data.

                        Returns
                            true if successfully compressed to the specified stream.
                */

                /*
                    Bitmap.CompressFormat
                        Specifies the known formats a bitmap can be compressed into.

                            Bitmap.CompressFormat  JPEG
                            Bitmap.CompressFormat  PNG
                            Bitmap.CompressFormat  WEBP
                */
        // Compress the bitmap with JPEG format and quality 50%
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);

        byte[] byteArray = stream.toByteArray();

        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        Log.i("RESPONSE", ""+byteArray.length+ " "+bitmap.getByteCount()+" "+ compressedBitmap.getByteCount());
        return compressedBitmap;
    }
}
