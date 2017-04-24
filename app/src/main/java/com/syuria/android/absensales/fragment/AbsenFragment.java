package com.syuria.android.absensales.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.syuria.android.absensales.R;
import com.syuria.android.absensales.app.AppConfig;
import com.syuria.android.absensales.app.AppController;
import com.syuria.android.absensales.helper.GPSTracker;
import com.syuria.android.absensales.helper.LocationAddress;
import com.syuria.android.absensales.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.format;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by HP on 15/01/2017.
 */

public class AbsenFragment extends Fragment {
    private static final String TAG = AbsenFragment.class.getSimpleName();
    private View fragmentView;
    private GPSTracker gps;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private TextView textDepot,textToko,textTanggal, textAttendance;
    private ImageView imgLokasi, imgCamera;
    private String kode_sales, kode_toko, nama_toko, depot, address;
    private String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    private final int CAMERA_REQ = 27;
    private final String DISMIS = "DISMIS";
    private String mCurrentPhotoPath, jenisAbsen;
    private CameraPhoto cameraPhoto;
    private IntentIntegrator qrScan;
    private Button btnAbsenMasuk, btnAbsenPulang, btnSelfie;

    public AbsenFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_absen, container, false);

        textAttendance = (TextView) fragmentView.findViewById(R.id.textAttendance);

        textDepot = (TextView) fragmentView.findViewById(R.id.textDepot);
        textToko = (TextView) fragmentView.findViewById(R.id.textToko);
        textTanggal = (TextView) fragmentView.findViewById(R.id.textTanggal);

        db = new SQLiteHandler(getContext());
