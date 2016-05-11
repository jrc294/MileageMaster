package com.aspiration.mileagemaster.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspiration.mileagemaster.R;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by jonathan.cook on 3/29/2016.
 */
public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ViewHolder> {

    private static final String LOG_TAG = ClientListAdapter.class.getSimpleName();

    private Cursor mDataset;
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ClientListAdapter(Cursor myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    @Override
    public ClientListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ClientListAdapter.ViewHolder holder, int position) {
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
        String sClientMileageCost = String.format("%s %s%.2f, %s %.2f%s", mContext.getResources().getString(R.string.charge_cost), Currency.getInstance(Locale.getDefault()).getSymbol(), mDataset.getFloat(2),
                mContext.getResources().getString(R.string.tax), mDataset.getFloat(3), "%");
        tvStandardChargeCost.setText(sClientMileageCost);
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
