package com.transvargo.transvargo;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.RefreshTokenAction;
import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.processing.StoreCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Created by BW.KOFFI on 13/08/2017.
 */

public class Boot extends Application {

    private static Transporteur transporteur;
    private ResponseHandler handler = new ResponseHandler() {
        @Override
        public void doSomething(Object data) {
            JSONObject token = (JSONObject) data;
            try {
                Log.e("###API token",token.getString("token"));
                transporteur.jwt = token.getString("token");
                StoreCache.store(Boot.this,StoreCache.TRANSVARGO_TRANSPORTEUR,transporteur);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void error(int httpCode, VolleyError error) {
            if(httpCode == 500){
                Toast.makeText(Boot.this, "Une erreur de connexion au serveeur eest survenue.", Toast.LENGTH_SHORT).show();
            }else if(httpCode == 403)
            {
                String response = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                try {
                    JSONObject objet = new JSONObject(response);
                    Toast.makeText(Boot.this, objet.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.error(httpCode, error);
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();

        //Souscription de au topic de Firebase "drivers"
        FirebaseMessaging.getInstance().subscribeToTopic("drivers");

        transporteur = StoreCache.getObject(this,StoreCache.TRANSVARGO_TRANSPORTEUR,Transporteur.class);

        this.refreshJWToken();
    }

    public static void setTransporteur(Transporteur transporteur){ Boot.transporteur  = transporteur; }

    public static Transporteur getTransporteurConnecte(){
        return transporteur;
    }

    private boolean refreshJWToken(){

        ApiTransvargo api = new ApiTransvargo(this);
        RefreshTokenAction refresh = new RefreshTokenAction(this.handler);
        api.executeHttpRequest(refresh);

        return true;
    }
}
