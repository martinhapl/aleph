package net.hapl.aleph.ui.reservation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.hapl.aleph.R;
import net.hapl.aleph.control.AlephControl;
import net.hapl.aleph.control.MainActivityComm;
import net.hapl.aleph.model.RezervaceDTO;
import net.hapl.aleph.ui.reservation.ReservationFragment;

import java.util.List;


public class ReservationListAdapter extends BaseAdapter {

    private static final String TAG = "ReservationListAdapter";

    private final Context context;
    private final List<RezervaceDTO> reservationItems;
    final MainActivityComm comm;

    /**
     * Constructor for searching
     * @param context
     * @param reservationItems
     */
    public ReservationListAdapter(Context context, List<RezervaceDTO> reservationItems) {
        this.context = context;
        this.reservationItems = reservationItems;
        comm = (MainActivityComm) this.context;
    }

    @Override
    public int getCount() { return reservationItems.size(); }

    @Override
    public Object getItem(int position) {
        return reservationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void notifyAdp() {
        this.notifyDataSetChanged();
    }

    private void openAlertCancelBorrow(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(R.string.reservation_cancel));
        alertDialogBuilder.setMessage(context.getString(R.string.reservation_cancel_question));

        alertDialogBuilder.setPositiveButton(context.getString(R.string.positiveAnswer),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                makeReservationCancel(position);
            }
        });

        // set negative button
        alertDialogBuilder.setNegativeButton(context.getString(R.string.cancelAnswer),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    private void makeReservationCancel(final int position) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(context, context.getString(R.string.reservation_progress_dialog_title),
                context.getString(R.string.reservation_progress_dialog_content), true);

        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "vymazano prvni: " + AlephControl.getInstance().getCtenar().getRezervaceDTOs().size());
                    final boolean result = AlephControl.getInstance().cancelHold(AlephControl.getInstance().getCtenar().getRezervaceDTOs().get(position));

                    if(result) {
                        AlephControl.getInstance().getCtenar().getRezervaceDTOs().remove(position);
                        Toast.makeText(context, context.getString(R.string.reservation_canceled), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "vymazano druhy: " + AlephControl.getInstance().getCtenar().getRezervaceDTOs().size());
                    }
                    else {Log.d(TAG, "vymazano treti: " + AlephControl.getInstance().getCtenar().getRezervaceDTOs().size());
                        Toast.makeText(context, context.getString(R.string.reservation_not_canceled), Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG, "vymazano ctvrty: " + AlephControl.getInstance().getCtenar().getRezervaceDTOs().size());

                    notifyAdp();

                } catch (Exception e) {
                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ReservationFragment.ViewHolder holder = new ReservationFragment.ViewHolder();

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_reservation_list, null);

            holder.bookTitleItem = (TextView) convertView.findViewById(R.id.bookTitleItem);
            holder.authorNameItem = (TextView) convertView.findViewById(R.id.authorNameItem);
            holder.deleteItem = (ImageButton) convertView.findViewById(R.id.deleteItem);
            holder.deleteItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    //openAlertCancelBorrow(pos);
                    AlephControl.getInstance().cancelHold(AlephControl.getInstance().getCtenar().getRezervaceDTOs().get(position));
                    AlephControl.getInstance().getCtenar().getRezervaceDTOs().remove(position);

                    notifyAdp();

                    comm.removeReservation();
                }
            });

            convertView.setTag(R.layout.item_reservation_list, holder);
        }
        else {
            holder = (ReservationFragment.ViewHolder) convertView.getTag(R.layout.item_reservation_list);
        }

        convertView.setTag(position);

        holder.bookTitleItem.setText(reservationItems.get(position).getNazev());
        holder.authorNameItem.setText(reservationItems.get(position).getAutor());

        holder.deleteItem.setTag(position);

        return convertView;
    }
}

