package com.aspiration.mileagemaster;

/**
 * Created by jonathan.cook on 5/15/2016.
 */
public class Client {

    String mName;
    double mCostPerMile;
    double mTaxRate;
    long mCharge1id;
    long mCharge2id;
    long mCharge3id;

    public Client(String name, double cost_per_mile, double tax_rate, long charge_1_id, long charge_2_id, long charge_3_id) {
        mName = name;
        mCostPerMile = cost_per_mile;
        mTaxRate = tax_rate;
        mCharge1id = charge_1_id;
        mCharge2id = charge_2_id;
        mCharge3id = charge_3_id;
    }

    public double getCostPerMile() {
        return mCostPerMile;
    }

    public void setCostPerMile(double mCostPerMile) {
        this.mCostPerMile = mCostPerMile;
    }

    public double getTaxRate() {
        return mTaxRate;
    }

    public void setTaxRate(double mTaxRate) {
        this.mTaxRate = mTaxRate;
    }

    public long getCharge1id() {
        return mCharge1id;
    }

    public void setCharge1id(long mCharge1id) {
        this.mCharge1id = mCharge1id;
    }

    public long getCharge2id() {
        return mCharge2id;
    }

    public void setCharge2id(long mCharge2id) {
        this.mCharge2id = mCharge2id;
    }

    public long getCharge3id() {
        return mCharge3id;
    }

    public void setCharge3id(long mCharge3id) {
        this.mCharge3id = mCharge3id;
    }


    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
