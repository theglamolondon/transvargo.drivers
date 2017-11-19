package com.transvargo.transvargo.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.behavior.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

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

        final JSONObject data = new JSONObject();
        try{
            data.put("firebasetoken", refreshedToken);
            data.put("immatriculation", Boot.getTransporteurConnecte().vehicule.immatriculation);
            data.put("id", Boot.getTransporteurConnecte().vehicule.id);
        }catch (JSONException e){
            e.printStackTrace();
        }

        ApiTransvargo api = new ApiTransvargo(getBaseContext());
        api.executeHttpRequest(new HttpRequest() {
            @Override
            public JsonObjectRequest executeJsonObjectHttpRequest(Context context) {
                return new JsonObjectRequest(Request.Method.POST, ApiTransvargo.FIREBASE_REFRESH_TOKEN, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("FireBase Refresh", response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
            }
        });

    }
}
