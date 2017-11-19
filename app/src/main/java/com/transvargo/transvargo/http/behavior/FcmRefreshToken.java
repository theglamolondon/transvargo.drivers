package com.transvargo.transvargo.http.behavior;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.model.Transporteur;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 29/09/2017.
 */

public class FcmRefreshToken extends HttpRequest {

    private String token;
    private ResponseHandler handler;

    public FcmRefreshToken(String refreshToken)
    {
        this.token = refreshToken;
    }

    @Override
    public JsonObjectRequest executeJsonObjectHttpRequest(Context context)
    {
        //Displaying token on logcat
        Log.d("###Trans-Firebase", "Refreshed token: " + token);

        final JSONObject data = new JSONObject();
        try{
            if(Boot.getTransporteurConnecte() != null){
                if(Boot.getTransporteurConnecte().typetransporteur_id == Transporteur.CHAUFFEUR_FLOTTE)
                {
                    data.put("firebasetoken", token);
                    data.put("immatriculation", Boot.getTransporteurConnecte().vehicule.immatriculation);
                    data.put("id", Boot.getTransporteurConnecte().vehicule.id);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Boot.getTransporteurConnecte().jwt);
                headers.put("Accept", "application/json");
                headers.put("x-app-navigateur", "app-android-transvargo");
                return headers;
            }
        };
    }
}