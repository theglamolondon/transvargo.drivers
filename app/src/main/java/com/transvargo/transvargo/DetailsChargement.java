package com.transvargo.transvargo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.transvargo.transvargo.model.Chargement;
import com.transvargo.transvargo.model.Offre;

public class DetailsChargement extends AppCompatActivity {

    MapView mapView;
    GoogleMap map;
    Chargement chargement;

    Button btn_call;
    Button btn_demarrer;
    TextView txt_dtls_chgmt_expediteur;
    TextView txt_dtls_chgmt_societe;
    TextView txt_dtls_chgmt_masse;
    TextView txt_dtls_chgmt_fragile;
    TextView txt_dtls_chgmt_distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_chargement);

        this.mapView = (MapView) findViewById(R.id.mapView);
        this.mapView.onCreate(savedInstanceState);

        this.btn_call = (Button) findViewById(R.id.btn_dtls_chgmt_call) ;
        this.btn_demarrer = (Button) findViewById(R.id.btn_dtls_chgmt_demarrer) ;
        this.txt_dtls_chgmt_expediteur = (TextView) findViewById(R.id.txt_dtls_chgmt_expediteur);
        this.txt_dtls_chgmt_societe = (TextView) findViewById(R.id.txt_dtls_chgmt_societe);
        this.txt_dtls_chgmt_masse = (TextView) findViewById(R.id.txt_dtls_chgmt_masse);
        this.txt_dtls_chgmt_fragile = (TextView) findViewById(R.id.txt_dtls_chgmt_fragile);
        this.txt_dtls_chgmt_distance = (TextView) findViewById(R.id.txt_dtls_chgmt_distance);


        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("chargement") != null)
        {
            Gson gson = new Gson();
            this.chargement = gson.fromJson(bundle.getString("chargement"),Chargement.class);

            this.txt_dtls_chgmt_expediteur.setText(this.chargement.expedition.client.nom+" "+this.chargement.expedition.client.prenoms);
            this.txt_dtls_chgmt_societe.setText(this.chargement.societechargement);
            this.txt_dtls_chgmt_masse.setText(String.format("%s kg",this.chargement.expedition.masse));
            this.txt_dtls_chgmt_fragile.setText(this.chargement.expedition.fragile ? "Oui" : "Non");
            this.txt_dtls_chgmt_distance.setText(String.format("%s km", this.chargement.expedition.distance ));

            this.btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = chargement.telephonelivraison != null ? chargement.telephonechargement : "";
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone.trim(), null));
                    startActivity(intent);
                }
            });

            this.btn_demarrer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        this.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.getUiSettings().setAllGesturesEnabled(true);

            //checkPermission
            if (ActivityCompat.checkSelfPermission(DetailsChargement.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(DetailsChargement.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);

                MapsInitializer.initialize(DetailsChargement.this);

                String[] raw = chargement.expedition.coordarrivee.split(",");
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Float.parseFloat(raw[0]), Float.parseFloat(raw[1])), 10);

                map.animateCamera(cameraUpdate);
            }else{
                Log.e("###Trans Permission","Aucune permission de Localisation n'a été accordée");
            }
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
