package com.huy9515gmail.newemercallmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserCustomAdapter extends ArrayAdapter<User> {

    private Context context;
    private int resource;
    private List<User> userList;

    public UserCustomAdapter(Context context, int resource, ArrayList<User> userList) {
        super(context, resource, userList);
        this.context = context;
        this.resource = resource;
        this.userList = userList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_user_listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.tvBHYT = convertView.findViewById(R.id.tvBHYT);
            viewHolder.tvCMND = convertView.findViewById(R.id.tvCMND);
            viewHolder.tvAvatar = convertView.findViewById(R.id.tvAvatar);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        User curUser = userList.get(position);
        viewHolder.tvAvatar.setText(String.valueOf(position+1));
        viewHolder.tvName.setText(curUser.getName());
        viewHolder.tvBHYT.setText(curUser.getBHYT());
        viewHolder.tvCMND.setText(curUser.getCMND());
        return convertView;
    }

    public class ViewHolder {
        TextView tvName, tvBHYT, tvCMND, tvAvatar;
    }
}