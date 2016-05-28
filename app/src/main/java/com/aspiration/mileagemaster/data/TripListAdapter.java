package com.aspiration.mileagemaster.data;

import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspiration.mileagemaster.R;

/**
 * Created by jonathan.cook on 4/28/2016.
 */
public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder>{

    private static final String LOG_TAG = StandardListAdapter.class.getSimpleName();

    private Cursor mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public TripListAdapter(Cursor myDataset) {
        mDataset = myDataset;
    }

    @Override
    public TripListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trip, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        mDataset.moveToPosition(position);

        CardView cv_card_view = (CardView) holder.mView.findViewById(R.id.card_view_trip);
        cv_card_view.setTag(mDataset.getLong(0));

        TextView tvTripDetails1 = (TextView) holder.mView.findViewById(R.id.tvTripDetails1);
        TextView tvTripDetails2 = (TextView) holder.mView.findViewById(R.id.tvTripDetails2);
        String sDescription = mDataset.getString(1);
        tvTripDetails1.setText(sDescription.substring(0,sDescription.indexOf("\n")));
        tvTripDetails2.setText(sDescription.substring(sDescription.indexOf("\n") + 1,sDescription.length()));

    }


    @Override
    public int getItemCount() {
        return mDataset.getCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
