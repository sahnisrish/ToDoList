package com.example.sahni.todolist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sahni on 16/2/18.
 */

public class Adapter extends BaseAdapter {
    ArrayList<ListItem> list;
    Context context;
    CheckedListener checkedListener;
    ItemLongClickedListener itemLongClickedListener;
    ItemClickedListener tagClickedListener;
    ItemClickedListener itemClickedListener;
    interface CheckedListener extends CompoundButton.OnCheckedChangeListener {

        @Override
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }
    interface ItemClickedListener extends View.OnClickListener{

    }
    interface ItemLongClickedListener extends View.OnLongClickListener{

    }
    Adapter(Context context, ArrayList<ListItem> list, CheckedListener checkedListener, ItemClickedListener itemClickedListener,ItemLongClickedListener itemLongClickedListener,ItemClickedListener tagClickedListener){
        this.context=context;
        this.list=list;
        this.checkedListener=checkedListener;
        this.itemClickedListener=itemClickedListener;
        this.itemLongClickedListener=itemLongClickedListener;
        this.tagClickedListener=tagClickedListener;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ListItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, parent, false);
            ViewHolder holder=new ViewHolder();
            holder.item=view.findViewById(R.id.ToDoLayout);
            holder.name=view.findViewById(R.id.item_name);
            holder.deadline=view.findViewById(R.id.item_deadline);
            holder.completed=view.findViewById(R.id.Completed);
            holder.tagsBar=view.findViewById(R.id.tagsBar);
            holder.completed.setOnCheckedChangeListener(checkedListener);
            holder.item.setOnClickListener(itemClickedListener);
            holder.item.setOnLongClickListener(itemLongClickedListener);
            holder.item.setTag(list.get(position).getId());
            view.setTag(holder);
        }
        ViewHolder holder=(ViewHolder)view.getTag();
        if((list.get(position).getDeadLineLong()<System.currentTimeMillis())&&(!Constant.format.format(list.get((position)).getDeadLineLong()).equals(Constant.format.format(System.currentTimeMillis()))))
        {
            holder.item.setBackgroundColor(holder.item.getResources().getColor(R.color.deselectedColor));
            holder.completed.setBackgroundColor(holder.item.getResources().getColor(R.color.deselectedColor));
        }
        else
        {
            int colorId;
            switch (list.get(position).getPriority())
            {
                case Constant.PRIORITY.HIGH:
                    colorId=R.color.priorityHigh;
                    break;
                case Constant.PRIORITY.MED:
                    colorId=R.color.priorityMedium;
                    break;
                case Constant.PRIORITY.LOW:
                    colorId=R.color.priorityLow;
                    break;
                default:
                    colorId=R.color.itemBackground;
            }
            holder.item.setBackgroundColor(holder.item.getResources().getColor(colorId));
            holder.completed.setBackgroundColor(holder.item.getResources().getColor(colorId));
        }
        holder.item.setTag(list.get(position).getId());
        holder.name.setText(list.get(position).getItemName());
        holder.deadline.setText(list.get(position).getDeadLine());
        holder.completed.setChecked(false);
        holder.completed.setTag(list.get(position).getId());
        holder.tagsBar.removeAllViews();
        if(list.get(position).getPriority()!= Constant.PRIORITY.NONE) {
            TagView priority = new TagView(context, list.get(position).getPriority());
            priority.addTag(holder.tagsBar, tagClickedListener, null);
        }
        TagView.addMultipleTags(context,holder.tagsBar,list.get(position).getId(),tagClickedListener,null);
        return view;
    }
    class ViewHolder{
        TextView name;
        TextView deadline;
        CheckBox completed;
        LinearLayout item;
        LinearLayout tagsBar;
    }
}
