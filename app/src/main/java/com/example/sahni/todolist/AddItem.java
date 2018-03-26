package com.example.sahni.todolist;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahni.todolist.Contract.ItemList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class AddItem extends AppCompatActivity implements AddFragment.AddCallBack,AddFragment.FinishCallBack,AddFragment.EditCallBack{
    Intent intent;
    Bundle bundle;
    AddFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        intent=getIntent();
        bundle=intent.getExtras();
        Log.e("ADD ITEM", "onCreate: "+bundle.get(Constant.REQUEST_KEY));
        fragment=new AddFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,fragment)
                .commit();
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
        if(item.getItemId()==Constant.MenuID.SAVE)
            fragment.save();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Exit")
                .setMessage("Do you want to exit without saving?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("ADD", "onClick: CLOSING" );
                        AddItem.super.onBackPressed();
                    }
                })
                .show();
    }

    @Override
    public void add(Bundle bundle) {
        intent.putExtras(bundle);
        setResult(Constant.RESULT_ADD, intent);
    }

    @Override
    public void edit(Bundle bundle) {
        intent.putExtras(bundle);
        Log.e("EDIT", "edit: "+bundle.getBoolean(Constant.EDIT) );
        setResult(Constant.RESULT_EDIT, intent);
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
