package com.bignerdranch.android.financeaccounting.controller;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.Utils.DateUtils;
import com.bignerdranch.android.financeaccounting.Utils.FragmentUtils;
import com.bignerdranch.android.financeaccounting.Utils.PreferencesHelper;
import com.bignerdranch.android.financeaccounting.controller.fragment.CategorySelectionFragment;
import com.bignerdranch.android.financeaccounting.controller.fragment.DatePickerFragment;
import com.bignerdranch.android.financeaccounting.controller.fragment.TimePickerFragment;
import com.bignerdranch.android.financeaccounting.model.Category;
import com.bignerdranch.android.financeaccounting.model.Item;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;

import static com.bignerdranch.android.financeaccounting.R.id.fragment_container;

public class EditItemFragment extends Fragment {

    public static final String TAG = EditItemFragment.class.getName();
    private static final String TYPE_OF_CATEGORY = "type_of_category";
    private static final String ITEM_ID = "item_id";
    private Calendar calendar = Calendar.getInstance();
    private Realm mRealm;
    private Unbinder unbinder;
    private String itemID;
    Toolbar mToolbar;

//    PreferencesHelper preferenceHelper;
//
//    private SharedPreferences sPref;
//    private final String SAVED_COMMENT = "saved_comment"; // need add to constants

//    private void saveComment() {
//        sPref = getActivity().getSharedPreferences("Comment", MODE_PRIVATE);
//        SharedPreferences.Editor ed = sPref.edit();
//        ed.putString(SAVED_COMMENT, etComment.getText().toString());
//        ed.apply();
//        Toast.makeText(getActivity(), "Comment saved", Toast.LENGTH_SHORT).show();
//    }
//
//    private void loadComment() {
//        sPref = getActivity().getSharedPreferences("Comment", MODE_PRIVATE);
//        String savedText = sPref.getString(SAVED_COMMENT, "default comment");
//        etComment.setText(savedText);
//        Toast.makeText(getActivity(), "Text loaded", Toast.LENGTH_SHORT).show();
//    }

    private refreshItemsRVListener mListener;

    @BindView(R.id.itemDate)
    TextView itemDate;

    @BindView(R.id.itemTime)
    TextView itemTime;

    @BindView(R.id.tvCategory)
    TextView tvCategory;

    @BindView(R.id.etAmount)
    EditText etAmount;

    @BindView(R.id.etComment)
    EditText etComment;

    @BindView(R.id.addCatBtn)
    Button addCatBtn;

    public EditItemFragment() {
    }

    public interface refreshItemsRVListener {
        void refreshItemsRV();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (refreshItemsRVListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implements interface refreshItemsRVListener");
        }
        Log.i(TAG, "onAttach");
    }

