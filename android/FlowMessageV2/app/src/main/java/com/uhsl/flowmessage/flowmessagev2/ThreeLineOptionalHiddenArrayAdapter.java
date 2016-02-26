package com.uhsl.flowmessage.flowmessagev2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Marcus on 19/02/2016.
 */
public class ThreeLineOptionalHiddenArrayAdapter extends ArrayAdapter<String[]> {

    private final Activity context;
    private final List<String[]> values;
    private final boolean hiddenRow1;
    private final boolean hiddenRow2;
    private final boolean hiddenRow3;

    static class ViewHolder {
        public TextView lineOne;
        public TextView lineTwo;
        public TextView lineThree;
    }


    public ThreeLineOptionalHiddenArrayAdapter(Activity context, List<String[]> values,
                                               boolean hiddenRow1, boolean hiddenRow2, boolean hiddenRow3) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.hiddenRow1 = hiddenRow1;
        this.hiddenRow2 = hiddenRow2;
        this.hiddenRow3 = hiddenRow3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getLayoutInflater();

            rowView = inflater.inflate(R.layout.three_line_listview_row, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.lineOne = (TextView) rowView.findViewById(R.id.three_line_listView_row_first_line);
            viewHolder.lineTwo = (TextView) rowView.findViewById(R.id.three_line_listView_row_second_line);
            viewHolder.lineThree = (TextView) rowView.findViewById(R.id.three_line_listView_row_third_line);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.lineOne.setText(values.get(position)[0]);
        viewHolder.lineTwo.setText(values.get(position)[1]);
        viewHolder.lineThree.setText(values.get(position)[2]);

        if (hiddenRow1)
            viewHolder.lineOne.setVisibility(View.GONE);
        if (hiddenRow2)
            viewHolder.lineTwo.setVisibility(View.GONE);
        if (hiddenRow3)
            viewHolder.lineThree.setVisibility(View.GONE);


        return rowView;
    }
}
