package com.transvargo.transvargo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.processing.StoreCache;

/**
 * Created by BW.KOFFI on 13/08/2017.
 */

public class Boot extends Application {

    private static Transporteur transporteur;

    @Override
    public void onCreate(){
        super.onCreate();

        transporteur = StoreCache.getObject(this,StoreCache.TRANSVARGO_TRANSPORTEUR,Transporteur.class);
    }

    public static Transporteur getTransporteurConnecte(){
        return transporteur;
    }
}
