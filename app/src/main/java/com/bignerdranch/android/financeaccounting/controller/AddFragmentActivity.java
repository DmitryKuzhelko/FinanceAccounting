package com.bignerdranch.android.financeaccounting.controller;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.financeaccounting.R;

import butterknife.ButterKnife;

public abstract class AddFragmentActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
    }

    public void addFragment(Fragment fragment, int fragContainer, boolean addToBackStack, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(fragContainer) == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(fragContainer, fragment, tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }
}