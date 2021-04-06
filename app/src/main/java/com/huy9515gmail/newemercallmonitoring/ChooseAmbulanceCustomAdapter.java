package com.huy9515gmail.newemercallmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Welcome on 8/27/2016.
 */
public class ChooseAmbulanceCustomAdapter extends ArrayAdapter<Ambulance> {

    private Context context;
    private int resource;
    private List<Ambulance> availableAmbulanceList;

    public ChooseAmbulanceCustomAdapter(Context context, int resource, ArrayList<Ambulance> availableAmbulanceList) {
        super(context, resource, availableAmbulanceList);
        this.context = context;
        this.resource = resource;
        this.availableAmbulanceList = availableAmbulanceList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_chooseambulance_listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = convertView.findViewById(R.id.tvNumber);
            viewHolder.tvTimes = convertView.findViewById(R.id.tvTimes);
            viewHolder.tvAvatar = convertView.findViewById(R.id.tvAvatar);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Ambulance currentAmbulance = availableAmbulanceList.get(position);
        viewHolder.tvAvatar.setText(String.valueOf(position+1));
        viewHolder.tvNumber.setText(currentAmbulance.getNumber().toString());
        viewHolder.tvTimes.setText(currentAmbulance.getTimes() + "");
        return convertView;
    }

    public class ViewHolder {
        TextView tvNumber, tvTimes, tvAvatar;
    }
}