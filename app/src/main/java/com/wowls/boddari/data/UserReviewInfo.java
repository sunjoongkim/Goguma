package com.wowls.boddari.data;

public class UserReviewInfo
{
    private String mStoreId;
    private float mRating;
    private String mComment;
    private String mUpdDate;

    public UserReviewInfo(String writerId, float rating, String comment, String updDate)
    {
        mStoreId = writerId;
        mRating = rating;
        mComment = comment;
        mUpdDate = updDate;
    }

    public String getWriterId()
    {
        return mStoreId;
    }

    public float getRating()
    {
        return mRating;
    }

    public String getComment()
    {
        return mComment;
    }

    public String getUpdDate()
    {
        return mUpdDate;
    }
}
