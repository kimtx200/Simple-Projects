package com.study.test;

/**
 * Created by taewon on 2016-11-08.
 */

import retrofit2.Call;
import retrofit2.http.GET;
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


public interface KobisAPI {

    @GET("webservice/rest/boxoffice/searchDailyBoxOfficeList.json")
    Call<KobisData> getData(@Query("key") String key, @Query("targetDt") String targetDt);

}
