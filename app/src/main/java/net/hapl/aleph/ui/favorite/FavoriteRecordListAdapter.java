package net.hapl.aleph.ui.favorite;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.control.FavoriteComm;


public class FavoriteRecordListAdapter extends BaseAdapter {

    private static final String TAG = "FavoriteListAdapter";

    private final Context context;
    private final FavoriteRecordFragment.ListTouchListener favoriteListTouchListener;
    private final FavoriteComm favoriteComm;


    /**
     * @param context
     * @param favoriteListTouchListener
     */
    public FavoriteRecordListAdapter(Context context, FavoriteRecordFragment.ListTouchListener favoriteListTouchListener) {
        this.context = context;
        this.favoriteListTouchListener = favoriteListTouchListener;
        favoriteComm = (FavoriteComm) context;
    }

    @Override
    public int getCount() { return AlephControl.getInstance().getFavouritesRepository().getFavourite().size(); }

    @Override
    public Object getItem(int position) {
        return AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void notifyAdp() {
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FavoriteRecordFragment.ViewHolder holder = new FavoriteRecordFragment.ViewHolder();

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_favorite_record_list, null);

            holder.imgIcon = (ImageView) convertView.findViewById(R.id.imageViewListFavoriteItem);
            holder.txtBook = (TextView) convertView.findViewById(R.id.bookListFavoriteItem);
            holder.txtAuthor = (TextView) convertView.findViewById(R.id.authorListFavoriteItem);
            holder.removeButton = (ImageButton) convertView.findViewById(R.id.removeButton);

            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();

                    AlephControl.getInstance().getFavouritesRepository().removeFavourite(pos);
                    AlephControl.getInstance().getFavouritesRepository().saveFavourites();

                    v.setVisibility(View.GONE);

                    notifyAdp();

                    favoriteComm.updateFromFavoriteAdapters();
                }
            });

            convertView.setTag(R.layout.item_favorite_record_list, holder);
        }
        else {
            holder = (FavoriteRecordFragment.ViewHolder) convertView.getTag(R.layout.item_favorite_record_list);
        }

        convertView.setTag(position);
        convertView.setOnTouchListener(favoriteListTouchListener);

        String nazevAndPodnazev;
        nazevAndPodnazev = AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getNazev() != null ?
                AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getNazev() : "";
        nazevAndPodnazev += AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getPodNazev() != null ?
                AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getPodNazev() : "";
        holder.txtBook.setText(nazevAndPodnazev);

        String yearAndAuthor;
        yearAndAuthor = AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getImprintRok() != null ? AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getImprintRok() +  " - " : "";
        yearAndAuthor += AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getAutor() != null ? AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getAutor() : "";
        holder.txtAuthor.setText(yearAndAuthor);

        if(AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getObalka() != null) {
            holder.imgIcon.setImageBitmap(AlephControl.getInstance().getFindRepository().
                    getImageFromDiskCache(AlephControl.getInstance().getFavouritesRepository().getFavourite().get(position).getObalka()));
        }
        else {
            holder.imgIcon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.medium));
        }

        holder.removeButton.setTag(position);
        holder.removeButton.setVisibility(View.GONE);
        return convertView;
    }
}
