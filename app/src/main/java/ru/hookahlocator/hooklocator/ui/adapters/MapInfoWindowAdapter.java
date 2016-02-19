package ru.hookahlocator.hooklocator.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import javax.inject.Inject;

import ru.hookahlocator.hooklocator.R;
import ru.hookahlocator.hooklocator.dagger.Injector;
import ru.hookahlocator.hooklocator.data.DataProvider;
import ru.hookahlocator.hooklocator.data.entities.Place;
import ru.hookahlocator.hooklocator.net.API;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * December 2015
 */
public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    final static String TAG = "MapInfoWindowAdapter";

    @Inject DataProvider dataProvider;
    @Inject ImageLoader imageLoader;

    private Marker showingMarker; // To prevent recursive calling from imageLoader callback

    private Context context;

    public MapInfoWindowAdapter(Context context) {
        Injector.getDataProviderComponent().inject(this);
        Injector.getViewsComponent().inject(this);
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Place place = dataProvider.getPlaceById(Integer.parseInt(marker.getTitle()));
        View view = View.inflate(context, R.layout.view_map_info_popup, null);
        final ImageView ivLogo = (ImageView) view.findViewById(R.id.v_map_info_logo);
        final String logoURL = API.URL + place.logo;
        showingMarker = marker;
        imageLoader.displayImage(logoURL, ivLogo, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }
            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }
            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if ((showingMarker != null) && (showingMarker.isInfoWindowShown())) {
                    //Log.v(TAG, "Loaded logo: " + logoURL);
                    showingMarker.hideInfoWindow();
                    showingMarker.showInfoWindow();
                }
            }
            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
        TextView tvTitle = (TextView) view.findViewById(R.id.v_map_info_title);
        tvTitle.setText(place.name);
        TextView tvMetro = (TextView) view.findViewById(R.id.v_map_info_metro);
        if (place.metro.length() == 0) {
            tvMetro.setVisibility(View.GONE);
        } else {
            tvMetro.setText(place.metro);
        }
        TextView tvAddress = (TextView) view.findViewById(R.id.v_map_info_address);
        tvAddress.setText(place.address);
        return view;
    }
}
