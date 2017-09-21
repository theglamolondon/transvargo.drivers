package com.transvargo.transvargo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.transvargo.transvargo.model.Offre;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsOffre extends MyActivityModel {

    private Gson gson = new Gson();
    private Offre offre = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private TextView txt_dtls_expire;
    private TextView txt_dtls_lieudepart;
    private TextView txt_dtls_datechargement;
    private TextView txt_dtls_chargement;
    private TextView txt_dtls_lieuarrive;
    private TextView txt_dtls_datelivraison;
    private TextView txt_dtls_livraison;
    private TextView txt_dtls_masse;
    private TextView txt_dtls_fragile;
    private TextView txt_dtls_distance;
    private TextView txt_dtls_typecamion;
    private TextView txt_dtls_prix;
    private Button btn_dtls_reserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_offre);

        this.txt_dtls_expire = (TextView) findViewById(R.id.txt_dtls_expire);
        this.txt_dtls_lieudepart = (TextView) findViewById(R.id.txt_dtls_lieudepart);
        this.txt_dtls_datechargement = (TextView) findViewById(R.id.txt_dtls_datechargement);
        this.txt_dtls_chargement = (TextView) findViewById(R.id.txt_dtls_chargement);
        this.txt_dtls_lieuarrive = (TextView) findViewById(R.id.txt_dtls_lieuarrive);
        this.txt_dtls_datelivraison = (TextView) findViewById(R.id.txt_dtls_datelivraison);
        this.txt_dtls_livraison = (TextView) findViewById(R.id.txt_dtls_livraison);
        this.txt_dtls_masse = (TextView) findViewById(R.id.txt_dtls_masse);
        this.txt_dtls_fragile = (TextView) findViewById(R.id.txt_dtls_fragile);
        this.txt_dtls_distance = (TextView) findViewById(R.id.txt_dtls_distance);
        this.txt_dtls_typecamion = (TextView) findViewById(R.id.txt_dtls_typecamion);
        this.txt_dtls_prix = (TextView) findViewById(R.id.txt_dtls_prix);

        this.btn_dtls_reserver = (Button) findViewById(R.id.btn_dtls_reserver);
        this.btn_dtls_reserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                String gOffre = gson.toJson(offre);

                Intent intent = new Intent(DetailsOffre.this, Reservation.class);
                intent.putExtra("offre",gOffre);
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("offre") != null)
        {
            this.offre = gson.fromJson(bundle.getString("offre"),Offre.class);
            this.fillView();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("offre",this.gson.toJson(this.offre));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.offre = gson.fromJson(savedInstanceState.getString("offre"),Offre.class);
    }

    private void fillView()
    {
        this.txt_dtls_expire.setText(String.format("Expire dans %s jour(s)", ((this.offre.dateexpiration.getTime() - (new Date()).getTime())/(1000*60*60*24)) ));
        this.txt_dtls_lieudepart.setText(this.offre.lieudepart);
        this.txt_dtls_datechargement.setText( String.format("Chargements à partir du %s", sdf.format(this.offre.datechargement)) );
        this.txt_dtls_chargement.setText( String.format("Contacter %s au %s. \n %s", this.offre.chargement.societechargement , this.offre.chargement.contactchargement, this.offre.chargement.adressechargement) );
        this.txt_dtls_lieuarrive.setText(this.offre.lieuarrivee);
        this.txt_dtls_datelivraison.setText( String.format("Livraison au plus tard le %s",sdf.format(this.offre.dateexpiration)) );
        this.txt_dtls_livraison.setText( String.format("Contacter %s au %s. \n %s", this.offre.chargement.societelivraison, this.offre.chargement.contactlivraison, this.offre.chargement.adresselivraison) );

        if(this.offre.masse <= 10000)
        {
            this.txt_dtls_masse.setText(String.format("%s tonne(s)",(offre.masse/1000)));
        }else {
            this.txt_dtls_masse.setText(String.format("%s kg",offre.masse));
        }

        this.txt_dtls_fragile.setText(this.offre.fragile ? "Oui" : "Non");
        this.txt_dtls_distance.setText( String.format("%s km",this.offre.distance) );

        this.txt_dtls_typecamion.setText( this.offre.typeCamion.libelle );
        this.txt_dtls_prix.setText( String.format("%s FCFA", this.offre.prix) );
    }
}
