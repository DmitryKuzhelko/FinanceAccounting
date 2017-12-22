package com.bignerdranch.android.financeaccounting.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.adapters.ItemListAdapter;
import com.bignerdranch.android.financeaccounting.controller.EditItemFragment;
import com.bignerdranch.android.financeaccounting.model.Item;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.bignerdranch.android.financeaccounting.R.id.fragment_container;

public class ItemListFragment extends Fragment {

    private static final String TAG = ItemListFragment.class.getName();
    private static final String TITLE_OF_CATEGORY = "title_of_category";

    private RecyclerView recyclerView;
    private ItemListAdapter mItemListAdapter;
    private Unbinder unbinder;
    private Realm mRealm;
    private RealmResults<Item> itemsListRealm;

    public static ItemListFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(TITLE_OF_CATEGORY, title);
        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(Realm.getDefaultConfiguration());
        itemsListRealm = mRealm.where(Item.class)
                .equalTo("mCategory.mTitle", getArguments().getString(TITLE_OF_CATEGORY))
                .findAll();
        Log.i(TAG, "OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setUI(view);
        Log.i(TAG, "onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    private void setUI(View view) {
        Log.i(TAG, "setUI");
        recyclerView = (RecyclerView) view.findViewById(R.id.rwItemsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mItemListAdapter = new ItemListAdapter(itemsListRealm, new ItemListAdapter.MyAdapterListener() {
            @Override
            public void onEditItemBtnClick(Item item) {
                addFragment(EditItemFragment.newInstanceForUpdate(item.getId()), fragment_container, true, "EditItemFragment");
            }

            @Override
            public void onDeleteItemBtnClick(Item item) {
                int position = itemsListRealm.indexOf(item);
                mItemListAdapter.deleteItem(itemsListRealm.indexOf(item));
                final Item deleteItem = mRealm.where(Item.class).equalTo("mId", item.getId()).findFirst();
                Toast.makeText(getContext(), "Позиция " + deleteItem.getComment() + " удалена", Toast.LENGTH_SHORT).show();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        deleteItem.deleteFromRealm();
                    }
                });
                mItemListAdapter.deleteItem(position);
            }
        });
        recyclerView.setAdapter(mItemListAdapter);
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

    public void refreshRecyclerView() {
        mItemListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        unbinder.unbind();
        mRealm.close();
    }
}
