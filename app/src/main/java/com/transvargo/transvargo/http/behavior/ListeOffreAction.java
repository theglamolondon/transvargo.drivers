package com.transvargo.transvargo.http.behavior;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.model.Chargement;
import com.transvargo.transvargo.model.Client;
import com.transvargo.transvargo.model.Identite;
import com.transvargo.transvargo.model.Offre;
import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.model.TypeCamion;
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
 * Created by BW.KOFFI on 11/08/2017.
 */


public class ListeOffreAction extends HttpRequest {

    private ResponseHandler handler;
    private Transporteur transporteur;

    public ListeOffreAction(ResponseHandler handler)
    {
        this.handler = handler;
        this.transporteur = Boot.getTransporteurConnecte();
    }

    @Override
    public JsonArrayRequest executeJsonArrayHttpRequest(Context context) {
        return makeRequest(context);
    }

    private JsonArrayRequest makeRequest(final Context context) {
        Log.w("#Trans-API#", "begin request : " + ApiTransvargo.OFFRE_LISTE_URL);

        return new JsonArrayRequest(Request.Method.GET, ApiTransvargo.OFFRE_LISTE_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("#Trans-API#", response.toString());

                JSONObject rOffre = null;
                Offre offre = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                ArrayList<Offre> list = new ArrayList<>();

                for (int i = 0; i <= response.length() - 1; i++)
                {
                    try {
                        rOffre = response.getJSONObject(i);

                        //Expedition
                        offre = new Offre();

                        if (rOffre.getString("datechargement") != null) {
                            try {
                                offre.datechargement = dateFormat.parse(rOffre.getString("datechargement"));
                            } catch (ParseException e) {
                                offre.datechargement = new Date();
                                e.printStackTrace();
                            }
                        }
                        if (rOffre.getString("dateexpiration") != null) {
                            try {
                                offre.dateexpiration = dateFormat.parse(rOffre.getString("dateexpiration"));
                            } catch (ParseException e) {
                                offre.dateexpiration = new Date();
                                e.printStackTrace();
                            }
                        }
                        if (rOffre.getString("dateheurelivraison") != null) {
                            try {
                                offre.dateheurelivraison = dateFormat.parse(rOffre.getString("dateheurelivraison"));
                            } catch (ParseException e) {
                                offre.dateheurelivraison = new Date();
                                e.printStackTrace();
                            }
                        }
                        if (rOffre.getString("dateheureacceptation") != null) {
                            try {
                                offre.dateheureacceptation = dateFormat.parse(rOffre.getString("dateheureacceptation"));
                            } catch (ParseException e) {
                                offre.dateheureacceptation = new Date();
                                e.printStackTrace();
                            }
                        }

                        offre.id = rOffre.getInt("id");
                        offre.reference = rOffre.getString("reference");
                        offre.coordarrivee = rOffre.getString("coordarrivee");
                        offre.coorddepart = rOffre.getString("coorddepart");
                        offre.masse = rOffre.getLong("masse");
                        offre.fragile = rOffre.getBoolean("fragile");
                        Double rPrix = (Transporteur.pourcentage * rOffre.getInt("prix"));
                        offre.prix = rPrix.intValue();
                        offre.distance = rOffre.getInt("distance");
                        offre.lieudepart = rOffre.getString("lieudepart");
                        offre.lieuarrivee = rOffre.getString("lieuarrivee");
                        offre.statut = rOffre.getString("statut");

                        //Client
                        JSONObject rClient = rOffre.getJSONObject("client");
                        Client client = new Client();
                        client.nom = rClient.getString("nom");
                        client.prenoms = rClient.getString("prenoms");
                        client.contact = rClient.getString("contact");
                        client.raisonsociale = rClient.getString("raisonsociale");

                        //Chargements
                        JSONObject rChargement = rOffre.getJSONObject("chargement");
                        Chargement chargement = new Chargement();
                        chargement.id = rChargement.getInt("id");
                        chargement.societechargement = rChargement.getString("societechargement");
                        chargement.contactchargement = rChargement.getString("contactchargement");
                        chargement.telephonechargement = rChargement.getString("telephonechargement");
                        chargement.adressechargement = rChargement.getString("adressechargement");
                        chargement.contactlivraison = rChargement.getString("contactlivraison");
                        chargement.telephonelivraison = rChargement.getString("telephonelivraison");
                        chargement.societelivraison = rChargement.getString("societelivraison");
                        chargement.adresselivraison = rChargement.getString("adresselivraison");
                        if (rChargement.getString("dateheurechargement") != null) {
                            try {
                                chargement.dateheurechargement = dateFormat.parse(rChargement.getString("dateheurechargement"));
                            } catch (ParseException e) {
                                chargement.dateheurechargement =new Date();
                                e.printStackTrace();
                            }
                        }

                        //Camion
                        JSONObject rCamion = rOffre.getJSONObject("type_camion");
                        TypeCamion typeCamion = new TypeCamion();
                        typeCamion.id = rCamion.getInt("id");
                        typeCamion.libelle = rCamion.getString("libelle");


                        //Binding
                        offre.client = client;
                        offre.chargement = chargement;
                        offre.typeCamion = typeCamion;

                        //Add to collection
                        list.add(offre);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                handler.doSomething(list);
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + transporteur.jwt);
                headers.put("x-app-navigateur", "app-android-transvargo");

                return headers;
            }
        };
    }
}