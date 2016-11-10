package com.study.test;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 *  Kobis API URL       :   http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.xml
 *  Kobis API KEy       :   71cd9a6592ef09f2d81b2711b39e044b
 *
 *  Naver_Search_API    :   https://openapi.naver.com/v1/search/movie.xml
 *  Naver client ID     :   yENwphZG7JRv01mCY0F8
 *  Naver Secret        :   _3ktvDPiRT
 *
 */

public interface NaverSearchAPI {

    @Headers({
            "X-Naver-Client-Id: yENwphZG7JRv01mCY0F8", "X-Naver-Client-Secret: _3ktvDPiRT"
    })
    @GET("v1/search/movie.xml")
    Call<NaverSearchData> getData(@Query("query") String query);
//    , @Query("display")int display, @Query("start") int start
}
