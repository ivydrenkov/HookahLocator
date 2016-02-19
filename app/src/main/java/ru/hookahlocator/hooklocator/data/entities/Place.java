package ru.hookahlocator.hooklocator.data.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.Html;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import ru.hookahlocator.hooklocator.data.entities.annotations.DBField;
import ru.hookahlocator.hooklocator.data.entities.annotations.JSONField;
import ru.hookahlocator.hooklocator.util.LocationUtils;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class Place extends BaseObject {
    final static String TAG = "Place";

    @JSONField
    @DBField
    public int id;
    @JSONField
    @DBField
    public String name;
    @JSONField(jsonName = "rate_all")
    @DBField
    public String rate;
    @JSONField
    @DBField
    public String country;
    @JSONField(jsonName = "crossStreet")
    @DBField
    public String address;
    @JSONField
    @DBField
    public String metro;
    @JSONField
    @DBField
    public String logo;
    @JSONField(jsonName = "main_image")
    @DBField(columnName = "main_image")
    public String mainImage;
    @JSONField
    @DBField
    public LatLng location = LocationUtils.STUB_LOCATION;

    @DBField
    public String currency;

    @Override
    public void parseFromJSON(JSONObject json) throws JSONException {
        currency = Html.fromHtml(json.getString("currency")).toString();
    }

    @Override
    public void fromDBCursor(Cursor c) {
    }

    @Override
    public void toDBValues(ContentValues cv) {
    }
}
