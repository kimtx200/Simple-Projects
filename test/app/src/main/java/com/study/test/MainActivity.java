package com.study.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static String KEY = "71cd9a6592ef09f2d81b2711b39e044b";
    private static String DATE = "20161101";
    private static String KOBIS_BASE_URL = "http://www.kobis.or.kr/kobisopenapi/";
    private static String NAVER_BASE_URL = "https://openapi.naver.com/";

    ArrayList<String> movie_boxOffice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*************************************************************************************/
        /******************************* 영화 진흥원 API 호출 ********************************/

        // Retrofit 객체 선언
        Retrofit client = new Retrofit.Builder()
                .baseUrl(KOBIS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Retrofit 객체에 GET 요청을 보내는 Interface 삽입
        KobisAPI kobisService = client.create(KobisAPI.class);

        // Base URL 과 Interface 가 결합 된 서비스에 원하는 쿼리전달
        Call<KobisData> kobisData = kobisService.getData(KEY, DATE);

        // 영화 고유 아이디 가져오기 (제목말고!!) -- 네이버에서는 고유아이디로 검색 안됨
        // 요청 한 쿼리에 대한 응답(Response)를 비동기적(enqueue)으로 받아 옴.
        kobisData.enqueue(new Callback<KobisData>() {
            @Override
            public void onResponse(Call<KobisData> call, Response<KobisData> response) {
                if (response.isSuccessful()) {
                    // json 형식에 매칭되는 데이터 클래스에 응답사항을 대입.
                    KobisData data = response.body();

                    // Java class 로 Converting 된 데이터 중 영화 제목을 가져 옴.
                    for (KobisData.DailyBoxOfficeList temp : data.getBoxOfficeResult().getDailyBoxOfficeList()) {
                        Log.i("BoxOffice_List", "--------------------" + temp.getMovieNm());
                        movie_boxOffice.add(temp.getMovieNm());
                        Log.e("LIST Test", movie_boxOffice.size() + "");
                    }

                    // 메인 스레드(Movie_boxOffice), kobis API, NaverSearch API 가 서로 다른 스레드 흐름을 가지므로
                    // kobis API 흐름 내에서 NaverSearch API 를 호출 하도록 함.
                    // 이 부분 나중에 구조를 재설계 해야할 필요가 있음.
                    getNaverData(movie_boxOffice);
                }
            }

            @Override
            public void onFailure(Call<KobisData> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    /*************************************************************************************/
    /******************************* 네이버 검색 API 호출 ********************************/

    private void getNaverData(ArrayList<String> titles) {

        Retrofit client = new Retrofit.Builder()
                .baseUrl(NAVER_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        NaverSearchAPI NaverService = client.create(NaverSearchAPI.class);

        // 영화 진흥원 API 로 부터 전달받은 BoxOffice List 를 이용해
        // For 문을 통해 순차적으로 네이버 검색 API에 get 요청을 보냄.
        for (String query : titles) {

            Call<NaverSearchData> naverData = NaverService.getData(query);
            naverData.enqueue(new Callback<NaverSearchData>() {
                @Override
                public void onResponse(Call<NaverSearchData> call, Response<NaverSearchData> response) {
                    if (response.isSuccessful()) {
                        NaverSearchData data = response.body();
                        for (NaverSearchResult temp : data.channel.getItems())
                            Log.i("NAVER TEST", "<><><><><><><><><><>" + temp.getTitle());
                    }
                }
                @Override
                public void onFailure(Call<NaverSearchData> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

}
