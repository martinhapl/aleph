package net.hapl.aleph.ui.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.control.SearchComm;
import net.hapl.aleph.model.PresentDTO;

import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";
    private static final String ARG_QUERY = "query";

    private String query;
    private int preLast;
    private int selectedPosition;
    private boolean bigScreen = false;

    // layout items
    private ListView searchResultList;
    private EditText searchEditText;
    private ImageButton searchButton;
    private RelativeLayout searchLayout;
    private RelativeLayout progressLayout;
    private LinearLayout loadingPanel;
    private TextView noResultTextView;
    private FrameLayout rightFragment;

    private SearchViewModel searchViewModel;
    private SearchListAdapter searchListAdapter;
    private SearchComm searchComm;
    private List<PresentDTO> presentDTO;
    private DetailFragment detailFragment;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);

        return fragment;
    }


    /**
     * Empty constructor required ?!
     */
    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if(savedInstanceState != null) {
            query = savedInstanceState.getString(ARG_QUERY, "");
        } else {
            Bundle args = getArguments();
            if (args != null) {
                query = args.getString(ARG_QUERY, "");
            } else {
                query = MainActivity.getContext().getQuery();
            }
        }

        androidx.appcompat.app.ActionBar actionBar = MainActivity.getContext().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.searchBackground)));
        }

        presentDTO = AlephControl.getInstance().getPresentDTOs();
        searchComm = (SearchComm) getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        outState.putString(ARG_QUERY, query);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: " + query);

        searchResultList.setSelection(selectedPosition);
        presentDTO = AlephControl.getInstance().getPresentDTOs();
        searchComm = (SearchComm) getActivity();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onDestroyView () {
        Log.d(TAG, "onDestroyView");

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

        super.onDestroyView();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        query = "";

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        // find UI items
        searchResultList = (ListView) root.findViewById(R.id.searchResult);
        searchEditText = (EditText) root.findViewById(R.id.searchEditText);
        searchButton = (ImageButton) root.findViewById(R.id.searchButton);
        searchLayout = (RelativeLayout) root.findViewById(R.id.searchLayout);
        progressLayout = (RelativeLayout) root.findViewById(R.id.progressLayout);
        loadingPanel = (LinearLayout) root.findViewById(R.id.loadingPanel);
        noResultTextView = (TextView) root.findViewById(R.id.noResultTextView);
        // pouze velky displey
        rightFragment = (FrameLayout) root.findViewById(R.id.right_search_fragment);

        searchLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);

        searchEditText.setText(query);
        searchEditText.setOnEditorActionListener(searchKeyListener);

        searchButton.setOnClickListener(searchButtonListener);
        searchResultList.setOnScrollListener(onScrollListener);

        if(rightFragment != null) {
            bigScreen = true;
        }



        loadSearchList();

        return root;
    }









    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");

        if(bigScreen) {
            inflater.inflate(R.menu.menu_detail_search, menu);
        }
        else {
            inflater.inflate(R.menu.menu_search, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            Log.d(TAG, "onOptionsItemSelected-action_favorite");

            if(detailFragment != null && bigScreen) {
                if(AlephControl.getInstance().addFavourite(presentDTO.get(detailFragment.getPosition()))) {
                    Toast.makeText(getActivity(), getString(R.string.add_record_to_favorite), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.add_record_to_favorite_false), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                if(AlephControl.getInstance().addFavouriteQuery(this.searchEditText.getText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.add_query_to_favorite), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.add_query_to_favorite_false), Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        else if(id == R.id.action_send) {
            sendRecordByEmail();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Send record by mail
     */
    private void sendRecordByEmail() {
        String messageToSend = AlephControl.getInstance().createMessageToSendByEmail(detailFragment.getPosition(), MainActivity.DETAIL_SEARCH_STATE);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_share_email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, messageToSend);
        Intent mailer = Intent.createChooser(intent, null);
        startActivity(mailer);
    }

    private boolean loadDetailFragment() {
        if(detailFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            detailFragment = DetailFragment.newInstance(MainActivity.DETAIL_SEARCH_STATE, 0);
            fragmentManager.beginTransaction().replace(R.id.right_search_fragment, detailFragment).commit();
            return true;
        }
        return false;
    }

    public void setSelectedPosition(int pos) {
        Log.d(TAG, "setSelectedPosition: " + Integer.toString(pos));
        this.selectedPosition = pos;
        searchResultList.setSelection(selectedPosition);
    }

    public void notifyAdapter() {
        searchResultList.post(new Runnable() {
            @Override
            public void run() {
                searchListAdapter.notifyDataSetChanged();
            }
        });

        if(bigScreen) {
            detailFragment.notifyAdapter(-1);
        }
    }

    public void loadAvailabilityFragment(int id) {
        FragmentManager fragmentManager = getFragmentManager();
        AvailabilityFragment availabilityFragment = AvailabilityFragment.newInstance(id, MainActivity.DETAIL_SEARCH_STATE);
        fragmentManager.beginTransaction().replace(R.id.right_search_fragment, availabilityFragment).addToBackStack(null).commit();
    }

    private void loadSearchList() {
        // query not empty and presentDTO empty -> search()
        if(!query.equals("")  && (presentDTO == null || (presentDTO.size() == 0))) {
            Log.d(TAG, "present dto null, query emnpty");
            search(query);
        }
        // query not same like in aleph control -> search()
        else if(!query.equals("") && !query.equals(AlephControl.getInstance().getQuery())) {
            Log.d(TAG, "query not empty, new query");
            search(query);
        }
        // presentDTO not empty -> load it
        else if(presentDTO != null) {
            Log.d(TAG, "present dto not null");
            setResultList();
            searchEditText.setText(AlephControl.getInstance().getQuery());

            if(bigScreen) {
                loadDetailFragment();
            }
        }
        // wait for set query
        else {
            searchEditText.requestFocus();

            // show soft input
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, 0);
        }
    }

    /**
     * Search query
     * @param query hledany retezec
     */
    public void search(final String query) {
        Log.d(TAG, "searching: " + query);

        searchLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        // hide software input
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

        this.query = query;

        new Thread(new Runnable() {
            @Override
            public void run() {
                presentDTO = AlephControl.getInstance().find(query, getActivity());

                // spusteni AsyncTasku
                //new ObalkaDownload(getActivity()).execute(presentDTO);

                if(bigScreen) {
                    if(!loadDetailFragment()) {
                        detailFragment.notifyAdapter(0);
                    }
                }

                if(presentDTO.size() == 0) {
                    noResultTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            searchLayout.setVisibility(View.VISIBLE);
                            progressLayout.setVisibility(View.GONE);
                            presentDTO.clear();
                            setResultList();
                            noResultTextView.setText(getString(R.string.no_search_result));
                        }
                    });
                }
                else {
                    Log.d(TAG, "searching: show result list");
                    searchResultList.post(new Runnable() {
                        @Override
                        public void run() {
                            searchLayout.setVisibility(View.VISIBLE);
                            progressLayout.setVisibility(View.GONE);
                            setResultList();
                            noResultTextView.setText("");
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Set result list with presentDTO
     */
    private void setResultList() {
        if(presentDTO != null) {
            searchListAdapter = new SearchListAdapter(getActivity(), R.layout.item_search_list, presentDTO);

            searchResultList.setAdapter(searchListAdapter);
            searchResultList.setOnItemClickListener(itemClickListener);
        }
    }

    /*********************************************
     LISTENERS
     **********************************************/

    /**
     *  Listener for item click in list of book
     */
    final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(bigScreen) {
                detailFragment.setSelectedPosition(position);
            }
            else {
                searchComm.searchStartDetailActivity(position, MainActivity.DETAIL_SEARCH_STATE);
            }
        }
    };

    /**
     * On Scroll listener for find next results, when end of list reached
     */
    final AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            final int lastItem = firstVisibleItem + visibleItemCount;

            searchComm.searchSetVisiblePosition(firstVisibleItem);

            if(lastItem == totalItemCount) {
                if(preLast!=lastItem) {
                    preLast = lastItem;

                    loadingPanel.post(new Runnable() {
                        @Override
                        public void run() {
                            loadingPanel.setVisibility(View.VISIBLE);
                        }
                    });

                    // get next items in thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AlephControl.getInstance().findNext();
                            presentDTO = AlephControl.getInstance().getPresentDTOs();

                            if(bigScreen) {
                                detailFragment.notifyAdapter(-1);
                            }

                            searchResultList.post(new Runnable() {
                                @Override
                                public void run() {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).start();
                }
            }
        }
    };

    // Search button listener - start searching, set progress bar on
    final View.OnClickListener searchButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            search(searchEditText.getText().toString());
            //loadingPanel.setVisibility(View.VISIBLE);
        }
    };

    final TextView.OnEditorActionListener searchKeyListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                search(searchEditText.getText().toString());
                return true;
            }
            return false;
        }
    };
}
