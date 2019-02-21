package com.wowls.boddari.retrofit;

import com.wowls.boddari.define.Define;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ReverseGeoService
{
    @Headers
    ({
        Define.HEADER_CLIENT_ID + ":" + Define.CLIENT_ID,
        Define.HEADER_CLIENT_SECRET + ":" + Define.CLIENT_SECRET
    })
    @GET(Define.URL_REVERSE_GEO)
    Call<ResponseBody> getAddress(@Query(Define.URL_COORDS) String coords,
                                  @Query(Define.URL_OUTPUT) String output);
}
