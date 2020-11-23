package net.hapl.aleph.ui.search;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.control.FavoriteComm;
import net.hapl.aleph.control.SearchComm;
import net.hapl.aleph.model.PresentDTO;
import net.hapl.aleph.ui.favorite.FavoriteRecordFragment;


public class DetailActivity extends AppCompatActivity implements SearchComm, FavoriteComm {
    private static final String TAG = "DetailActivity";

    private DetailFragment detailFragment;
    private FavoriteRecordFragment favoriteRecordFragment;

    private static final String PARAM_DETAIL_STATE = "PARAM_DETAIL_STATE";
    private static final String PARAM_SELECTED_POSITION = "PARAM_SELECTED_POSITION";
    private static final String POSITION_IN_DTO = "POSITION_IN_DTO";

    private int selectedPosition;
    private int detailState;

    FrameLayout left_container;
    FrameLayout view;
    View child;

    boolean bigScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global);

        if(savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(PARAM_SELECTED_POSITION, 0);
            detailState = savedInstanceState.getInt(PARAM_DETAIL_STATE, 0);
        }
        else {
            Intent intent = getIntent();
            selectedPosition = intent.getIntExtra(PARAM_SELECTED_POSITION, 0);
            detailState = intent.getIntExtra(PARAM_DETAIL_STATE, 0);
        }

        view = (FrameLayout) findViewById(R.id.content_frame);
        child = getLayoutInflater().inflate(R.layout.activity_detail, null);
        view.addView(child);

        FragmentManager fragmentManager = getSupportFragmentManager();
        detailFragment = DetailFragment.newInstance(detailState, selectedPosition);
        fragmentManager.beginTransaction().replace(R.id.detail_container, detailFragment).addToBackStack(null).commit();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        outState.putInt(PARAM_DETAIL_STATE, detailState);
        outState.putInt(PARAM_SELECTED_POSITION, selectedPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        ActionBar actionBar = getSupportActionBar();

        if (detailState == MainActivity.DETAIL_SEARCH_STATE) {
            inflater.inflate(R.menu.menu_detail_search, menu);

            actionBar.setTitle(getString(R.string.title_search));
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.searchBackground, getTheme())));
        } else {
            inflater.inflate(R.menu.menu_detail_favorite, menu);

            actionBar.setTitle(getString(R.string.title_favorite));
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.favoriteBackground, getTheme())));
        }

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.show();

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected " + item);
        int id = item.getItemId();

        if(id == android.R.id.home) {

            if(bigScreen) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Log.d(TAG, "fragmentManager.getBackStackEntryCount()" + fragmentManager.getBackStackEntryCount());
                if(fragmentManager.getBackStackEntryCount() > 1) {
                    fragmentManager.popBackStack();
                    return true;
                }
            }

            Intent output = new Intent();
            output.putExtra("PARAM_SELECTED_POSITION", detailFragment.getPosition());
            output.putExtra("PARAM_DETAIL_STATE", this.detailState);
            setResult(RESULT_OK, output);
            finish();

            return true;
        }
        else if(id == R.id.action_send) {
            sendRecordByEmail();
            return true;
        }
        else if(id == R.id.action_delete) {
            deleteRecord(detailFragment.getPosition());
            return true;
        }
        else if(id == R.id.action_favorite) {
            addToFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendDataFromFavoriteQueryFragment(String query) {
    }

    @Override
    public void showDetailFromFavoriteRecordFragment(int position) {
        detailFragment.setSelectedPosition(position);
    }

    @Override
    public void searchSetVisiblePosition(int position) {
    }

    @Override
    public void searchChangedAdapter() {
    }

    @Override
    public void searchStartDetailActivity(int position, int detailState) {
    }

    @Override
    public void updateFromFavoriteAdapters() {
        detailFragment.notifyAdapter(-1);

        if(bigScreen) {
            favoriteRecordFragment.notifyAdapter();
        }
    }

    @Override
    public void searchAvailabilityShow(int position) {
        if(bigScreen) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            AvailabilityFragment availabilityFragment = AvailabilityFragment.newInstance(position, detailState);
            fragmentManager.beginTransaction().replace(R.id.detail_container, availabilityFragment).addToBackStack(null).commit();
        }
        else {
            Intent intent = new Intent(this, AvailabilityActivity.class);
            intent.putExtra(POSITION_IN_DTO, position);
            intent.putExtra(PARAM_DETAIL_STATE, detailState);
            startActivity(intent);
        }
    }

    private void deleteRecord(int position) {
        Log.d(TAG, "size: "+ AlephControl.getInstance().getFavouritesRepository().getFavourite().size());
        AlephControl.getInstance().getFavouritesRepository().removeFavourite(position);
        AlephControl.getInstance().getFavouritesRepository().loadFavourites();
        Log.d(TAG, "size: "+AlephControl.getInstance().getFavouritesRepository().getFavourite().size());

        detailFragment.notifyAdapter(-1);

        if(bigScreen) {
            favoriteRecordFragment.notifyAdapter();
        }
    }

    /**
     * Send record by mail
     */
    private void sendRecordByEmail() {
        if(detailState == MainActivity.DETAIL_SEARCH_STATE && AlephControl.getInstance().getFindRepository().getPresentDTOs() == null) {
            return;
        }
        String messageToSend = AlephControl.getInstance().createMessageToSendByEmail(detailFragment.getPosition(), detailState);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_share_email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, messageToSend);
        Intent mailer = Intent.createChooser(intent, null);
        startActivity(mailer);
    }


    /**
     * Add record to favorite
     */
    private void addToFavorite() {
        PresentDTO presentDTO;

        presentDTO = AlephControl.getInstance().getFindRepository().getPresentDTOs().get(detailFragment.getPosition());
        AlephControl.getInstance().getFavouritesRepository().loadFavourites();

        if(AlephControl.getInstance().getFavouritesRepository().addFavourite(presentDTO)) {
            Toast.makeText(this, getString(R.string.add_record_to_favorite), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, getString(R.string.add_record_to_favorite_false), Toast.LENGTH_SHORT).show();
        }

        AlephControl.getInstance().getFavouritesRepository().saveFavourites();
    }


    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            return;
        }

        Intent output = new Intent();
        output.putExtra("PARAM_SELECTED_POSITION",  detailFragment.getPosition());
        output.putExtra("PARAM_DETAIL_STATE", this.detailState);
        setResult(RESULT_OK, output);
        finish();

        super.onBackPressed();
    }
}