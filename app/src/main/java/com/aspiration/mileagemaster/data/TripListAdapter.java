package com.aspiration.mileagemaster.data;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspiration.mileagemaster.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonathan.cook on 4/28/2016.
 */
public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder>{

    private static final String LOG_TAG = StandardListAdapter.class.getSimpleName();

    private Cursor mDataset;
    public int selected_position = -1;
    public long selected_trip;
    private Callback mCallback;
    Map<Long,Integer> selected = new HashMap<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public void setTrip(int pos) {
        selected_position = pos;
    }

    public int getTrip() {
        return selected_position;
    }



    public TripListAdapter(Cursor myDataset ,Callback callback) {
        mDataset = myDataset;
        mCallback = callback;
    }

    @Override
    public TripListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trip, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        CardView cv_card_view = (CardView) vh.mView.findViewById(R.id.card_view_trip);

        cv_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Updating old as well as new positions
                TripListAdapter.this.notifyItemChanged(selected_position);
                selected_trip = (long) v.getTag();
                selected_position = selected.get(selected_trip);
                TripListAdapter.this.notifyItemChanged(selected_position);
                mCallback.startactivity(v);

                // Do your another stuff for your onClick
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        mDataset.moveToPosition(position);

        final CardView cv_card_view = (CardView) holder.mView.findViewById(R.id.card_view_trip);
        cv_card_view.setTag(mDataset.getLong(0));

        selected.put(mDataset.getLong(0), position);

        TextView tvTripDetails1 = (TextView) holder.mView.findViewById(R.id.tvTripDetails1);
        TextView tvTripDetails2 = (TextView) holder.mView.findViewById(R.id.tvTripDetails2);
        String sDescription = mDataset.getString(1);
        tvTripDetails1.setText(sDescription.substring(0,sDescription.indexOf("\n")));
        tvTripDetails2.setText(sDescription.substring(sDescription.indexOf("\n") + 1,sDescription.length()));

        if(selected_position == position){
            // Here I am just highlighting the background
            cv_card_view.setBackgroundColor(Color.LTGRAY);
        }else{
            cv_card_view.setBackgroundColor(Color.WHITE);
        }

    }




    @Override
    public int getItemCount() {
        return mDataset.getCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public interface Callback {

        public void startactivity(View view);

    }
}
