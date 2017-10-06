package com.transvargo.transvargo.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
        Log.i("Location","Sevice Location démarré");
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


    public void trackGPSLocation()
    {
        LocationManager locationManager = (LocationManager)  getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("LocationChanged","Speed:"+location.getSpeed()+" Lat:"+location.getLatitude()+" Long:"+location.getLongitude());

                /*
                DepanV2WebApi webApi = new DepanV2WebApi(getBaseContext());
                webApi.storeGpsPosition(location.getLongitude()+"",location.getLatitude()+"",location.getSpeed()+""
                        ,android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                        */
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
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 45000, 0,locationListener); //120000,200
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 45000, 0,locationListener); //120000,200
        }catch (SecurityException $e){
            Log.i("LocationError",$e.getMessage());
        }
    }

}
