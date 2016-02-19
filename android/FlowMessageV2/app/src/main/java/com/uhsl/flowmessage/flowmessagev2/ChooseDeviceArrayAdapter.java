package com.uhsl.flowmessage.flowmessagev2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcus on 19/02/2016.
 */
public class ChooseDeviceArrayAdapter extends ArrayAdapter<String[]> {

    private final Activity context;
    private final ArrayList<String[]> values;

    static class ViewHolder {
        public TextView lineOne;
        public TextView lineTwo;
    }


    public ChooseDeviceArrayAdapter(Activity context, ArrayList<String[]> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getLayoutInflater();

            rowView = inflater.inflate(R.layout.choose_device_row, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.lineOne = (TextView) rowView.findViewById(R.id.choose_device_row_first_line);
            viewHolder.lineTwo = (TextView) rowView.findViewById(R.id.choose_device_row_second_line);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.lineOne.setText(values.get(position)[0]);
        viewHolder.lineTwo.setText(values.get(position)[1]);

        return rowView;
    }
}
