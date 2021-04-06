package com.huy9515gmail.newemercallmonitoring;

/**
 * Created by Admin on 01-12-17.
 */

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
public class AmbulanceCustomAdapter extends ArrayAdapter<Ambulance> {

    private Context context;
    private int resource;
    private List<Ambulance> ambulanceList;

    public AmbulanceCustomAdapter(Context context, int resource, ArrayList<Ambulance> ambulanceList) {
        super(context, resource, ambulanceList);
        this.context = context;
        this.resource = resource;
        this.ambulanceList = ambulanceList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_ambulance_listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = convertView.findViewById(R.id.tvNumber);
            viewHolder.tvStatus = convertView.findViewById(R.id.tvStatus);
            viewHolder.tvAvatar = convertView.findViewById(R.id.tvAvatar);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Ambulance currentAmbulance = ambulanceList.get(position);
        viewHolder.tvAvatar.setText(String.valueOf(position+1));
        viewHolder.tvNumber.setText(currentAmbulance.getNumber());
        viewHolder.tvStatus.setText((currentAmbulance.getStatus() == true) ? ("Unavailable") : ("Available"));
        return convertView;
    }

    public class ViewHolder {
        TextView tvNumber, tvStatus, tvAvatar;
    }
}