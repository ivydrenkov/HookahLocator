package ru.hookahlocator.hooklocator.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import ru.hookahlocator.hooklocator.data.entities.annotations.DBField;
import ru.hookahlocator.hooklocator.data.entities.annotations.JSONField;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class PlaceAdditional extends BaseObject {
    final static String TAG = "Place";

    @JSONField(jsonName = "zav_id")
    @DBField
    public int id;
    @JSONField(jsonName = "cost_calian")
    @DBField
    public String costHookah;
    @JSONField(jsonName = "cost_tea")
    @DBField
    public String costTea;
    @JSONField(jsonName = "is_shop")
    @DBField
    public String isShop;
    @JSONField(jsonName = "is_alcohol")
    @DBField
    public String isAlcohol;
    @JSONField(jsonName = "is_food")
    @DBField
    public String isFood;
    @JSONField(jsonName = "is_alltime")
    @DBField
    public String isAllTime;
    @JSONField
    @DBField
    public String metro;
    @JSONField(jsonName = "open")
    @DBField
    public String timeOpen;
    @JSONField(jsonName = "close")
    @DBField
    public String timeClose;
    @JSONField(jsonName = "weekend_open")
    @DBField
    public String timeWeekendOpen;
    @JSONField(jsonName = "weekend_close")
    @DBField
    public String timeWeekendClose;

    @Override
    public void parseFromJSON(JSONObject json) throws JSONException {
    }

    @Override
    public void fromDBCursor(Cursor c) {
    }

    @Override
    public void toDBValues(ContentValues cv) {
    }
}
