package net.hapl.aleph.ui.search;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityActivity extends BaseActivity {

    final static String TAG = "AvailabilityActivity";

    private static final String PARAM_DETAIL_STATE = "PARAM_DETAIL_STATE";
    private static final String POSITION_IN_DTO = "POSITION_IN_DTO";

    private int positionInDto;
    private int detailState;
    private Fragment fragment;

    private FrameLayout view;
    private View child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate layoutu do global layout
        view = (FrameLayout) findViewById(R.id.content_frame);
        child = getLayoutInflater().inflate(R.layout.activity_availability, null);
        view.addView(child);

        Intent intent = getIntent();
        positionInDto = intent.getIntExtra(POSITION_IN_DTO, 0);
        detailState = intent.getIntExtra(PARAM_DETAIL_STATE, 0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = AvailabilityFragment.newInstance(positionInDto, detailState);
        fragmentManager.beginTransaction().replace(R.id.activity_availability_content, fragment).commit();

        // hide hamburger
        setDrawerIndicator(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reservation, menu);
        ActionBar actionBar = getSupportActionBar();

        if(detailState == MainActivity.DETAIL_SEARCH_STATE) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.searchBackground)));
        }
        else {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.favoriteBackground)));
        }

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.show();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNavItemSelected(int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        switch (id) {
            case MainActivity.SEARCH_FRAGMENT:
                intent.putExtra("FRAGMENT", MainActivity.SEARCH_FRAGMENT);
                break;
            case MainActivity.ACCOUNT_FRAGMENT:
                intent.putExtra("FRAGMENT", MainActivity.ACCOUNT_FRAGMENT);
                break;
            case MainActivity.FAVORITE_FRAGMENT:
                intent.putExtra("FRAGMENT", MainActivity.FAVORITE_FRAGMENT);
                break;
            case MainActivity.INFO_FRAGMENT:
                intent.putExtra("FRAGMENT", MainActivity.INFO_FRAGMENT);
                break;
            case MainActivity.SETTINGS_FRAGMENT:
                intent.putExtra("FRAGMENT", MainActivity.SETTINGS_FRAGMENT);
                break;
        }

        startActivity(intent);
        finish();
    }
}

