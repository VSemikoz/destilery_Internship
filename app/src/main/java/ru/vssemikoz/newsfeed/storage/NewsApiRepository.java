package ru.vssemikoz.newsfeed.storage;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vssemikoz.newsfeed.MainApplication;
import ru.vssemikoz.newsfeed.models.Category;
import ru.vssemikoz.newsfeed.models.NewsApiResponse;

public class NewsApiRepository {
    private MainApplication mainApplication;
    private Callback<NewsApiResponse> callbackNewsItemList;

    public interface RequestListener {
        void onApiRequestSuccess(Response<NewsApiResponse> response);

        void onApiRequestFailure(Throwable t);
    }

    public NewsApiRepository(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
    }

    public void getNewsFromApi(Category category, RequestListener listener) {
        String categoryKey = null;
        String countryKey = "ru";
        String KEY = mainApplication.getKEY();
        Call<NewsApiResponse> call;
        if (category != Category.ALL) {
            categoryKey = category.name();
        }
        call = mainApplication.getNewsApi().getNews(countryKey, categoryKey, KEY);
        call.enqueue(getCallbackNewsItemList(listener));
    }

    private Callback<NewsApiResponse> getCallbackNewsItemList(RequestListener listener) {
        if (callbackNewsItemList == null) {
            initNewsItemListCallback(listener);
        }
        return callbackNewsItemList;
    }

    private void initNewsItemListCallback(RequestListener listener) {
        callbackNewsItemList = new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<NewsApiResponse> call, @NotNull Response<NewsApiResponse> response) {
                listener.onApiRequestSuccess(response);
            }

            @Override
            public void onFailure(@NotNull Call<NewsApiResponse> call, @NotNull Throwable t) {
                listener.onApiRequestFailure(t);
            }
        };
    }
}
