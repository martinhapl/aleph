package net.hapl.aleph.ui.search;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.hapl.aleph.MainActivity;
import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.model.PresentDTO;

import java.util.List;

public class SearchListAdapter extends BaseAdapter  {

    private static final String TAG = "SearchListAdapter";

    private final Context context;
    private final List<PresentDTO> searchItems;

    /**
     * Constructor for searching
     * @param context
     * @param layoutResourceId
     * @param searchItems
     */
    public SearchListAdapter(Context context, int layoutResourceId, List<PresentDTO> searchItems) {
        this.context = context;
        this.searchItems = searchItems;
    }

    @Override
    public int getCount() {
        return searchItems.size();
    }

    @Override
    public Object getItem(int position) {
        return searchItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_search_list, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imageViewListItem);
        TextView txtBook = (TextView) convertView.findViewById(R.id.bookListItem);
        TextView txtAuthor = (TextView) convertView.findViewById(R.id.authorListItem);

        if(searchItems.get(position).getObalka() != null) {
            imgIcon.setImageBitmap(AlephControl.getInstance().getFindRepository().getImageFromDiskCache(searchItems.get(position).getObalka()));
        } else {
            imgIcon.setImageBitmap(BitmapFactory.decodeResource(MainActivity.getContext().getResources(), R.drawable.medium));
        }

        String yearAndAuthor = (searchItems.get(position).getImprintRok() != null ? searchItems.get(position).getImprintRok() + " - " : "") + (searchItems.get(position).getAutor() != null ? searchItems.get(position).getAutor() : "");
        txtAuthor.setText(yearAndAuthor);

        String nameAndSubname = (searchItems.get(position).getNazev() != null ? searchItems.get(position).getNazev() : "") + (searchItems.get(position).getPodNazev() != null ? searchItems.get(position).getPodNazev() : "");
        txtBook.setText(nameAndSubname);

        return convertView;
    }
}
