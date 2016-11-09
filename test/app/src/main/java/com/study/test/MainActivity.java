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

        Retrofit client = new Retrofit.Builder()
                .baseUrl(KOBIS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KobisAPI kobisService = client.create(KobisAPI.class);
        Call<KobisData> kobisData = kobisService.getData(KEY, DATE);

        // 한국 영화 진흥원 API GET Request
        kobisData.enqueue(new Callback<KobisData>() {
            @Override
            public void onResponse(Call<KobisData> call, Response<KobisData> response) {
                if (response.isSuccessful()) {
                    KobisData data = response.body();
                    for (KobisData.DailyBoxOfficeList temp : data.getBoxOfficeResult().getDailyBoxOfficeList()) {
                        Log.i("BoxOffice_List", "--------------------"+temp.getMovieNm());
                        movie_boxOffice.add(temp.getMovieNm());
                    }
                }
            }

            @Override
            public void onFailure(Call<KobisData> call, Throwable t) {
                t.printStackTrace();
            }
        });

        for (String test : movie_boxOffice)
            Log.e("LIST Test", "000000000000" + test);

        client = new Retrofit.Builder()
                .baseUrl(NAVER_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        NaverSearchAPI NaverService = client.create(NaverSearchAPI.class);

        for (int i = 0; i < movie_boxOffice.size(); i++) {
            String query = movie_boxOffice.get(i);
            Call<NaverSearchData> naverData = NaverService.getData(query);
            naverData.enqueue(new Callback<NaverSearchData>() {
                @Override
                public void onResponse(Call<NaverSearchData> call, Response<NaverSearchData> response) {
                    if (response.isSuccessful()) {
                        NaverSearchData data = response.body();
                        for (NaverSearchResult temp : data.channel.getItems())
                            Log.i("NAVER TEST", "><><><><><><><><><><><" + temp.getTitle() + " | ");
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
