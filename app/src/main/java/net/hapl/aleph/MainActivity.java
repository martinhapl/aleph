package net.hapl.aleph;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import net.hapl.aleph.control.FavoriteComm;
import net.hapl.aleph.control.MainActivityComm;
import net.hapl.aleph.control.SearchComm;
import net.hapl.aleph.ui.BaseActivity;
import net.hapl.aleph.ui.search.DetailActivity;
import java.io.File;

public class MainActivity extends BaseActivity implements SearchComm, FavoriteComm, MainActivityComm {

    public static File DIRECTORY = null;
    public static File IMAGECACHEDIR = null;

    public static final int DETAIL_SEARCH_STATE = 0;
    public static final int DETAIL_FAVORITE_STATE = 1;
    public static final String SFX_SID = "APP_ID_ALEPH";

    public static final int SEARCH_FRAGMENT = 0;
    public static final int ACCOUNT_FRAGMENT = 1;
    public static final int FAVORITE_FRAGMENT = 2;
    public static final int INFO_FRAGMENT = 3;
    public static final int SETTINGS_FRAGMENT = 4;
    public static final int SETTINGS_FRAGMENT_ONLY = 5;

    public static final String[] FRAGMENTS_TAGS = {"SEARCH_FRAGMENT",
            "ACCOUNT_FRAGMENT", "FAVORITE_FRAGMENT", "INFO_FRAGMENT",
            "SETTINGS_FRAGMENT", "SETTINGS_FRAGMENT_ONLY"};

    private static final String PARAM_DETAIL_STATE = "PARAM_DETAIL_STATE";
    private static final String PARAM_SELECTED_POSITION = "PARAM_SELECTED_POSITION";
    private static final String PARAM_COLOR = "PARAM_COLOR";
    private static final String PARAM_TITLE = "PARAM_TITLE";
    private static final String TAG = "MainActivity";

    private int selectedPosition;
    private int detailState;
    private String query = "";



    private static MainActivity context = null;
    public static MainActivity getContext() {
        return context;
    }

    private Fragment fragment;
    private NavController navController;

    public String getQuery() {
        return query;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_search, R.id.navigation_account, R.id.navigation_favorite,
                R.id.navigation_information, R.id.navigation_settings)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        DIRECTORY = new File(getContext().getFilesDir(), "net.hapl.aleph");
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
        }

        IMAGECACHEDIR = new File(getContext().getCacheDir(), "net.hapl.aleph");
        if (!IMAGECACHEDIR.exists()) {
            IMAGECACHEDIR.mkdir();
        }
    }

    public void onNavItemSelected(int id) {
        Log.d(TAG, "onNavItemSelected:  "  + id );


    }


/*
    public void onNavItemSelected(int id) {
        Log.d(TAG, "onNavItemSelected:  "  + id );
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragment = null;

        // show hamburger
        setDrawerIndicator(true);

        switch ((int)id) {
            case SEARCH_FRAGMENT:
                fragment = SearchFragment.newInstance(query);
                cleanFragmentStack();
                onSectionAttached(SEARCH_FRAGMENT);
                query = "";
                break;

            case ACCOUNT_FRAGMENT:
                AlephControl.getInstance().getConfig();
                if(!AlephControl.getInstance().checkUserConfig()) {
                    onNavItemSelected(SETTINGS_FRAGMENT);
                    showWarningSettingsDialog();
                    return;
                }
                fragment = AccountFragment.newInstance();
                onSectionAttached(ACCOUNT_FRAGMENT);
                cleanFragmentStack();
                break;

            case FAVORITE_FRAGMENT:
                fragment = FavoriteFragment.newInstance();
                onSectionAttached(FAVORITE_FRAGMENT);
                cleanFragmentStack();
                break;

            case INFO_FRAGMENT:
                fragment = InformationFragment.newInstance();
                onSectionAttached(INFO_FRAGMENT);
                cleanFragmentStack();
                break;

            case SETTINGS_FRAGMENT:
                fragment = SettingsFragment.newInstance(SETTINGS_FRAGMENT);
                onSectionAttached(SETTINGS_FRAGMENT);
                cleanFragmentStack();
                break;

            case SETTINGS_FRAGMENT_ONLY:
                fragment = SettingsFragment.newInstance(SETTINGS_FRAGMENT_ONLY);
                onSectionAttached(SETTINGS_FRAGMENT);
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, FRAGMENTS_TAGS[id]).addToBackStack(FRAGMENTS_TAGS[id]).commit();
    }

    private void showWarningSettingsDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.availability_warning));
        alertDialogBuilder.setMessage(getString(R.string.warning_setting_dialog));

        alertDialogBuilder.setPositiveButton(getString(R.string.checkAnswer),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }


    public void onSectionAttached(int number) {

        if (navMenuTitles == null) {
            navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        }
        if(navMenuColors == null) {
            navMenuColors = getResources().obtainTypedArray(R.array.nav_drawable_color);
        }
        mTitle = navMenuTitles[number];
        mColor = navMenuColors.getColor(number, 0);

        restoreActionBar();

    }




    private void cleanFragmentStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // clean Back Stack
        while(fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
    }
 */


    @Override
    public void sendDataFromFavoriteQueryFragment(String query) {
        this.query = query;
        navController.navigate( R.id.navigation_search);
        //navController.getCurrentDestination().

      //  NavHostFragment navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment);
      //  navHostFragment.getChildFragmentManager().getFragments().get(0);


     //   SearchFragment searchFragment = SearchFragment.newInstance(query);

     //   FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
     //   ft.replace(R.layout.fragment_search, searchFragment);
     //   ft.commit();

     //   searchFragment.search(query);

    }

    @Override
    public void showDetailFromFavoriteRecordFragment(int position) {
        startDetailActivity(position, MainActivity.DETAIL_FAVORITE_STATE);
    }

    @Override
    public void updateFromFavoriteAdapters() {

    }

    @Override
    public void updateSearchAdapter() {

    }

    @Override
    public void removeReservation() {

    }



    @Override
    public void searchSetVisiblePosition(int position) {
        Log.d(TAG, "searchSetVisiblePosition");
    }

    @Override
    public void searchChangedAdapter() {
        Log.d(TAG, "searchChangedAdapter");
    }

    @Override
    public void searchAvailabilityShow(int id) {
        Log.d(TAG, "searchAvailabilityShow");

      /*  if(fragment.getTag().equals(FRAGMENTS_TAGS[SEARCH_FRAGMENT])) {
            ((SearchFragment)fragment).loadAvailabilityFragment(id);
        }

        // hide hamburger
        setDrawerIndicator(false);  */
    }

    @Override
    public void searchStartDetailActivity(int position, int detailState) {
        Log.d(TAG, "searchStartDetailActivity");

        startDetailActivity(position, detailState);
    }




    public void setDetailState(int state) {
        this.detailState = state;
    }

    public void setSelectedPosition(int pos) {
        this.selectedPosition = pos;
    }

    public void startDetailActivity(int pos, int state) {
        setSelectedPosition(pos);
        setDetailState(state);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(PARAM_SELECTED_POSITION, pos);
        intent.putExtra(PARAM_DETAIL_STATE, state);
        startActivityForResult(intent, state);
    }

}