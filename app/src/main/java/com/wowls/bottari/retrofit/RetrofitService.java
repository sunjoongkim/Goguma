package com.wowls.bottari.retrofit;

import com.wowls.bottari.define.Define;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @DELETE(Define.URL_USER + "{path}")
    Call<ResponseBody> deleteUser(@Path("path") String userId);

    @PUT(Define.URL_USER + "{path}")
    Call<ResponseBody> updateUser(@Path("path") String userId, @Body Map<String, String> data);

    @POST(Define.URL_ROOM)
    Call<ResponseBody> getRoomId(@Body Map<String, String> user);

    // Store Management
    @POST(Define.URL_STORE_MANAGER + Define.URL_OWNER + "/{path}")
    Call<ResponseBody> saveStoreInfo(@Path("path") String ownerId, @Body Map<String, String> user);

    @GET(Define.URL_STORE_MANAGER + Define.URL_OWNER + "/{path}")
    Call<ResponseBody> showOwnStoreList(@Path("path") String ownerId);

    @GET(Define.URL_STORE_MANAGER + Define.URL_STORE + "/{path}")
    Call<ResponseBody> showOwnMenuList(@Path("path") String storeId);

    @POST(Define.URL_STORE_MANAGER + Define.URL_STORE + "/{path}")
    Call<ResponseBody> saveMenuInfo(@Path("path") String storeId, @Body Map<String, String> menu);

    // Store
    @GET(Define.URL_STORE + Define.URL_SEARCH)
    Call<ResponseBody> showStoreList();

    @GET(Define.URL_STORE + Define.URL_SEARCH)
    Call<ResponseBody> showStoreList(@Query(Define.URL_KEYWORDS) String... keywords);

    @GET(Define.URL_STORE + "/{path}")
    Call<ResponseBody> showStoreInfo(@Path("path") String storeId);

}
