package com.wowls.boddari.ui.search.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.wowls.boddari.R;
import com.wowls.boddari.data.UserReviewInfo;

import java.util.ArrayList;

public class UserReviewAdapter extends RecyclerView.Adapter<UserReviewAdapter.UserReviewVH>
{
    private static final String LOG = "Goguma";

    private Context mContext;

    private ArrayList<UserReviewInfo> mReviewList;

    public UserReviewAdapter(ArrayList<UserReviewInfo> reviewList)
    {
        mReviewList = reviewList;
    }

    @NonNull
    @Override
    public UserReviewVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_store_info_user_review_item, viewGroup, false);

        return new UserReviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReviewVH storeInfoVH, int i)
    {
        storeInfoVH.textStoreName.setText(mReviewList.get(i).getWriterId());
        storeInfoVH.ratingBar.setRating(mReviewList.get(i).getRating());
        storeInfoVH.textDate.setText(mReviewList.get(i).getUpdDate());
        storeInfoVH.textReview.setText(mReviewList.get(i).getComment());
    }

    @Override
    public int getItemCount()
    {
        return mReviewList.size();
    }

    public class UserReviewVH extends RecyclerView.ViewHolder
    {
        protected TextView textStoreName;
        protected RatingBar ratingBar;
        protected TextView textDate;
        protected TextView textReview;

        public UserReviewVH(@NonNull View view)
        {
            super(view);

            textStoreName = (TextView) view.findViewById(R.id.text_store_name);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            textDate = (TextView) view.findViewById(R.id.text_upd_date);
            textReview = (TextView) view.findViewById(R.id.text_review);

            textStoreName.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
//                    Intent intent = new Intent(mContext, SearchInfoActivity.class);
//                    mContext.startActivity(intent);
                }
            });
        }
    }
}
