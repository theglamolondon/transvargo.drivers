package com.transvargo.transvargo.http.behavior;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.model.Chargement;
import com.transvargo.transvargo.model.Client;
import com.transvargo.transvargo.model.Offre;
import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.model.Vehicule;
import com.transvargo.transvargo.processing.StoreCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 17/09/2017.
 */

public class ListeExpeditionsAction extends HttpRequest {

    private ResponseHandler handler;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ListeExpeditionsAction(ResponseHandler handler)
    {
        this.handler = handler;
    }

    public JsonArrayRequest executeJsonArrayHttpRequest(final Context context)
    {
        Log.w("#Trans-API#", "begin request : " + String.format(ApiTransvargo.MY_EXPEDITIONS, Boot.getTransporteurConnecte().identite.id));

        return new JsonArrayRequest(Request.Method.GET, String.format(ApiTransvargo.MY_EXPEDITIONS, Boot.getTransporteurConnecte().identite.id), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("#Trans-API#", response.toString());

                StoreCache.store(context,StoreCache.TRANSVARGO_MY_CHARGEMENTS, response.toString());

                handler.doSomething(processResponse(response));
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
                public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + Boot.getTransporteurConnecte().jwt);
                headers.put("x-app-navigateur", "app-android-transvargo");

                return headers;
            }
        };
    }

    private ArrayList<Chargement> processResponse(JSONArray response)
    {
        JSONObject rChargement ;
        Chargement chargement ;

        ArrayList<Chargement> list = new ArrayList<>();

        for (int i = 0; i <= response.length() - 1; i++)
        {
            chargement = new Chargement();

            try {
                rChargement = response.getJSONObject(i);
                chargement.id = rChargement.getInt("id");

                if(rChargement.getString("dateheurechargement") != null)
                {
                    try {
                        chargement.dateheurechargement = dateFormat.parse(rChargement.getString("dateheurechargement"));
                    } catch (ParseException e) {
                                chargement.dateheurechargement = new Date();
                        e.printStackTrace();
                    }
                }else {
                    chargement.dateheurechargement = new Date();
                }

                chargement.adresselivraison = rChargement.getString("adresselivraison");
                chargement.adressechargement = rChargement.getString("adressechargement");
                chargement.societechargement = rChargement.getString("societechargement");
                chargement.contactchargement = rChargement.getString("contactchargement");
                chargement.adresselivraison = rChargement.getString("adresselivraison");
                chargement.societelivraison = rChargement.getString("societelivraison");

                chargement.vehicule = getVehiculeFromJSON(rChargement.getJSONObject("vehicule"));
                chargement.expedition = getExpeditionFromJSON(rChargement.getJSONObject("expedition"));

                list.add(chargement);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return list;
    }

    private Vehicule getVehiculeFromJSON(JSONObject rVehicule)
    {
        Vehicule vehicule = new Vehicule();

        try{
            vehicule.id = rVehicule.getInt("id");
            vehicule.immatriculation = rVehicule.getString("immatriculation");
            vehicule.telephone = rVehicule.getString("telephone");
            vehicule.chauffeur = rVehicule.getString("chauffeur");

        }catch (JSONException e){
            e.printStackTrace();
        }

        return  vehicule;
    }

    private Offre getExpeditionFromJSON(JSONObject rExpedition)
    {
        Offre offre = new Offre();

        try {
            if (rExpedition.getString("datechargement") != null) {
                try {
                    offre.datechargement = dateFormat.parse(rExpedition.getString("datechargement"));
                } catch (ParseException e) {
                    offre.datechargement = new Date();
                    e.printStackTrace();
                }
            }
            if (rExpedition.getString("dateexpiration") != null) {
                try {
                    offre.dateexpiration = dateFormat.parse(rExpedition.getString("dateexpiration"));
                } catch (ParseException e) {
                    offre.dateexpiration = new Date();
                    e.printStackTrace();
                }
            }
            if (rExpedition.getString("dateheurelivraison") != null) {
                try {
                    offre.dateheurelivraison = dateFormat.parse(rExpedition.getString("dateheurelivraison"));
                } catch (ParseException e) {
                    offre.dateheurelivraison = new Date();
                    e.printStackTrace();
                }
            }
            if (rExpedition.getString("dateheureacceptation") != null) {
                try {
                    offre.dateheureacceptation = dateFormat.parse(rExpedition.getString("dateheureacceptation"));
                } catch (ParseException e) {
                    offre.dateheureacceptation = new Date();
                    e.printStackTrace();
                }
            }

            offre.id = rExpedition.getInt("id");
            offre.reference = rExpedition.getString("reference");
            offre.coordarrivee = rExpedition.getString("coordarrivee");
            offre.coorddepart = rExpedition.getString("coorddepart");
            offre.masse = rExpedition.getLong("masse");
            offre.fragile = rExpedition.getBoolean("fragile");

            Double rPrix = (Transporteur.pourcentage * rExpedition.getInt("prix"));
            offre.prix = rPrix.intValue();

            offre.distance = rExpedition.getInt("distance");
            offre.lieudepart = rExpedition.getString("lieudepart");
            offre.lieuarrivee = rExpedition.getString("lieuarrivee");
            offre.statut = rExpedition.getString("statut");

            //Client
            JSONObject rClient = rExpedition.getJSONObject("client");
            Client client = new Client();
            client.nom = rClient.getString("nom");
            client.prenoms = rClient.getString("prenoms");
            client.contact = rClient.getString("contact");
            client.raisonsociale = rClient.getString("raisonsociale");

            offre.client = client;

        }catch (JSONException e){
            e.printStackTrace();
        }

        return offre;
    }
}
