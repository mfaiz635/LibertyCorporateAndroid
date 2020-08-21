package libertypassage.com.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import libertypassage.com.app.R;


public class GenderAdapter extends ArrayAdapter<String> {
    private Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/CenturyGothic.ttf");


    public GenderAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(font);
        return view;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(font);
        if (position == 0) {
            // Set the hint text color gray
            view.setTextColor(getContext().getResources().getColor(R.color.white));
        } else {
            view.setTextColor(getContext().getResources().getColor(R.color.white));
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0) {
            return false;
        }
        return true;
    }
}
