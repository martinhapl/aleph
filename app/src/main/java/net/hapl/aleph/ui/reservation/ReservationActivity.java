package net.hapl.aleph.ui.reservation;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.ui.BaseActivity;

public class ReservationActivity extends BaseActivity {

    final static String TAG = "ReservationActivity";

    private Fragment fragment;

    FrameLayout view;
    View child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_reservation);

        view = (FrameLayout) findViewById(R.id.content_frame);
        child = getLayoutInflater().inflate(R.layout.activity_reservation, null);
        view.addView(child);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = ReservationFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // hide hamburger
        setDrawerIndicator(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reservation, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.accountBackground)));

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

