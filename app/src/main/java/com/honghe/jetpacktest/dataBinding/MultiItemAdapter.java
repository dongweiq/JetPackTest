package com.honghe.jetpacktest.dataBinding;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public class MultiItemAdapter extends BaseBindRecyclerViewAdapter<IBaseBindingAdapterItem> {
    public MultiItemAdapter(Context context, List<IBaseBindingAdapterItem> mList) {
        super(context, mList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateMyViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindMyViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
