package ru.hookahlocator.hooklocator.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class LocationUtils {

    public final static LatLng STUB_LOCATION = new LatLng(0, 0);

    public static float getDistanceBetweenInMeters(LatLng latLng1, LatLng latLng2) {
        float[] distance = new float[3];
        Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, distance);
        float distanceMeters = distance[0];
        return distanceMeters;
    }

}
