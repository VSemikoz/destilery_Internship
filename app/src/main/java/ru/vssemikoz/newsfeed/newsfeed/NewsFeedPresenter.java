package ru.vssemikoz.newsfeed.newsfeed;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vssemikoz.newsfeed.MainApplication;
import ru.vssemikoz.newsfeed.R;
import ru.vssemikoz.newsfeed.adapters.NewsFeedAdapter;
import ru.vssemikoz.newsfeed.dao.NewsItemDAO;
import ru.vssemikoz.newsfeed.models.Category;
import ru.vssemikoz.newsfeed.models.NewsApiResponse;
import ru.vssemikoz.newsfeed.models.NewsApiResponseItem;
import ru.vssemikoz.newsfeed.models.NewsItem;
import ru.vssemikoz.newsfeed.navigator.Navigator;
import ru.vssemikoz.newsfeed.storage.NewsApiRepository;
import ru.vssemikoz.newsfeed.storage.NewsStorage;

import static androidx.core.util.Preconditions.checkNotNull;

public class NewsFeedPresenter implements NewsFeedContract.Presenter {
    private final NewsFeedContract.View newsFeedView;// TODO: 17.03.2020 rename into view

    private String TAG = NewsFeedPresenter.class.getName();
    private boolean showOnlyFavorite = false;
    private Category category = Category.ALL;
    private List<NewsItem> news;

    private Callback<NewsApiResponse> callbackNewsItemList;
    private MainApplication mainApplication;
    private NewsStorage newsStorage;

    NewsFeedPresenter(NewsFeedContract.View tasksView, MainApplication mainApplication){
        newsFeedView = checkNotNull(tasksView, "tasksView cannot be null!");
        newsFeedView.setPresenter(this);
        this.mainApplication = mainApplication;
    }

    @Override
    public void start() {
        initNewsItemListCallback();
        initNewsStorage();
        Log.d(TAG, "start: ");
        loadNews();
        newsFeedView.showNews();
    }

    @Override
    public void loadNews() {
        performCall();
        news = getNewsFromDB();
    }

    @Override
    public String getDisplayDescriptionText() {
        return mainApplication.getString(R.string.tv_category_string_prefix) +
                Category.getDisplayName(category);
    }

    @Override
    public void openNewsDetails(int position) {
        showNewsInBrowserByUrl(position);
    }

    @Override
    public void changeFavoriteState(int position) {
        NewsItem item = news.get(position);
        item.invertFavoriteState();
        newsStorage.updateNews(item);
        if (!item.isFavorite() && showOnlyFavorite) {
            news.remove(position);
            newsFeedView.getAdapter().notifyItemRemoved(position);
            if (news.isEmpty()) {
                newsFeedView.setEmptyViewOnDisplay();
            }
        } else {
            newsFeedView.getAdapter().notifyItemChanged(position);
        }
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public void setShowFavorite(Boolean showOnlyFavorite) {
        this.showOnlyFavorite = showOnlyFavorite;
    }

    @Override
    public Boolean getShowFavorite() {
        return this.showOnlyFavorite;
    }

    @Override
    public List<NewsItem> getNews() {
        return news;
    }

    @Override
    public void invertFavoriteState() {
        showOnlyFavorite = !showOnlyFavorite;
    }

    private void showNewsInBrowserByUrl(int position) {
        NewsItem item = news.get(position);
        String url = item.getUrl();
        newsFeedView.openNews(url);
    }

    private List<NewsItem> getNewsFromDB() {
        return newsStorage.getNewsFromDB(showOnlyFavorite, category);
    }

    private void initNewsStorage() {
        NewsItemDAO newsItemDAO = mainApplication.getNewsDataBase().newsItemDAO();
        newsStorage = new NewsStorage(newsItemDAO);
    }

    private void initNewsItemListCallback() {
        // TODO: 17.03.2020 extract in network layer
        callbackNewsItemList = new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<NewsApiResponse> call, @NotNull Response<NewsApiResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse " + response.code());
                    return;
                }
                newsStorage.insertUnique(getNewsItemListByResponse(response, category));
                Log.d(TAG, "onResponse: ");
                newsFeedView.getAdapter().setItems(news);
                newsFeedView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NotNull Call<NewsApiResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure " + Objects.requireNonNull(t.getMessage()));
            }
        };
    }

    private void performCall() {
        Log.d(TAG, "performCall: ");
        NewsApiRepository newsApiRepository = new NewsApiRepository(mainApplication);
        newsApiRepository.getNewsFromApi(category, callbackNewsItemList);
    }

    private List<NewsItem> getNewsItemListByResponse(Response<NewsApiResponse> response, Category category) {
        List<NewsItem> news = new ArrayList<>();
        List<NewsApiResponseItem> newsApiResponseItems = Objects.requireNonNull(response.body()).getNewsApiResponseItemList();
        for (NewsApiResponseItem newsApiResponseItem : newsApiResponseItems) {
            news.add(new NewsItem(newsApiResponseItem, category));
        }
        return news;
    }
}
