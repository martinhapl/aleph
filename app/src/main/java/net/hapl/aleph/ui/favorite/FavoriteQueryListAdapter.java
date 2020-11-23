package net.hapl.aleph.ui.favorite;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;

import java.util.List;



public class FavoriteQueryListAdapter extends BaseAdapter {
    private static String TAG = "FavoriteQueryListAdapter";

    private final Context context;
    private final List<String> queryItems;
    private final FavoriteQueryFragment.ListTouchListener listTouchListener;

    public FavoriteQueryListAdapter(Context context, List<String> queries, FavoriteQueryFragment.ListTouchListener listTouchListener) {
        this.context = context;
        this.queryItems = queries;
        this.listTouchListener = listTouchListener;
    }

    @Override
    public int getCount() {
        return queryItems.size();
    }

    @Override
    public Object getItem(int position) {
        return queryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void notifyAdp() {
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavoriteQueryFragment.ViewHolder holder = new FavoriteQueryFragment.ViewHolder();

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_favorite_query_list, null);

            holder.favorite_query_list_query = (TextView) convertView.findViewById(R.id.favorite_query_list_item);
            holder.removeButton = (ImageButton) convertView.findViewById(R.id.removeQueryItemButton);

            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();

                    AlephControl.getInstance().getFavouriteQueryRepository().removeFavouriteQuery(pos);

                    v.setVisibility(View.GONE);

                    notifyAdp();
                }
            });

            convertView.setTag(R.layout.item_favorite_query_list, holder);
        } else {
            holder = (FavoriteQueryFragment.ViewHolder) convertView.getTag(R.layout.item_favorite_query_list);
        }

        convertView.setTag(position);
        convertView.setOnTouchListener(listTouchListener);

        holder.favorite_query_list_query.setText(queryItems.get(position));

        holder.removeButton.setTag(position);
        holder.removeButton.setVisibility(View.GONE);

        return convertView;
    }
}

