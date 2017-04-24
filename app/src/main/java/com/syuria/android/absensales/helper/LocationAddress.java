package com.syuria.android.absensales.helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by HP on 23/11/2016.
 */

public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        //Log.d(TAG, "run: addr"+address);
                        //Log.d(TAG, "run: addrlength"+address.getMaxAddressLineIndex());
                        if (address.getMaxAddressLineIndex() > 0) {
                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                sb.append(address.getAddressLine(i)).append(", ");
                                //Log.d(TAG, "run: appand"+sb.toString());
                            }
                            //sb.append(address.getLocality()).append("\n");
                            //sb.append(address.getPostalCode()).append("\n");
                            //sb.append(address.getCountryName());
                            result = sb.toString();
                            result = result.substring(0, result.length() - 2);
                            //Log.d(TAG, "run: "+latitude+" "+longitude+" "+result);
                        }else {
                            result = "Tidak bisa mendeteksi lokasi."+
                                    "\nLatitude: " + latitude + " Longitude: " + longitude;
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        //result = "Latitude: " + latitude + " Longitude: " + longitude +
                        //        "\n\nAddress:\n" + result;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Tidak bisa mendeteksi lokasi."+
                                "\nLatitude: " + latitude + " Longitude: " + longitude;
                        bundle.putString("address", result);
                        bundle.putString("lat", String.valueOf(latitude));
                        bundle.putString("long", String.valueOf(longitude));
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
