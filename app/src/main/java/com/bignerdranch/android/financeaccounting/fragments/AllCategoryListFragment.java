package com.bignerdranch.android.financeaccounting.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bignerdranch.android.financeaccounting.Constants;
import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.Utils.DateUtils;
import com.bignerdranch.android.financeaccounting.Utils.FragmentUtils;
import com.bignerdranch.android.financeaccounting.Utils.PreferencesHelper;
import com.bignerdranch.android.financeaccounting.adapters.SimpleDividerItemDecoration;
import com.bignerdranch.android.financeaccounting.model.Category;
import com.bignerdranch.android.financeaccounting.model.Item;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.bignerdranch.android.financeaccounting.R.id.catTitle;
import static com.bignerdranch.android.financeaccounting.R.id.catTotalAmount;
import static com.bignerdranch.android.financeaccounting.R.id.fragment_container;
import static com.bignerdranch.android.financeaccounting.Utils.DateUtils.getDateForPeriod;

public class AllCategoryListFragment extends Fragment {

    public static final String TAG = AllCategoryListFragment.class.getName();
    private static final String TYPE_OF_CATEGORIES = "type of categories";

    private RecyclerView recyclerView;
    private ListAdapter mListAdapter;
    private Unbinder unbinder;
    private Realm mRealm;
    private RealmResults<Category> categoriesList;
    private String[] data = {Constants.YEAR, Constants.MONTH, Constants.WEEK, Constants.DAY};
    private String selectedRange;
    private int shift = 0;
    private long[] timeRange = new long[2];
    private Toolbar mToolbar;


    @BindView(R.id.totalAmount)
    TextView totalAmount;

    @BindView(R.id.actionLeftBtn)
    ImageButton leftBtn;

    @BindView(R.id.actionRightBtn)
    ImageButton rightBtn;

    @BindView(R.id.tvTimeRange)
    TextView tvTimeRange;

    @OnClick({R.id.actionRightBtn})
    void onClickRightBtn(View view) {
        shift += 1;
        setUI(getSelectedCategories(shift, selectedRange));
    }

    @OnClick({R.id.actionLeftBtn})
    void onClickLeftBtn(View view) {
        shift -= 1;
        setUI(getSelectedCategories(shift, selectedRange));
    }

    private void setUI(List<Category> selectedCategories) {
        totalAmount.setText(String.valueOf(getTotalAmount(selectedCategories)));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mListAdapter = new ListAdapter(selectedCategories);
        recyclerView.setAdapter(mListAdapter);
    }

    private List<Category> getSelectedCategories(int shift, String period) {
        switch (period) {
            case Constants.YEAR:
                timeRange = DateUtils.getYearTimeRange(shift);
                break;
            case Constants.MONTH:
                timeRange = DateUtils.getMonthTimeRange(shift);
                break;
            case Constants.WEEK:
                timeRange = DateUtils.getWeekTimeRange(shift);
                break;
            case Constants.DAY:
                timeRange = DateUtils.getDayTimeRange(shift);
                break;
        }
        List<Category> selectedCatList = new ArrayList<>();
        categoriesList = mRealm.where(Category.class).equalTo("mType", getArguments().getString(TYPE_OF_CATEGORIES)).findAll();
        List<Category> copiedCatList = mRealm.copyFromRealm(categoriesList);
        for (Category category : copiedCatList) {
            double totalCatAmount = 0;
            List<Item> itemList = mRealm.where(Item.class).equalTo("mCategory.mTitle", category.getTitle()).between("mDate", timeRange[0], timeRange[1]).findAll();
            for (Item item : itemList) {
                totalCatAmount += item.getAmount();
            }
            if (totalCatAmount != 0) {
                category.setTotalAmount(totalCatAmount);
                selectedCatList.add(category);
            }
        }
        tvTimeRange.setText(getDateForPeriod(timeRange[0], timeRange[1], period));

        return selectedCatList;
    }

    private double getTotalAmount(List<Category> selectedCategories) {
        double totalAmount = 0;
        for (Category category : selectedCategories) {
            totalAmount += category.getTotalAmount();
        }
        return totalAmount;
    }

    public AllCategoryListFragment() {
    }

    public static AllCategoryListFragment newInstance(String catType) {
        Bundle args = new Bundle();
        args.putString(TYPE_OF_CATEGORIES, catType);
        AllCategoryListFragment fragment = new AllCategoryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        setHasOptionsMenu(true);
        PreferencesHelper.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        recyclerView = (RecyclerView) view.findViewById(R.id.rwCategoriesList);
        setToolbar(view);
        return view;
    }

    private void setToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.spinnerToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle(getArguments().getString(TYPE_OF_CATEGORIES));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_spinner, menu);

        MenuItem item = menu.findItem(R.id.spinner_item);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(adapter.getPosition(PreferencesHelper.getRange(Constants.LAST_SELECTED_RANGE)));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PreferencesHelper.addProperty(Constants.LAST_SELECTED_RANGE, data[position]);
                selectedRange = PreferencesHelper.getRange(Constants.LAST_SELECTED_RANGE);
                setUI(getSelectedCategories(0, selectedRange));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentUtils.closeFragment(getFragmentManager());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ListAdapter extends RecyclerView.Adapter<ListHolder> {
        private List<Category> mCategoriesList;

        public ListAdapter(List<Category> catList) {
            mCategoriesList = catList;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_of_all_cat_list, parent, false);
            return new ListHolder(view);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            Category category = mCategoriesList.get(position);
            holder.bindCategory(category);
        }

        @Override
        public int getItemCount() {
            return mCategoriesList.size();
        }

        public void setCategories(List<Category> categories) {
            mCategoriesList = categories;
        }
    }

    private class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Category mCategory;
        private TextView mCatTitleTextView;
        private TextView totalCatAmount;

        public void bindCategory(Category category) {
            mCategory = category;
            mCatTitleTextView.setText(mCategory.getTitle());
            totalCatAmount.setText(String.valueOf(mCategory.getTotalAmount()));
        }

        public ListHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mCatTitleTextView = (TextView) itemView.findViewById(catTitle);
            totalCatAmount = (TextView) itemView.findViewById(catTotalAmount);
        }

        @Override
        public void onClick(View v) {
            FragmentUtils.addFragment(getFragmentManager(), ItemListFragment.newInstance(mCategory.getTitle(), timeRange), fragment_container, "ItemListFragment");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shift = 0;
        unbinder.unbind();
        mRealm.close();
    }
}