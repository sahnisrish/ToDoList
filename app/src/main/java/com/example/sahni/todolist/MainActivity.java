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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Adapter.CheckedListener, Adapter.ItemClickedListener {
    ListView listView;
    ArrayList<ListItem> list;
    ArrayList<Integer> toBeDeleted;
    Adapter adapter;
    ItemOpenHelper openHelper;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.ListView);
        openHelper=ItemOpenHelper.getInstance(this);
        list=fetchData();
        toBeDeleted=new ArrayList<>();
        adapter=new Adapter(this,list,this,this);
        listView.setAdapter(adapter);
    }

    private ArrayList<ListItem> fetchData() {
        ArrayList<ListItem> listItems=new ArrayList<>();
        SQLiteDatabase database=openHelper.getReadableDatabase();
        Cursor cursor=database.query(Contract.ItemList.TABLE_NAME,null,null,null,null,null,Contract.ItemList.DEADLINE);
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
        Adapter.Tag tag=(Adapter.Tag)buttonView.getTag();
        int id=tag.id;
        LinearLayout item=tag.item;
        if(isChecked){
            toBeDeleted.add(id);
            item.setBackgroundResource(R.drawable.selected_list_item);
            TextView text=item.findViewById(R.id.item_name);
            text.setPaintFlags(text.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
            text.setTextColor(getResources().getColor(R.color.colorAccent));
            item.setEnabled(false);
        }
        else{
            item.setBackgroundResource(R.drawable.list_item);
            if(toBeDeleted.contains(id))
                toBeDeleted.remove(toBeDeleted.indexOf(id));
            TextView text=item.findViewById(R.id.item_name);
            text.setPaintFlags(text.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
            text.setTextColor(getResources().getColor(R.color.textColor));
            item.setEnabled(true);
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
                if(toBeDeleted.size()>0)
                    deleteItems();
                Intent intent = new Intent(this, AddItem.class);
                startActivityForResult(intent, Constant.REQUEST_ADD);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((resultCode==Constant.RSULT_ADD)&&(requestCode==Constant.REQUEST_ADD)){
            bundle=data.getExtras();
            int id=bundle.getInt(Constant.ID_KEY,-1);
            SQLiteDatabase database=openHelper.getReadableDatabase();
            String[] args={id+""};
            Cursor cursor=database.query(Contract.ItemList.TABLE_NAME,null,Contract.ItemList.ID+" =?",args,null,null,null);
            cursor.moveToFirst();
            ListItem listItem=new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                    cursor.getInt(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                    cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
            int pos;
            for(pos=0;pos<list.size();pos++)
                if(!list.get(pos).liesAbove(listItem))
                    break;
            list.add(pos,listItem);
            adapter.notifyDataSetChanged();
        }
        else if((requestCode==Constant.REQUEST_EDIT)&&(resultCode==Constant.RSULT_EDIT)){
            bundle=data.getExtras();
            int id=bundle.getInt(Constant.ID_KEY,-1);
            SQLiteDatabase database=openHelper.getReadableDatabase();
            String[] args={id+""};
            Cursor cursor=database.query(Contract.ItemList.TABLE_NAME,null,Contract.ItemList.ID+" =?",args,null,null,null);
            cursor.moveToFirst();
            ListItem listItem=new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                    cursor.getInt(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                    cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
            int pos;
            for(pos=0;pos<list.size();pos++)
                if(list.get(pos).getId()==id)
                    break;
            list.remove(pos);
            list.add(pos,listItem);
            adapter.notifyDataSetChanged();
        }
    }

    private void deleteItems() {
        SQLiteDatabase database=openHelper.getWritableDatabase();
        for(int i=0;i<toBeDeleted.size();i++)
        {
            database.delete(Contract.ItemList.TABLE_NAME,Contract.ItemList.ID+"=?", new String[]{toBeDeleted.get(i) + ""});
        }
        list.clear();
        toBeDeleted.clear();
        list.addAll(fetchData());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(toBeDeleted.size()>0)
            deleteItems();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(toBeDeleted.size()>0)
            deleteItems();
        Intent intent=new Intent(this,DispalyItem.class);
        bundle=new Bundle();
        bundle.putInt(Constant.ID_KEY,(Integer) v.getTag());
        intent.putExtras(bundle);
        startActivityForResult(intent,Constant.REQUEST_EDIT);
    }
}
