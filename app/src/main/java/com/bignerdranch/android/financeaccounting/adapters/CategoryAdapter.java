package com.bignerdranch.android.financeaccounting.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.android.financeaccounting.R;
import com.bignerdranch.android.financeaccounting.controller.fragment.CategorySelectionFragment;
import com.bignerdranch.android.financeaccounting.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    public static final String TAG = CategorySelectionFragment.class.getName();
    private List<Category> mCategories;
    public static onClickListener listener;

    public CategoryAdapter(List<Category> categories) {
        mCategories = categories;
    }

    public interface onClickListener {
        void onEditCatBtnClick(Category category, int position);

        void onDeleteCatBtnClick(Category category, int position);

        void onItemClick(Category category);
    }

    public void setClickListener(onClickListener listener) {
        this.listener = listener;
    }

    public void setItem(int position, Category category) {
        mCategories.set(position, category);
        super.notifyItemChanged(position);
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_cat_list, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        Category category = mCategories.get(position);
        holder.bindCategory(category);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public void addCategory(int position) {
        notifyItemInserted(position);
    }

    public void deleteCategory(int position) {
        notifyItemRemoved(position);
    }


    public void setCategory(int position, Category category) {
        notifyItemChanged(position, category);
    }


    public class CategoryHolder extends RecyclerView.ViewHolder {
        private Category mCategory;
        private TextView mCatTitleTextView;
        private ImageButton editCatBtn;
        private ImageButton deleteCatBtn;

        public void bindCategory(Category category) {
            mCategory = category;
            mCatTitleTextView.setText(mCategory.getTitle());
        }

        public CategoryHolder(View itemView) {
            super(itemView);
            mCatTitleTextView = (TextView) itemView.findViewById(R.id.catTitle);
            editCatBtn = (ImageButton) itemView.findViewById(R.id.editCatBtn);
            deleteCatBtn = (ImageButton) itemView.findViewById(R.id.delCatBtn);

            editCatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CategoryAdapter.listener != null) {
                        CategoryAdapter.listener.onEditCatBtnClick(mCategories.get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });

            deleteCatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CategoryAdapter.listener != null) {
                        CategoryAdapter.listener.onDeleteCatBtnClick(mCategories.get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CategoryAdapter.listener != null) {
                        CategoryAdapter.listener.onItemClick(mCategories.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}