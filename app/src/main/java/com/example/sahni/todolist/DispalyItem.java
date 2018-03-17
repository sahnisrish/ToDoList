package com.example.sahni.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DispalyItem extends AppCompatActivity {
    Bundle bundle;
    int id;
    ListItem item;
    Intent intent;
    ItemOpenHelper openHelper=ItemOpenHelper.getInstance(this);
    ListView Comments;
    ArrayList<Comments> comments;
    CommentsAdapter adapter;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispaly_item);
        intent=getIntent();
        bundle=intent.getExtras();
        setData();
    }

    private void setData() {
        id=bundle.getInt(Constant.ID_KEY);
        database=openHelper.getReadableDatabase();
        Cursor cursor=database.query(Contract.ItemList.TABLE_NAME,null,Contract.ItemList.ID+"=?", new String[]{id + ""},null,null,null);
        cursor.moveToNext();
        item=new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                cursor.getLong(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                cursor.getInt(cursor.getColumnIndex(Contract.ItemList.PRIORITY)),
                cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
        LinearLayout dataBar=findViewById(R.id.dataBar);
        TextView ToDo=dataBar.findViewById(R.id.todo);
        TextView Description=dataBar.findViewById(R.id.description);
        TextView Date=dataBar.findViewById(R.id.date);
        ToDo.setText(item.getItemName());
        Description.setText(cursor.getString(cursor.getColumnIndex(Contract.ItemList.DESCRIPTION)));
        Date.setText(item.getDeadLine());
        LinearLayout tagsBar=findViewById(R.id.tags);
        tagsBar.removeAllViews();
        if(item.getPriority()!=Constant.PRIORITY.NONE)
        {
            TagView priority = new TagView(this, item.getPriority());
            priority.addTag(tagsBar, null, null);
        }
        TagView.addMultipleTags(this,tagsBar,id,null,null);
        setComments();
    }

    private void setComments() {
        Comments=findViewById(R.id.comments);
        comments=new ArrayList<>();
        database=openHelper.getReadableDatabase();
        Cursor cursor=database.query(Contract.Comments.TABLE_NAME,null,Contract.Comments.ITEM_ID+"=?", new String[]{id + ""},null,null,Contract.Comments.DATE);
        while (cursor.moveToNext()){
            Comments comment=new Comments(cursor.getInt(cursor.getColumnIndex(Contract.Comments.ID)),
                    cursor.getString(cursor.getColumnIndex(Contract.Comments.COMMENT)),
                    cursor.getInt(cursor.getColumnIndex(Contract.Comments.ITEM_ID)),
                    cursor.getLong(cursor.getColumnIndex(Contract.Comments.DATE)));
            comments.add(comment);
        }
        adapter=new CommentsAdapter(this,comments,null);
        Comments.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        menu.add(Menu.NONE,Constant.MenuID.EDIT,Menu.NONE,"EDIT");
        MenuItem edit=menu.findItem(Constant.MenuID.EDIT);
        edit.setIcon(R.drawable.ic_create_white_48dp);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==Constant.MenuID.EDIT)
        {
            Intent intent = new Intent(this, AddItem.class);
            bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_EDIT);
            intent.putExtras(bundle);
            startActivityForResult(intent, Constant.REQUEST_EDIT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Constant.RSULT_EDIT)
        {
            setData();
            bundle=data.getExtras();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(bundle.getBoolean(Constant.EDIT,false)) {
            intent.putExtras(bundle);
            setResult(Constant.RSULT_EDIT, intent);
//            Log.e("INTENT", "onBackPressed: "+bundle.getBoolean(Constant.EDIT,false));
        }
        finish();
        super.onBackPressed();
    }
}
