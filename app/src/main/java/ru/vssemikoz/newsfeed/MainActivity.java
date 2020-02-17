package ru.vssemikoz.newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vssemikoz.newsfeed.DAO.NewsItemDAO;
import ru.vssemikoz.newsfeed.DataBase.NewsAppDataBase;
import ru.vssemikoz.newsfeed.adapters.NewsFeedAdapter;
import ru.vssemikoz.newsfeed.api.NewsApi;
import ru.vssemikoz.newsfeed.models.NewsItem;
import ru.vssemikoz.newsfeed.models.NewsItemList;

public class MainActivity extends AppCompatActivity {
    String KEY = "c94a57cbbb50497f94a2bb167dc91fc5";
    List<NewsItem> newsItems = new ArrayList<NewsItem>();
    NewsAppDataBase newsDataBase;
    NewsItemDAO newsItemDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDataBase();
        Callback<NewsItemList> callbackNewsItemList = new Callback<NewsItemList>() {
            @Override
            public void onResponse(Call<NewsItemList> call, Response<NewsItemList> response) {
                if (!response.isSuccessful()){
                    Log.d("MyLog", "onResponse " + response.code());
                    return;
                }
                newsItemDAO.insertUnique(response.body().getNewsItem());
                Log.d("MyLog", String.valueOf(newsItemDAO.getAll().size()));
                initRecView();
            }

            @Override
            public void onFailure(Call<NewsItemList> call, Throwable t) {
                Log.d("MyLog", "onFailure " + t.getMessage());
            }
        };

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApi newsApi = retrofit.create(NewsApi.class);
        Call<NewsItemList> call = newsApi.getNews("ru", KEY);
        call.enqueue(callbackNewsItemList);

    }

    void initRecView(){
        initNewsItemsData();
        RecyclerView recyclerView =  findViewById(R.id.rv_news_feed);
        NewsFeedAdapter adapter = new NewsFeedAdapter(newsItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    void initNewsItemsData(){
        newsItems = newsItemDAO.getAll();
    }

    void initDataBase(){
        newsDataBase = Room.databaseBuilder(this,
                NewsAppDataBase.class, "news_data_base")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        newsItemDAO = newsDataBase.newsItemDAO();
    }
}
