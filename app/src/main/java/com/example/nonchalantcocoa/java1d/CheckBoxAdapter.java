package com.example.nonchalantcocoa.java1d;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckBoxAdapter extends ArrayAdapter<String> {

    // Map for checking the state of the items
    private Map itemStateMap= new HashMap<String,Boolean>();

    public CheckBoxAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i("Logcat", "getView() is called");
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_cuisine, parent, false);
        }

        final CheckBox cuisineCheckBox = (CheckBox) convertView.findViewById(R.id.cuisineCheckBox);

        final String currentCuisine = getItem(position);
        if (currentCuisine != null){
            cuisineCheckBox.setVisibility(View.VISIBLE);
            Log.i("Logcat", "Adapter set visibility: " + currentCuisine);
            cuisineCheckBox.setText(currentCuisine);
            cuisineCheckBox.setChecked(true);
            itemStateMap.put(currentCuisine, true);

            cuisineCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemStateMap.put(currentCuisine, cuisineCheckBox.isChecked());
                    Log.i("Logcat", "Set " + currentCuisine + " to " + cuisineCheckBox.isChecked());
                }
            });
        }

        return convertView;
    }

    public Map getItemStateMap(){
        return this.itemStateMap;
    }


}
