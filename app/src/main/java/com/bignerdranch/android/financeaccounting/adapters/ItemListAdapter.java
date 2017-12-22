package com.bignerdranch.android.financeaccounting.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.Utils;
import com.bignerdranch.android.financeaccounting.model.Item;

import java.util.List;

import static com.bignerdranch.android.financeaccounting.R.id.ItemAmount;
import static com.bignerdranch.android.financeaccounting.R.id.ItemComment;
import static com.bignerdranch.android.financeaccounting.R.id.ItemDate;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListHolder> {

    public static final String TAG = ItemListAdapter.class.getName();
    private List<Item> mItems;
    public MyAdapterListener onClickListener;

    public interface MyAdapterListener {
        void onEditItemBtnClick(Item item);
        void onDeleteItemBtnClick(Item item);
    }

    public ItemListAdapter(List<Item> items, MyAdapterListener listener) {
        mItems = items;
        onClickListener = listener;
    }

    public void deleteItem(int index) {
        notifyItemRemoved(index);
    }

    @Override
    public ItemListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_items_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemListHolder holder, int position) {
        Item item = mItems.get(position);
        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(List<Item> items) {
        mItems = items;
    }

    public class ItemListHolder extends RecyclerView.ViewHolder {
        private Item mItem;
        private TextView mItemDate;
        private TextView mItemAmount;
        private TextView mItemComment;
        private ImageButton editItemBtn;
        private ImageButton deleteItemBtn;

        public void bindItem(Item item) {
            mItem = item;
            mItemDate.setText(Utils.getFullDateString(mItem.getDate()));
            mItemAmount.setText(String.valueOf(mItem.getAmount()));
            mItemComment.setText(mItem.getComment());
        }

        //need to transit findViewById to ButterKnife
        public ItemListHolder(View itemView) {
            super(itemView);
            mItemDate = (TextView) itemView.findViewById(ItemDate);
            mItemAmount = (TextView) itemView.findViewById(ItemAmount);
            mItemComment = (TextView) itemView.findViewById(ItemComment);
            editItemBtn = (ImageButton) itemView.findViewById(R.id.editItemBtn);
            deleteItemBtn = (ImageButton) itemView.findViewById(R.id.deleteItemBtn);

            editItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onEditItemBtnClick(mItems.get(getAdapterPosition()));
                }
            });

            deleteItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onDeleteItemBtnClick(mItems.get(getAdapterPosition()));
                }
            });
        }
    }
}