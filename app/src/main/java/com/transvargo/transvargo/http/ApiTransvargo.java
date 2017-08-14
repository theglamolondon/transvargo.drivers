package com.transvargo.transvargo.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.transvargo.transvargo.http.behavior.HttpRequest;

import org.json.JSONObject;

/**
 * Created by BW.KOFFI on 09/08/2017.
 */

public class ApiTransvargo {

    private static String url = "https://www.transvargo.com/public/api/";
    public static String LOGIN_URL = ApiTransvargo.url + "login";
    public static String OFFRE_LISTE_URL = ApiTransvargo.url + "expeditions/offers/list";

    private ApiTransvargo mInstance;
    private RequestQueue mRequestQueue;

    private Context mContext;

    public ApiTransvargo(Context context)
    {
        this.mContext = context;
    }

    private RequestQueue getRequestQueue(){
        if(mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mContext);

        return mRequestQueue;
    }

    public void executeHttpRequest(HttpRequest http)
    {
        JsonArrayRequest _rA = http.executeJsonArrayHttpRequest(mContext);
        JsonObjectRequest _rO = http.executeJsonObjectHttpRequest(mContext);

        if(_rA != null){ getRequestQueue().add(_rA); }

        if(_rO != null){ getRequestQueue().add(_rO); }

        getRequestQueue().start();
    }

    /*                             JsonObjectRequest request
    public void executeHttpRequest(JsonArrayRequest request)
    {
        getRequestQueue().add(request);
    }
    */
}
