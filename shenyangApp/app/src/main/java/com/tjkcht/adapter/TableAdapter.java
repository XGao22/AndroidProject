package com.tjkcht.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tjkcht.pojo.MaterialsBarCode;
import com.tjkcht.wulingapp.R;

import java.util.List;

public class TableAdapter extends BaseAdapter {
    private List<MaterialsBarCode> list;
    private LayoutInflater inflater;

    public TableAdapter(Context context, List<MaterialsBarCode> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(list!=null){
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MaterialsBarCode materials = (MaterialsBarCode) this.getItem(position);

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.bar_code_item, null);
            viewHolder.barCode = (TextView) convertView.findViewById(R.id.tv_barCode);
            viewHolder.materialCode = (TextView) convertView.findViewById(R.id.tv_materialCode);
            viewHolder.materialName = (TextView) convertView.findViewById(R.id.tv_materialName);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.barCode.setText(materials.getBarCode());
        viewHolder.barCode.setTextSize(13);
        viewHolder.materialCode.setText(materials.getMaterialCode());
        viewHolder.materialCode.setTextSize(13);
        viewHolder.materialName.setText(materials.getMaterialName());
        viewHolder.materialName.setTextSize(13);

        return convertView;
    }

    public static class ViewHolder{
        public TextView barCode;
        public TextView materialCode;
        public TextView materialName;
    }
}
