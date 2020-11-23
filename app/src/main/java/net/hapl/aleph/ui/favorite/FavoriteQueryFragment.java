package net.hapl.aleph.ui.favorite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.control.FavoriteComm;


public class FavoriteQueryFragment extends Fragment {

    public static final String TAG = "FavoriteQueryFragment";
    private FavoriteComm comm;
    private ListView favoriteList;
    private boolean mItemPressed = false;
    private boolean mSwiping = false;
    private ListTouchListener favoriteListTouchListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static FavoriteQueryFragment newInstance() {
        return new FavoriteQueryFragment();
    }

    /**
     * Empty constructor required
     */
    public FavoriteQueryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setHasOptionsMenu(true);

        favoriteListTouchListener = new ListTouchListener();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        comm = (FavoriteComm) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_favorite_record, container, false);

        favoriteList = (ListView) rootView.findViewById(R.id.favorite_list);

        FavoriteQueryListAdapter favoriteQueryListAdapter = new FavoriteQueryListAdapter(MainActivity.getContext(),
                AlephControl.getInstance().getFavouriteQueryRepository().getFavouriteQuery(), favoriteListTouchListener);

        favoriteList.setAdapter(favoriteQueryListAdapter);
        favoriteList.setOnItemClickListener(itemClickListener);

        // Inflate the layout for this fragment
        return rootView;
    }

    /**
     *
     */
    final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "itemClickListener: " + position);
            //cm.sendDataFromFavoriteQueryFragment(favoriteList.getAdapter().getItem(position).toString());
        }
    };

    public static class ViewHolder {
        public TextView favorite_query_list_query;
        public ImageButton removeButton;
    }

    public class ListTouchListener implements View.OnTouchListener {

        float mDownX;
        float deltaX;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {

            final ViewHolder holder = (ViewHolder) v.getTag(R.layout.item_favorite_query_list);
            int position = (Integer) v.getTag();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if (mItemPressed) {
                        //return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    deltaX = 0;
                }
                break;
                case MotionEvent.ACTION_CANCEL: {
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                }
                break;
                case MotionEvent.ACTION_MOVE: {
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
                    // only click
                    if(Math.abs(deltaX) < 10) {
                        comm.sendDataFromFavoriteQueryFragment(favoriteList.getAdapter().getItem(position).toString());
                        return false;
                    }

                    if ((-deltaX) > v.getWidth()/5) {
                        v.setAlpha(1);
                        v.setTranslationX(0);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.removeButton.setVisibility(View.VISIBLE);
                            }
                        });
                    } else if(deltaX > v.getWidth()/5) {
                        v.setAlpha(1);
                        v.setTranslationX(0);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.removeButton.setVisibility(View.GONE);
                            }
                        });
                    } else {
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


