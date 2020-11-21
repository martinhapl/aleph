package net.hapl.aleph.ui.search;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.model.ItemDataDTO;

import java.util.List;

public class AvailabilityFragment extends Fragment {

    private static final String TAG = "AvailabilityFragment";

    private ListView availabilityList;
    private TextView noAvailabilityTextView;
    private LinearLayout availabilityLoadingPanel;

    private List<ItemDataDTO> itemDataDTOs;

    private static final String POSITION_IN_DTO = "POSITION_IN_DTO";
    private static final String PARAM_DETAIL_STATE = "PARAM_DETAIL_STATE";

    int positionInDto;
    int detailState;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static AvailabilityFragment newInstance(int positionInDto, int detailState) {
        AvailabilityFragment fragment = new AvailabilityFragment();

        Bundle args = new Bundle();
        args.putInt(POSITION_IN_DTO, positionInDto);
        args.putInt(PARAM_DETAIL_STATE, detailState);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Empty constructor required
     */
    public AvailabilityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if(savedInstanceState != null) {
            positionInDto = savedInstanceState.getInt(POSITION_IN_DTO, 0);
            detailState = savedInstanceState.getInt(PARAM_DETAIL_STATE, 0);
        }
        else {
            Bundle args = getArguments();
            positionInDto = args.getInt(POSITION_IN_DTO, 0);
            detailState = args.getInt(PARAM_DETAIL_STATE, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_availability, container, false);

        availabilityList = (ListView) rootView.findViewById(R.id.availabilityList);
        noAvailabilityTextView = (TextView) rootView.findViewById(R.id.noAvailabilityTextView);
        availabilityLoadingPanel = (LinearLayout) rootView.findViewById(R.id.availabilityLoadingPanel);

        availabilityLoadingPanel.setVisibility(View.VISIBLE);

        loadAvailability();

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(POSITION_IN_DTO, positionInDto);
        outState.putInt(PARAM_DETAIL_STATE, detailState);
        super.onSaveInstanceState(outState);
    }

    private void loadAvailability() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(detailState == MainActivity.DETAIL_SEARCH_STATE) {
                    itemDataDTOs = AlephControl.getInstance().loadItemData(AlephControl.getInstance().getFindRepository().getPresentDTOs().get(positionInDto));
                }
                else {
                    itemDataDTOs = AlephControl.getInstance().loadItemData(AlephControl.getInstance().getFavouritesRepository().getFavourite().get(positionInDto));
                }

                availabilityList.post(new Runnable() {
                    @Override
                    public void run() {
                        availabilityLoadingPanel.setVisibility(View.GONE);

                        if(itemDataDTOs != null || (itemDataDTOs.size() == 0)) {
                            noAvailabilityTextView.setVisibility(View.GONE);
                            availabilityList.setVisibility(View.VISIBLE);

                            AvailabilityListAdapter availabilityListAdapter = new AvailabilityListAdapter(getActivity(), R.layout.item_availability_list, itemDataDTOs);

                            availabilityList.setAdapter(availabilityListAdapter);
                            availabilityList.setOnItemClickListener(itemClickListener);
                        }
                        else {
                            noAvailabilityTextView.setVisibility(View.VISIBLE);
                            availabilityList.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();
    }


    /**
     * On Item Click Listener for availability list
     */
    final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // check user login
            AlephControl.getInstance().getConfig();
            if(AlephControl.getInstance().checkUserConfig()) {
                openAlertReservation(position);
            }
            else {
                openAlertNotLogin();
            }
        }
    };

    private void startSetConfigActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        //intent.putExtra("FRAGMENT", MainActivity.SETTINGS_FRAGMENT_ONLY);
        startActivity(intent);
    }

    private void openAlertNotLogin() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.availability_warning));
        alertDialogBuilder.setMessage(getString(R.string.alert_dialog_not_login));

        alertDialogBuilder.setPositiveButton(getString(R.string.alert_dialog_set),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                startSetConfigActivity();
            }
        });

        // set negative button
        alertDialogBuilder.setNegativeButton(getString(R.string.cancelAnswer),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    private void openAlertReservation(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.reservationDialogTitle));
        alertDialogBuilder.setMessage(getString(R.string.makeReservationDialog));

        alertDialogBuilder.setPositiveButton(getString(R.string.positiveAnswer),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                makeReservation(position);
            }
        });

        // set negative button
        alertDialogBuilder.setNegativeButton(getString(R.string.negativeAnswer),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    private void makeReservation(final int position) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.progressDialogTitle), getString(R.string.progressDialogContent), true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(AlephControl.getInstance().createHold(itemDataDTOs.get(position))) {
                        availabilityList.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getString(R.string.availability_reservation_created), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        availabilityList.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getString(R.string.availability_reservation_not_created), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }

}

