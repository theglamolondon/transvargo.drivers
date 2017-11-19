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
import com.transvargo.transvargo.model.Offre;
import com.transvargo.transvargo.model.TypeCamion;
import com.transvargo.transvargo.model.Vehicule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 16/08/2017.
 */

public class VehiculeListAction extends HttpRequest {

    private ResponseHandler handler;
    private Offre offre;

    public VehiculeListAction(Offre offre, ResponseHandler handler)
    {
        this.offre = offre;
        this.handler = handler;
    }

    @Override
    public JsonArrayRequest executeJsonArrayHttpRequest(Context context) {

        String url = String.format(ApiTransvargo.VEHICULE_LISTE_URL, Boot.getTransporteurConnecte().identite.id, offre.typeCamion.id);
        Log.w("#Trans-API#","begin request : "+ url);


        return new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.i("#Trans-API#", response.toString());

                JSONObject rVehicule = null;
                JSONObject rTypeCamion = null;

                Vehicule vehicule = null;
                TypeCamion typeCamion = null;

                ArrayList<Vehicule> list = new ArrayList<>();

                for (int i = 0; i <= response.length() - 1; i++)
                {
                    try {
                        rVehicule = response.getJSONObject(i);

                        vehicule = new Vehicule();
                        vehicule.id = rVehicule.getInt("id");
                        vehicule.chauffeur = rVehicule.getString("chauffeur");
                        vehicule.immatriculation = rVehicule.getString("immatriculation");
                        vehicule.telephone = rVehicule.getString("telephone");

                        //Type de camion
                        rTypeCamion = rVehicule.getJSONObject("type_camion");
                        typeCamion = new TypeCamion();
                        typeCamion.id = rTypeCamion.getInt("id");
                        typeCamion.libelle = rTypeCamion.getString("libelle");

                        vehicule.typeCamion = typeCamion;

                        //Add to list
                        list.add(vehicule);

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
                handler.error( (error.networkResponse != null ? error.networkResponse.statusCode : 0), error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers  = new HashMap<>();

                headers.put("Authorization", "Bearer " + Boot.getTransporteurConnecte().jwt);
                headers.put("Accept", "application/json");
                headers.put("x-app-navigateur","app-android-transvargo");

                return headers ;
            }
        };
    }

}
