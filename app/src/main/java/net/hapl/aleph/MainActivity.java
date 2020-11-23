package net.hapl.aleph;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import net.hapl.aleph.control.FavoriteComm;
import net.hapl.aleph.control.MainActivityComm;
import net.hapl.aleph.control.SearchComm;
import net.hapl.aleph.ui.search.DetailActivity;
import net.hapl.aleph.ui.search.SearchFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity implements SearchComm, FavoriteComm, MainActivityComm {

    public static File DIRECTORY = null;
    public static File IMAGECACHEDIR = null;

    public static final int DETAIL_SEARCH_STATE = 0;
    public static final int DETAIL_FAVORITE_STATE = 1;
    public static final String SFX_SID = "APP_ID_ALEPH";

    private static final String PARAM_DETAIL_STATE = "PARAM_DETAIL_STATE";
    private static final String PARAM_SELECTED_POSITION = "PARAM_SELECTED_POSITION";
    //private static final String PARAM_COLOR = "PARAM_COLOR";
    //private static final String PARAM_TITLE = "PARAM_TITLE";
    private static final String TAG = "MainActivity";

    //private String query = "";



    private static MainActivity context = null;
    public static MainActivity getContext() {
        return context;
    }

    private Fragment fragment;
    private NavController navController;



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


        navView.getMenu().getItem(0).getIcon().setColorFilter(ContextCompat.getColor(context,
                R.color.searchBackground), PorterDuff.Mode.MULTIPLY);

        //navView.setItemBackground(new ColorDrawable(getResources().getColor(R.color.searchBackground)));



        //navView.setItemBackgroundResource(R.color.searchBackground);
        //navView.getBadge(0).setBackgroundColor(getResources().getColor(R.color.searchBackground));

        //navView.setItemBackgroundResource(R.color.searchBackground);
        //navView.getBadge(R.id.navigation_search).setBackgroundColor(getResources().getColor(R.color.searchBackground));

        DIRECTORY = new File(getContext().getFilesDir(), "net.hapl.aleph");
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
        }

        IMAGECACHEDIR = new File(getContext().getCacheDir(), "net.hapl.aleph");
        if (!IMAGECACHEDIR.exists()) {
            IMAGECACHEDIR.mkdir();
        }
    }


    @Override
    public void sendDataFromFavoriteQueryFragment(String query) {
        Log.d(TAG, "sendDataFromFavoriteQueryFragment: " + query);
        SearchFragment.setFavoriteQuery(query);
        navController.navigate( R.id.navigation_search);
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


    public void startDetailActivity(int pos, int state) {

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(PARAM_SELECTED_POSITION, pos);
        intent.putExtra(PARAM_DETAIL_STATE, state);
        startActivityForResult(intent, state);
    }

}