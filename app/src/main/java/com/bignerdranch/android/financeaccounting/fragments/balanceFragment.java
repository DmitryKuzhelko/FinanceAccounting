package com.bignerdranch.android.financeaccounting.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.Utils.FragmentUtils;

import butterknife.BindView;

public class balanceFragment extends Fragment {

//    private RecyclerView recyclerView;
//    private ListAdapter mListAdapter;
    private Toolbar mToolbar;
//    private Unbinder unbinder;
//    private Realm mRealm;

    @BindView(R.id.actionLeftBtnBalance)
    ImageButton leftBtn;

    @BindView(R.id.actionRightBtnBalance)
    ImageButton rightBtn;

    @BindView(R.id.tvYearRange)
    TextView tvYearRange;

//    @OnClick({R.id.actionRightBtnBalance})
//    void onClickRightBtn(View view) {
//        shift += 1;
//        setUI(getSelectedCategories(shift, selectedRange));
//    }
//
//    @OnClick({R.id.actionLeftBtnBalance})
//    void onClickLeftBtn(View view) {
//        shift -= 1;
//        setUI(getSelectedCategories(shift, selectedRange));
//    }
//
//    private List<Category> getMonths() {
//        switch (period) {
//            case Constants.YEAR:
//                timeRange = DateUtils.getYearTimeRange(shift);
//                break;
//            case Constants.MONTH:
//                timeRange = DateUtils.getMonthTimeRange(shift);
//                break;
//            case Constants.WEEK:
//                timeRange = DateUtils.getWeekTimeRange(shift);
//                break;
//            case Constants.DAY:
//                timeRange = DateUtils.getDayTimeRange(shift);
//                break;
//        }
//        List<Category> selectedCatList = new ArrayList<>();
//        categoriesList = mRealm.where(Category.class).equalTo("mType", getArguments().getString(TYPE_OF_CATEGORIES)).findAll();
//        List<Category> copiedCatList = mRealm.copyFromRealm(categoriesList);
//        for (Category category : copiedCatList) {
//            double totalCatAmount = 0;
//            List<Item> itemList = mRealm.where(Item.class).equalTo("mCategory.mTitle", category.getTitle()).between("mDate", timeRange[0], timeRange[1]).findAll();
//            for (Item item : itemList) {
//                totalCatAmount += item.getAmount();
//            }
//            if (totalCatAmount != 0) {
//                category.setTotalAmount(totalCatAmount);
//                selectedCatList.add(category);
//            }
//        }
//        tvTimeRange.setText(getDateForPeriod(timeRange[0], timeRange[1], period));
//
//        return selectedCatList;
//    }

//    private double getTotalAmount(List<Category> selectedCategories) {
//        double totalAmount = 0;
//        for (Category category : selectedCategories) {
//            totalAmount += category.getTotalAmount();
//        }
//        return totalAmount;
//    }

    public balanceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance, container, false);
//        unbinder = ButterKnife.bind(this, view);
//        recyclerView = (RecyclerView) view.findViewById(R.id.rwMonthsList);
        setToolbar(view);
        return view;
    }

    private void setToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.simpleToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("Баланс за год");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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



//    private class MonthAdapter extends RecyclerView.Adapter<ListHolder> {
//        private List<Category> mMonthsList;
//
//        public MonthAdapter(List<Category> catList) {
//            mMonthsList = catList;
//        }
//
//        @Override
//        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//            View view = layoutInflater.inflate(R.layout.item_of_all_cat_list, parent, false);
//            return new ListHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(ListHolder holder, int position) {
//            Category category = mMonthsList.get(position);
//            holder.bindCategory(category);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mMonthsList.size();
//        }
//
//        public void setCategories(List<Category> categories) {
//            mMonthsList = categories;
//        }
//    }
//
//    private class ListHolder extends RecyclerView.ViewHolder {
//        private Category mCategory;
//        private TextView mCatTitleTextView;
//        private TextView totalCatAmount;
//
//        public void bindCategory(Category category) {
//            mCategory = category;
//            mCatTitleTextView.setText(mCategory.getTitle());
//            totalCatAmount.setText(String.valueOf(mCategory.getTotalAmount()));
//        }
//
//        public ListHolder(View itemView) {
//            super(itemView);
//            mCatTitleTextView = (TextView) itemView.findViewById(catTitle);
//            totalCatAmount = (TextView) itemView.findViewById(catTotalAmount);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        shift = 0;
//        unbinder.unbind();
//        mRealm.close();
    }
}