//        cameraPhoto = new CameraPhoto(getContext());

        HashMap<String, String> sales = db.getSalesDetails();
        kode_sales = sales.get("kode_sales");
        kode_toko = sales.get("kode_toko");
        nama_toko = sales.get("nama_toko");
        depot = sales.get("depot");

        textToko.setText(nama_toko);
        textDepot.setText(depot);
        textTanggal.setText(date);

        /*btnSelfie = (Button) fragmentView.findViewById(R.id.btnSelfie);
        btnSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cameraPhoto = new CameraPhoto(getContext());
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQ);
                    //mCurrentPhotoPath = cameraPhoto.getPhotoPath();
                } catch (IOException e) {
                    e.printStackTrace();
                    viewSnackBar(fragmentView, "CAMERA ERROR", DISMIS);
                }
            }
        });*/
        imgCamera = (ImageView) fragmentView.findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cameraPhoto = new CameraPhoto(getContext());
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQ);
                    //mCurrentPhotoPath = cameraPhoto.getPhotoPath();
                } catch (IOException e) {
                    e.printStackTrace();
                    viewSnackBar(fragmentView, "CAMERA ERROR", DISMIS);
                }
            }
        });

        imgLokasi = (ImageView) fragmentView.findViewById(R.id.imgLokasi);
        imgLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentPhotoPath != null) {
                    //Log.d(TAG, "onClick: Masuk GPS");
                    gps = new GPSTracker(getContext());
                    //Log.d(TAG, "onClick: Keluar gps");
                    // check if GPS enabled
                    if (gps.canGetLocation()) {
                        // SQLite database handler
                        //db = new SQLiteHandler(getContext());

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        final LocationAddress locationAddress = new LocationAddress();
                        locationAddress.getAddressFromLocation(latitude, longitude,
                                getContext(), new GeocoderHandler());


                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                }else {
                    viewSnackBar(fragmentView,"Mohon Selfie Dahulu",DISMIS);
                }
            }
        });

        btnAbsenMasuk = (Button) fragmentView.findViewById(R.id.btnAbsenMasuk);
        btnAbsenPulang = (Button) fragmentView.findViewById(R.id.btnAbsenPulang);

        btnAbsenMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                try {
                    Date inTime = sdf.parse("08:30");
                    Date CurrentTime = sdf.parse(sdf.format(new Date()));

                    if (CurrentTime.before(inTime)) {
                        salesAbsen("MASUK");
                    }else {
                        viewSnackBar(view,"Lewat batas waktu absen", DISMIS);
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        btnAbsenPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                try {
                    Date inTime = sdf.parse("16:30");
                    Date CurrentTime = sdf.parse(sdf.format(new Date()));

                    if (CurrentTime.after(inTime)) {
                        salesAbsen("PULANG");
                    }else {
                        viewSnackBar(view,"Belum masuk waktu pulang", DISMIS);
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return fragmentView;
    }

    private void salesAbsen(String jenisAbsen){
        if (mCurrentPhotoPath != null) {
            if(!textAttendance.getText().toString().isEmpty()) {
                this.jenisAbsen = jenisAbsen;
                qrScan = IntentIntegrator.forSupportFragment(AbsenFragment.this);
                qrScan.setPrompt("SCAN QR-CODE TOKO");
                qrScan.initiateScan();
            }else{
                viewSnackBar(fragmentView,"Klik Lokasi",DISMIS);
            }
        } else {
            viewSnackBar(fragmentView,"Mohon Selfie Dahulu",DISMIS);
        }
    }

    private void viewSnackBar(View view, String message, String action){
        Snackbar bar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //v.setText("You pressed Dismiss!!");
                    }
                });
        bar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        bar.show();
    }

    private void setNullSefie(){
        imgCamera.setImageDrawable(null);
        imgCamera.setBackgroundResource(R.drawable.ic_camera_alt_black_24dp);
        mCurrentPhotoPath = null;
        textAttendance.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQ && resultCode == RESULT_OK){
            //setPic();
            //cameraPhoto.addToGallery();
            mCurrentPhotoPath = cameraPhoto.getPhotoPath();
            if (mCurrentPhotoPath != null) {
                try {
                    Bitmap bitmap = ImageLoader.init().from(mCurrentPhotoPath).requestSize(256, 256).getBitmap();
                    imgCamera.setBackgroundResource(0);
                    imgCamera.setImageBitmap(bitmap); //imageView is your ImageView
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    viewSnackBar(fragmentView, "GAGAL MANGAMBIL FILE", DISMIS);
                }
            }else{
                viewSnackBar(fragmentView, "GAGAL MANGAMBIL FILE CAMERA", DISMIS);
            }
        }else if( requestCode == CAMERA_REQ && resultCode == RESULT_CANCELED) {
            File file = new File(cameraPhoto.getPhotoPath());
            boolean delete = file.delete(); Log.i("delete",String.valueOf(delete));
            setNullSefie();
        }else if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                viewSnackBar(fragmentView, "Result not found", DISMIS);
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    if(obj.getString("kode").equals("Nippon Paint")){
                        //HH converts hour in 24 hours format (0-23), day calculation
                        /*SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date_absen = format.format(new Date());
                        String date_time = obj.getString("date_time");
                        Date d1 = null;
                        Date d2 = null;
                        long beda = 0;
                        try {
                            d1 = format.parse(date_time);
                            d2 = format.parse(date_absen);
                            //in milliseconds
                            long diff = d2.getTime() - d1.getTime();
                            beda = diff / 1000;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (beda < 60 && beda > 0) {*/
                            String url = null;
                            try {
                                if (jenisAbsen == "MASUK") {
                                    url = AppConfig.URL_ABSEN_MASUK;
                                } else if (jenisAbsen == "PULANG") {
                                    url = AppConfig.URL_ABSEN_PULANG;
                                }
                                if (mCurrentPhotoPath != null) {
                                    Bitmap bitmap = ImageLoader.init().from(mCurrentPhotoPath).requestSize(256, 256).getBitmap();
                                    String encodeImage = ImageBase64.encode(bitmap);
                                    storeAttendance(kode_sales, textAttendance.getText().toString(), url, encodeImage);
                                }else {
                                    viewSnackBar(fragmentView, "GAGAL MANGAMBIL CAMERA QR", DISMIS);
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                viewSnackBar(fragmentView, "GAGAL MANGAMBIL FILE", DISMIS);
                            }
                        /*}else {
                            viewSnackBar(fragmentView, "QR CODE EXPIRED", DISMIS);
                        }*/
                    }else{
                        viewSnackBar(fragmentView, "KODE TIDAK COCOK", DISMIS);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    viewSnackBar(fragmentView, "QR CODE SALAH", DISMIS);
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    address = bundle.getString("address");
                    break;
                default:
                    address = null;
            }
            if(address.substring(0,5).equals("Tidak")){
                getAdress(gps.getLatitude(),gps.getLongitude());
            }else{
                textAttendance.setText(address);
            }
        }
    }

    private void getAdress(final double latitude, final double longitude) {
        // Tag used to cancel the request
        String tag_string_req = "req_lokasi";
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Mengambil Lokasi ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                        String.valueOf(latitude)+"," + String.valueOf(longitude)
                        + "&key=AIzaSyBRUzenhg7CScmMgZZidQOsqYOvRuFh0mg",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Attendance Response: " + response.toString());
                        hideDialog();

                        try {
                            //boolean error = jObj.getBoolean("error");
                            String status = new JSONObject(response).getString("status");
                            if (status.equals("OK")) {
                                JSONObject jObj = new JSONObject(response).getJSONArray("results").getJSONObject(0);
                                String alamat = jObj.getString("formatted_address");
                                textAttendance.setText(alamat);
                            } else {
                                // Error occurred in registration. Get the error
                                // message
                                //String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getContext(),
                                        status, Toast.LENGTH_LONG).show();
                                textAttendance.setText(status);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Atttendance Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        "Error Tidak bisa konek ke server "+error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void storeAttendance(final String uid, final String address, final String url, final String encodeImage) {
        // Tag used to cancel the request
        String tag_string_req = "req_attendance";
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Submiting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Attendance Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        setNullSefie();
                        viewSnackBar(fragmentView, "Absen Sukses", DISMIS);
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        viewSnackBar(fragmentView, errorMsg, DISMIS);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Atttendance Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        "Error Tidak bisa konek ke db "+error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                //params.put("kode_sales", uid);
                params.put("uid", uid);
                params.put("lokasi", address);
                params.put("photo",encodeImage);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
