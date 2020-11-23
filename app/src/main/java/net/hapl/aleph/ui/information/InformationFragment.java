package net.hapl.aleph.ui.information;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;

public class InformationFragment extends Fragment {

    public static Fragment newInstance() {
        return new InformationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String TAG = "InformationFragment";
        Log.d(TAG, "onCreate");

        androidx.appcompat.app.ActionBar actionBar = MainActivity.getContext().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.infoBackground, getActivity().getTheme())));
        }

        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InformationViewModel informationViewModel = ViewModelProviders.of(this).get(InformationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_information, container, false);
        final TextView textView = root.findViewById(R.id.text_information);
        informationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}