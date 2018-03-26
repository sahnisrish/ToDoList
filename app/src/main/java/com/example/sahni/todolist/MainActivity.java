package com.example.sahni.todolist;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainFragment.Add, MainFragment.Display,MainFragment.Edit,MainFragment.TagDescription,AddFragment.AddCallBack,AddFragment.EditCallBack,AddFragment.FinishCallBack {
    android.support.v7.widget.Toolbar toolbar;
    AppBarLayout appBar;
    LinearLayout titleTag;
    Menu menu;
    Bundle bundle;
    MainFragment fragment;
    AddFragment addFragment;
    DisplayFragment displayFragment;
    boolean isLandscape=false;
    FrameLayout displayPane;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        fragment= (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        titleTag=findViewById(R.id.titleTag);
        final FloatingActionButton add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.add();
            }
        });
        displayPane =findViewById(R.id.display_pane);

        if(displayPane !=null)
            isLandscape=true;
        if(!isLandscape) {
            appBar = findViewById(R.id.appBar);
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (menu != null) {
                        if ((verticalOffset >= 0) && (verticalOffset <= appBarLayout.getHeight()) && (add.getVisibility() == View.VISIBLE))
                            menu.findItem(Constant.MenuID.ADD).setVisible(false);
                        else
                            menu.findItem(Constant.MenuID.ADD).setVisible(true);
                    }
                }

            });
        }
        else if(savedInstanceState!=null)
        {
            List<Fragment> fragments=getSupportFragmentManager().getFragments();
            if(fragments.size()==2){
                if(fragments.get(1) instanceof AddFragment)
                {
                    bundle=fragments.get(1).getArguments();
                    addFragment= (AddFragment) fragments.get(1);
                    displayFAB();
                }
                else if(fragments.get(1) instanceof DisplayFragment)
                {
                    bundle=fragments.get(1).getArguments();
                    displayFragment= (DisplayFragment) fragments.get(1);
                    displayFAB();
                }
            }
            else{
                displayPane.setVisibility(View.GONE);
            }
        }
        else{
            displayPane.setVisibility(View.GONE);
        }
        if((savedInstanceState!=null)&&(savedInstanceState.getBoolean(Constant.HAS_TAG)))
        {
            fragment.hasTag=true;
            if(!savedInstanceState.getBoolean(Constant.HAS_PRIORITY)) {
                Log.e("TAG", "onCreate NON-PRIORITY" );
                fragment.tag = new TagView(this,
                        savedInstanceState.getString(Constant.TAG),
                        savedInstanceState.getInt(Constant.TAG_ID));
            }
            else {
                Log.e("TAG", "onCreate PRIORITY" );
                fragment.tag = new TagView(this,
                        savedInstanceState.getInt(Constant.TAG_ID));
            }
            fragment.onClick(fragment.tag);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(fragment.hasTag)
        {
            outState.putInt(Constant.TAG_ID,fragment.tag.getId());
            outState.putString(Constant.TAG,fragment.tag.getTitle());
            outState.putBoolean(Constant.HAS_TAG,true);
            if(fragment.tag.isPriority()) {
                Log.e("TAG", "onSet PRIORITY" );
                outState.putBoolean(Constant.HAS_PRIORITY, true);
            }
            else
                outState.putBoolean(Constant.HAS_PRIORITY,false);
        }
        else {
            outState.putBoolean(Constant.HAS_TAG,false);
        }
        super.onSaveInstanceState(outState);
    }

    private void displayFAB() {
        fab=findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        Log.e("DISPLAY", "AddDisplayFragment: FAB VISIBLE");
        if(bundle.getInt(Constant.REQUEST_KEY)==Constant.REQUEST_DISPLAY) {
            fab.setImageResource(R.drawable.ic_create_white_48dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallEdit(bundle);
                }
            });
        }
        else if((bundle.getInt(Constant.REQUEST_KEY)==Constant.REQUEST_ADD)||((bundle.getInt(Constant.REQUEST_KEY)==Constant.REQUEST_EDIT)))
        {
            fab.setImageResource(R.drawable.ic_save_white_48dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFragment.save();
                }
            });
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        this.menu=menu;
        if(!isLandscape) {
            menu.add(Menu.NONE, Constant.MenuID.ADD, Menu.NONE, "Add");
            MenuItem add = menu.findItem(Constant.MenuID.ADD);
            add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            add.setIcon(R.drawable.ic_add_black_24dp);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==Constant.MenuID.ADD)
            fragment.add();
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.result(requestCode,resultCode,data);
    }
    @Override
    public void CallAdd(boolean hasTag, TagView tag) {
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
        if(!isLandscape) {
            Intent intent = new Intent(MainActivity.this, AddItem.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, Constant.REQUEST_ADD);
        }
        else
            AddAddFragment();
    }
    @Override
    public void onBackPressed() {
        if(addFragment!=null){
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
                            getSupportFragmentManager().beginTransaction().remove(addFragment).commit();
                            bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_DISPLAY);
                            AddDisplayFragment();
                            addFragment=null;
                        }
                    })
                    .show();
        }
        else if(displayFragment!=null)
        {
            getSupportFragmentManager().beginTransaction().remove(displayFragment).commit();
            displayPane.setVisibility(View.GONE);
        }
        else if(fragment.hasTag==true)
        {
            fragment.hasTag=false;
            fragment.modifyData();
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onStop() {
        fragment.DeleteExtraTags();
        super.onStop();
    }


    @Override
    public void CallDisplay(Bundle bundle) {
        bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_DISPLAY);
        this.bundle=bundle;
        if(!isLandscape) {
            Intent intent = new Intent(this, DisplayItem.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, Constant.REQUEST_DISPLAY);
        }
        else {
            if(addFragment==null)
                AddDisplayFragment();
            else {
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
                                getSupportFragmentManager().beginTransaction().remove(addFragment).commit();
                                AddDisplayFragment();
                            }
                        })
                        .show();
            }
        }
    }

    private void AddDisplayFragment() {
        displayPane.setVisibility(View.VISIBLE);
        addFragment=null;
        displayFragment=new DisplayFragment();
        displayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.display_pane,displayFragment)
                .commit();
        displayFAB();
    }

    @Override
    public void CallEdit(Bundle bundle) {
        bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_EDIT);
        this.bundle=bundle;
        if(!isLandscape) {
            Intent intent = new Intent(this, AddItem.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, Constant.REQUEST_EDIT);
        }
        else
            AddAddFragment();
    }

    private void AddAddFragment() {
        displayPane.setVisibility(View.VISIBLE);
        displayFragment=null;
        addFragment=new AddFragment();
        addFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.display_pane,addFragment)
                .commit();
        displayFAB();
    }

    @Override
    public void setTagDescription(int i,TextView tagDetails) {
        titleTag.removeAllViews();
        if(i==Constant.TAGS_SELECTED)
        {
            titleTag.addView(tagDetails);
        }
    }

    @Override
    public void add(Bundle bundle) {
        bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_DISPLAY);
        this.bundle=bundle;
        getSupportFragmentManager().beginTransaction().remove(addFragment).commit();
        AddDisplayFragment();
    }

    @Override
    public void edit(Bundle bundle) {
        bundle.putInt(Constant.REQUEST_KEY,Constant.REQUEST_DISPLAY);
        this.bundle=bundle;
        getSupportFragmentManager().beginTransaction().remove(addFragment).commit();
        AddDisplayFragment();
    }

    @Override
    public void finishActivity() {
        addFragment=null;
    }
}
