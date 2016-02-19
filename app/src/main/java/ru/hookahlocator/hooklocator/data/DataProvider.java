package ru.hookahlocator.hooklocator.data;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import ru.hookahlocator.hooklocator.dagger.Injector;
import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.data.entities.City;
import ru.hookahlocator.hooklocator.data.entities.Place;
import ru.hookahlocator.hooklocator.data.entities.PlaceAdditional;
import ru.hookahlocator.hooklocator.data.entities.PlaceFullData;
import ru.hookahlocator.hooklocator.net.API;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class DataProvider implements Callback {
    final static String TAG = "DataProvider";

    @Inject OkHttpClient httpClient;
    @Inject JSONParser parser;
    @Inject DataBase dataBase;

    String currentRequest;
    IDataListener listenerForLoadingData;


    private static final Map<String, String> TABLES_MAP;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put(API.REQUEST_CITIES, DataBase.TABLE_CITIES);
        aMap.put(API.REQUEST_PLACES, DataBase.TABLE_PLACES);
        aMap.put(API.REQUEST_PLACE_FULL, DataBase.TABLE_PLACES_DATA);
        aMap.put(API.REQUEST_PLACE_ADDITIONAL, DataBase.TABLE_PLACES_ADDITIONAL);
        TABLES_MAP = Collections.unmodifiableMap(aMap);
    }

    private static final Map<String, Class> CLASSES_MAP;
    static {
        Map<String, Class> aMap = new HashMap<String, Class>();
        aMap.put(API.REQUEST_CITIES, City.class);
        aMap.put(API.REQUEST_PLACES, Place.class);
        aMap.put(API.REQUEST_PLACE_FULL, PlaceFullData.class);
        aMap.put(API.REQUEST_PLACE_ADDITIONAL, PlaceAdditional.class);
        CLASSES_MAP = Collections.unmodifiableMap(aMap);
    }

    private String appVersion = "-";

    public DataProvider(Context context) {
        Injector.getDataProviderComponent().inject(this);
        try {
            appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Log.v(TAG, "Application version: " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "Unknown";
        }
    }


    public void getCitiesAsync(IDataListener listener) {
        String request = API.REQUEST_CITIES;
        getItemsAsync(listener, API.URL + request, API.REQUEST_CITIES);
    }

    public void getPlacesAsync(IDataListener listener, String cityAbbr) {
        String request = API.REQUEST_PLACES.replace("%CITY_ABBR%", cityAbbr);
        getItemsAsync(listener, API.URL + request, API.REQUEST_PLACES);
    }

    public @Nullable Place getPlaceById(int id) {
        Place place = dataBase.getItemFromTable(DataBase.TABLE_PLACES, Place.class, "id=" + id);
        if (place == null) { //maybe in favorites?
            place = dataBase.getItemFromTable(DataBase.TABLE_FAVORITE_PLACES, Place.class, "id=" + id);
        }
        return place;
    }

    public ArrayList<Place> getFavoritePlacesForAllCities() {
        return dataBase.getItemsFromTable(DataBase.TABLE_FAVORITE_PLACES, Place.class);
    }

    public boolean isPlaceFavorite(int placeId) {
        Place favorite = dataBase.getItemFromTable(DataBase.TABLE_FAVORITE_PLACES, Place.class,
                "id=" + placeId);
        return favorite != null;
    }

    public void favoritePlace(Place place) {
        Log.v(TAG, "Place «" + place.name + "» now favorite");
        dataBase.saveItemToTable(place, DataBase.TABLE_FAVORITE_PLACES);
    }

    public void unfavoritePlace(Place place) {
        Log.v(TAG, "Place «" + place.name + "» now NOT favorite");
        dataBase.getDataBase().delete(DataBase.TABLE_FAVORITE_PLACES,
                "id=?",
                new String[]{String.valueOf(place.id)});
    }

    public void getPlaceFullInfoAsync(IDataListener listener, int placeId) {
        String request = API.REQUEST_PLACE_FULL.replace("%PLACE_ID%", String.valueOf(placeId));
        getItemAsync(listener, API.URL + request, API.REQUEST_PLACE_FULL, "id=" + placeId);
    }

    public void getPlaceAdditionalInfoAsync(IDataListener listener, int placeId) {
        String request = API.REQUEST_PLACE_ADDITIONAL.replace("%PLACE_ID%", String.valueOf(placeId));
        getItemAsync(listener, API.URL + request, API.REQUEST_PLACE_ADDITIONAL, "id="+placeId);
    }

    public void dropPlaces() {
        dataBase.deleteTable(DataBase.TABLE_PLACES);
        dataBase.deleteTable(DataBase.TABLE_PLACES_DATA);
    }

    public void dropCities() {
        dataBase.deleteTable(DataBase.TABLE_CITIES);
    }

    @Nullable
    public City getCityByAbbr(String abbr) {
        City city = dataBase.getItemFromTable(DataBase.TABLE_CITIES, City.class, "abbr='"+abbr+"'");
        return city;
    }

    private <T extends BaseObject> void getItemsAsync(IDataListener listener, String request, String cleanRequest) {
        Log.v(TAG, "Request «" + request + "»");
        String table = TABLES_MAP.get(cleanRequest);
        Class tClass = CLASSES_MAP.get(cleanRequest);
        ArrayList<T> list = null;
        if (table.length() > 0) { //This data keeps in DB
            list = dataBase.getItemsFromTable(table, tClass);
        }
        if (list != null) { //Found data
            listener.onDataReady(list);
        }else{ //Need to load from server
            Request req= new Request.Builder()
                    .get()
                    .url(request)
                    .build();
            httpClient.newCall(req).enqueue(this);
            listenerForLoadingData = listener;
            currentRequest = cleanRequest;
        }
    }

    private <T extends BaseObject> void getItemAsync(IDataListener listener, String request, String cleanRequest, String whereToDB) {
        Log.v(TAG, "Request «" + request + "»");
        String table = TABLES_MAP.get(cleanRequest);
        Class tClass = CLASSES_MAP.get(cleanRequest);
        ArrayList<T> list = null;
        if (table.length() > 0) { //This data keeps in DB
            T item = (T) dataBase.getItemFromTable(table, tClass, whereToDB);
            if (item != null) {
                list = new ArrayList<>();
                list.add(item);
            }
        }
        if (list != null) { //Found data
            listener.onDataReady(list);
        }else { //Need to load from server
            Request req = new Request.Builder()
                    .get()
                    .url(request)
                    .build();
            httpClient.newCall(req).enqueue(this);
            listenerForLoadingData = listener;
            currentRequest = cleanRequest;
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        e.printStackTrace();
        listenerForLoadingData.onDataFailure();
    }

    @Override
    public void onResponse(Response response) throws IOException {
        Class tClass = CLASSES_MAP.get(currentRequest);
        String table = TABLES_MAP.get(currentRequest);
        String jsonData = response.body().string();
        try {
            if (currentRequest.equals(API.REQUEST_PLACE_FULL)) { //requested single item
                JSONObject jsonObject = new JSONObject(jsonData);
                BaseObject object = (BaseObject) parser.parseObject(jsonObject, tClass);
                ArrayList<BaseObject> containerList = new ArrayList<>();
                containerList.add(object);
                listenerForLoadingData.onDataReady(containerList);
            } else {
                JSONArray jsonArray = new JSONArray(jsonData);
                ArrayList<? extends BaseObject> data = parser.parseObjects(jsonArray, tClass);
                Log.v(TAG, "Loaded " + data.size() + " " + tClass.getSimpleName());
                if (table.length() > 0) { //This data keeps in DB
                    dataBase.saveItemsToTableDestructively(data, table);
                }
                listenerForLoadingData.onDataReady(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listenerForLoadingData.onDataFailure();
        }
    }
}
