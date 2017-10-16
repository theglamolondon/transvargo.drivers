package com.transvargo.transvargo.http;

import android.support.annotation.UiThread;

import com.android.volley.VolleyError;

import java.util.List;

/**
 * Created by BW.KOFFI on 09/08/2017.
 */

public abstract class ResponseHandler {
    public <T extends Object> void doSomething(List<T> data){};

    public void doSomething(Object data){};

    public void error(int httpCode, VolleyError error) {}
}
