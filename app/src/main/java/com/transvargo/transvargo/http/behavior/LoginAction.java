package com.transvargo.transvargo.http.behavior;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.JsonObject;
import com.transvargo.transvargo.Boot;
import com.transvargo.transvargo.Principal;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.model.Identite;
import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.model.TypeCamion;
import com.transvargo.transvargo.model.Vehicule;
import com.transvargo.transvargo.processing.StoreCache;
import com.transvargo.transvargo.service.Tracking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 10/08/2017.
 */

public class LoginAction extends HttpRequest
{
    public ResponseHandler action;

    private String email;
    private String password;

    public LoginAction(String mEmail, String mPwd)
    {
        this.email = mEmail;
        this.password = mPwd;
    }

    @Override
    public JsonObjectRequest executeJsonObjectHttpRequest(Context context) {
        return this.makeRequest(context);
    }

    private JsonObjectRequest makeRequest(final Context context)
    {
        Log.w("#Trans-API#","begin request : "+ApiTransvargo.LOGIN_URL);

        JSONObject params  = new JSONObject();
        try {
            params.put("email",email);
            params.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(Request.Method.POST, ApiTransvargo.LOGIN_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("#Trans-API#",response.toString());

                        try {
                            JSONObject jIdentite = response.getJSONObject("data");
                            JSONObject jTransporteur = jIdentite.getJSONObject("transporteur");
                            String jwt = response.getString("token");

                            Vehicule vehicule = null;

                            try{
                                JSONObject jVehicule = response.getJSONObject("vehicule");
                                vehicule = new Vehicule();
                                vehicule.immatriculation = jVehicule.getString("immatriculation");
                                vehicule.id = jVehicule.getInt("id");
                                vehicule.chauffeur = jVehicule.getString("chauffeur");
                                vehicule.telephone = jVehicule.getString("telephone");
                                vehicule.typeCamion = new TypeCamion();
                                vehicule.typeCamion.id = jVehicule.getInt("typecamion_id");
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                            Transporteur transporteur = new Transporteur();
                            transporteur.jwt = jwt;
                            transporteur.nom = jTransporteur.getString("nom");
                            transporteur.prenoms = jTransporteur.getString("prenoms");
                            transporteur.raisonsociale = jTransporteur.getString("raisonsociale");
                            transporteur.contact = jTransporteur.getString("contact");
                            transporteur.comptecontribuable = jTransporteur.getString("comptecontribuable");
                            transporteur.ville = jTransporteur.getString("ville");
                            transporteur.nationalite = jTransporteur.getString("nationalite");
                            transporteur.datenaissance = jTransporteur.getString("datenaissance");
                            transporteur.lieunaissance = jTransporteur.getString("lieunaissance");
                            transporteur.rib = jTransporteur.getString("rib");
                            transporteur.datecreation = jTransporteur.getString("datecreation");
                            transporteur.typetransporteur_id = jTransporteur.getInt("typetransporteur_id");

                            Identite identite = new Identite();
                            identite.email = jIdentite.getString("email");
                            identite.id = jTransporteur.getInt("identiteaccess_id");
                            identite.statut = jIdentite.getInt("statut");

                            transporteur.identite = identite;
                            transporteur.vehicule = vehicule;

                            StoreCache.store(context,StoreCache.TRANSVARGO_TRANSPORTEUR,transporteur);

                            Toast.makeText(context,"Connexion réussie",Toast.LENGTH_SHORT).show();

                            Boot.setTransporteur(transporteur);

                            action.doSomething(null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //action.error(500, null);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("#Trans-API#",error.toString());
                        try {
                            if (error.networkResponse.statusCode == 403) {
                                Toast.makeText(context, "Login ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                            }
                            action.error(error.networkResponse.statusCode, error);
                        }catch (NullPointerException e){
                            action.error(0, error);
                            Toast.makeText(context,"Problème de connexion à internet ou au serveur.",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers  = new HashMap<>();

                headers.put("x-app-navigateur","app-android-transvargo");
                headers.put("Accept", "application/json");

                return headers ;
            }
        };
    }
}
