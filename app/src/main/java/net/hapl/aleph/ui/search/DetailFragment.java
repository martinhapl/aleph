package net.hapl.aleph.ui.search;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.control.SearchComm;
import net.hapl.aleph.model.PresentDTO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";

    private static final String PARAM_DETAIL_STATE = "PARAM_DETAIL_STATE";
    private static final String PARAM_SELECTED_POSITION = "PARAM_SELECTED_POSITION";
    private static final String POSITION_IN_DTO = "POSITION_IN_DTO";

    private int selectedPosition;
    private int detailState;

    private FrameLayout view;
    private View child;
    private MyDetailAdapter mAdapter;
    private ViewPager mPager;
    private SearchComm comm;

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
            Log.d(TAG, "onCreate-selectedPosition" + Integer.toString(selectedPosition));
        }
        else {
            Bundle args = getArguments();
            selectedPosition = args.getInt(PARAM_SELECTED_POSITION, 0);
            detailState = args.getInt(PARAM_DETAIL_STATE, 0);
        }

        comm = (SearchComm) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mAdapter = new MyDetailAdapter(getActivity().getSupportFragmentManager());
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

    //-----------------------------------------------------------------------------------------

    /**
     * Adapter for ViewPager
     */
    public class MyDetailAdapter extends FragmentStatePagerAdapter {
        int readedLast = 0;

        public MyDetailAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if(detailState == MainActivity.DETAIL_SEARCH_STATE) {
                return AlephControl.getInstance().getPresentDTOs().size();
            }
            else {
                return AlephControl.getInstance().getFavourite().size();
            }
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {

            if(detailState == MainActivity.DETAIL_SEARCH_STATE) {
                if((position == (AlephControl.getInstance().getPresentDTOs().size() - 1))) {
                    if(readedLast != (AlephControl.getInstance().getPresentDTOs().size())) {
                        Log.d(TAG, "read next");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                readedLast = AlephControl.getInstance().getPresentDTOs().size();
                                AlephControl.getInstance().findNext();

                                mPager.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataChanged();
                                    }
                                });

                                comm.searchChangedAdapter();
                            }
                        }).start();
                    }
                }
            }

            return ArrayListFragment.newInstance(position, detailState);
        }

        private void notifyDataChanged(){
            notifyDataSetChanged();
        }
    }

    //--------------------------------------------------------------------------------------

    /**
     *
     */
    public static class ArrayListFragment extends Fragment {
        private static final String TAG = "ArrayListFragment";

        int mNum;
        int detailState;

        LinearLayout availabilityCheck;
        LinearLayout nextServices;

        SearchComm comm;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num, int detailState) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            args.putInt("PARAM_DETAIL_STATE", detailState);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
            detailState = getArguments() != null ? getArguments().getInt("PARAM_DETAIL_STATE") : 1;

            comm = (SearchComm) getActivity();
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View fragment_pager_detail = inflater.inflate(R.layout.fragment_pager_detail, container, false);

            loadDetailBook(fragment_pager_detail);

            availabilityCheck = (LinearLayout) fragment_pager_detail.findViewById(R.id.availibilityCheck);
            availabilityCheck.setOnClickListener(onAvailabilityCheckListener);

            nextServices = (LinearLayout) fragment_pager_detail.findViewById(R.id.nextServices);
            nextServices.setOnClickListener(onNextServicesListener);

            return fragment_pager_detail;
        }

        /**
         * Load detail of searched book to layout
         * @param fragment_pager_list detail layout
         */
        private void loadDetailBook(View fragment_pager_list){
            LinearLayout detailLayout = (LinearLayout) fragment_pager_list.findViewById(R.id.detailLayout);
            View imageView = fragment_pager_list.findViewById(R.id.imageViewListItem);
            PresentDTO presentDTO;

            if(detailState == MainActivity.DETAIL_SEARCH_STATE) {
                presentDTO = AlephControl.getInstance().getPresentDTOs().get(mNum);
            }
            else {
                AlephControl.getInstance().loadFavourites();
                presentDTO = AlephControl.getInstance().getFavourite().get(mNum);
            }

            if(presentDTO.getObalka() != null) {
                ((ImageView)imageView).setImageBitmap(AlephControl.getInstance().getImageFromDiskCache(presentDTO.getObalka()));
            }
            else {
                ((ImageView)imageView).setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.medium));
            }

            View bookName = fragment_pager_list.findViewById(R.id.bookListItem);
            if(presentDTO.getPodNazev() != null) {
                ((TextView)bookName).setText(presentDTO.getNazev() + presentDTO.getPodNazev());
            }
            else {
                ((TextView)bookName).setText(presentDTO.getNazev());
            }

            View authorName = fragment_pager_list.findViewById(R.id.authorListItem);
            ((TextView)authorName).setText(presentDTO.getAutor());

            if (presentDTO.getImprint() != null) {
                createTextViews(detailLayout, getString(R.string.nakladatel_udaje), presentDTO.getImprint());
            }

            if (presentDTO.getVydani() != null) {
                createTextViews(detailLayout, getString(R.string.vydani), presentDTO.getVydani());
            }

            if (presentDTO.getPopis() != null) {
                createTextViews(detailLayout, getString(R.string.fyzicky_popis), presentDTO.getPopis());
            }

            if (presentDTO.getPoznamka() != null) {
                int i;
                for(i = 0; i < presentDTO.getPoznamka().size(); i++) {
                    if(i == 0) {
                        createTextViews(detailLayout, getString(R.string.poznamky), presentDTO.getPoznamka().get(i));
                    }
                    else {
                        createTextViews(detailLayout, "", presentDTO.getPoznamka().get(i));
                    }
                }
            }

            if (presentDTO.getPredmet() != null) {
                int i;
                for(i = 0; i < presentDTO.getPredmet().size(); i++) {
                    if(i == 0){
                        createTextViews(detailLayout, getString(R.string.predmet), presentDTO.getPredmet().get(i));
                    }
                    else {
                        createTextViews(detailLayout, "", presentDTO.getPredmet().get(i));
                    }
                }
            }

            if (presentDTO.getIsbn() != null) {
                createTextViews(detailLayout, getString(R.string.isbn), presentDTO.getIsbn());
            }

            if (presentDTO.getJazyk() != null) {
                createTextViews(detailLayout, getString(R.string.jazyk), presentDTO.getJazyk());
            }
        }

        /**
         * Create text views for detail of book
         * @param title
         * @param value
         */
        private void createTextViews(LinearLayout fragment_pager_detail, String title, String value) {
            LinearLayout.LayoutParams titleLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout.LayoutParams valueLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            valueLayout.setMargins(20, 0, 0, 0);

            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView titleView = new TextView(getActivity());
            TextView valueView = new TextView(getActivity());

            titleView.setText(title);
            titleView.setLayoutParams(titleLayout);

            valueView.setText(value);
            valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            valueView.setTextColor(getResources().getColor(R.color.grayText));
            valueView.setGravity(Gravity.RIGHT);
            valueView.setLayoutParams(valueLayout);

            linearLayout.addView(titleView);
            linearLayout.addView(valueView);

            fragment_pager_detail.addView(linearLayout);

            // separator line
            LinearLayout.LayoutParams separatorLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
            separatorLayout.setMargins(0, 16, 0, 16);

            View view = new View(getActivity());
            view.setBackgroundColor(getResources().getColor(R.color.grayText));
            view.setLayoutParams(separatorLayout);

            fragment_pager_detail.addView(view);
        }

        final View.OnClickListener onAvailabilityCheckListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.searchAvailabilityShow(mNum);
            }
        };

        final View.OnClickListener onNextServicesListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PresentDTO presentDTO;

                if(detailState == MainActivity.DETAIL_SEARCH_STATE) {
                    presentDTO = AlephControl.getInstance().getPresentDTOs().get(mNum);
                }
                else {
                    AlephControl.getInstance().loadFavourites();
                    presentDTO = AlephControl.getInstance().getFavourite().get(mNum);
                }

                String SFX = AlephControl.getInstance().createSFX(presentDTO);

                if(SFX != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(SFX));
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.detail_no_isbn), Toast.LENGTH_SHORT);
                }
            }
        };
    }
}
