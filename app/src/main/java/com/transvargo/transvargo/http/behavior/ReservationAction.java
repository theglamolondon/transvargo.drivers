package com.transvargo.transvargo.http.behavior;

import android.content.Context;
import android.support.annotation.UiThread;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 15/08/2017.
 */

public class ReservationAction extends HttpRequest {

    ResponseHandler handler;

    private String immatriculation;
    private String reference;

    public ReservationAction(String reference, String immatriculation, ResponseHandler handler){
        this.immatriculation = immatriculation;
        this.reference = reference;
        this.handler = handler;
    }

    @Override
    public JsonObjectRequest executeJsonObjectHttpRequest(Context context) {
        return makeRequest();
    }

    private JsonObjectRequest makeRequest(){

        Log.w("#Trans-API#","begin request : "+ApiTransvargo.ACCEPT_OFFRE_URL);

        JSONObject params  = new JSONObject();
        try {
            params.put("immatriculation",this.immatriculation);
            params.put("reference",this.reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(Request.Method.POST, ApiTransvargo.ACCEPT_OFFRE_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("###Trans-API",response.toString());
                handler.doSomething(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                handler.error(error.networkResponse != null ? error.networkResponse.statusCode : 0, error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers  = new HashMap<>();

                headers.put("Authorization", "Bearer " + Boot.getTransporteurConnecte().jwt);
                headers.put("x-app-navigateur","app-android-transvargo");

                return headers ;
            }
        };
    }
}