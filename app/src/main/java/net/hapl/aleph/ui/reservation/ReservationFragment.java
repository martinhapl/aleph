package net.hapl.aleph.ui.reservation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;

public class ReservationFragment extends Fragment {

    private static final String TAG = "ReservationFragment";

    private ListView reservationList;
    private TextView noReservationTextView;

    ReservationListAdapter reservationListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static ReservationFragment newInstance() {
        ReservationFragment fragment = new ReservationFragment();
        return fragment;
    }

    /**
     * Empty constructor required
     */
    public ReservationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reservation, container, false);

        reservationList = (ListView) rootView.findViewById(R.id.reservationList);
        noReservationTextView = (TextView) rootView.findViewById(R.id.noReservationTextView);

        if(AlephControl.getInstance().getCtenar().getRezervaceDTOs().size() == 0) {
            reservationList.setVisibility(View.GONE);
            noReservationTextView.setVisibility(View.VISIBLE);
        }
        else {
            reservationList.setVisibility(View.VISIBLE);
            noReservationTextView.setVisibility(View.GONE);

            reservationListAdapter = new ReservationListAdapter(getActivity(), AlephControl.getInstance().getCtenar().getRezervaceDTOs());

            reservationList.setAdapter(reservationListAdapter);
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public static class ViewHolder {
        public TextView bookTitleItem;
        public TextView authorNameItem;
        public ImageButton deleteItem;
    }
}

