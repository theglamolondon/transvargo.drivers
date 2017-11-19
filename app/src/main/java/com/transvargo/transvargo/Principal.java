package com.transvargo.transvargo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.ListeOffreAction;
import com.transvargo.transvargo.model.Offre;
import com.transvargo.transvargo.processing.ListeProcessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Principal extends MyActivityModel {

    String liste_save_bundle = "liste_offre";
    List<Offre> listeOffre = null;
    Gson gson = new Gson();
    RelativeLayout rWait;

    ListView lwListeOffres  = null;
    SwipeRefreshLayout swipe_layout  = null;
    ResponseHandler handler = new ResponseHandler() {
        @Override
        public <T extends Object> void doSomething(List<T> data) {

            swipe_layout.setRefreshing(false);

            ArrayList<Offre> list = (ArrayList<Offre>) data;
            fillListe(list);
        }

        @Override
        public void error(int httpCode, VolleyError error) {
            ProgressBar progress = (ProgressBar) findViewById(R.id.progressbar);
            progress.setVisibility(View.INVISIBLE);
            error.printStackTrace();
            TextView txterror = (TextView) findViewById(R.id.txt_error);
            txterror.setText("Impossible de se connecter à internet." + "\n\r" + "Glisser vers le bas pour actualiser");

            swipe_layout.setRefreshing(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_principal);

        this.rWait = (RelativeLayout) findViewById(R.id.rWait);

        this.lwListeOffres = (ListView) findViewById(R.id.lwListeOffres);
        this.swipe_layout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        this.lwListeOffres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Offre offreSelected = listeOffre.get(position);
                Intent intent = new Intent(Principal.this,DetailsOffre.class);
                intent.putExtra("offre",gson.toJson(offreSelected));
                startActivity(intent);
            }
        });
        this.swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_layout.setRefreshing(true);
                loadData();
            }
        });

        BottomNavigationView navigationBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        //navigationBar.

        navigationBar.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){

                        case R.id.action_ongle_1:
                            //startActivity(new Intent(getBaseContext(), Principal.class));
                            break;
                        case R.id.action_ongle_2:
                            startActivity(new Intent(getBaseContext(), Chargements.class));
                            break;
                        case R.id.action_ongle_3:
                            //Action quand onglet 2 sélectionné
                            break;
                    }

                    return true;
                };
            }
        );

        this.loadData();
    }

    private void loadData()
    {
        ApiTransvargo api = new ApiTransvargo(this.getApplicationContext());
        ListeOffreAction liste = new ListeOffreAction(handler);
        api.executeHttpRequest(liste);
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
                System.exit(0);
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