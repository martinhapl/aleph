package net.hapl.aleph.ui.search;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.control.SearchComm;

    /**
     * Adapter for ViewPager
     */
    public class DetailAdapter extends FragmentStatePagerAdapter {

        private static final String TAG = "DetailAdapter";

        private int readedLast = 0;
        private DetailFragment detailFragment;

        public DetailAdapter(DetailFragment detailFragment) {
            super(detailFragment.getActivity().getSupportFragmentManager());
            this.detailFragment = detailFragment;
        }

        @Override
        public int getCount() {
            if(detailFragment.getDetailState() == MainActivity.DETAIL_SEARCH_STATE) {
                return AlephControl.getInstance().getFindRepository().getPresentDTOs().size();
            } else {
                return AlephControl.getInstance().getFavouritesRepository().getFavourite().size();
            }
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            if (detailFragment.getDetailState() == MainActivity.DETAIL_SEARCH_STATE) {
                if ((position == (AlephControl.getInstance().getFindRepository().getPresentDTOs().size() - 1))) {
                    if (readedLast != (AlephControl.getInstance().getFindRepository().getPresentDTOs().size())) {
                        Log.d(TAG, "read next");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                readedLast = AlephControl.getInstance().getFindRepository().getPresentDTOs().size();
                                AlephControl.getInstance().getFindRepository().findNext();

                                detailFragment.getPager().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataChanged();
                                    }
                                });

                                SearchComm comm = (SearchComm) detailFragment.getActivity();
                                comm.searchChangedAdapter();
                            }
                        }).start();
                    }
                }
            }

            return ArrayListFragment.newInstance(position, detailFragment.getDetailState());
        }

        private void notifyDataChanged(){
            notifyDataSetChanged();
        }
    }


