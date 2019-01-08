package com.example.kornel.alphaui.sharelocation;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.ListItemClickListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShareYourLocationListAdapter extends RecyclerView.Adapter<ShareYourLocationListAdapter.ShareYourLocationListViewHolder> {
    private Context mContext;
    private final ListItemClickListener mOnClickListener;
    private List<SharedLocationInfo> mSharedLocationInfoList;

    public ShareYourLocationListAdapter(Context context, ListItemClickListener onClickListener, List<SharedLocationInfo> sharedLocationInfoList) {
        mContext = context;
        mOnClickListener = onClickListener;
        mSharedLocationInfoList = sharedLocationInfoList;
    }

    @Override
    public ShareYourLocationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_share_your_location;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ShareYourLocationListViewHolder viewHolder = new ShareYourLocationListViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShareYourLocationListViewHolder shareYourLocationListViewHolder, int position) {
        if ((mSharedLocationInfoList == null) || (mSharedLocationInfoList.size() == 0)) {
            shareYourLocationListViewHolder.mMessageTextView.setText("ERROR");
        } else {
            final SharedLocationInfo sli = mSharedLocationInfoList.get(position);
            final String workoutType = sli.getWorkoutType();

            shareYourLocationListViewHolder.mWorkoutNameTextView.setText(workoutType);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shareYourLocationListViewHolder.mWorkoutIconImageView.setImageDrawable(mContext.getResources().getDrawable(IconUtils.getWorkoutIcon(workoutType), mContext.getApplicationContext().getTheme()));
            } else {
                shareYourLocationListViewHolder.mWorkoutIconImageView.setImageDrawable(mContext.getResources().getDrawable(IconUtils.getWorkoutIcon(workoutType)));
            }

            shareYourLocationListViewHolder.mMessageTextView.setText(sli.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return ((mSharedLocationInfoList != null) && (mSharedLocationInfoList.size() != 0) ? mSharedLocationInfoList.size() : 0);
    }

    void loadNewData(List<SharedLocationInfo> sharedLocationInfoList) {
        mSharedLocationInfoList = sharedLocationInfoList;

        Collections.sort(mSharedLocationInfoList, new Comparator<SharedLocationInfo>() {
            @Override
            public int compare(SharedLocationInfo o1, SharedLocationInfo o2) {
                return o1.getWorkoutType().compareTo(o2.getWorkoutType());
            }
        });

        notifyDataSetChanged();
    }

    class ShareYourLocationListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mWorkoutIconImageView;
        private TextView mWorkoutNameTextView;
        private TextView mMessageTextView;

        public ShareYourLocationListViewHolder(View itemView) {
            super(itemView);
            mWorkoutIconImageView = itemView.findViewById(R.id.workoutIconImageView);
            mWorkoutNameTextView = itemView.findViewById(R.id.workoutNameTextView);
            mMessageTextView = itemView.findViewById(R.id.messageTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}