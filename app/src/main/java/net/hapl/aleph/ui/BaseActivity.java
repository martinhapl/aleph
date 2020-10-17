package net.hapl.aleph.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import net.hapl.aleph.R;
import net.hapl.aleph.control.NavDrawerItem;
import net.hapl.aleph.control.NavDrawerListAdapter;

import java.util.ArrayList;


public abstract class BaseActivity extends AppCompatActivity {  //ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ListView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    protected abstract void onNavItemSelected( int id );

    //TODO: zjistit kde vzit
    private final Toolbar toolbar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.global);

        mTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0],
                navMenuIcons.getResourceId(0, -1), 0xffb4b347));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],
                navMenuIcons.getResourceId(1, -1), 0xff6b9997));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2],
                navMenuIcons.getResourceId(2, -1), 0xffd14335));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3],
                navMenuIcons.getResourceId(3, -1), 0xff3b2e0b));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4],
                navMenuIcons.getResourceId(4, -1), 0xff615144));

        // Recycle the typed array
        navMenuIcons.recycle();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new NavDrawerListAdapter(this, navDrawerItems ));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        this.initDrawerShadow();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        drawerToggle = new ActionBarDrawerToggle (
               this,
                drawerLayout,
                toolbar,
                //getDrawerIcon(),
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }



    public void setDrawerIndicator(boolean value) {
        drawerToggle.setDrawerIndicatorEnabled(value);
    }

    protected void initDrawerShadow() {
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    protected int getDrawerIcon() {
        return R.drawable.ic_drawer;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*if ( navConf.getActionMenuItemsToHideWhenDrawerOpen() != null ) {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
            for( int iItem : navConf.getActionMenuItemsToHideWhenDrawerOpen()) {
                menu.findItem(iItem).setVisible(!drawerOpen);
            }
        }*/
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            if ( this.drawerLayout.isDrawerOpen(this.mDrawerList)) {
                this.drawerLayout.closeDrawer(this.mDrawerList);
            }
            else {
                this.drawerLayout.openDrawer(this.mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    protected ListView getDrawerList() {
        return mDrawerList;
    }

    protected ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int position) {

        this.onNavItemSelected(position);
        mDrawerList.setItemChecked(position, true);

        if ( this.drawerLayout.isDrawerOpen(this.mDrawerList)) {
            drawerLayout.closeDrawer(mDrawerList);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(mDrawerList);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(mDrawerList);
    }
}

