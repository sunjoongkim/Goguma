package com.wowls.boddari.data;

public class StoreReviewInfo
{
    private String mWriterId;
    private int mRating;
    private String mComment;
    private String mUpdDate;

    public StoreReviewInfo(String writerId, int rating, String comment, String updDate)
    {
        mWriterId = writerId;
        mRating = rating;
        mComment = comment;
        mUpdDate = updDate;
    }

    public String getWriterId()
    {
        return mWriterId;
    }

    public int getRating()
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
