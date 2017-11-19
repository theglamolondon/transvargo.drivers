package com.transvargo.transvargo.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.FcmRefreshToken;
import com.transvargo.transvargo.http.behavior.HttpRequest;
import com.transvargo.transvargo.model.Transporteur;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 29/09/2017.
 */

public class MyFirebaseInstantIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh()
    {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d("###Trans-Firebase", "Refreshed token: " + refreshedToken);

        ApiTransvargo api = new ApiTransvargo(getBaseContext());
        api.executeHttpRequest(new FcmRefreshToken(refreshedToken));
    }
}