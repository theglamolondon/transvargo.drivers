package com.transvargo.transvargo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.FcmRefreshToken;
import com.transvargo.transvargo.http.behavior.RefreshTokenAction;
import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.processing.StoreCache;
import com.transvargo.transvargo.service.Tracking;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Created by BW.KOFFI on 13/08/2017.
 */

public class Boot extends Application
{
    private static Transporteur transporteur;

    private ResponseHandler handler = new ResponseHandler()
    {
        @Override
        public void doSomething(Object data) {
            JSONObject token = (JSONObject) data;
            try {
                Log.e("###API token",token.getString("token"));
                if(transporteur != null) {
                    transporteur.jwt = token.getString("token");
                    StoreCache.store(Boot.this, StoreCache.TRANSVARGO_TRANSPORTEUR, transporteur);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void error(int httpCode, VolleyError error)
        {
            if(httpCode == 500){
                Toast.makeText(Boot.this, "Une erreur de connexion au serveur est survenue.", Toast.LENGTH_SHORT).show();
            }else if(httpCode == 403){
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
    public void onCreate()
    {
        super.onCreate();
        transporteur = StoreCache.getObject(this,StoreCache.TRANSVARGO_TRANSPORTEUR,Transporteur.class);

        if(transporteur != null)
        {
            this.refreshJWToken();

            if(transporteur.vehicule != null)
            {
                //Démarrage du service de géolocalisation
                startService(new Intent(this, Tracking.class));
            }

            if(transporteur.typetransporteur_id == Transporteur.CHAFFEUR_PATRON || transporteur.typetransporteur_id == Transporteur.PROPRIETAIRE_FLOTTE)
            {
                //Souscription au topic de Firebase "drivers"
                FirebaseMessaging.getInstance().subscribeToTopic(transporteur.identite.email.replace("@","#"));
            }else{
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.e("###FireBase", token);
                this.sendLastFcmToken(token);
            }
        }
    }

    public static void setTransporteur(Transporteur transporteur){ Boot.transporteur  = transporteur; }

    public static Transporteur getTransporteurConnecte(){
        return transporteur;
    }

    private void refreshJWToken()
    {
        ApiTransvargo api = new ApiTransvargo(this);
        RefreshTokenAction refresh = new RefreshTokenAction(this.handler);
        api.executeHttpRequest(refresh);
    }

    private void sendLastFcmToken(String token)
    {
        ApiTransvargo api = new ApiTransvargo(getBaseContext());
        api.executeHttpRequest(new FcmRefreshToken(token));
    }
}