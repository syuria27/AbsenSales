package com.syuria.android.absensales.fragment;

/**
 * Created by HP on 01/01/2017.
 */
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.syuria.android.absensales.R;
import com.syuria.android.absensales.adapter.AbsenAdapter;
import com.syuria.android.absensales.app.AppConfig;
import com.syuria.android.absensales.app.AppController;
import com.syuria.android.absensales.helper.SQLiteHandler;
import com.syuria.android.absensales.model.Absen;
import com.syuria.android.absensales.utils.DateDisplayUtils;
import com.syuria.android.absensales.widget.SimpleDatePickerDialog;
import com.syuria.android.absensales.widget.SimpleDatePickerDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class HistoryFragment extends Fragment implements SimpleDatePickerDialog.OnDateSetListener {
    private View fragmentView;
    private ImageView imgTanggal;
    private TextView txtTanggal;
    private ListView lvAbsenReport;
    private ProgressDialog pDialog;
    private List<Absen> absenList;
    private SQLiteHandler db;
    private String kode_sales;
    private Button btnCariMasuk, btnCariPulang;
    static final String DISMIS = "DISMIS";

    public HistoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_history, container, false);
        // SQLite database handler
        db = new SQLiteHandler(getContext());
        // Fetching user details from sqlite
        HashMap<String, String> sales = db.getSalesDetails();
        kode_sales = sales.get("kode_sales");

        txtTanggal = (TextView) fragmentView.findViewById(R.id.txtTanggal);
        imgTanggal = (ImageView) fragmentView.findViewById(R.id.imgTanggal);
        imgTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySimpleDatePickerDialogFragment();
            }
        });

        lvAbsenReport = (ListView) fragmentView.findViewById(R.id.lvHistoryAbsen);

        btnCariMasuk = (Button) fragmentView.findViewById(R.id.btnCariMasuk);
        btnCariMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtTanggal.getText().equals("TANGGAL")) {
                    viewSnackBar(view,"Masukan Tanggal",DISMIS);
                }else {
                    getHistory("masuk");
                }
            }
        });

        btnCariPulang = (Button) fragmentView.findViewById(R.id.btnCarinPulang);
        btnCariPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtTanggal.getText().equals("TANGGAL")) {
                    viewSnackBar(view,"Masukan Tanggal",DISMIS);
                }else {
                    getHistory("pulang");
                }
            }
        });

        return fragmentView;
    }

    private void viewSnackBar(View view, String message, String action){
        Snackbar bar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //v.setText("You pressed Dismiss!!");
                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bar.setActionTextColor(getActivity().getResources().getColor(R.color.colorAccent, getActivity().getTheme()));
        }else {
            bar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        }
        bar.show();
    }

    private void getHistory(String flag){
        String bulanTahun = txtTanggal.getText().toString();
        int posSpace = bulanTahun.indexOf(" ");
        int tahun = Integer.parseInt(bulanTahun.substring(posSpace+1, posSpace+5));
        String bulan = bulanTahun.substring(0,posSpace);
        int bulani = getMonthNumber(bulan);
        getHistoryAbsen(kode_sales,bulani,tahun,flag);
    }

    @Override
    public void onDateSet(int year, int monthOfYear) {
        txtTanggal.setText(DateDisplayUtils.formatMonthYear(year, monthOfYear));
    }

    private void displaySimpleDatePickerDialogFragment() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        datePickerDialogFragment.setOnDateSetListener(this);
        datePickerDialogFragment.show(getChildFragmentManager(), null);
    }

    public static int getMonthNumber(String month) {
        int monthNumber = 0;
        if (month == null) {
            return monthNumber;
        }

        switch (month) {
            case "JANUARY":
                monthNumber = 1;
                break;
            case "JANUARI":
                monthNumber = 1;
                break;
            case "FEBRUARY":
                monthNumber = 2;
                break;
            case "FEBRUARI":
                monthNumber = 2;
                break;
            case "MARCH":
                monthNumber = 3;
                break;
            case "MARET":
                monthNumber = 3;
                break;
            case "APRIL":
                monthNumber = 4;
                break;
            case "MAY":
                monthNumber = 5;
                break;
            case "MEI":
                monthNumber = 5;
                break;
            case "JUNE":
                monthNumber = 6;
                break;
            case "JUNI":
                monthNumber = 6;
                break;
            case "JULY":
                monthNumber = 7;
                break;
            case "JULI":
                monthNumber = 7;
                break;
            case "AUGUST":
                monthNumber = 8;
                break;
            case "AGUSTUS":
                monthNumber = 8;
                break;
            case "SEPTEMBER":
                monthNumber = 9;
                break;
            case "OCTOBER":
                monthNumber = 10;
                break;
            case "OKTOBER":
                monthNumber = 10;
                break;
            case "NOVEMBER":
                monthNumber = 11;
                break;
            case "DECEMBER":
                monthNumber = 12;
                break;
            case "DESEMBER":
                monthNumber = 12;
                break;
            default:
                monthNumber = 0;
                break;
        }
        return monthNumber;
    }

    public void getHistoryAbsen(final String kode_sales, final int bulan, final int tahun, final String flag ){
        String tag_string_req = "req_get_absen_report";
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Get Data ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_ABSEN+"/"+flag+"/"+kode_sales+"/"+bulan+"/"+tahun, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get data Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        absenList = new ArrayList<Absen>();
                        JSONArray data = jObj.getJSONArray("history");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject objData = data.getJSONObject(i);
                            if(objData.getString("jam_"+flag).equals("null")){
                            }else {
                                Absen absen = new Absen();
                                absen.setKode_absen(objData.getString("kode_absen"));
                                absen.setTanggal(objData.getString("tanggal"));
                                absen.setJam(objData.getString("jam_" + flag));
                                absen.setLokasi(objData.getString("lokasi_" + flag));
                                absenList.add(absen);
                            }
                        }
                        if (absenList.size() > 0)
                            lvAbsenReport.setAdapter(new AbsenAdapter(getContext(), R.layout.lv_history,absenList));
                        else{
                            lvAbsenReport.setAdapter(null);
                            viewSnackBar(fragmentView,"Data Kosong",DISMIS);
                        }

                    } else {
                        lvAbsenReport.setAdapter(null);
                        String errorMsg = jObj.getString("error_msg");
                        viewSnackBar(fragmentView,errorMsg,DISMIS);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseBody = new String( error.networkResponse.data, "utf-8" );
                    JSONObject jsonObject = new JSONObject( responseBody );
                    viewSnackBar(fragmentView,jsonObject.getString("error_msg"),"DISMIS");
                } catch ( JSONException e ) {
                    viewSnackBar(fragmentView,"Connection fail..","DISMIS");
                } catch (UnsupportedEncodingException ue_error){
                    viewSnackBar(fragmentView,"Connection fail..","DISMIS");
                } catch (Exception e){
                    viewSnackBar(fragmentView,"Connection fail..","DISMIS");
                }
                lvAbsenReport.setAdapter(null);
                hideDialog();
            }
        });
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
