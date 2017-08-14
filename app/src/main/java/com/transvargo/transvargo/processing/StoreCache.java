package com.transvargo.transvargo.processing;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by BW.KOFFI on 11/08/2017.
 */

public class StoreCache {

    private static String TRANVARGO_CACHE = "transvargocache";
    public static String TRANSVARGO_TRANSPORTEUR = "transporteur";

    public static <T> boolean store(Context context, String key, T model)
    {
        SharedPreferences settings = context.getSharedPreferences(TRANVARGO_CACHE,0);
        final SharedPreferences.Editor editor = settings.edit();

        Gson gson = new Gson();

        editor.putString(key, gson.toJson(model));

        editor.apply();

        return true;
    }

    public static String getString(Context context, String key)
    {
        SharedPreferences settings = context.getSharedPreferences(TRANVARGO_CACHE, 0);

        return settings.getString(key, null);
    }

    public static <T extends Object> T getObject(Context context, String key, Class<T> tClass)
    {
        SharedPreferences settings = context.getSharedPreferences(TRANVARGO_CACHE, 0);

        String raw = getString(context, key);

        if(raw != null)
        {
            Gson gson = new Gson();
            return gson.fromJson(raw,tClass);
        }else{
            return null;
        }
    }

}
