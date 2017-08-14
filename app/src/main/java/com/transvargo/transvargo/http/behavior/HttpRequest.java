package com.transvargo.transvargo.http.behavior;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by BW.KOFFI on 10/08/2017.
 */

public abstract class HttpRequest {

    public JsonObjectRequest executeJsonObjectHttpRequest(Context context)
    { return null;}

    public JsonArrayRequest executeJsonArrayHttpRequest(Context context)
    { return null;}
}