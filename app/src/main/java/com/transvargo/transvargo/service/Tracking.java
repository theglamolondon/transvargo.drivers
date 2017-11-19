package com.transvargo.transvargo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.behavior.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 05/10/2017.
 */

public class Tracking extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Location","Sevice de localisation démarré");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        (new Runnable() {
            @Override
            public void run() {
                trackGPSLocation();
            }
        }).run();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service d'envoi de position GPS périodique arrêté", Toast.LENGTH_LONG).show();
    }

    private void trackGPSLocation()
    {
        LocationManager locationManager = (LocationManager)  getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.i("LocationChanged","Speed:"+location.getSpeed()+" Lat:"+location.getLatitude()+" Long:"+location.getLongitude());

                ApiTransvargo webApi = new ApiTransvargo(getBaseContext());

                final JSONObject data = new JSONObject();
                try{
                    data.put("latitude",location.getLatitude()+"");
                    data.put("longitude",location.getLongitude()+"");
                    data.put("speed",location.getSpeed()+"");
                    data.put("vehicule_id",Boot.getTransporteurConnecte().vehicule.id);
                }catch (JSONException e){
                    e.printStackTrace();
                }

                webApi.executeHttpRequest(new HttpRequest() {
                    @Override
                    public JsonObjectRequest executeJsonObjectHttpRequest(Context context) {
                        return new JsonObjectRequest(Request.Method.POST, ApiTransvargo.LOCATION_GPS, data, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("LocationUpdate",response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("LocationUpdate",error.getMessage());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers  = new HashMap<>();

                                headers.put("Authorization", "Bearer " + Boot.getTransporteurConnecte().jwt);
                                headers.put("Accept", "application/json");
                                headers.put("x-app-navigateur","app-android-transvargo");

                                return headers ;
                            }
                        };
                    }
                });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        try{
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 60000, 100,locationListener); //120000,200
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100,locationListener); //120000,200
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 100,locationListener); //120000,200
        }catch (SecurityException $e){
            Log.e("LocationError",$e.getMessage());
        }catch (NullPointerException $e){
            Log.e("LocationError",$e.getMessage());
        }
    }
}
