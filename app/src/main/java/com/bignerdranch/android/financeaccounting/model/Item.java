package com.bignerdranch.android.financeaccounting.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Item extends RealmObject {

    @PrimaryKey
    @Required
    private String mId;
    private long mDate;
    private String mComment;
    private double mAmount;
    private Category mCategory;

    public Item() {
        this(UUID.randomUUID().toString());
    }

    public Item(String id) {
        mId = id;
    }

    //getters and setters
    public String getId() {
        return mId;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public void setId(String id) {
        mId = id;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    @Override
    public String toString() {
        return "Item{" +
                "mId='" + mId + '\'' +
                ", mDate=" + mDate +
                ", mComment='" + mComment + '\'' +
                ", mAmount=" + mAmount +
                ", mCategory=" + mCategory +
                '}';
    }
}
