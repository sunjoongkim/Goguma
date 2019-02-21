package com.wowls.boddari.ui.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wowls.boddari.R;
import com.wowls.boddari.data.StoreReviewInfo;
import com.wowls.boddari.ui.search.review.UserReviewActivity;

import java.util.ArrayList;

public class StoreReviewAdapter extends RecyclerView.Adapter<StoreReviewAdapter.StoreReviewVH>
{
    private static final String LOG = "Goguma";

    private Context mContext;

    private ArrayList<StoreReviewInfo> mReviewList;

    public StoreReviewAdapter(ArrayList<StoreReviewInfo> reviewList)
    {
        mReviewList = reviewList;
    }

    @NonNull
    @Override
    public StoreReviewVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_store_info_2_item, viewGroup, false);

        return new StoreReviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreReviewVH storeInfoVH, int i)
    {
        Glide.with(mContext)
             .load(R.drawable.bg_profile)
             .apply(new RequestOptions().circleCrop())
             .into(storeInfoVH.imageProfile);

        storeInfoVH.textWriterId.setText(mReviewList.get(i).getWriterId());
        storeInfoVH.ratingBar.setRating(mReviewList.get(i).getRating());
        storeInfoVH.textDate.setText(mReviewList.get(i).getUpdDate());
        storeInfoVH.textReview.setText(mReviewList.get(i).getComment());
    }

    @Override
    public int getItemCount()
    {
        return mReviewList.size();
    }

    public class StoreReviewVH extends RecyclerView.ViewHolder
    {
        protected ImageView imageProfile;
        protected TextView textWriterId;
        protected RatingBar ratingBar;
        protected TextView textDate;
        protected TextView textReview;

        public StoreReviewVH(@NonNull View view)
        {
            super(view);

            imageProfile = (ImageView) view.findViewById(R.id.image_profile);
            textWriterId = (TextView) view.findViewById(R.id.text_writer_id);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            textDate = (TextView) view.findViewById(R.id.text_upd_date);
            textReview = (TextView) view.findViewById(R.id.text_review);

            textWriterId.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, UserReviewActivity.class);
                    intent.putExtra("user_id", textWriterId.getText().toString());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
