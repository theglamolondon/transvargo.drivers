package com.transvargo.transvargo.storage;

import java.util.Objects;

/**
 * Created by BW.KOFFI on 09/08/2017.
 */

public interface Store {

    public boolean put(String key, Object data);
    public <T extends Object> T get(String key);
}