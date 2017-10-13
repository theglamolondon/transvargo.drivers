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
import com.transvargo.transvargo.model.Chargement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 29/09/2017.
 */

public class DelivryChargement extends HttpRequest {

    private Chargement chargement;
    private String url;
    private ResponseHandler handler;

    public DelivryChargement(Chargement chargement, ResponseHandler handler)
    {
        this.chargement = chargement;
        this.handler = handler;
        this.url = String.format(ApiTransvargo.DELIVRY_EXPEDITION, Boot.getTransporteurConnecte().identite.id);
    }

    @Override
    public JsonObjectRequest executeJsonObjectHttpRequest(Context context) {
        Log.w("#Trans-API#","begin request : "+ this.url);

        JSONObject params  = new JSONObject();
        try {
            params.put("reference", this.chargement.expedition.reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JsonObjectRequest(Request.Method.POST, this.url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Trans-API", response.toString());
                handler.doSomething(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("#Trans-API#", error.toString());
                try {
                    handler.error(error.networkResponse != null ? error.networkResponse.statusCode : 0, error);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + Boot.getTransporteurConnecte().jwt);
                headers.put("x-app-navigateur", "app-android-transvargo");

                return headers;
            }
        };
    }
}
