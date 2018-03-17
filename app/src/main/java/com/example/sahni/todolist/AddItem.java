package com.example.sahni.todolist;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahni.todolist.Contract.ItemList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, View.OnLongClickListener {
    //Activity
    int REQUEST;
    int id;
    EditText ToDo;
    EditText Description;
    Intent intent;
    Bundle bundle;
    //DateSet
    Spinner Day;
    Spinner Month;
    Spinner Year;
    String setDate;
    Long presetDate;
    ArrayAdapter<String> days;
    ArrayAdapter<String> months;
    ArrayAdapter<String> years;
    //Tags
    LinearLayout selectedTags;
    Button addTag;
    EditText tagText;
    ArrayList<Integer> addedTagIds;
    ArrayList<Integer> removedTagIds;
    //Comments
    ListView addedComments;
    ArrayList<Comments> comments;
    CommentsAdapter adapter;
    Button addComment;
    EditText commentText;
    ArrayList<Integer> deletedComments;
    //Priority
    Button priority;
    //Database
    ItemOpenHelper openHelper;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        setPriorityTag();
        openHelper=ItemOpenHelper.getInstance(this);
        ToDo=findViewById(R.id.item);
        setTag();
        Description=findViewById(R.id.idescription);
        intent=getIntent();
        bundle=intent.getExtras();
        REQUEST=bundle.getInt(Constant.REQUEST_KEY);
        priority=findViewById(R.id.none);
        if(REQUEST==Constant.REQUEST_EDIT)
            setValues();
        else if((REQUEST==Constant.REQUEST_ADD)&&(bundle.getBoolean(Constant.HAS_TAG,false)))
            addTag(bundle.getInt(Constant.TAG_ID), bundle.getString(Constant.TAG));
        else if((REQUEST==Constant.REQUEST_ADD)&&(bundle.getBoolean(Constant.HAS_PRIORITY,false)))
            getPriority(bundle.getInt(Constant.Priority));
        setComment();
        createDateBar();
        priority.setEnabled(false);
        priority.setBackgroundResource(R.drawable.button);
    }

    private void setComment() {
        addedComments = findViewById(R.id.addedComments);
        addComment=findViewById(R.id.addcomment);
        deletedComments=new ArrayList<>();
        comments = new ArrayList<>();
        if(bundle.getInt(Constant.ID_KEY,-1)>0)
        {
            int id=bundle.getInt(Constant.ID_KEY);
            database=openHelper.getReadableDatabase();
            Cursor cursor=database.query(Contract.Comments.TABLE_NAME,null,Contract.Comments.ITEM_ID+"=?", new String[]{id + ""},null,null,Contract.Comments.DATE);
            while (cursor.moveToNext()){
                Comments comment=new Comments(cursor.getInt(cursor.getColumnIndex(Contract.Comments.ID)),
                                    cursor.getString(cursor.getColumnIndex(Contract.Comments.COMMENT)),
                                    cursor.getInt(cursor.getColumnIndex(Contract.Comments.ITEM_ID)),
                                    cursor.getLong(cursor.getColumnIndex(Contract.Comments.DATE)));
                comments.add(comment);
            }
        }
        adapter=new CommentsAdapter(this, comments, new CommentsAdapter.onDeleteClick() {
            @Override
            public void onClick(View v) {
                int position=(int)v.getTag();
                Comments comment=comments.get(position);
                if((comment.getId()!=Constant.NOT_IN_DB)&&(comment.getItem_id()!=Constant.NOT_IN_DB))
                    deletedComments.add(comment.getId());
                comments.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        addedComments.setAdapter(adapter);
        addComment.setOnClickListener(this);
        commentText=findViewById(R.id.AddComments);
    }

    private void getPriority(int anInt) {
        switch (anInt){
            case Constant.PRIORITY.HIGH:
                priority=findViewById(R.id.high);
                break;
            case Constant.PRIORITY.MED:
                priority=findViewById(R.id.medium);
                break;
            case Constant.PRIORITY.LOW:
                priority=findViewById(R.id.low);
                break;
            default:
                priority=findViewById(R.id.none);
        }
    }

    private void setPriorityTag() {
        priority=findViewById(R.id.low);
        priority.setTag(Constant.PRIORITY.LOW);
        priority=findViewById(R.id.medium);
        priority.setTag(Constant.PRIORITY.MED);
        priority=findViewById(R.id.high);
        priority.setTag(Constant.PRIORITY.HIGH);
        priority=findViewById(R.id.none);
        priority.setTag(Constant.PRIORITY.NONE);
    }

    private void setTag() {
        removedTagIds=new ArrayList<>();
        addedTagIds=new ArrayList<>();
        selectedTags=findViewById(R.id.selectedTags);
        addTag=findViewById(R.id.addtags);
        tagText=findViewById(R.id.AddTag);
        addTag.setOnClickListener(this);
    }
    private void setValues() {
        id=bundle.getInt(Constant.ID_KEY);
        Log.e("ID", "setValues: "+id );
        database=openHelper.getWritableDatabase();
        Cursor cursor=database.query(ItemList.TABLE_NAME,null, ItemList.ID+"=?", new String[]{id + ""},null,null,null);
        cursor.moveToNext();
        ListItem item= new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                cursor.getLong(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                cursor.getInt(cursor.getColumnIndex(ItemList.PRIORITY)),
                cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
        ToDo.setText(item.getItemName());
        Description.setText(cursor.getString(cursor.getColumnIndex(ItemList.DESCRIPTION)));
        TagView.addMultipleTags(this,selectedTags,id,this,this);
        presetDate=item.getDeadLineLong();
        getPriority(item.getPriority());
    }
    private void createDateBar() {
        Day=findViewById(R.id.day);
        Month=findViewById(R.id.month);
        Year=findViewById(R.id.year);
        Day.setOnItemSelectedListener(this);
        Month.setOnItemSelectedListener(this);
        Year.setOnItemSelectedListener(this);
        days= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,Constant.IntSet(1,31));
        months= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,Constant.setMonth());
        years= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constant.IntSet(2018,2025));
        days.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        months.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        years.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Day.setAdapter(days);
        Month.setAdapter(months);
        Year.setAdapter(years);
        setDate();
    }

    private void setDate() {
        if(presetDate==null) {
            presetDate = System.currentTimeMillis();
        }
        Date date=new Date(presetDate);
        setDate=Constant.format.format(date);
        Day.setSelection(days.getPosition(setDate.substring(0,2)));
        Month.setSelection(months.getPosition(setDate.substring(3,6)));
        Year.setSelection(years.getPosition(setDate.substring(7)));
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setDate=Day.getSelectedItem().toString()+"/"+Month.getSelectedItem().toString()+"/"+Year.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        menu.add(Menu.NONE,Constant.MenuID.SAVE,Menu.NONE,"SAVE");
        MenuItem save=menu.findItem(Constant.MenuID.SAVE);
        save.setIcon(R.drawable.ic_save_white_48dp);
        save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if((item.getItemId()==Constant.MenuID.SAVE)&&(!ToDo.getText().toString().equals("")))
        {
            try {
                if((Constant.format.format(EpochDate()).equals(Constant.format.format(System.currentTimeMillis())))||(EpochDate()>System.currentTimeMillis()))
                {
                    database=openHelper.getWritableDatabase();
                    ContentValues values=new ContentValues();
                    values.put(ItemList.ITEM,ToDo.getText().toString());
                    values.put(ItemList.DEADLINE,EpochDate());
                    values.put(ItemList.DESCRIPTION,Description.getText().toString());
                    values.put(ItemList.PRIORITY,(int) priority.getTag());
                    if(REQUEST==Constant.REQUEST_ADD) {
                        id = (int) database.insert(ItemList.TABLE_NAME, null, values);
                        Bundle bundle=new Bundle();
                        bundle.putInt(Constant.ID_KEY, Integer.parseInt(id+""));
                        bundle.putBoolean(Constant.HAS_TAG, this.bundle.getBoolean(Constant.HAS_TAG,false));
                        bundle.putBoolean(Constant.HAS_PRIORITY,this.bundle.getBoolean(Constant.HAS_PRIORITY,false));
                        intent.putExtras(bundle);
                        setResult(Constant.RSULT_ADD,intent);
                    }
                    else if(REQUEST==Constant.REQUEST_EDIT){
                        database.update(ItemList.TABLE_NAME,values, ItemList.ID+"=?", new String[]{id + ""});
                        Bundle bundle=new Bundle();
                        bundle.putInt(Constant.ID_KEY, Integer.parseInt(id+""));
                        bundle.putBoolean(Constant.EDIT,true);
                        intent.putExtras(bundle);
                        setResult(Constant.RSULT_EDIT,intent);
//                        Log.e("INTENT", "onEdit: "+bundle.getBoolean(Constant.EDIT,false));
                    }
                    createNotification(id);
                    ModifyTagSet();
                    PutComments();
                    finish();
                }
                else
                    Toast.makeText(this,"Invalid Deadline",Toast.LENGTH_SHORT).show();
                Log.e("System Date",Constant.format.format(System.currentTimeMillis()));
            }
            catch (ParseException e) {
                Log.e("Parse Exception","Date Format Not Parsable");
            }

        }
        else if(item.getItemId()==Constant.MenuID.SAVE)
            Toast.makeText(this,"EMPTY VALUES NOT ALLOWED!",Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    private void createNotification(int id) {
        AlarmManager alarm= (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent=new Intent(this,Receiver.class);
        intent.putExtra(Constant.ID_KEY,id);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,Constant.REQUEST_NOTIFY,intent,PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent==null) {
            pendingIntent = PendingIntent.getBroadcast(this, Constant.REQUEST_NOTIFY, intent, 0);
        }
        else{
            alarm.cancel(pendingIntent);
        }
        try {
            alarm.set(AlarmManager.RTC, EpochDate(),pendingIntent);
        } catch (ParseException e) {
            Log.e("DATE", "createNotification: invalid type" );
        }
    }

    private void PutComments() {
        database=openHelper.getWritableDatabase();
        for(int i=0;i<comments.size();i++)
        {
            if(comments.get(i).getId()==Constant.NOT_IN_DB)
            {
                ContentValues values=new ContentValues();
                values.put(Contract.Comments.COMMENT,comments.get(i).getComment());
                values.put(Contract.Comments.ITEM_ID,id);
                values.put(Contract.Comments.DATE,comments.get(i).getDateLong());
                database.insert(Contract.Comments.TABLE_NAME,null,values);
            }
        }
        for(int i=0;i<deletedComments.size();i++)
            database.delete(Contract.Comments.TABLE_NAME, Contract.Comments.ID+"=?",new String[]{deletedComments.get(i)+""});
    }

    private Long EpochDate() throws ParseException {
        Long EpochDate;
        Date date=Constant.format.parse(setDate);
        EpochDate=date.getTime();
        return  EpochDate;
    }

    private void ModifyTagSet() {
        if(!removedTagIds.isEmpty())
        {
            database=openHelper.getWritableDatabase();
            for(int i=0;i<removedTagIds.size();i++) {
                database.delete(Contract.TagAssignment.TABLE_NAME, Contract.TagAssignment.TAG_ID + " = ? AND " + Contract.TagAssignment.ITEM_ID + " = ?", new String[]{removedTagIds.get(i) + "", id + ""});
                Cursor cursor=database.query(Contract.TagAssignment.TABLE_NAME,null,Contract.TagAssignment.TAG_ID+" = ?", new String[]{removedTagIds.get(i) + ""},null,null,null);
                if(!cursor.moveToNext())
                    database.delete(Contract.TagsList.TABLE_NAME,Contract.TagsList.ID+" = ?", new String[]{removedTagIds.get(i) + ""});
            }
        }
        if(!addedTagIds.isEmpty())
        {
            database=openHelper.getWritableDatabase();
            for (int i=0;i<addedTagIds.size();i++)
            {
                ContentValues values=new ContentValues();
                values.put(Contract.TagAssignment.ITEM_ID,id);
                values.put(Contract.TagAssignment.TAG_ID,addedTagIds.get(i));
                database.insert(Contract.TagAssignment.TABLE_NAME,null,values);
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addtags){
            if(tagText.getText().toString().equals(""))
                Toast.makeText(this,"ENTER SOME VALUE",Toast.LENGTH_SHORT).show();
            else{
                int id;
                String tag=tagText.getText().toString().toLowerCase();
                database=openHelper.getWritableDatabase();
                Cursor cursor=database.query(Contract.TagsList.TABLE_NAME,null,Contract.TagsList.TAG+" = ?", new String[]{tag},null,null,null);
                if(cursor.moveToNext())
                    id=cursor.getInt(cursor.getColumnIndex(Contract.TagsList.ID));
                else
                {
                    ContentValues values=new ContentValues();
                    values.put(Contract.TagsList.TAG,tag);
                    id= (int) database.insert(Contract.TagsList.TABLE_NAME,null,values);
                }
                if(addedTagIds.indexOf(id)<0)
                    addTag(id,tag);
                else
                    Toast.makeText(this,"Tag already exists",Toast.LENGTH_SHORT).show();
            }
        }
        else if(v.getId()==R.id.addcomment){
            if(commentText.getText().toString().equals(""))
                Toast.makeText(this,"ENTER SOME VALUE",Toast.LENGTH_SHORT).show();
            else{
                Comments comment=new Comments(commentText.getText().toString(),System.currentTimeMillis());
                comments.add(comment);
                adapter.notifyDataSetChanged();
                commentText.setText("");
            }
        }
        else {
            TextView viewTag=(TextView)v ;
            Toast.makeText(this,viewTag.getText().toString(),Toast.LENGTH_SHORT).show();
        }

    }

    private void addTag(Integer id,String tag) {
        TagView view=new TagView(this,tag,id);
        view.addTag(selectedTags,this,this);
        tagText.setText("");
        addedTagIds.add(id);
        if(id==bundle.getInt(Constant.TAG_ID,-1))
            bundle.putBoolean(Constant.HAS_TAG,true);
    }

    @Override
    public boolean onLongClick(View v) {
        final TextView textView=(TextView)v;
        final Integer id=textView.getId();
        AlertDialog.Builder builder=new AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Remove Tag");
        builder.setMessage("Do you want to remove the tag?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int i;
                for(i=0;i<addedTagIds.size();i++)
                    if(addedTagIds.get(i)==id)
                        break;
                if(i==addedTagIds.size())
                    removedTagIds.add(id);
                else
                    addedTagIds.remove(i);
                if(id==bundle.getInt(Constant.TAG_ID,-1))
                    bundle.remove(Constant.HAS_TAG);
                selectedTags.removeView(textView);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
        return true;
    }

    public void setPriority(View view) {
        priority.setEnabled(true);
        priority.setBackgroundResource(R.drawable.selected_button);
        priority=(Button)view;
        priority.setBackgroundResource(R.drawable.button);
        priority.setEnabled(true);
        if((int)priority.getTag()!=bundle.getInt(Constant.Priority,-1))
            bundle.remove(Constant.HAS_PRIORITY);
        else if((int)priority.getTag()==bundle.getInt(Constant.Priority,-1))
            bundle.putBoolean(Constant.HAS_PRIORITY,true);
    }
}
