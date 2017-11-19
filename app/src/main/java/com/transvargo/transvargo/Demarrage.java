package com.transvargo.transvargo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.processing.StoreCache;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Demarrage extends AppCompatActivity {

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{

                    Transporteur transporteur = StoreCache.getObject(Demarrage.this,StoreCache.TRANSVARGO_TRANSPORTEUR,Transporteur.class);

                    //Si le transporteur est dans le cache, on passe à la liste des offres
                    if(transporteur != null)
                    {
                        Log.i("#Trans-API#","Check user connected");
                        if(transporteur.typetransporteur_id != Transporteur.CHAUFFEUR_FLOTTE)
                        {
                            Log.i("#Trans-API#",transporteur.identite.id + " "+transporteur.nom + " "+ transporteur.prenoms);
                            Intent intent = new Intent(Demarrage.this, Principal.class);
                            startActivity(intent);
                        }else{
                            Log.i("#Trans-API#",transporteur.vehicule.immatriculation + " | " +transporteur.vehicule.chauffeur );
                            Intent intent = new Intent(Demarrage.this, Chargements.class);
                            startActivity(intent);
                        }
                        finish();

                    }else{ //On affiche la vue de connnexion

                        Intent openApp = new Intent(Demarrage.this,Login.class);
                        startActivity(openApp);
                    }
                }
            }
        };

        timer.start();

        setContentView(R.layout.activity_demarrage);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
