package com.example.sahni.todolist;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Adapter.CheckedListener, Adapter.ItemClickedListener, Adapter.ItemLongClickedListener{
    ListView listView;
    ArrayList<ListItem> list;
    Adapter adapter;
    ItemOpenHelper openHelper;
    SQLiteDatabase database;
    TextView tag;
    boolean hasTag=false;
    LinearLayout titleTag;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openHelper=ItemOpenHelper.getInstance(this);
        DeleteExtraTags();
        listView=findViewById(R.id.ListView);
        titleTag=findViewById(R.id.titleTag);
        list=fetchData();
        adapter=new Adapter(this,list,this,this,this,this);
        listView.setAdapter(adapter);
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
            int tagId=(int)tag.getTag();
            final String Query="SELECT "+Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.ID+", "+
                    Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.DEADLINE+", "+
                    Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.ITEM+" FROM "+
                    Contract.ItemList.TABLE_NAME+", "+Contract.TagAssignment.TABLE_NAME+
                    " WHERE "+Contract.ItemList.TABLE_NAME+"."+Contract.ItemList.ID+"="+Contract.TagAssignment.TABLE_NAME+"."+Contract.TagAssignment.ITEM_ID+
                    " AND "+Contract.TagAssignment.TABLE_NAME+"."+Contract.TagAssignment.TAG_ID+"="+tagId;
            cursor=database.rawQuery(Query,null);
        }
        else
            cursor=database.query(Contract.ItemList.TABLE_NAME,null,null,null,null,null,Contract.ItemList.DEADLINE);
        while(cursor.moveToNext()){
            ListItem listItem=new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                    cursor.getInt(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                    cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
            listItems.add(listItem);
        }
        return listItems;
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
        int id=(int)buttonView.getTag();
        if(isChecked){
            deleteItem(id);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        menu.add(Menu.NONE,Constant.MenuID.ADD,Menu.NONE,"ADD");
        MenuItem add=menu.findItem(Constant.MenuID.ADD);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==Constant.MenuID.ADD) {
                Intent intent = new Intent(this, AddItem.class);
                bundle=new Bundle();
                bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_ADD);
                if(hasTag)
                {
                    bundle.putBoolean(Constant.HAS_TAG,true);
                    bundle.putString(Constant.TAG,tag.getText().toString());
                    bundle.putInt(Constant.TAG_ID,(int)tag.getTag());
                }
                intent.putExtras(bundle);
                startActivityForResult(intent, Constant.REQUEST_ADD);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((resultCode==Constant.RSULT_ADD)&&(requestCode==Constant.REQUEST_ADD)){
            bundle=data.getExtras();
            if((bundle.getBoolean(Constant.HAS_TAG)&&hasTag)||(!hasTag)) {
                int id=bundle.getInt(Constant.ID_KEY,-1);
                database = openHelper.getReadableDatabase();
                String[] args = {id + ""};
                Cursor cursor = database.query(Contract.ItemList.TABLE_NAME, null, Contract.ItemList.ID + " =?", args, null, null, null);
                cursor.moveToFirst();
                ListItem listItem = new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                        cursor.getInt(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                        cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
                int pos;
                for (pos = 0; pos < list.size(); pos++)
                    if (!list.get(pos).liesAbove(listItem))
                        break;
                list.add(pos, listItem);
            }
        }
        else if((requestCode==Constant.REQUEST_EDIT)&&(resultCode==Constant.RSULT_EDIT)){
            bundle=data.getExtras();
            int id=bundle.getInt(Constant.ID_KEY,-1);
            if(bundle.getBoolean(Constant.EDIT)) {
                database = openHelper.getReadableDatabase();
                String[] args = {id + ""};
                Cursor cursor = database.query(Contract.ItemList.TABLE_NAME, null, Contract.ItemList.ID + " =?", args, null, null, null);
                cursor.moveToFirst();
                ListItem listItem = new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                        cursor.getInt(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                        cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
                int pos;
                for (pos = 0; pos < list.size(); pos++)
                    if (list.get(pos).getId() == id)
                        break;
                list.remove(pos);
                list.add(pos, listItem);
            }
        }
        DeleteExtraTags();
        adapter.notifyDataSetChanged();
    }

    private void deleteItem(int id) {
        database=openHelper.getWritableDatabase();
        database.delete(Contract.TagAssignment.TABLE_NAME,Contract.TagAssignment.ITEM_ID+" = ?", new String[]{id + ""});
        DeleteExtraTags();
        database.delete(Contract.ItemList.TABLE_NAME,Contract.ItemList.ID+"=?", new String[]{id + ""});
        list.clear();
        list.addAll(fetchData());
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        if(v instanceof TextView)
        {
            titleTag.removeAllViews();
            tag=new TextView(this);
            tag.setText(((TextView) v).getText());
            tag.setTag(v.getTag());
            tag.setBackgroundColor(tag.getResources().getColor(R.color.colorPrimary));
            tag.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            tag.setTextColor(tag.getResources().getColor(R.color.tagBar));
            tag.setPadding(20,20,0,10);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tag.setLayoutParams(layoutParams);
            titleTag.addView(tag);
            list.clear();
            hasTag=true;
            list.addAll(fetchData());
            adapter.notifyDataSetChanged();

        }
        else if(v instanceof LinearLayout)
        {
            Intent intent = new Intent(this, DispalyItem.class);
            bundle = new Bundle();
            bundle.putInt(Constant.ID_KEY, (Integer) v.getTag());
            bundle.putInt(Constant.REQUEST_KEY, Constant.REQUEST_EDIT);
            intent.putExtras(bundle);
            startActivityForResult(intent, Constant.REQUEST_EDIT);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Intent intent = new Intent(this, AddItem.class);
        bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_EDIT);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constant.REQUEST_EDIT);
        return true;
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
}
