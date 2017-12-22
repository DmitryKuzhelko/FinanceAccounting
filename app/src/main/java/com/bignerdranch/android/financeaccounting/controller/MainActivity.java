package com.bignerdranch.android.financeaccounting.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.Utils.DateUtils;
import com.bignerdranch.android.financeaccounting.Utils.FragmentUtils;
import com.bignerdranch.android.financeaccounting.controller.fragment.AllCategoryListFragment;
import com.bignerdranch.android.financeaccounting.controller.fragment.CategorySelectionFragment;
import com.bignerdranch.android.financeaccounting.controller.fragment.ItemListFragment;
import com.bignerdranch.android.financeaccounting.model.Item;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class MainActivity extends AppCompatActivity implements EditItemFragment.refreshItemsRVListener, CategorySelectionFragment.setCategoryTitle {

    private static final String TAG = MainActivity.class.getName();
    private static final String EXTRA = "MainActivity";
    private Toolbar mToolbar;
    private Realm mRealm;
    private FragmentManager fragmentManager;

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
                FragmentUtils.addFragment(fragmentManager, EditItemFragment.newInstance(COSTS_CATEGORY_TYPE), fragment_container, "EditItemFragment");
                break;
            case addIncomesBtn:
                FragmentUtils.addFragment(fragmentManager, EditItemFragment.newInstance(INCOMES_CATEGORY_TYPE), fragment_container, "EditItemFragment");
                break;
            case BalanceBtn:
                FragmentUtils.addFragment(fragmentManager, new balanceFragment(), fragment_container, "balanceFragment");
                break;
            case allCostsBtn:
                FragmentUtils.addFragment(fragmentManager, AllCategoryListFragment.newInstance(COSTS_CATEGORY_TYPE), fragment_container, "AllCategoryListFragment");
                break;
            case allIncomesBtn:
                FragmentUtils.addFragment(fragmentManager, AllCategoryListFragment.newInstance(INCOMES_CATEGORY_TYPE), fragment_container, "AllCategoryListFragment");
                break;
            default:
                break;
        }
    }

    private void fillInCostsFields(long[] dayRange, long[] weekRange, long[] monthRange) {
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

    private void fillInIncomesFields(long[] monthRange) {
        double totalMonthIncomes = 0;

        RealmResults<Item> allIncomesList = mRealm.where(Item.class).equalTo("mCategory.mType", INCOMES_CATEGORY_TYPE).findAll();
        List<Item> monthIncomesList = allIncomesList.where().between("mDate", monthRange[0], monthRange[1]).findAll();

        for (Item item : monthIncomesList) {
            totalMonthIncomes += item.getAmount();
        }
        monthIncomes.setText(String.valueOf(totalMonthIncomes));
    }

    private void fillInCurrentBalanceField(long[] AllTimeRange) {
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
        fillInCostsFields(DateUtils.getDayTimeRange(), DateUtils.getWeekTimeRange(), DateUtils.getMonthTimeRange());
        fillInIncomesFields(DateUtils.getMonthTimeRange());
        fillInCurrentBalanceField(DateUtils.getAllTimeRange());
    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.simpleToolbar);
        setSupportActionBar(mToolbar);
//        mToolbar.hideOverflowMenu();
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        return false;
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_simple, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        setToolbar();
        setUI();
        fragmentManager = getSupportFragmentManager();
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
        Toast.makeText(MainActivity.this, "onBackPressed", Toast.LENGTH_SHORT).show();
        setUI();
        super.onBackPressed();
    }
}