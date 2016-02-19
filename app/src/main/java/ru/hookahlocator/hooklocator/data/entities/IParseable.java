package ru.hookahlocator.hooklocator.data.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public interface IParseable {
    /**
     * Used for uncommon cases
     * @param json
     * @throws JSONException
     */
    void parseFromJSON(JSONObject json) throws JSONException;
}
