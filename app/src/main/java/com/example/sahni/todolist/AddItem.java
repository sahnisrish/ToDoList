package com.example.sahni.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahni.todolist.Contract.ItemList;

public class AddItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner Day;
    Spinner Month;
    Spinner Year;
    int day=0;
    int year=0;
    int month=0;
    EditText ToDo;
    EditText Description;
    Intent intent;
    Bundle bundle;
    ArrayAdapter<String> days;
    ArrayAdapter<String> months;
    ArrayAdapter<String> years;
    int REQUEST;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ToDo=findViewById(R.id.item);
        Description=findViewById(R.id.idescription);
        intent=getIntent();
        bundle=intent.getExtras();
        Day=findViewById(R.id.day);
        Month=findViewById(R.id.month);
        Year=findViewById(R.id.year);
        REQUEST=bundle.getInt(Constant.REQUEST_KEY);
        if(REQUEST==Constant.REQUEST_EDIT)
            setValues();
        createDateBar();
    }

    private void setValues() {
        id=bundle.getInt(Constant.ID_KEY);
        ItemOpenHelper openHelper=ItemOpenHelper.getInstance(this);
        SQLiteDatabase database=openHelper.getWritableDatabase();
        Cursor cursor=database.query(ItemList.TABLE_NAME,null, ItemList.ID+"=?", new String[]{id + ""},null,null,null);
        cursor.moveToNext();
        ListItem item= new ListItem(cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM)),
                cursor.getInt(cursor.getColumnIndex(Contract.ItemList.DEADLINE)),
                cursor.getInt(cursor.getColumnIndex(Contract.ItemList.ID)));
        ToDo.setText(item.getItemName());
        Description.setText(cursor.getString(cursor.getColumnIndex(ItemList.DESCRIPTION)));
        ParseDate(item.getDeadLineInt());
    }

    private void ParseDate(Integer deadLine) {
        day=Integer.parseInt(deadLine.toString().substring(6,8))-Integer.parseInt(Constant.DAYS.get(0));
        month=Integer.parseInt(deadLine.toString().substring(4,6));
        day=Integer.parseInt(deadLine.toString().substring(0,4))-Integer.parseInt(Constant.YEARS.get(0));
    }

    private void createDateBar() {
        Day.setOnItemSelectedListener(this);
        Month.setOnItemSelectedListener(this);
        Year.setOnItemSelectedListener(this);
        days= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,Constant.DAYS);
        months= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,Constant.MONTHS);
        years= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constant.YEARS);
        days.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        months.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        years.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Day.setAdapter(days);
        Month.setAdapter(months);
        Year.setAdapter(years);
        Day.setSelection(day);
        Month.setSelection(month);
        Year.setSelection(year);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.day:
                day=position;
                break;
            case R.id.month:
                month=position;
                break;
            case R.id.year:
                year=position;
        }
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
        save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==Constant.MenuID.SAVE)
        {
            ItemOpenHelper openHelper=ItemOpenHelper.getInstance(this);
            SQLiteDatabase database=openHelper.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(ItemList.ITEM,ToDo.getText().toString());
            if(month>9)
                values.put(ItemList.DEADLINE,Integer.parseInt(Constant.YEARS.get(year)+month+Constant.DAYS.get(day)+""));
            else
                values.put(ItemList.DEADLINE,Integer.parseInt(Constant.YEARS.get(year)+"0"+month+Constant.DAYS.get(day)));
            values.put(ItemList.DESCRIPTION,Description.getText().toString());
            if(REQUEST==Constant.REQUEST_ADD) {
                id = (int) database.insert(ItemList.TABLE_NAME, null, values);
                Bundle bundle=new Bundle();
                bundle.putInt(Constant.ID_KEY, Integer.parseInt(id+""));
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
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
