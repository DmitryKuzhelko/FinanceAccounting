package com.bignerdranch.android.financeaccounting;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.android.financeaccounting.Utils.DateUtils;
import com.bignerdranch.android.financeaccounting.Utils.FragmentUtils;
import com.bignerdranch.android.financeaccounting.fragments.AllCategoryListFragment;
import com.bignerdranch.android.financeaccounting.fragments.CategorySelectionFragment;
import com.bignerdranch.android.financeaccounting.fragments.EditItemFragment;
import com.bignerdranch.android.financeaccounting.fragments.ItemListFragment;
import com.bignerdranch.android.financeaccounting.fragments.balanceFragment;
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
                FragmentUtils.addFragment(fragmentManager, EditItemFragment.newInstance(COSTS_CATEGORY_TYPE), fragment_container, EditItemFragment.class.getName());
                break;
            case addIncomesBtn:
                FragmentUtils.addFragment(fragmentManager, EditItemFragment.newInstance(INCOMES_CATEGORY_TYPE), fragment_container, EditItemFragment.class.getName());
                break;
            case BalanceBtn:
                FragmentUtils.addFragment(fragmentManager, new balanceFragment(), fragment_container, balanceFragment.class.getName());
                break;
            case allCostsBtn:
                FragmentUtils.addFragment(fragmentManager, AllCategoryListFragment.newInstance(COSTS_CATEGORY_TYPE), fragment_container, AllCategoryListFragment.class.getName());
                break;
            case allIncomesBtn:
                FragmentUtils.addFragment(fragmentManager, AllCategoryListFragment.newInstance(INCOMES_CATEGORY_TYPE), fragment_container, AllCategoryListFragment.class.getName());
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fragmentManager = getSupportFragmentManager();
        setToolbar();
        setUI();
    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.simpleToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Учёт финансов");
    }

    private void setUI() {
        fillInCostsFields(DateUtils.getDayTimeRange(0), DateUtils.getWeekTimeRange(0), DateUtils.getMonthTimeRange(0));
        fillInIncomesFields(DateUtils.getMonthTimeRange(0));
        fillInCurrentBalanceField(DateUtils.getYearTimeRange(0));
    }

    private void fillInCostsFields(long[] dayRange, long[] weekRange, long[] monthRange) {
        double totalDayCosts = 0;
        double totalWeekCosts = 0;
        double totalMonthCosts = 0;

        RealmResults<Item> allCostsList = mRealm.where(Item.class).equalTo("mCategory.mType", COSTS_CATEGORY_TYPE).findAllAsync();
        List<Item> dayCostsList = allCostsList.where().between("mDate", dayRange[0], dayRange[1]).findAllAsync();
        List<Item> weekCostsList = allCostsList.where().between("mDate", weekRange[0], weekRange[1]).findAllAsync();
        List<Item> monthCostsList = allCostsList.where().between("mDate", monthRange[0], monthRange[1]).findAllAsync();

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

        RealmResults<Item> allIncomesList = mRealm.where(Item.class).equalTo("mCategory.mType", INCOMES_CATEGORY_TYPE).findAllAsync();
        List<Item> monthIncomesList = allIncomesList.where().between("mDate", monthRange[0], monthRange[1]).findAllAsync();

        for (Item item : monthIncomesList) {
            totalMonthIncomes += item.getAmount();
        }
        monthIncomes.setText(String.valueOf(totalMonthIncomes));
    }

    private void fillInCurrentBalanceField(long[] AllTimeRange) {
        double totalCosts = 0;
        double totalIncomes = 0;

        RealmResults<Item> allCostsList = mRealm.where(Item.class).equalTo("mCategory.mType", COSTS_CATEGORY_TYPE).findAllAsync();
        for (Item cost : allCostsList) {
            totalCosts += cost.getAmount();
        }
        RealmResults<Item> allIncomesList = mRealm.where(Item.class).equalTo("mCategory.mType", INCOMES_CATEGORY_TYPE).findAllAsync();
        for (Item income : allIncomesList) {
            totalIncomes += income.getAmount();
        }
        currentBalance.setText(String.valueOf(totalIncomes - totalCosts));
    }

    @Override
    public void refreshItemsRV() {
        ItemListFragment fragment = (ItemListFragment) getSupportFragmentManager()
                .findFragmentByTag(ItemListFragment.class.getName());
        if (fragment != null) {
            fragment.refreshRecyclerView();
        }
    }

    @Override
    public void setCatTitle(String title) {
        EditItemFragment fragment = (EditItemFragment) getSupportFragmentManager()
                .findFragmentByTag(EditItemFragment.class.getName());
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
        setUI();
        super.onBackPressed();
    }
}