package com.transvargo.transvargo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.ListeOffreAction;
import com.transvargo.transvargo.http.behavior.ReservationAction;
import com.transvargo.transvargo.http.behavior.VehiculeListAction;
import com.transvargo.transvargo.model.Chargement;
import com.transvargo.transvargo.model.Offre;
import com.transvargo.transvargo.model.Vehicule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reservation extends MyActivityModel {

    TextView txt_reserv_typecamion;
    TextView txt_reserv_prix;
    TextView txt_reserv_distance;
    Spinner spi_reserv_list_vehicle;
    TextView txt_reserv_chauffeur;
    Button btn_reserv_accept;

    private Offre offre;
    private String immatriculation;
    ArrayList<Vehicule> listeVehicule;
    private Gson gson = new Gson();

    private ProgressDialog progressDialog;

    private ResponseHandler handler = new ResponseHandler() {
        @Override
        public <T extends Object> void doSomething(List<T> data) {
            listeVehicule = (ArrayList<Vehicule>) data;
            fillView();
            progressDialog.dismiss();
        }

        @Override
        public void error(int httpCode, VolleyError error) {

            progressDialog.dismiss();
            Toast.makeText(Reservation.this,"Impossible de se connecter à internet",Toast.LENGTH_SHORT).show();
        }
    };

    private ResponseHandler reservationHandler = new ResponseHandler() {
        @Override
        public void doSomething(Object data) {
            super.doSomething(data);

            JSONObject json = (JSONObject) data;

            try {
                Toast.makeText(Reservation.this, json.getString("message"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            goToMesChargements();
        }

        @Override
        public void error(int httpCode, VolleyError error) {

            progressDialog.dismiss();
            Toast.makeText(Reservation.this,"Impossible de se connecter à internet",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservation);

        this.txt_reserv_typecamion = (TextView) findViewById(R.id.txt_reserv_typecamion);
        this.txt_reserv_prix = (TextView) findViewById(R.id.txt_reserv_prix);
        this.txt_reserv_distance = (TextView) findViewById(R.id.txt_reserv_distance);
        this.txt_reserv_chauffeur = (TextView) findViewById(R.id.txt_reserv_chauffeur);

        this.spi_reserv_list_vehicle = (Spinner) findViewById(R.id.spi_reserv_list_vehicle);
        this.spi_reserv_list_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt_reserv_chauffeur.setText(listeVehicule.get(position).chauffeur);
                immatriculation = listeVehicule.get(position).immatriculation;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.btn_reserv_accept = (Button) findViewById(R.id.btn_reserv_accept);

        this.btn_reserv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Traitement en cours. Veuillez patienter SVP ...");
                ApiTransvargo api = new ApiTransvargo(Reservation.this);
                ReservationAction reservation = new ReservationAction(offre.reference, immatriculation, reservationHandler);
                api.executeHttpRequest(reservation);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("offre") != null)
        {
            this.offre = gson.fromJson(bundle.getString("offre"),Offre.class);
        }

        this.getRemoteListVehicle();
    }

    private void goToMesChargements()
    {
        progressDialog.dismiss();
        Intent intent = new Intent(Reservation.this, Chargements.class);
        startActivity(intent);
        Log.w("###Trans-API", "Mes chargements");
        finish();
    }

    private void getRemoteListVehicle()
    {
        this.showDialog("Récupération de la liste de vos véhicules. Veuillez patienter SVP ...");
        ApiTransvargo api = new ApiTransvargo(this.getApplicationContext());
        VehiculeListAction liste = new VehiculeListAction(offre, handler);
        api.executeHttpRequest(liste);
    }

    private void fillView()
    {
        this.txt_reserv_typecamion.setText(offre.typeCamion.libelle);
        this.txt_reserv_distance.setText(String.format("%s km",offre.distance));
        this.txt_reserv_prix.setText(String.format("%s FCFA",offre.prix));

        List<String> immatriculation = new ArrayList<>();
        for (int i = 0 ; i <= this.listeVehicule.size() - 1; i++)
        {
            immatriculation.add(i, this.listeVehicule.get(i).immatriculation);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Reservation.this,android.R.layout.simple_spinner_dropdown_item,immatriculation);
        this.spi_reserv_list_vehicle.setAdapter(adapter);

    }

    private void showDialog(String message)
    {
        this.progressDialog = ProgressDialog.show(Reservation.this, "", message, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("offre",this.gson.toJson(this.offre));
        outState.putString("vehicules",this.gson.toJson(this.listeVehicule.toArray()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.offre = gson.fromJson(savedInstanceState.getString("offre"),Offre.class);

        Vehicule[] vehicles = gson.fromJson(savedInstanceState.getString("vehicules"),Vehicule[].class);
        this.listeVehicule = new ArrayList<>(Arrays.asList(vehicles));

        fillView();
    }
}
