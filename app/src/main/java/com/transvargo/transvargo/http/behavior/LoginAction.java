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
import com.transvargo.transvargo.Principal;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.model.Identite;
import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.processing.StoreCache;

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

                            Identite identite = new Identite();
                            identite.email = jIdentite.getString("email");
                            identite.id = jIdentite.getInt("typeidentite_id");
                            identite.statut = jIdentite.getInt("statut");

                            Transporteur transporteur = new Transporteur();
                            transporteur.identite = identite;
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

                            StoreCache.store(context,StoreCache.TRANSVARGO_TRANSPORTEUR,transporteur);

                            Toast.makeText(context,"Connexion réussie",Toast.LENGTH_SHORT).show();

                            action.doSomething(new ArrayList<>());

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        }catch (NullPointerException e){
                            Toast.makeText(context,"Problème de connexion à internet ou au serveur.",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers  = new HashMap<>();

                headers.put("x-app-navigateur","app-android-transvargo");

                return headers ;
            }
        };
    }
}
