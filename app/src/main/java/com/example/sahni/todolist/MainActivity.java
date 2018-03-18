package com.example.sahni.todolist;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListAdapterRecycle.TagClickedListener, ListAdapterRecycle.CheckedListener, ListAdapterRecycle.ItemClickedListener, ListAdapterRecycle.ItemLongClickedListener {
    //Views
    RecyclerView RecyclerListView;
    android.support.v7.widget.Toolbar toolbar;
    LinearLayout titleTag;
    AppBarLayout appBar;
    TextView tagDetails;
    TagView tag;
    Menu menu;
    //List
    ArrayList<ListItem> list;
    ListAdapterRecycle adapter;
    //Database
    ItemOpenHelper openHelper;
    SQLiteDatabase database;
    //Tag
    boolean hasTag=false;
    //DataTransfer
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        //MenuItemAdded
        final FloatingActionButton add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallAdd();
            }
        });
        appBar=findViewById(R.id.appBar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(menu!=null) {
                    if ((verticalOffset>=0)&&(verticalOffset<=appBarLayout.getHeight())&&(add.getVisibility()==View.VISIBLE))
                        menu.findItem(Constant.MenuID.ADD).setVisible(false);
                    else
                        menu.findItem(Constant.MenuID.ADD).setVisible(true);
                }
            }

        });
        openHelper=ItemOpenHelper.getInstance(this);
        titleTag=findViewById(R.id.titleTag);

        RecyclerListView=findViewById(R.id.List);
        list=fetchData();
        adapter=new ListAdapterRecycle(this, list, this, this, this, this);
        RecyclerListView.setAdapter(adapter);
        RecyclerListView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        RecyclerListView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void CallAdd() {
        Intent intent = new Intent(MainActivity.this, AddItem.class);
        bundle=new Bundle();
        bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_ADD);
        if((hasTag)&&(!tag.isPriority()))
        {
            bundle.putBoolean(Constant.HAS_TAG,true);
            bundle.putString(Constant.TAG,tag.getTitle());
            bundle.putInt(Constant.TAG_ID,tag.getId());
        }
        else if((hasTag)&&(tag.isPriority()))
        {
            bundle.putBoolean(Constant.HAS_PRIORITY,true);
            bundle.putInt(Constant.Priority,tag.getId());
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, Constant.REQUEST_ADD);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        this.menu=menu;
        menu.add(Menu.NONE, Constant.MenuID.ADD,Menu.NONE,"Add");
        MenuItem add=menu.findItem(Constant.MenuID.ADD);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        add.setIcon(R.drawable.ic_add_black_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==Constant.MenuID.ADD)
            CallAdd();
        return super.onOptionsItemSelected(item);
    }

    private void DeleteExtraTags() {
        database=openHelper.getWritableDatabase();
        Cursor cursor=database.query(Contract.TagsList.TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            int tagId=cursor.getInt(cursor.getColumnIndex(Contract.TagsList.ID));
            final String Query="SELECT * FROM "+Contract.ItemList.TABLE_NAME+", "+Contract.TagAssignment.TABLE_NAME+
                    " WHERE "+Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.ID+"="+Contract.TagAssignment.TABLE_NAME+"."+Contract.TagAssignment.ITEM_ID+
                    " AND "+Contract.TagAssignment.TABLE_NAME+"."+Contract.TagAssignment.TAG_ID+"="+tagId;
            Cursor cursor1=database.rawQuery(Query,null);
            if(cursor1.moveToNext()){

            }
            else
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((resultCode==Constant.RSULT_ADD)&&(requestCode==Constant.REQUEST_ADD)){
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
        else if((requestCode==Constant.REQUEST_EDIT)&&(resultCode==Constant.RSULT_EDIT)){
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
                list.add(pos, listItem);
                adapter.notifyItemChanged(pos);
            }
        }
        DeleteExtraTags();
    }

    private void CancelNotification(int id) {
        AlarmManager alarm= (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent=new Intent(this,Receiver.class);
        intent.putExtra(Constant.ID_KEY,id);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,Constant.REQUEST_NOTIFY,intent,PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent!=null)
            alarm.cancel(pendingIntent);
    }

    @Override
    public void onClick(View v) {
            tag=new TagView(this,(TagView) v);
            titleTag.removeAllViews();
            tagDetails=new TextView(this);
            tagDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            tagDetails.setPadding(20,20,0,10);
            tagDetails.setTextColor(tagDetails.getResources().getColor(R.color.tagText));
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tagDetails.setLayoutParams(layoutParams);
            list.clear();
            hasTag=true;
            list.addAll(fetchData());
            tagDetails.setText(list.size()+" items opened");
            titleTag.addView(tagDetails);
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(hasTag==true)
        {
            hasTag=false;
            list.clear();
            titleTag.removeAllViews();
            list.addAll(fetchData());
            adapter.notifyDataSetChanged();
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onStop() {
        DeleteExtraTags();
        super.onStop();
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DispalyItem.class);
        bundle = new Bundle();
        bundle.putInt(Constant.ID_KEY, list.get(position).getId());
        bundle.putInt(Constant.REQUEST_KEY, Constant.REQUEST_EDIT);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constant.REQUEST_EDIT);
    }

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(this, AddItem.class);
        bundle=new Bundle();
        bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_EDIT);
        bundle.putInt(Constant.ID_KEY,list.get(position).getId());
        intent.putExtras(bundle);
        startActivityForResult(intent, Constant.REQUEST_EDIT);
    }
}
