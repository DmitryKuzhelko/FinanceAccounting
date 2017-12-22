package com.bignerdranch.android.financeaccounting.controller.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.financeaccounting.Constants;
import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.Utils;
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
import static com.bignerdranch.android.financeaccounting.Utils.getDateForPeriod;

public class AllCategoryListFragment extends Fragment { // SHOWING all categories fragment

    public static final String TAG = AllCategoryListFragment.class.getName();
    private static final String TYPE_OF_CATEGORY = "type_of_category";

    private RecyclerView recyclerView;
    private ListAdapter mListAdapter;
    private Unbinder unbinder;
    private Realm mRealm;
    private RealmResults<Category> categoriesList;
    private String[] data = {Constants.YEAR, Constants.MONTH, Constants.WEEK, Constants.DAY, Constants.RANGE};
    private String selectedRange = Constants.MONTH;
    @BindView(R.id.totalAmount)
    TextView totalAmount;

    @BindView(R.id.actionLeftBtn)
    ImageButton leftBtn;

    @BindView(R.id.actionRightBtn)
    ImageButton rightBtn;

    @BindView(R.id.tvTimeRange)
    TextView tvTimeRange;

    private int shift = 0;

    @OnClick({R.id.actionRightBtn})
    void onClickRightBtn(View view) {
        shift += 1;
        setUI(getSelectedCategories(shift, selectedRange));
    }

    @OnClick({R.id.actionLeftBtn})
    void onClickLeftBtn(View view) {
        shift -= 1;
        setUI(getSelectedCategories(shift,  selectedRange));
    }

    private void setUI(List<Category> selectedCategories) {
        totalAmount.setText(String.valueOf(getTotalAmount(selectedCategories)));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListAdapter = new ListAdapter(selectedCategories);
        recyclerView.setAdapter(mListAdapter);
    }

    private List<Category> getSelectedCategories(int shift, String period) {
        long[] timeRange = new long[2];

        switch (period) {
            case Constants.YEAR:
                timeRange = Utils.getYear(shift);
                break;
            case Constants.MONTH:
                timeRange = Utils.getMonth(shift);
                break;
            case Constants.WEEK:
                timeRange = Utils.getWeek(shift);
                break;
            case Constants.DAY:
                timeRange = Utils.getDay(shift);
                break;
            case Constants.RANGE:
//                timeRange = Utils.getRange(shift);
                break;
        }

        List<Category> selectedCatList = new ArrayList<>();
        categoriesList = mRealm.where(Category.class).equalTo("mType", getArguments().getString(TYPE_OF_CATEGORY)).findAll();
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
        args.putString(TYPE_OF_CATEGORY, catType);
        AllCategoryListFragment fragment = new AllCategoryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        recyclerView = (RecyclerView) view.findViewById(R.id.rwCategoriesList);
        setUI(getSelectedCategories(shift, selectedRange));
        //set spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(getActivity(), "Position = " + position, Toast.LENGTH_SHORT).show();
                selectedRange = data[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                selectedRange = data[1];
            }
        });
        return view;
    }


    private void addFragment(Fragment fragment, int fragContainer, boolean addToBackStack, String tag) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(fragContainer, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shift = 0;//???
        unbinder.unbind();
        mRealm.close();
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
            addFragment(ItemListFragment.newInstance(mCategory.getTitle()), fragment_container, true, "ItemListFragment");
        }
    }
}
