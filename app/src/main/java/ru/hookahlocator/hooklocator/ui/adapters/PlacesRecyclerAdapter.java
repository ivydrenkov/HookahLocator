package ru.hookahlocator.hooklocator.ui.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import javax.inject.Inject;

import ru.hookahlocator.hooklocator.R;
import ru.hookahlocator.hooklocator.dagger.Injector;
import ru.hookahlocator.hooklocator.data.entities.Place;
import ru.hookahlocator.hooklocator.net.API;
import ru.hookahlocator.hooklocator.util.LocationUtils;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class PlacesRecyclerAdapter extends RecyclerView.Adapter<PlacesRecyclerAdapter.ViewHolder>{

    @Inject ImageLoader imageLoader;

    ArrayList<Place> places;
    ArrayList<Place> placesFiltered = new ArrayList<>();
    LatLng location;
    String filter;

    IOnItemClick onItemClickListener;

    public PlacesRecyclerAdapter(ArrayList<Place> places) {
        Injector.getViewsComponent().inject(this);
        this.places = places;
        setFilter("");
    }

    public void updateLocation(LatLng location) {
        this.location = location;
        for (int i=0; i<placesFiltered.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public void setOnItemClickListener(IOnItemClick onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Place place = placesFiltered.get(position);
        holder.name.setText(place.name);
        holder.rate.setText(place.rate);
        holder.metro.setText(place.metro);
        holder.address.setText(place.address);
        if (location != null) {
            float distanceKm = LocationUtils.getDistanceBetweenInMeters(location, place.location)/1000;
            holder.distance.setText(String.format("%.1f км", distanceKm));
        }
        imageLoader.displayImage(API.URL + place.mainImage, holder.bgImage);
        holder.bgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placesFiltered.size();
    }

    @Nullable
    public Place getItem(int position) {
        if (position < placesFiltered.size()) {
            return placesFiltered.get(position);
        }
        return null;
    };

    public void setFilter(String filter) {
        this.filter = filter;
        placesFiltered.clear();
        for (Place place: places) {
            if ((filter.length()==0) || (place.name.toLowerCase().contains(filter))) {
                placesFiltered.add(place);
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView rate;
        private TextView metro;
        private TextView address;
        private TextView distance;
        private ImageView bgImage;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.it_place_name);
            rate = (TextView) itemView.findViewById(R.id.it_place_rate);
            metro = (TextView) itemView.findViewById(R.id.it_place_metro);
            address = (TextView) itemView.findViewById(R.id.it_place_address);
            distance = (TextView) itemView.findViewById(R.id.it_place_distance);
            bgImage = (ImageView) itemView.findViewById(R.id.it_place_bg_image);
        }
    }

    public interface IOnItemClick {
        void onItemClick(int position);
    }


}
