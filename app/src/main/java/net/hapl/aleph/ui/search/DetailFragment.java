package net.hapl.aleph.ui.search;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.hapl.aleph.R;


public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";

    private static final String PARAM_DETAIL_STATE = "PARAM_DETAIL_STATE";
    private static final String PARAM_SELECTED_POSITION = "PARAM_SELECTED_POSITION";
    private static final String POSITION_IN_DTO = "POSITION_IN_DTO";

    private int selectedPosition;
    private int detailState;

    private DetailAdapter mAdapter;
    private ViewPager mPager;

    public static DetailFragment newInstance(int detailState, int selectedPosition) {
        DetailFragment fragment = new DetailFragment();

        Bundle args = new Bundle();
        args.putInt(PARAM_DETAIL_STATE, detailState);
        args.putInt(PARAM_SELECTED_POSITION, selectedPosition);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Empty constructor required
     */
    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            detailState = savedInstanceState.getInt(PARAM_DETAIL_STATE);
            selectedPosition = savedInstanceState.getInt(PARAM_SELECTED_POSITION, 0);
            Log.d(TAG, "onCreate-selectedPosition" + selectedPosition);
        } else {
            Bundle args = getArguments();
            selectedPosition = args.getInt(PARAM_SELECTED_POSITION, 0);
            detailState = args.getInt(PARAM_DETAIL_STATE, 0);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mAdapter = new DetailAdapter(this);

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(selectedPosition);
        mPager.setOnPageChangeListener(myPageChangeListener);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        notifyAdapter(selectedPosition);
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(PARAM_SELECTED_POSITION);
            Log.d(TAG, "onActivityCreated: " + selectedPosition);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(PARAM_DETAIL_STATE, detailState);
        outState.putInt(PARAM_SELECTED_POSITION, selectedPosition);

        Log.d(TAG, "onSaveInstanceState: " + selectedPosition);

        super.onSaveInstanceState(outState);
    }

    final ViewPager.OnPageChangeListener myPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public int getPosition() {
        return this.selectedPosition;
    }

    public void notifyAdapter(final int position) {
        Log.d(TAG, "notifyAdapter");
        mPager.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();

                if(position >= 0) {
                    mPager.setCurrentItem(position);
                }
            }
        });
    }

    public void setSelectedPosition(int position) {
        mPager.setCurrentItem(position);
        selectedPosition = position;
    }


    public ViewPager getPager() {
        return mPager;
    }

    public int getDetailState() {
        return detailState;
    }
}
