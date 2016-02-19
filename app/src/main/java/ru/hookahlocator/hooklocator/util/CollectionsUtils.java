package ru.hookahlocator.hooklocator.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.hookahlocator.hooklocator.data.entities.Favorite;
import ru.hookahlocator.hooklocator.data.entities.Place;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class CollectionsUtils {

    public static ArrayList<Place> getFavoritePlaces(ArrayList<Place> placesSource, ArrayList<Favorite> favorites) {
        ArrayList<Place> places = new ArrayList<>();
        for (Place place: placesSource) {
            for (Favorite favorite: favorites) {
                if (place.id == favorite.placeId) { //place is favorite
                    places.add(place);
                    break;
                }
            }
        }
        return places;
    }

    public static ArrayList<Place> getPlacesSortedByDistanceFromLocationAsc(ArrayList<Place> placesSource, final LatLng location) {
        ArrayList<Place> places = new ArrayList<>(placesSource.size());
        for (Place place : placesSource) {
            places.add(place);
        }
        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                int distance1 = (int) LocationUtils.getDistanceBetweenInMeters(location, o1.location);
                int distance2 = (int) LocationUtils.getDistanceBetweenInMeters(location, o2.location);
                return distance1 - distance2;
            }
        });
        return places;
    }

    public static ArrayList<Place> getPlacesSortedByRatingDesc(ArrayList<Place> placesSource) {
        ArrayList<Place> places = new ArrayList<>(placesSource.size());
        for (Place place : placesSource) {
            places.add(place);
        }
        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                int rate1 = 0;
                try {
                    rate1 = (int) (Float.parseFloat(o1.rate) * 100);
                } catch (NumberFormatException e) { }
                int rate2 = 0;
                try {
                    rate2 = (int) (Float.parseFloat(o2.rate) * 100);
                } catch (NumberFormatException e) { }
                return rate2 - rate1;
            }
        });
        return places;
    }
}
