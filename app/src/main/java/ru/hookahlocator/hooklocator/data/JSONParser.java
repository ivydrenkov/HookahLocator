package ru.hookahlocator.hooklocator.data;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.hookahlocator.hooklocator.data.entities.IParseable;
import ru.hookahlocator.hooklocator.data.entities.annotations.JSONField;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class JSONParser {
    final static String TAG = "JSONParser";

    public <T extends IParseable> ArrayList<T> parseObjects(JSONArray json, Class<T> type) {
        ArrayList<T> items = new ArrayList<>();
        for (int i=0; i<json.length(); i++) {
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                T object = parseObject(jsonObject, type);
                if (object != null) {
                    items.add(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    @Nullable
    public <T extends IParseable> T parseObject(JSONObject json, Class<T> type) {
        try {
            T object = type.newInstance();
            for (Field field: type.getFields()) {
                if (field.isAnnotationPresent(JSONField.class)) {
                    parseField(object, field, json);
                }
            }
            object.parseFromJSON(json);
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseField(IParseable object, Field field, JSONObject jsonObject) throws JSONException, IllegalAccessException {
        field.setAccessible(true);
        JSONField jsonField = field.getAnnotation(JSONField.class);
        String jsonName = jsonField.jsonName();
        if (jsonName.equals(jsonField.DEFAULT)) {
            jsonName = field.getName();
        }
        Type type = field.getType();
        if (type == int.class) {
            field.set(object, jsonObject.optInt(jsonName, 0));
        } else if (type == String.class) {
            String value = jsonObject.optString(jsonName, "");
            field.set(object, value);
        } else if (type == LatLng.class) {
            try {
                double latitude = Double.parseDouble(jsonObject.getString("lat"));
                double longitude = Double.parseDouble(jsonObject.getString("lng"));
                field.set(object, new LatLng(latitude, longitude));
            } catch (NumberFormatException e) {
                Log.e(TAG, Object.class.getSimpleName() + "json has bad coordinates");
            }
        }
    }
}
