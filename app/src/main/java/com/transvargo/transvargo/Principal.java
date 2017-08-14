package com.transvargo.transvargo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        public void error(int httpCode) {
            ProgressBar progress = (ProgressBar) findViewById(R.id.progressbar);
            progress.setVisibility(View.INVISIBLE);

            TextView txterror = (TextView) findViewById(R.id.txt_error);

            txterror.setText("Impossible de connecter à internet");
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
}