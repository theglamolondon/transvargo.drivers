package com.transvargo.transvargo.http.behavior;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 13/09/2017.
 */

public class RefreshTokenAction extends HttpRequest {

    private ResponseHandler handler;

    public RefreshTokenAction(ResponseHandler handler){
        this.handler = handler;
    }

    @Override
    public JsonObjectRequest executeJsonObjectHttpRequest(Context context) {
        return new JsonObjectRequest(Request.Method.GET, ApiTransvargo.LOGIN_REFRESH_TOKEN, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handler.doSomething(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               try{
                   int status = error.networkResponse != null ? error.networkResponse.statusCode : 0;
                    handler.error(status, error);
                }catch (NullPointerException e){
                   e.printStackTrace();
               }
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
