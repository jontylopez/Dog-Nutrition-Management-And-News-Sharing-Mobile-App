package com.example.woof;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CusHomeFragment extends Fragment {

    private LinearLayout newsItemLayout;
    private dBaseOperations db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cus_home, container, false);
        newsItemLayout = rootView.findViewById(R.id.news_items_layout);
        db = new dBaseOperations(getContext());
        loadNewsItems();
        return rootView;
    }

    private void loadNewsItems() {
        ArrayList<News> newsList = db.getAllNews();
        if (newsList != null) {
            for (News news : newsList) {
                addNewsItemView(news);
            }
        }
    }

    private void addNewsItemView(News news) {
        View newsView = getLayoutInflater().inflate(R.layout.customer_news_layout, newsItemLayout, false);


        TextView newsDescription = newsView.findViewById(R.id.news_description);
        ImageView newsImage = newsView.findViewById(R.id.news_image);


        newsDescription.setText(news.getnDescription());

        if (news.getnImage() != null && news.getnImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(news.getnImage(), 0, news.getnImage().length);
            newsImage.setImageBitmap(bitmap);
        }

        newsItemLayout.addView(newsView);
    }
}
