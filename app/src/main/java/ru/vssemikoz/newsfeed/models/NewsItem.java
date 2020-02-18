package ru.vssemikoz.newsfeed.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.vssemikoz.newsfeed.TypeConverters.DateConverter;
import ru.vssemikoz.newsfeed.TypeConverters.URIConverter;

@Entity(indices = @Index(value = "title", unique = true))
@TypeConverters({URIConverter.class, DateConverter.class})
public class NewsItem {
    @PrimaryKey(autoGenerate = true)
    public  int newsId;
    public  String author;
    public  String title;
    public  String description;
    public  String content;
    public  String url;
    @ColumnInfo(name = "image_url")
    public String imageUrl;
    @ColumnInfo(name = "published_at")
    public  Date publishedAt;

    public NewsItem(){

    }

    public NewsItem(NewsApiResponseItem newsApiResponseItem){
        this.author = newsApiResponseItem.getAuthor();
        this.title = newsApiResponseItem.getTitle();
        this.description = newsApiResponseItem.getDescription();
        this.content = newsApiResponseItem.getContent();
        this.url = newsApiResponseItem.getUrl();
        this.imageUrl = newsApiResponseItem.getImageUrl();
        this.publishedAt = DateConverter.fromString(newsApiResponseItem.getPublishedAt());
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }
}
