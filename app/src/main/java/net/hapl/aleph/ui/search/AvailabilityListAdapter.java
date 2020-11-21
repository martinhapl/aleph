package net.hapl.aleph.ui.search;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.hapl.aleph.R;
import net.hapl.aleph.model.ItemDataDTO;

import java.util.List;


public class AvailabilityListAdapter extends BaseAdapter {

    private static final String TAG = "AvailabilityListAdapter";

    private final Context context;
    private final List<ItemDataDTO> availabilityItems;

    public AvailabilityListAdapter(Context context, int layoutResourceId, List<ItemDataDTO> searchItems) {
        this.context = context;
        this.availabilityItems = searchItems;
    }

    @Override
    public int getCount() { return availabilityItems.size(); }

    @Override
    public Object getItem(int position) {
        return availabilityItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_availability_list, null);
        }

        TextView availabilityLibrary = (TextView) convertView.findViewById(R.id.availabilityLibrary);
        TextView availabilityInfo = (TextView) convertView.findViewById(R.id.availabilityInfo);
        TextView availabilityCode = (TextView) convertView.findViewById(R.id.availabilityCode);

        Log.d(TAG, "knihovna: " + availabilityItems.get(position).getSubLibrary());

        availabilityLibrary.setText(availabilityItems.get(position).getSubLibrary());
        availabilityInfo.setText(availabilityItems.get(position).getiStatus() + " (" + availabilityItems.get(position).getDueDate() + ")");
        availabilityCode.setText(availabilityItems.get(position).getCollection());

        return convertView;
    }
}
