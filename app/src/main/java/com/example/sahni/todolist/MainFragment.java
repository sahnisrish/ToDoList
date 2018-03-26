package com.example.sahni.todolist;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements ListAdapterRecycle.CheckedListener, ListAdapterRecycle.ItemClickedListener, ListAdapterRecycle.ItemLongClickedListener, ListAdapterRecycle.TagClickedListener {
    //Views
    RecyclerView RecyclerListView;
    TagView tag;
    TextView tagDetails;
    //List
    ArrayList<ListItem> list;
    ListAdapterRecycle adapter;
    //Database
    ItemOpenHelper openHelper;
    SQLiteDatabase database;
    //Tag
    boolean hasTag=false;
    //Activity
    Context activity;
    //Add
    Add addItem;
    Display displayItem;
    Edit editItem;
    TagDescription tagDescription;
    Bundle bundle;

    interface Add{
        void CallAdd(boolean hasTag,TagView tag);
    }
    interface Display{
        void CallDisplay(Bundle bundle);
    }
    interface Edit{
        void CallEdit(Bundle bundle);
    }
    interface TagDescription{
        void setTagDescription(int i,TextView tagDetails);
    }

    public MainFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity=context;
        try {
            addItem=(Add)activity;
            editItem=(Edit)activity;
            displayItem=(Display)activity;
            tagDescription =(TagDescription)activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Activity should implement Listeners");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main, container, false);
        openHelper=ItemOpenHelper.getInstance(activity);
        RecyclerListView=rootView.findViewById(R.id.List);
        list=fetchData();
        adapter=new ListAdapterRecycle(activity, list, this, this, this, this);
        RecyclerListView.setAdapter(adapter);
        RecyclerListView.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        RecyclerListView.addItemDecoration(new DividerItemDecoration(activity,DividerItemDecoration.VERTICAL));
        return rootView;
    }

    void DeleteExtraTags() {
        database=openHelper.getWritableDatabase();
        Cursor cursor=database.query(Contract.TagsList.TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            int tagId=cursor.getInt(cursor.getColumnIndex(Contract.TagsList.ID));
            final String Query="SELECT * FROM "+Contract.ItemList.TABLE_NAME+", "+Contract.TagAssignment.TABLE_NAME+
                    " WHERE "+Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.ID+"="+Contract.TagAssignment.TABLE_NAME+"."+Contract.TagAssignment.ITEM_ID+
                    " AND "+Contract.TagAssignment.TABLE_NAME+"."+Contract.TagAssignment.TAG_ID+"="+tagId;
            Cursor cursor1=database.rawQuery(Query,null);
            if(!cursor1.moveToNext())
                database.delete(Contract.TagsList.TABLE_NAME, Contract.TagsList.ID+"=?", new String[]{tagId + ""});
        }
    }

    private ArrayList<ListItem> fetchData() {
        ArrayList<ListItem> listItems=new ArrayList<>();
        database=openHelper.getReadableDatabase();
        Cursor cursor;
        if(hasTag) {
            int tagId=tag.getId();
            if(tag.isPriority())
                cursor=database.query(Contract.ItemList.TABLE_NAME,null,Contract.ItemList.PRIORITY+"=?", new String[]{tagId + ""},null,null,Contract.ItemList.PRIORITY + " DESC, "+Contract.ItemList.DEADLINE);
            else
            {
                final String Query="SELECT "+Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.ID+", "+
                        Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.DEADLINE+", "+
                        Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.PRIORITY+", "+
                        Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.ITEM+" FROM "+
                        Contract.ItemList.TABLE_NAME+", "+Contract.TagAssignment.TABLE_NAME+
                        " WHERE "+Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.ID+"="+Contract.TagAssignment.TABLE_NAME+"."+Contract.TagAssignment.ITEM_ID+
                        " AND "+Contract.TagAssignment.TABLE_NAME+"."+Contract.TagAssignment.TAG_ID+"="+tagId+" ORDER BY "+Contract.ItemList.PRIORITY + " DESC, "+Contract.ItemList.DEADLINE;
                cursor=database.rawQuery(Query,null);
            }
        }
        else
            cursor=database.query(Contract.ItemList.TABLE_NAME,null,null,null,null,null,Contract.ItemList.PRIORITY + " DESC, "+Contract.ItemList.DEADLINE);
        while(cursor.moveToNext()){
            ListItem listItem=new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                    cursor.getLong(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                    cursor.getInt(cursor.getColumnIndex(Contract.ItemList.PRIORITY)),
                    cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
            listItems.add(listItem);
        }
        return listItems;
    }
    @Override
    public void onClick(View v) {
        tag=new TagView(activity,(TagView) v);
        tagDetails=new TextView(activity);
        tagDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        tagDetails.setPadding(20,20,0,10);
        tagDetails.setTextColor(tagDetails.getResources().getColor(R.color.tagText));
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tagDetails.setLayoutParams(layoutParams);
        list.clear();
        hasTag=true;
        list.addAll(fetchData());
        tagDetails.setText(list.size()+" items opened");
        tagDescription.setTagDescription(Constant.TAGS_SELECTED,tagDetails);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckChanged(int position,boolean isChecked) {
        if(isChecked){
            int id=list.get(position).getId();
            database=openHelper.getWritableDatabase();
            database.delete(Contract.TagAssignment.TABLE_NAME,Contract.TagAssignment.ITEM_ID+" = ?", new String[]{id + ""});
            DeleteExtraTags();
            database.delete(Contract.ItemList.TABLE_NAME,Contract.ItemList.ID+"=?", new String[]{id + ""});
            list.remove(position);
            if(hasTag)
                tagDetails.setText(list.size()+" items opened");
            adapter.notifyItemRemoved(position);
            CancelNotification(id);
        }
    }

    private void CancelNotification(int id) {
        AlarmManager alarm= (AlarmManager)activity.getSystemService(ALARM_SERVICE);
        Intent intent=new Intent(activity,Receiver.class);
        intent.putExtra(Constant.ID_KEY,id);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(activity,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if(pendingIntent!=null) {
            alarm.cancel(pendingIntent);
            Log.e("Cancel", "CancelNotification: "+id );
        }
        NotificationManager notificationManager=(NotificationManager) activity.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    @Override
    public void onItemClick(int position) {
        bundle = new Bundle();
        bundle.putInt(Constant.ID_KEY, list.get(position).getId());
        bundle.putInt(Constant.REQUEST_KEY, Constant.REQUEST_EDIT);
        displayItem.CallDisplay(bundle);
    }

    @Override
    public void onItemLongClick(int position) {
        bundle=new Bundle();
        bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_EDIT);
        bundle.putInt(Constant.ID_KEY,list.get(position).getId());
        editItem.CallEdit(bundle);
    }

    public void result(int requestCode, int resultCode, Intent data) {
        if((resultCode==Constant.RESULT_ADD)&&(requestCode==Constant.REQUEST_ADD)){
            bundle=data.getExtras();
            if(((bundle.getBoolean(Constant.HAS_TAG)||bundle.getBoolean(Constant.HAS_PRIORITY))&&hasTag)||(!hasTag)) {
                int id=bundle.getInt(Constant.ID_KEY,-1);
                database = openHelper.getReadableDatabase();
                String[] args = {id + ""};
                Cursor cursor = database.query(Contract.ItemList.TABLE_NAME, null, Contract.ItemList.ID + " =?", args, null, null, null);
                cursor.moveToFirst();
                ListItem listItem = new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                        cursor.getLong(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                        cursor.getInt(cursor.getColumnIndex(Contract.ItemList.PRIORITY)),
                        cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
                int pos;
                for (pos = 0; pos < list.size(); pos++)
                    if (!list.get(pos).liesAbove(listItem))
                        break;
                list.add(pos, listItem);
                adapter.notifyItemInserted(pos);
                if(hasTag)
                    tagDetails.setText(list.size()+" items opened");
            }
        }
        else if(((requestCode==Constant.REQUEST_EDIT)||(requestCode==Constant.REQUEST_DISPLAY))&&(resultCode==Constant.RESULT_EDIT)){
            bundle=data.getExtras();
            int id=bundle.getInt(Constant.ID_KEY,-1);
            if(bundle.getBoolean(Constant.EDIT)) {
                Log.e("INTENT", "onActivityResult: "+Constant.EDIT);
                database = openHelper.getReadableDatabase();
                String[] args = {id + ""};
                Cursor cursor = database.query(Contract.ItemList.TABLE_NAME, null, Contract.ItemList.ID + " =?", args, null, null, null);
                cursor.moveToFirst();
                ListItem listItem = new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                        cursor.getLong(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                        cursor.getInt(cursor.getColumnIndex(Contract.ItemList.PRIORITY)),
                        cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
                int pos;
                for (pos = 0; pos < list.size(); pos++)
                    if (list.get(pos).getId() == id)
                        break;
                list.remove(pos);
                adapter.notifyItemRemoved(pos);
                for (pos = 0; pos < list.size(); pos++)
                    if (!list.get(pos).liesAbove(listItem))
                        break;
                list.add(pos, listItem);
                adapter.notifyItemInserted(pos);
            }
        }
        DeleteExtraTags();
    }

    public void modifyData() {
        list.clear();
        tagDescription.setTagDescription(Constant.NO_TAGS_SELECTED,null);
        list.addAll(fetchData());
        adapter.notifyDataSetChanged();
    }

    public void add() {
        addItem.CallAdd(hasTag,tag);
    }

}
