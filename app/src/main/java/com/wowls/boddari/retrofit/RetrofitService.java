package com.wowls.boddari.retrofit;


import com.wowls.boddari.define.Define;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService
{
    // Login
    @POST(Define.URL_USER)
    Call<ResponseBody> createUser(@Body Map<String, String> user);

    @GET(Define.URL_USER + "/{userId}")
    Call<ResponseBody> getUser(@Path("userId") String userId);

    @GET(Define.URL_USER)
    Call<ResponseBody> getProfile();

    @GET()
    Call<ResponseBody> getProfileImage();

    @DELETE(Define.URL_USER + "{path}")
    Call<ResponseBody> deleteUser(@Path("path") String userId);

    @PUT(Define.URL_USER + "{path}")
    Call<ResponseBody> updateUser(@Path("path") String userId, @Body Map<String, String> data);

    @POST(Define.URL_ROOM)
    Call<ResponseBody> getRoomId(@Body Map<String, String> user);

    // Store Management
    @POST(Define.URL_STORE_MANAGER + Define.URL_OWNER + "/{path}" + Define.URL_STORE)
    Call<ResponseBody> saveStoreInfo(@Path("path") String ownerId, @Body Map<String, String> user);

    @GET(Define.URL_STORE_MANAGER + Define.URL_OWNER + "/{path}" + Define.URL_STORE)
    Call<ResponseBody> showOwnStoreList(@Path("path") String ownerId);

    @GET(Define.URL_STORE_MANAGER + Define.URL_OWNER + "/{ownerId}" + Define.URL_STORE + "/{storeId}" + Define.URL_MENU)
    Call<ResponseBody> showOwnMenuList(@Path("ownerId") String ownerId, @Path("storeId") String storeId);

    @POST(Define.URL_STORE_MANAGER + Define.URL_OWNER + "/{ownerId}" + Define.URL_STORE + "/{storeId}" + Define.URL_MENU)
    Call<ResponseBody> saveMenuInfo(@Path("ownerId") String ownerId, @Path("storeId") String storeId, @Body Map<String, Object> menu);

    // Store
    @GET(Define.URL_STORE + Define.URL_SEARCH)
    Call<ResponseBody> showStoreList();

    @GET(Define.URL_STORE + Define.URL_SEARCH)
    Call<ResponseBody> showStoreList(@Query(Define.URL_KEYWORDS) String... keywords);

    @GET(Define.URL_STORE + "/{path}")
    Call<ResponseBody> showStoreInfo(@Path("path") String storeId);

    // Review
    @POST(Define.URL_REVIEW + Define.URL_WRITER + "/{path}" + Define.URL_STORE)
    Call<ResponseBody> saveReview(@Path("path") String writerId, @Body Map<String, String> review);

    @GET(Define.URL_REVIEW + Define.URL_STORE + "/{path}")
    Call<ResponseBody> showReviewListByStore(@Path("path") String storeId);

    @GET(Define.URL_REVIEW + Define.URL_WRITER + "/{path}")
    Call<ResponseBody> showReviewListByWriter(@Path("path") String writerId);

    // Image
    @Multipart
    @POST(Define.URL_IMAGE_STORAGE + Define.URL_STORE + "/{storeId}")
    Call<ResponseBody> saveImageList(@Path("storeId") String storeId, @Part MultipartBody.Part[] image);

    @GET(Define.URL_IMAGE_STORAGE + Define.URL_STORE + "/{storeId}" + Define.URL_IMAGE + Define.URL_SEARCH)
    Call<ResponseBody> showImage(@Path("storeId") String storeId, @Query(Define.URL_IMAGE_ORDERS) int order);

}
