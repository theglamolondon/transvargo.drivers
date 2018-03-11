package com.transvargo.transvargo;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.DelivryChargement;
import com.transvargo.transvargo.http.behavior.FinishChargement;
import com.transvargo.transvargo.http.behavior.StartChargement;
import com.transvargo.transvargo.model.Chargement;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BW.KOFFI on 12/10/2017.
 */

public class LivraisonChargement extends MyActivityModel {
    MapView mapView;
    GoogleMap map;
    Chargement chargement;
    ProgressDialog progressDialog;

    String OTP = null;
    EditText txt_otp;

    Button btn_call;
    Button btn_navigation;
    Button btn_livrer;
    TextView txt_livr_chgmt_receptionniste;
    TextView txt_livr_chgmt_societe;
    TextView txt_livr_chgmt_masse;
    TextView txt_livr_chgmt_fragile;
    TextView txt_livr_chgmt_distance;

    ResponseHandler responseHandler = new ResponseHandler() {
        @Override
        public void doSomething(Object data) {
            JSONObject objet = (JSONObject) data;
            try {
                OTP = objet.getString("otp");
                Toast.makeText(LivraisonChargement.this,objet.getString("message"),Toast.LENGTH_SHORT).show();
                showOtpCheck();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }

        @Override
        public void error(int httpCode, VolleyError error) {
            Toast.makeText(LivraisonChargement.this,"Impossible de se connecter à internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_livraison);

        /* Binding des widgets */
        this.btn_call = (Button) findViewById(R.id.btn_livr_chgmt_call);
        this.btn_navigation = (Button) findViewById(R.id.btn_livr_chgmt_map);
        this.btn_livrer = (Button) findViewById(R.id.btn_livr_chgmt_livrer);

        this.txt_livr_chgmt_receptionniste = (TextView)  findViewById(R.id.txt_livr_chgmt_receptionniste);
        this.txt_livr_chgmt_societe = (TextView)  findViewById(R.id.txt_livr_chgmt_societe);
        this.txt_livr_chgmt_masse = (TextView)  findViewById(R.id.txt_livr_chgmt_masse);
        this.txt_livr_chgmt_fragile = (TextView)  findViewById(R.id.txt_livr_chgmt_fragile);
        //this.txt_livr_chgmt_distance = (TextView)  findViewById(R.id.txt_livr_chgmt_distance);

        this.mapView = (MapView) findViewById(R.id.mapView);
        this.mapView.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("chargement") != null)
        {
            Gson gson = new Gson();
            this.chargement = gson.fromJson(bundle.getString("chargement"),Chargement.class);

            this.txt_livr_chgmt_receptionniste.setText(String.format("%s %s",this.chargement.expedition.client.nom, this.chargement.expedition.client.prenoms));
            this.txt_livr_chgmt_societe.setText(this.chargement.societelivraison);
            this.txt_livr_chgmt_masse.setText(String.format("%s tonne(s)",this.chargement.expedition.tonnage != null ? this.chargement.expedition.tonnage.masse : "ND" ));
            this.txt_livr_chgmt_fragile.setText(this.chargement.expedition.fragile ? "Oui" : "Non");
            //this.txt_livr_chgmt_distance.setText(String.format("%s km", this.chargement.expedition.distance ));

            this.btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = chargement.telephonelivraison != null ? chargement.telephonelivraison : "";
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone.trim(), null));
                    startActivity(intent);
                }
            });
            this.btn_navigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URLMaps = "google.navigation:q=%s"; //google.navigation:q=latitude,longitude

                    //Lancement google Maps
                    Uri googleMapUri = Uri.parse(String.format(URLMaps,chargement.expedition.coordarrivee));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW,googleMapUri);
                    //mapIntent.setPackage("com.google.android.apps.maps");
                    startActivityForResult(mapIntent,1234);
                }
            });

            this.btn_livrer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog = ProgressDialog.show(LivraisonChargement.this,"","Validation de la livraison en cours ...",true);

                    ApiTransvargo api = new ApiTransvargo(LivraisonChargement.this);
                    DelivryChargement delivry = new DelivryChargement(chargement, responseHandler);
                    api.executeHttpRequest(delivry);
                }
            });

            /* Google Map*/
            this.mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    map.getUiSettings().setAllGesturesEnabled(true);

                    //checkPermission
                    if (ActivityCompat.checkSelfPermission(LivraisonChargement.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(LivraisonChargement.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        Log.e("###Trans Permission","Aucune permission de Localisation n'a été accordée");

                        //Activer la permission si elle n'est pas donnée
                        if (ActivityCompat.shouldShowRequestPermissionRationale(LivraisonChargement.this, Manifest.permission.ACCESS_FINE_LOCATION))
                        {
                            addMarker(map, chargement);
                        }

                    }else{
                        addMarker(map, chargement);
                    }
                }
            });
        }
    }

    private void addMarker(GoogleMap map, Chargement chargement)
    {
        try {
            map.setMyLocationEnabled(true);
        }catch (SecurityException e){
            e.printStackTrace();
        }
        MapsInitializer.initialize(LivraisonChargement.this);

        String[] raw = chargement.expedition.coordarrivee.split(",");
        //ajout du marker
        LatLng departMarker = new LatLng(Float.parseFloat(raw[0]), Float.parseFloat(raw[1]));
        map.addMarker(new MarkerOptions().position(departMarker).title(chargement.societelivraison+" | "+chargement.contactlivraison).icon(BitmapDescriptorFactory.fromResource(R.drawable.package32)));

        //Annimation de la vue
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(departMarker, 10);
        map.animateCamera(cameraUpdate);
    }

    private void showOtpCheck()
    {
        LayoutInflater inflater = getLayoutInflater();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View inflator = inflater.inflate(R.layout.otp_checking, null);
        builder.setView(inflator);
        builder.setPositiveButton("Vérifier", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                validateDelivry();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LivraisonChargement.this, "Vérification de livraison annulée", Toast.LENGTH_LONG).show();
            }
        });

        txt_otp = (EditText) inflator.findViewById(R.id.txt_otp_check);
        builder.create();
        builder.show();
    }

    private void validateDelivry(){
        if(txt_otp.getText().toString().equals(OTP)){
            progressDialog = ProgressDialog.show(LivraisonChargement.this,"","Vérification du code ...",true);

            ApiTransvargo api = new ApiTransvargo(LivraisonChargement.this.getApplicationContext());
            FinishChargement finish = new FinishChargement(chargement, new ResponseHandler() {
                @Override
                public void doSomething(Object data) {
                    JSONObject objet = (JSONObject) data;
                    try {
                        OTP = objet.getString("otp");
                        Toast.makeText(LivraisonChargement.this,objet.getString("message"),Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    startActivity(new Intent(LivraisonChargement.this, Chargements.class));
                    finish();
                }

                @Override
                public void error(int httpCode, VolleyError error) {
                    Toast.makeText(LivraisonChargement.this,"Impossible de se connecter à internet",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            api.executeHttpRequest(finish);
        }else{
            Toast.makeText(LivraisonChargement.this,"Le code de validation n'est pas correcte. Veuillez réessayer SVP",Toast.LENGTH_LONG).show();
        }
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
