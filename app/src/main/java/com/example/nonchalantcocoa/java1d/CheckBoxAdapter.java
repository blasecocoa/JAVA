package com.example.nonchalantcocoa.java1d;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckBoxAdapter extends ArrayAdapter<Cuisine> {

    // sparse boolean array for checking the state of the items
    private Map itemStateMap= new HashMap<String,Boolean>();

    public CheckBoxAdapter(Context context, int resource, List<Cuisine> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i("Logcat", "getView() is called");
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_cuisine, parent, false);
        }

        final CheckBox cuisineCheckBox = (CheckBox) convertView.findViewById(R.id.cuisineCheckBox);

        final Cuisine currentCuisine = getItem(position);
        if (currentCuisine != null){
            cuisineCheckBox.setVisibility(View.VISIBLE);
            Log.i("Logcat", "Adapter set visibility: " + currentCuisine.getText());
            cuisineCheckBox.setText(currentCuisine.getText());
            cuisineCheckBox.setChecked(true);
            itemStateMap.put(currentCuisine.getText(), true);

            cuisineCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemStateMap.put(currentCuisine.getText(), cuisineCheckBox.isChecked());
                    Log.i("Logcat", "Set " + currentCuisine.getText() + " to " + cuisineCheckBox.isChecked());
                }
            });
        }

        return convertView;
    }

    public Map getItemStateMap(){
        return this.itemStateMap;
    }


}
