package com.example.kornel.alphaui;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PaceAdapter extends RecyclerView.Adapter<PaceAdapter.PaceViewHolder> {

    private final ListItemClickListener mOnClickListener;
    private List<String> mPaceList;

    public PaceAdapter(ListItemClickListener onClickListener, List<String> paceList) {
        mOnClickListener = onClickListener;
        mPaceList = paceList;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @NonNull
    @Override
    public PaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.pace_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PaceViewHolder paceViewHolder = new PaceViewHolder(view);

        return paceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PaceViewHolder paceViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mPaceList == null) || (mPaceList.size() == 0)) {
            paceViewHolder.paceItemTextView.setText("ERROR");
        } else {
            paceViewHolder.paceItemTextView.setText(mPaceList.get(position));
        }

        // Googles way
        // paceViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return  ((mPaceList != null) && (mPaceList.size() != 0) ? mPaceList.size() : 1);
    }

    void loadNewData(List<String> newPaceList) {
        mPaceList = newPaceList;
        notifyDataSetChanged();
    }

    class PaceViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView paceItemTextView;

        public PaceViewHolder(View itemView) {
            super(itemView);

            paceItemTextView = itemView.findViewById(R.id.paceItemTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        private void bind(int listIndex) {

            paceItemTextView.setText(String.valueOf(listIndex));
        }
    }
}