    //return new item
    public static EditItemFragment newInstance(String catType) {
        Bundle args = new Bundle();
        args.putString(TYPE_OF_CATEGORY, catType);
        EditItemFragment fragment = new EditItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //return item for editing
    public static EditItemFragment newInstanceForUpdate(String itemId) {
        Bundle args = new Bundle();
        args.putString(ITEM_ID, itemId);
        EditItemFragment fragment = new EditItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        setHasOptionsMenu(true);
        PreferencesHelper.init(getContext());
        Log.i(TAG, "itemID " + itemID);
        Log.i(TAG, "ON_CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_item, container, false);
        unbinder = ButterKnife.bind(this, view);
        setToolbar(view);
        setUI();
        Log.i(TAG, "ON_CREATE_VIEW");
        return view;
    }

    private void setToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.editToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("Расходы");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveItem:
                createOrUpdateItem();
                Log.d(TAG, "Save item clicked");
                return true;
            case android.R.id.home:
                FragmentUtils.closeFragment(getFragmentManager());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //fills in all fields of fragment when element from Realm is edited
    //set current date and time for item first created
    private void setUI() {
        itemID = getArguments().getString(ITEM_ID);
        if (itemID == null) {
            DateUtils.setCurrentTimeAndDate(calendar);
            tvCategory.setText(PreferencesHelper.getCategory(PreferencesHelper.CATEGORY));
            itemDate.setText(DateUtils.getDate(calendar.getTimeInMillis()));
            itemTime.setText(DateUtils.getTime(calendar.getTimeInMillis()));
        } else {
            Item item = mRealm.where(Item.class)
                    .equalTo("mId", itemID)
                    .findFirst();
            etAmount.setText(String.valueOf(item.getAmount()));
            tvCategory.setText(item.getCategory().getTitle());
            etComment.setText(item.getComment());
            itemDate.setText(DateUtils.getDate(item.getDate()));
            itemTime.setText(DateUtils.getTime(item.getDate()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    //create or update item. Depends on situation
    private void createOrUpdateItem() {
        if (itemID == null) {
            createItem();
        } else {
            updateItem(itemID);
        }
        FragmentUtils.closeFragment(getFragmentManager());
    }

    //create a new item in Realm
    private void createItem() {
        final Item newItem = new Item();
        setAllFields(newItem);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                mRealm.copyToRealmOrUpdate(newItem);
                Log.i(TAG, "New item created: " + newItem.toString());
                Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //updates item from Realm
    private void updateItem(String itemID) {
        final Item item = mRealm.where(Item.class)
                .equalTo("mId", itemID)
                .findFirst();
        final Item newItem = mRealm.copyFromRealm(item);
        setAllFields(newItem);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealmOrUpdate(newItem);
                Log.i(TAG, "Item updated: " + newItem.toString());
                Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
            }
        });
        mListener.refreshItemsRV();
    }

    //set all fields for the specified item
    private void setAllFields(Item item) {
        item.setDate(getDate());
        item.setCategory(getCategory(item));
        item.setAmount(getAmount());
        item.setComment(getComment());
    }

    //return value from tvItemDate and tvItemTime fields
    private long getDate() {
        String date = itemDate.getText().toString();
        String time = itemTime.getText().toString();
        return DateUtils.getFullDateLong(date, time);
    }

    //return value from etAmount
    private double getAmount() {
        double amount = Double.valueOf(etAmount.getText().toString());
        return (amount != 0) ? amount : 0;
    }

    //return value from etComment
    private String getComment() {
        String comment = etComment.getText().toString();
        return (comment != null) ? comment : "defaultComment";
    }

    public void setCategoryTitle(String title) {
        tvCategory.setText(title);
    }

    //return category by title from tvCategory
    private Category getCategory(final Item item) {
        final Category category = mRealm.where(Category.class).equalTo("mTitle", tvCategory.getText().toString()).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                category.setTotalAmount(category.getTotalAmount() - item.getAmount() + getAmount());
            }
        });
        return category;
    }

    @OnClick({R.id.addCatBtn})
    void selectCategory(View view) {
        FragmentUtils.addFragment(getFragmentManager(), CategorySelectionFragment.newInstance(getArguments().getString(TYPE_OF_CATEGORY)), fragment_container, "CategorySelectionFragment");
    }

    @OnClick({R.id.itemDate})
    void setItemDate(View view) {
        DialogFragment datePickerFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                itemDate.setText(DateUtils.getDate(calendar.getTimeInMillis()));
            }

            @Override
            public void onCancel(DialogInterface dialog) {
            }
        };
        datePickerFragment.show(getActivity().getFragmentManager(), DatePickerFragment.class.getName());
    }

    @OnClick({R.id.itemTime})
    void setItemTime(View view) {
        DialogFragment timePickerFragment = new TimePickerFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                itemTime.setText(DateUtils.getTime(calendar.getTimeInMillis()));
            }

            @Override
            public void onCancel(DialogInterface dialog) {
            }
        };
        timePickerFragment.show(getActivity().getFragmentManager(), TimePickerFragment.class.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        PreferencesHelper.addProperty(PreferencesHelper.CATEGORY, tvCategory.getText().toString());
        unbinder.unbind();
        mRealm.close();
    }
}