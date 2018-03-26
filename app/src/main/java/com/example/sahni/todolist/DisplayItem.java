package com.example.sahni.todolist;

import android.app.FragmentTransaction;
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

public class DisplayItem extends AppCompatActivity{
    DisplayFragment fragment;
    Intent intent;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispaly_item);
        intent=getIntent();
        bundle=intent.getExtras();
        fragment=new DisplayFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
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
        fragment.result(requestCode,resultCode,data);
        if(data!=null)
            bundle=data.getExtras();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        Log.e("DISPLAY", "onResume: RESUME" );
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        if(bundle.getBoolean(Constant.FROM_NOTIFICATION,false))
        {
            Intent intent=new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if(bundle.getBoolean(Constant.EDIT,false)) {
            intent.putExtras(bundle);
            setResult(Constant.RESULT_EDIT, intent);
            Log.e("INTENT", "onBackPressed: "+bundle.getBoolean(Constant.EDIT,false));
        }
        super.onBackPressed();
    }
}
