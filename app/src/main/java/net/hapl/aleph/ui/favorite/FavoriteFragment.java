package net.hapl.aleph.ui.favorite;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;

import static net.hapl.aleph.MainActivity.FAVORITE_FRAGMENT;
import static net.hapl.aleph.MainActivity.SEARCH_FRAGMENT;

public class FavoriteFragment extends Fragment {

    private static String TAG = "FavoriteFragment";
    private int selectedPosition;
    private FragmentTabHost mTabHost;


    public static Fragment newInstance() {
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        return favoriteFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        if((mTabHost = (FragmentTabHost)root.findViewById(android.R.id.tabhost)) != null) {
            mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

            mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.action_record))
                    .setIndicator(getString(R.string.action_record)), FavoriteRecordFragment.class, null);

            mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.action_query))
                    .setIndicator(getString(R.string.action_query)), FavoriteQueryFragment.class, null);
        }


        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        androidx.appcompat.app.ActionBar actionBar = MainActivity.getContext().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.favoriteBackground)));
        }


        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        // required to invoke onCreateOptionsMenu()
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getChildFragmentManager();
        Log.d(TAG, Integer.toString(fm.getBackStackEntryCount()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");

        inflater.inflate(R.menu.favorite, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setSelectedPosition(int pos) {
        this.selectedPosition = pos;
        if(mTabHost != null) {
            FragmentManager fm = getChildFragmentManager();
            FavoriteRecordFragment frag;
            if((frag = (FavoriteRecordFragment) fm.findFragmentById(R.id.realtabcontent)) != null) {
                frag.setSelectedPosition(selectedPosition);
            }
        }
        else {
            FragmentManager fm = getChildFragmentManager();
            FavoriteRecordFragment frag;
            if((frag = (FavoriteRecordFragment) fm.findFragmentById(R.id.favorite_record_fragment)) != null) {
                frag.setSelectedPosition(selectedPosition);
            }
        }
    }


}