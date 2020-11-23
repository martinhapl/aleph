package net.hapl.aleph.ui.settings;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
    }

    public static Fragment newInstance(int state) {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        androidx.appcompat.app.ActionBar actionBar = MainActivity.getContext().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.settingsBackground, getActivity().getTheme())));
        }

        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}