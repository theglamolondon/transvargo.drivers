package com.transvargo.transvargo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.ListeOffreAction;
import com.transvargo.transvargo.model.Offre;
import com.transvargo.transvargo.processing.ListeProcessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Principal extends MyActivityModel {

    String liste_save_bundle = "liste_offre";
    List<Offre> listeOffre = null;
    Gson gson = new Gson();

    RelativeLayout rWait;

    ListView lwListeOffres  = null;
    ResponseHandler handle = new ResponseHandler() {
        @Override
        public <T extends Object> void doSomething(List<T> data) {
            ArrayList<Offre> list = (ArrayList<Offre>) data;
            fillListe(list);
        }

        @Override
        public void error(int httpCode, VolleyError error) {
            ProgressBar progress = (ProgressBar) findViewById(R.id.progressbar);
            progress.setVisibility(View.INVISIBLE);

            TextView txterror = (TextView) findViewById(R.id.txt_error);

            txterror.setText("Impossible de se connecter à internet");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        this.rWait = (RelativeLayout) findViewById(R.id.rWait);

        this.lwListeOffres = (ListView) findViewById(R.id.lwListeOffres);
        this.lwListeOffres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Offre offreSelected = listeOffre.get(position);

                Intent intent = new Intent(Principal.this,DetailsOffre.class);
                intent.putExtra("offre",gson.toJson(offreSelected));
                startActivity(intent);
            }
        });

        BottomNavigationView navigationBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        navigationBar.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){

                        case R.id.action_ongle_1:
                            startActivity(new Intent(getBaseContext(), Principal.class));
                            break;
                        case R.id.action_ongle_2:
                            startActivity(new Intent(getBaseContext(), Chargement.class));
                            break;
                        case R.id.action_ongle_3:
                            //Action quand onglet 2 sélectionné
                            break;

                        default:
                            //Action quand onglet 3 sélectionné

                            break;
                    }

                    return true;
                };
            }
        );

        new Runnable(){
            @Override
            public void run() {

                ApiTransvargo api = new ApiTransvargo(getBaseContext());
                ListeOffreAction liste = new ListeOffreAction(handle);

                api.executeHttpRequest(liste);

            }
        }.run();

    }

    private void fillListe(List<Offre> offres)
    {
        this.rWait.setVisibility(View.INVISIBLE);

        this.listeOffre = offres;

        ListeProcessing adapter = new ListeProcessing(Principal.this, this.listeOffre);
        lwListeOffres.setAdapter(adapter);
        lwListeOffres.setClickable(true);
    }

    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(this.listeOffre != null)
        {
            outState.putString(this.liste_save_bundle,this.gson.toJson(this.listeOffre.toArray()));
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onSaveInstanceState(inState);

        if(inState.getString(this.liste_save_bundle) != null)
        {
            Offre[] offres = this.gson.fromJson(inState.getString(this.liste_save_bundle),Offre[].class);
            List<Offre> liste = new ArrayList<Offre>(Arrays.asList(offres));

            this.fillListe(liste);
        }
    }

    public void onBackPressed()
    {
        Log.e("#Trans-API#","back button pressed");

        AlertDialog.Builder builder = new AlertDialog.Builder(Principal.this);
        builder.setMessage("Êtes-vous sur de vouloir quitter l'application ?");
        builder.setCancelable(false);

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}