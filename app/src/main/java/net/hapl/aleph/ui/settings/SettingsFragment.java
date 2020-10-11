package net.hapl.aleph.ui.settings;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceFragmentCompat;
import net.hapl.aleph.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SettingsViewModel settingsViewModel;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);

        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}