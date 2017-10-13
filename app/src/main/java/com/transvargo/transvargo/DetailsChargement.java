package com.transvargo.transvargo;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.StartChargement;
import com.transvargo.transvargo.model.Chargement;
import com.transvargo.transvargo.model.Offre;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsChargement extends AppCompatActivity {

    MapView mapView;
    GoogleMap map;
    Chargement chargement;
    ProgressDialog progressDialog;

    Button btn_call;
    Button btn_navigation;
    Button btn_demarrer;
    TextView txt_dtls_chgmt_expediteur;
    TextView txt_dtls_chgmt_societe;
    TextView txt_dtls_chgmt_masse;
    TextView txt_dtls_chgmt_fragile;
    TextView txt_dtls_chgmt_distance;

    ResponseHandler responseHandler = new ResponseHandler() {
        @Override
        public void doSomething(Object data) {
            JSONObject objet = (JSONObject) data;
            try {
                Toast.makeText(DetailsChargement.this,objet.getString("message"),Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }

        @Override
        public void error(int httpCode, VolleyError error) {
            Toast.makeText(DetailsChargement.this,"Impossible de se connecter à internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_chargement);
        this.progressDialog = new ProgressDialog(this);
        this.mapView = (MapView) findViewById(R.id.mapView);
        this.mapView.onCreate(savedInstanceState);

        this.btn_call = (Button) findViewById(R.id.btn_dtls_chgmt_call);
        this.btn_demarrer = (Button) findViewById(R.id.btn_dtls_chgmt_demarrer);
        this.btn_navigation = (Button) findViewById(R.id.btn_dtls_chgmt_map);
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
            this.btn_navigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URLMaps = "google.navigation:q=%s"; //google.navigation:q=latitude,longitude

                    //Lancement google Maps
                    Uri googleMapUri = Uri.parse(String.format(URLMaps,chargement.expedition.coorddepart));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW,googleMapUri);
                    //mapIntent.setPackage("com.google.android.apps.maps");
                    startActivityForResult(mapIntent,1234);
                }
            });

            this.btn_demarrer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog = ProgressDialog.show(DetailsChargement.this,"","Démarrage du chargement en cours...",true);
                    new Runnable() {
                        @Override
                        public void run() {
                            ApiTransvargo api = new ApiTransvargo(DetailsChargement.this);
                            StartChargement start = new StartChargement(chargement, responseHandler);
                            api.executeHttpRequest(start);
                        }
                    }.run();
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
                    && ActivityCompat.checkSelfPermission(DetailsChargement.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Log.e("###Trans Permission","Aucune permission de Localisation n'a été accordée");

                //Activer la permission si elle n'est pas donnée
                if (ActivityCompat.shouldShowRequestPermissionRationale(DetailsChargement.this, Manifest.permission.ACCESS_COARSE_LOCATION))
                {
                    map.setMyLocationEnabled(true);

                    MapsInitializer.initialize(DetailsChargement.this);

                    String[] raw = chargement.expedition.coorddepart.split(",");
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Float.parseFloat(raw[0]), Float.parseFloat(raw[1])), 10);

                    map.animateCamera(cameraUpdate);
                }

            }else{
                map.setMyLocationEnabled(true);

                MapsInitializer.initialize(DetailsChargement.this);

                String[] raw = chargement.expedition.coorddepart.split(",");
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Float.parseFloat(raw[0]), Float.parseFloat(raw[1])), 10);

                map.animateCamera(cameraUpdate);
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
