package ru.vssemikoz.newsfeed;

import org.junit.Test;

import java.util.ArrayList;

import ru.vssemikoz.newsfeed.data.LocalNewsStorage;
import ru.vssemikoz.newsfeed.models.Category;

import ru.vssemikoz.newsfeed.models.NewsItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = ExampleUnitTest.class.getName();
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test1() {
        Category all = Category.ALL;
        assertEquals(Category.getCategoryName(all), "ALL");
    }

    @Test
    public void test2() {
        ArrayList<NewsItem> emptyList = new ArrayList<>();
        LocalNewsStorage storage = mock(LocalNewsStorage.class);
        when(storage.getFiltered(true, Category.ALL)).thenReturn(emptyList);

        assertEquals(storage.getFiltered(true, Category.ALL), emptyList);
    }
}
