package net.hapl.aleph.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceFragmentCompat;
import net.hapl.aleph.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static Fragment newInstance(int state) {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }

    private SettingsViewModel settingsViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);

        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}