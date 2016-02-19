package ru.hookahlocator.hooklocator.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.hookahlocator.hooklocator.R;
import ru.hookahlocator.hooklocator.data.entities.City;

public class CitiesAdapter extends BaseAdapter {
    ArrayList<City> list;

    public CitiesAdapter(ArrayList<City> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = View.inflate(parent.getContext(), R.layout.item_city, null);
        }
        City item = list.get(position);
        TextView tvName = (TextView) view.findViewById(R.id.it_city_name);
        tvName.setText(item.name);
        TextView tvCount = (TextView) view.findViewById(R.id.it_city_hookahs);
        tvCount.setText(String.valueOf(item.hookahsCount));
        return view;
    }
}
