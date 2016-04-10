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
 * Created by jonathan.cook on 3/29/2016.
 */
public class StandardListAdapter extends RecyclerView.Adapter<StandardListAdapter.ViewHolder> {

    private static final String LOG_TAG = StandardListAdapter.class.getSimpleName();

    private Cursor mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StandardListAdapter(Cursor myDataset) {
        mDataset = myDataset;
    }

    @Override
    public StandardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(StandardListAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        mDataset.moveToPosition(position);

        CardView cv_card_view = (CardView) holder.mView.findViewById(R.id.card_view);
        cv_card_view.setTag(mDataset.getLong(0));

        TextView tvStandardChargeDesc = (TextView) holder.mView.findViewById(R.id.tvStandardChargeDesc);
        //tvStandardChargeDesc.setText(mDataset[position]);

        String sStandardChargeDesc = mDataset.getString(1);
        tvStandardChargeDesc.setText(sStandardChargeDesc);

        TextView tvStandardChargeCost = (TextView) holder.mView.findViewById(R.id.tvStandardChargeCost);
        String sStandardChargeCost = String.valueOf(mDataset.getFloat(2));
        tvStandardChargeCost.setText(sStandardChargeCost);
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
