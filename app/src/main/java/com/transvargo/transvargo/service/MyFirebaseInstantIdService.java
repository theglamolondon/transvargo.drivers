package com.transvargo.transvargo.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by BW.KOFFI on 29/09/2017.
 */

public class MyFirebaseInstantIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d("###Trans-Firebase", "Refreshed token: " + refreshedToken);
    }
}
