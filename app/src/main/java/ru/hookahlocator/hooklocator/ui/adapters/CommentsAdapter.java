package ru.hookahlocator.hooklocator.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.hookahlocator.hooklocator.R;
import ru.hookahlocator.hooklocator.data.entities.Comment;

public class CommentsAdapter extends BaseAdapter {
    ArrayList<Comment> list;

    public CommentsAdapter(ArrayList<Comment> list) {
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
            view = View.inflate(parent.getContext(), R.layout.item_comment, null);
        }
        Comment item = list.get(position);
        TextView tvName = (TextView) view.findViewById(R.id.it_comment_name);
        tvName.setText(item.nickname);
        TextView tvDate = (TextView) view.findViewById(R.id.it_comment_date);
        tvDate.setText(item.date);
        TextView tvText = (TextView) view.findViewById(R.id.it_comment_text);
        tvText.setText(item.text);
        return view;
    }
}
