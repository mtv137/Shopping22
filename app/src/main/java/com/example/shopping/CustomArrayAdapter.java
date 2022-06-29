package com.example.shopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<ListItemDress> {
    private LayoutInflater inflater;
    private List<ListItemDress> listItem = new ArrayList<>();
    private Context context;  // лишнее


    public CustomArrayAdapter(@NonNull Context context, int resource, List<ListItemDress> listItem, LayoutInflater inflater) {
        super(context, resource, listItem);
        this.inflater = inflater;
        this.listItem = listItem;
        this.context = context; //лишняя
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        ListItemDress listItemMain = listItem.get(position);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_1, null, false);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.tvName);
            viewHolder.price = convertView.findViewById(R.id.tvPrice);
            viewHolder.hyperlink = convertView.findViewById(R.id.tvHyperlink);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(listItemMain.getTitleDress());
        viewHolder.price.setText(listItemMain.getPriceDress());
        viewHolder.hyperlink.setText(listItemMain.getHyperlinkDress());

        return convertView;
    }

    //******
    public void updateData(List<ListItemDress> listItem){
        this.listItem = listItem;
        this.notifyDataSetChanged();
    }
    //******
    private class ViewHolder{
        TextView name;
        TextView price;
        TextView hyperlink;
    }
}
