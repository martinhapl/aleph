package net.hapl.aleph.ui.favorite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.control.FavoriteComm;


public class FavoriteRecordFragment extends Fragment {

    public static final String TAG = "FavoriteRecordFragment";
    private ListTouchListener favoriteListTouchListener;
    private FavoriteRecordListAdapter favoriteRecordListAdapter;
    private ListView favoriteList;
    private int selectedPosition;
    private boolean mItemPressed = false;
    private boolean mSwiping = false;
    private FavoriteComm comm;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static FavoriteRecordFragment newInstance() {
        return new FavoriteRecordFragment();
    }

    /**
     * Empty constructor required
     */
    public FavoriteRecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setHasOptionsMenu(true);

        favoriteListTouchListener = new ListTouchListener();

        AlephControl.getInstance().getFavouritesRepository().loadFavourites();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        favoriteRecordListAdapter = new FavoriteRecordListAdapter(getActivity(), favoriteListTouchListener);

        favoriteList.setAdapter(favoriteRecordListAdapter);
        favoriteList.setSelection(selectedPosition);

        comm = (FavoriteComm) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_favorite_record, container, false);
        //View itemView = inflater.inflate(R.layout.item_favorite_record_list, container);

        favoriteList = (ListView) rootView.findViewById(R.id.favorite_list);

        // Inflate the layout for this fragment
        return rootView;
    }

    public void notifyAdapter() {
        favoriteList.post(new Runnable() {
            @Override
            public void run() {
                favoriteRecordListAdapter.notifyDataSetInvalidated();
            }
        });
    }

    public void setSelectedPosition(int pos) {
        selectedPosition = pos;
        favoriteList.setSelection(pos);
    }

    public static class ViewHolder {
        public ImageView imgIcon;
        public TextView txtBook;
        public TextView txtAuthor;
        public ImageButton removeButton;
    }

    final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick");
        }
    };


    public class ListTouchListener implements AdapterView.OnTouchListener {

        float mDownX;
        float deltaX;


        @Override
        public boolean onTouch(final View v, MotionEvent event) {

            final ViewHolder holder = (ViewHolder) v.getTag(R.layout.item_favorite_record_list);
            int position = (Integer) v.getTag();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Log.d(TAG, "onTouch  ACTION_DOWN");
                    if(mItemPressed) {
                        //return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    deltaX = 0;
                }
                break;
                case MotionEvent.ACTION_CANCEL: {
                    Log.d(TAG, "onTouch  ACTION_CANCEL");
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                }
                break;
                case MotionEvent.ACTION_MOVE: {
                    Log.d(TAG, "onTouch  ACTION_MOVE");
                    float x = event.getX() + v.getTranslationX();

                    deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        int mSwipeSlop = -1;
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            favoriteList.requestDisallowInterceptTouchEvent(true);
                            //mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    Log.d(TAG, "onTouch  ACTION_UP");
                    // only click
                    if(Math.abs(deltaX) < 10) {
                        comm.showDetailFromFavoriteRecordFragment(position);
                        return false;
                    }

                    if((-deltaX) > v.getWidth()/5) {
                        v.setAlpha(1);
                        v.setTranslationX(0);//v.getWidth()/5 * -1);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.removeButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else if(deltaX > v.getWidth()/5) {
                        v.setAlpha(1);
                        v.setTranslationX(0);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.removeButton.setVisibility(View.GONE);
                            }
                        });
                    }
                    else {
                        v.setAlpha(1);
                        v.setTranslationX(0);
                    }

                    mItemPressed = false;
                }

                default:
                    return false;
            }
            return true;
        }
    }
}
