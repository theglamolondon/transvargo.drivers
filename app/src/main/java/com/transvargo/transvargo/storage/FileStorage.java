package com.transvargo.transvargo.storage;

/**
 * Created by BW.KOFFI on 09/08/2017.
 */

public class FileStorage implements Store {
    @Override
    public boolean put(String key, Object data) {
        return false;
    }

    @Override
    public <T> T get(String key) {
        return null;
    }
}