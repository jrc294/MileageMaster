package com.aspiration.mileagemaster.data;

import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspiration.mileagemaster.R;

/**
 * Created by jonathan.cook on 3/29/2016.
 */
public class StandardChargeAdapter extends RecyclerView.Adapter<StandardChargeAdapter.ViewHolder> {

    private static final String LOG_TAG = TripContract.StandardChargeEntry.class.getSimpleName();

    private Cursor mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
            Log.d(LOG_TAG, "StandardChargeAdapter ViewHolder constructor");
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StandardChargeAdapter(Cursor myDataset) {
        Log.d(LOG_TAG, "StandardChargeAdapter constructor");
        mDataset = myDataset;
    }

    @Override
    public StandardChargeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "StandardChargeAdapter onCreateViewHolder");
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(StandardChargeAdapter.ViewHolder holder, int position) {
        Log.d(LOG_TAG, "StandardChargeAdapter onBindViewHolder");
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        mDataset.moveToPosition(position);

        CardView cv_card_view = (CardView) holder.mView.findViewById(R.id.card_view);
        cv_card_view.setTag(mDataset.getLong(mDataset.getColumnIndex(TripContract.StandardChargeEntry._ID)));

        TextView tvStandardChargeDesc = (TextView) holder.mView.findViewById(R.id.tvStandardChargeDesc);
        //tvStandardChargeDesc.setText(mDataset[position]);

        String sStandardChargeDesc = mDataset.getString(mDataset.getColumnIndex(TripContract.StandardChargeEntry.COLUMN_NAME));
        tvStandardChargeDesc.setText(sStandardChargeDesc);

        TextView tvStandardChargeCost = (TextView) holder.mView.findViewById(R.id.tvStandardChargeCost);
        String sStandardChargeCost = String.valueOf(mDataset.getFloat(mDataset.getColumnIndex(TripContract.StandardChargeEntry.COLUMN_COST)));
        tvStandardChargeCost.setText(sStandardChargeCost);


        //tvStandardChargeCost.setText(mDataset[position]);
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
