package com.you.signaldemo;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Fgb on 2016/10/19.
 *
 */

public class MyAdapter extends BaseAdapter {
    private List<File> mData;
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder viewHolder;
    private HashMap<Integer, Integer> isVisibleMap;// 用来控制CheckBox的显示状况
    private HashMap<Integer, Boolean> isCheckMap;

    public MyAdapter(Context context,List<File> mData){
        this.context=context;
        this.mInflater=LayoutInflater.from(context);
        this.mData=mData;
        isVisibleMap=new HashMap<>();
        isCheckMap=new HashMap<>();
        initDate();
    }


    @Override
    public int getCount() {
        int number = 0;
        if (mData!= null) {
            number = mData.size();
        }
        return number;
    }

    @Override
    public Object getItem(int position) {
        return null!=mData.get(position)?mData.get(position):null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 初始化isSelectedMap的数据
    private void initDate() {
        for (int i = 0; i < mData.size(); i++) {
            getIsVisibleMap().put(i,CheckBox.GONE);
            getIsCheckMap().put(i,false);
        }
    }


    public HashMap<Integer, Integer> getIsVisibleMap() {
        return isVisibleMap;
    }
    public HashMap<Integer,Boolean> getIsCheckMap(){
        return isCheckMap;
    }
    private class ViewHolder{
        private TextView fileName;
        private TextView modifyName;
        private CheckBox checkBox;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final File file= (File) getItem(position);
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.pic_list_view,null);
            viewHolder=new ViewHolder();
            viewHolder.fileName= (TextView) convertView.findViewById(R.id.tvFileName);
            viewHolder.modifyName= (TextView) convertView.findViewById(R.id.tvModifyTime);
            viewHolder.checkBox= (CheckBox) convertView.findViewById(R.id.the_checkbox);
            final ViewHolder finalViewHolder=viewHolder;
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int radiaoId = (int) finalViewHolder.checkBox.getTag();
                    if(isChecked)
                    {
                        //将选中的放入hashmap中
                        isCheckMap.put(radiaoId, true);
                    }else {
                        isCheckMap.put(radiaoId,false);
                    }
                }
            });
            convertView.setTag(viewHolder);
            viewHolder.checkBox.setTag(position);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
            viewHolder.checkBox.setVisibility(getIsVisibleMap().get(position));
            viewHolder.checkBox.setTag(position);
        }
        viewHolder.fileName.setText(file.getName());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String time=simpleDateFormat.format(new Date(file.lastModified()));
        viewHolder.modifyName.setText(time);
        //找到需要选中的条目
        if(isCheckMap!=null && isCheckMap.containsKey(position))
        {
            viewHolder.checkBox.setChecked(isCheckMap.get(position));
        }
        else
        {
            viewHolder.checkBox.setChecked(false);
        }
        return convertView;
    }
}
