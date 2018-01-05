package com.bignerdranch.android.financeaccounting.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentUtils {

    public static void addFragment(FragmentManager fm, Fragment fragment, int fragContainer, String tag) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(fragContainer, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.addToBackStack(null);
//        if (addToBackStack) {
//            ft.addToBackStack(null);
//        }
        ft.commit();
    }

    public static void closeFragment(FragmentManager fragmentManager) {
        fragmentManager.popBackStack();
    }
}