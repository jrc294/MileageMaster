package com.aspiration.mileagemaster;

/**
 * Created by jonathan.cook on 5/15/2016.
 */
public class Trip {

    String mDate;
    long mClient;
    String mTo;
    String mFrom;
    String mDistance;
    double mDistanceCost;
    int mComplete;
    long mCharge1;
    long mCharge2;
    long mCharge3;
    double mChargeCost1;
    double mChargeCost2;
    double mChargeCost3;
    String mAdHocCharge1;
    String mAdHocCharge2;
    String mAdHocCharge3;
    double mAdHocChargeCost1;
    double mAdHocChargeCost2;
    double mAdHocChargeCost3;
    double mTaxCost;
    String mNotes;

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public void setClient(long mClient) {
        this.mClient = mClient;
    }

    public void setTo(String mTo) {
        this.mTo = mTo;
    }

    public void setFrom(String mFrom) {
        this.mFrom = mFrom;
    }

    public void setDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public void setDistanceCost(double mDistanceCost) {
        this.mDistanceCost = mDistanceCost;
    }

    public void setComplete(int mComplete) {
        this.mComplete = mComplete;
    }

    public void setCharge1(long mCharge1) {
        this.mCharge1 = mCharge1;
    }

    public void setCharge2(long mCharge2) {
        this.mCharge2 = mCharge2;
    }

    public void setCharge3(long mCharge3) {
        this.mCharge3 = mCharge3;
    }

    public void setChargeCost1(double mChargeCost1) {
        this.mChargeCost1 = mChargeCost1;
    }

    public void setChargeCost2(double mChargeCost2) {
        this.mChargeCost2 = mChargeCost2;
    }

    public void setChargeCost3(double mChargeCost3) {
        this.mChargeCost3 = mChargeCost3;
    }

    public void setAdHocCharge1(String mAdHocCharge1) {
        this.mAdHocCharge1 = mAdHocCharge1;
    }

    public void setAdHocCharge2(String mAdHocCharge2) {
        this.mAdHocCharge2 = mAdHocCharge2;
    }

    public void setAdHocCharge3(String mAdHocCharge3) {
        this.mAdHocCharge3 = mAdHocCharge3;
    }

    public void setAdHocChargeCost1(double mAdHocChargeCost1) {
        this.mAdHocChargeCost1 = mAdHocChargeCost1;
    }

    public void setAdHocChargeCost2(double mAdHocChargeCost2) {
        this.mAdHocChargeCost2 = mAdHocChargeCost2;
    }

    public void setAdHocChargeCost3(double mAdHocChargeCost3) {
        this.mAdHocChargeCost3 = mAdHocChargeCost3;
    }

    public void setTaxCost(double mTaxCost) {
        this.mTaxCost = mTaxCost;
    }

    public void setNotes(String mNotes) {
        this.mNotes = mNotes;
    }

    public String getDate() {
        return mDate;
    }

    public long getClient() {
        return mClient;
    }

    public String getTo() {
        return mTo;
    }

    public String getFrom() {
        return mFrom;
    }

    public String getDistance() {
        return mDistance;
    }

    public double getDistanceCost() {
        return mDistanceCost;
    }

    public int getComplete() {
        return mComplete;
    }

    public long getCharge1() {
        return mCharge1;
    }

    public long getCharge2() {
        return mCharge2;
    }

    public long getCharge3() {
        return mCharge3;
    }

    public double getChargeCost1() {
        return mChargeCost1;
    }

    public double getChargeCost2() {
        return mChargeCost2;
    }

    public double getChargeCost3() {
        return mChargeCost3;
    }

    public String getAdHocCharge1() {
        return mAdHocCharge1;
    }

    public String getAdHocCharge2() {
        return mAdHocCharge2;
    }

    public String getAdHocCharge3() {
        return mAdHocCharge3;
    }

    public double getAdHocChargeCost1() {
        return mAdHocChargeCost1;
    }

    public double getAdHocChargeCost2() {
        return mAdHocChargeCost2;
    }

    public double getAdHocChargeCost3() {
        return mAdHocChargeCost3;
    }

    public double getTaxCost() {
        return mTaxCost;
    }

    public String getNotes() {
        return mNotes;
    }
}
