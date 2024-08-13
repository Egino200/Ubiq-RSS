package com.example.ubiqrss;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    List<String> subs;
    List<Drawable> pics;
    List<String> titles;

    public MyListAdapter(Activity context, ArrayList<String> title, ArrayList<String> sub, ArrayList<Drawable> pics) {
        super(context, R.layout.mylist, title);
        this.context = context;
        this.pics = pics;
        this.subs = sub;
        this.titles = title;

    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        titleText.setText(titles.get(position));
        imageView.setImageDrawable(pics.get(position));
        subtitleText.setText(subs.get(position));

        return rowView;

    };
}
