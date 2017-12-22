package com.bignerdranch.android.financeaccounting.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Category extends RealmObject {
    @PrimaryKey
    @Required
    private String mId;
    private String mTitle;
    private String mType;
    private double mTotalAmount;

    public Category() {
        this(UUID.randomUUID().toString());
    }

    public Category(String id) {
        mId = id;
    }

    //getters and setters
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public double getTotalAmount() {
        return mTotalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        mTotalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Category{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mType='" + mType + '\'' +
                ", mTotalAmount=" + mTotalAmount +
                '}';
    }
}
