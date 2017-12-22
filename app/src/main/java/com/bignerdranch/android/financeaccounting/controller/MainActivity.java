package com.bignerdranch.android.financeaccounting.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.Utils;
import com.bignerdranch.android.financeaccounting.controller.fragment.AllCategoryListFragment;
import com.bignerdranch.android.financeaccounting.controller.fragment.CategorySelectionFragment;
import com.bignerdranch.android.financeaccounting.controller.fragment.ItemListFragment;
import com.bignerdranch.android.financeaccounting.model.Category;
import com.bignerdranch.android.financeaccounting.model.Item;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.bignerdranch.android.financeaccounting.Constants.COSTS_CATEGORY_TYPE;
import static com.bignerdranch.android.financeaccounting.Constants.INCOMES_CATEGORY_TYPE;
import static com.bignerdranch.android.financeaccounting.R.id.BalanceBtn;
import static com.bignerdranch.android.financeaccounting.R.id.addCostsBtn;
import static com.bignerdranch.android.financeaccounting.R.id.addIncomesBtn;
import static com.bignerdranch.android.financeaccounting.R.id.allCostsBtn;
import static com.bignerdranch.android.financeaccounting.R.id.allIncomesBtn;
import static com.bignerdranch.android.financeaccounting.R.id.fragment_container;

public class MainActivity extends AddFragmentActivity implements EditItemFragment.refreshItemsRVListener, CategorySelectionFragment.setCategoryTitle {

    private static final String TAG = MainActivity.class.getName();
    private static final String EXTRA = "MainActivity";
    Realm mRealm;

    @BindView(R.id.dayCostsAmount)
    TextView dayCosts;

    @BindView(R.id.weekCostsAmount)
    TextView weekCosts;

    @BindView(R.id.monthCostsAmount)
    TextView monthCosts;

    @BindView(R.id.monthIncomesAmount)
    TextView monthIncomes;

    @BindView(R.id.currentBalanceAmount)
    TextView currentBalance;

    @OnClick({addCostsBtn, addIncomesBtn, BalanceBtn, allCostsBtn, allIncomesBtn})
    void addFragmentBtn(View view) {
        switch (view.getId()) {
            case addCostsBtn:
                addFragment(EditItemFragment.newInstance(COSTS_CATEGORY_TYPE), fragment_container, true, "EditItemFragment");
                break;
            case addIncomesBtn:
                addFragment(EditItemFragment.newInstance(INCOMES_CATEGORY_TYPE), fragment_container, true, "EditItemFragment");
                break;
            case BalanceBtn:
                addFragment(new balanceFragment(), fragment_container, true, "balanceFragment");
                break;
            case allCostsBtn:
                addFragment(AllCategoryListFragment.newInstance(COSTS_CATEGORY_TYPE), fragment_container, true, "AllCategoryListFragment");
                break;
            case allIncomesBtn:
                addFragment(AllCategoryListFragment.newInstance(INCOMES_CATEGORY_TYPE), fragment_container, true, "AllCategoryListFragment");
                break;
            default:
                break;
        }
    }

    private void setCostsFields(long[] dayRange, long[] weekRange, long[] monthRange) {
        double totalDayCosts = 0;
        double totalWeekCosts = 0;
        double totalMonthCosts = 0;

        RealmResults<Item> allCostsList = mRealm.where(Item.class).equalTo("mCategory.mType", COSTS_CATEGORY_TYPE).findAll();
        List<Item> dayCostsList = allCostsList.where().between("mDate", dayRange[0], dayRange[1]).findAll();
        List<Item> weekCostsList = allCostsList.where().between("mDate", weekRange[0], weekRange[1]).findAll();
        List<Item> monthCostsList = allCostsList.where().between("mDate", monthRange[0], monthRange[1]).findAll();

        for (Item item : dayCostsList) {
            totalDayCosts += item.getAmount();
        }
        dayCosts.setText(String.valueOf(totalDayCosts));
        for (Item item : weekCostsList) {
            totalWeekCosts += item.getAmount();
        }
        weekCosts.setText(String.valueOf(totalWeekCosts));

        for (Item item : monthCostsList) {
            totalMonthCosts += item.getAmount();
        }
        monthCosts.setText(String.valueOf(totalMonthCosts));
    }

    private void setIncomesFields(long[] monthRange) {
        double totalMonthIncomes = 0;

        RealmResults<Item> allIncomesList = mRealm.where(Item.class).equalTo("mCategory.mType", INCOMES_CATEGORY_TYPE).findAll();
        List<Item> monthIncomesList = allIncomesList.where().between("mDate", monthRange[0], monthRange[1]).findAll();

        for (Item item : monthIncomesList) {
            totalMonthIncomes += item.getAmount();
        }
        monthIncomes.setText(String.valueOf(totalMonthIncomes));
    }

    private void setCurrentBalanceField(long[] AllTimeRange) {
        double totalCosts = 0;
        double totalIncomes = 0;

        RealmResults<Item> allCostsList = mRealm.where(Item.class).equalTo("mCategory.mType", COSTS_CATEGORY_TYPE).findAll();
        for (Item cost : allCostsList) {
            totalCosts += cost.getAmount();
        }
        RealmResults<Item> allIncomesList = mRealm.where(Item.class).equalTo("mCategory.mType", INCOMES_CATEGORY_TYPE).findAll();
        for (Item income : allIncomesList) {
            totalIncomes += income.getAmount();
        }
        currentBalance.setText(String.valueOf(totalIncomes - totalCosts));
    }

    private void setUI() {
        setCostsFields(Utils.getDayTimeRange(), Utils.getWeekTimeRange(), Utils.getMonthTimeRange());
        setIncomesFields(Utils.getMonthTimeRange());
        setCurrentBalanceField(Utils.getAllTimeRange());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        Log.i(TAG, "onStart");
        Log.i(TAG, " All items size = " + mRealm.where(Item.class).findAll().size() + " " + mRealm.where(Item.class).findAll());//temporarily
        Log.i(TAG, " All categories size = " + mRealm.where(Category.class).findAll().size() + " " + mRealm.where(Category.class).findAll());//temporarily
        setUI();
    }

    public static Intent newIntent(Context packageContext, String catTitle) {
        Intent intent = new Intent(packageContext, MainActivity.class);
        intent.putExtra(EXTRA, catTitle);
        return intent;
    }

    @Override
    public void refreshItemsRV() {
        ItemListFragment fragment = (ItemListFragment) getSupportFragmentManager()
                .findFragmentByTag("ItemListFragment");
        if (fragment != null) {
            fragment.refreshRecyclerView();
        }
    }

    @Override
    public void setCatTitle(String title) {
        EditItemFragment fragment = (EditItemFragment) getSupportFragmentManager()
                .findFragmentByTag("EditItemFragment");
        if (fragment != null) {
            fragment.setCategoryTitle(title);
        }
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        setUI();
        super.onBackPressed();
    }
}