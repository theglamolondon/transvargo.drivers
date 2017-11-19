package com.transvargo.transvargo.http;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
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

    private static String url = "http://www.transvargo.com/public/api/";
    //private static String url = "http://192.168.43.147:8080/transvargo/public/api/";
    //private static String url = "http://soc-701hj12:8080/transvargo/public/api/";
    //private static String url = "http://192.168.1.24:8080/transvargo/public/api/";
    public static String LOGIN_URL = ApiTransvargo.url + "login";
    public static String LOGIN_REFRESH_TOKEN = ApiTransvargo.url + "refresh/token";
    public static String OFFRE_LISTE_URL = ApiTransvargo.url + "expeditions/offers/list";
    public static String VEHICULE_LISTE_URL = ApiTransvargo.url + "expeditions/transporteur/%s/vehicule/%s/list"; //1er %s => id transporteur & 2ème %s => categorie du véhicule
    public static String ACCEPT_OFFRE_URL = ApiTransvargo.url + "expeditions/offers/accept";
    public static String MY_EXPEDITIONS = ApiTransvargo.url + "%s/expeditions/list"; //1er %s => id transporteur
    public static String START_EXPEDITION = ApiTransvargo.url + "%s/chargement/start"; //1er %s => id transporteur & %s => reference de l'expedition
    public static String DELIVRY_EXPEDITION = ApiTransvargo.url + "%s/chargement/delivry"; //1er %s => id transporteur
    public static String FINISH_EXPEDITION = ApiTransvargo.url + "%s/chargement/finish"; //1er %s => id transporteur
    public static String LOCATION_GPS = ApiTransvargo.url + "gps/location/store";
    public static String FIREBASE_REFRESH_TOKEN = ApiTransvargo.url + "token/refresh";

    private ApiTransvargo mInstance;
    private RequestQueue mRequestQueue;

    private Context mContext;

    public ApiTransvargo(Context context)
    {
        this.mContext = context;
    }

    private RequestQueue getRequestQueue(){
        if(mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());

        return mRequestQueue;
    }

    public void executeHttpRequest(HttpRequest http)
    {
        JsonArrayRequest _rA = http.executeJsonArrayHttpRequest(mContext);
        JsonObjectRequest _rO = http.executeJsonObjectHttpRequest(mContext);

        if(_rA != null){
            _rA.setRetryPolicy(new DefaultRetryPolicy(120000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            getRequestQueue().add(_rA);
        }
        if(_rO != null) {
            _rO.setRetryPolicy(new DefaultRetryPolicy(120000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            getRequestQueue().add(_rO);
        }
        //getRequestQueue().start();  //Fais planter le processus avant son achèvement
    }
}
