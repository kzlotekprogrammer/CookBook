package pl.cookbook.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.entities.Unit;

public class UnitsAdapter extends ArrayAdapter<Unit> {

    public UnitsAdapter(Context context, List<Unit> unitList) {
        super(context, 0, unitList);
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Unit unit = (Unit) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent,false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(unit.name);
        return convertView;
    }

    public @NonNull View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        Unit unit = (Unit) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent,false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(unit.name);
        return convertView;
    }
}
