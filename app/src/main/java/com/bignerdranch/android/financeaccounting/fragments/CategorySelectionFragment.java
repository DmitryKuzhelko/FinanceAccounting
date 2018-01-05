package com.bignerdranch.android.financeaccounting.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.Utils.FragmentUtils;
import com.bignerdranch.android.financeaccounting.adapters.CategoryAdapter;
import com.bignerdranch.android.financeaccounting.adapters.SimpleDividerItemDecoration;
import com.bignerdranch.android.financeaccounting.model.Category;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

public class CategorySelectionFragment extends Fragment {

    private static final String TAG = CategorySelectionFragment.class.getName();
    private static final String TYPE_OF_CATEGORY = "type_of_categories";
    private String categoryType;
    private setCategoryTitle mListener;
    private Toolbar mToolbar;

    @BindView(R.id.etAddCat)
    EditText etNewCategory;

    @BindView(R.id.addNewCatBtn)
    Button addNewCatBtn;

    private RecyclerView recyclerView;
    private CategoryAdapter mCategoryAdapter;
    private RealmResults<Category> catListRealm;
    private EditText etTitle;
    private Realm mRealm;
    private Unbinder unbinder;

    public interface setCategoryTitle {
        void setCatTitle(String catTitle);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (setCategoryTitle) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implements interface setCategoryTitle");
        }
        Log.i(TAG, "onAttach");
    }

    public CategorySelectionFragment() {
    }

    public static CategorySelectionFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE_OF_CATEGORY, type);
        CategorySelectionFragment fragment = new CategorySelectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        setHasOptionsMenu(true);
        categoryType = getArguments().getString(TYPE_OF_CATEGORY);
        catListRealm = mRealm.where(Category.class).equalTo("mType", categoryType).findAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setToolbar(view);
        setUI(view);
        return view;
    }

    private void setToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.simpleToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("Категории");
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

    private void setUI(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rwCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mCategoryAdapter = new CategoryAdapter(catListRealm);
        mCategoryAdapter.setClickListener(new CategoryAdapter.onClickListener() {
            @Override
            public void onEditCatBtnClick(final Category category, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Изменение названия");

                View view = getActivity().getLayoutInflater().inflate(R.layout.edit_cat_title_dialog, null);
                builder.setView(view);

                builder.setCancelable(true);

                final TextInputLayout etCatTitle = (TextInputLayout) view.findViewById(R.id.etCatTitle);
                etTitle = etCatTitle.getEditText();
                etTitle.setText(category.getTitle());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateTitle(category, position, etTitle.getText().toString());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

            @Override
            public void onDeleteCatBtnClick(Category category, int position) {
                deleteCategory(category, position);
            }

            @Override
            public void onItemClick(Category category) {
                mListener.setCatTitle(category.getTitle());
                FragmentUtils.closeFragment(getFragmentManager());
            }
        });
        recyclerView.setAdapter(mCategoryAdapter);
    }

    private void updateTitle(Category category, int position, final String catTitle) { // update category field (mCatTitle)
        final Category updatedCategory = mRealm.where(Category.class)
                .equalTo("mTitle", category.getTitle())
                .findFirstAsync();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (updatedCategory != null) {
                    updatedCategory.setTitle(catTitle);
                }
            }
        });
        mCategoryAdapter.setCategory(position, updatedCategory);
    }

    private void deleteCategory(Category category, int position) {
        final Category deleteCategory = mRealm.where(Category.class).equalTo("mId", category.getId()).findFirstAsync();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                deleteCategory.deleteFromRealm();
            }
        });
        mCategoryAdapter.deleteCategory(position);
    }

    @OnClick({R.id.addNewCatBtn})
    void addNewCat(View view) {
        final Category newCategory = new Category();
        newCategory.setTitle(etNewCategory.getText().toString());
        newCategory.setType(categoryType);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                mRealm.copyToRealm(newCategory);
            }
        });
        Log.i(TAG, "Category created " + newCategory.toString());
        Toast.makeText(getContext(), "Категория " + etNewCategory.getText().toString() + " добавлена", Toast.LENGTH_SHORT).show();
        mCategoryAdapter.addCategory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        mRealm.close();
    }
}