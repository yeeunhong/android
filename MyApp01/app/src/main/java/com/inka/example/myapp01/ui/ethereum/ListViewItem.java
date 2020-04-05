package com.inka.example.myapp01.ui.ethereum;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.BaseAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListViewItem implements Comparable<ListViewItem>, Runnable {
    public Drawable iconDrawable;
    public TokenItemData item;

    public Activity context;
    public BaseAdapter baseAdapter;

    public ListViewItem(TokenItemData item) {
        this.item = item;
    }

    @Override
    public int compareTo( ListViewItem listViewItem ) {
        return item.compareTo( listViewItem.item );
    }

    @Override
    public void run() {
        File imgFile = new File( context.getCacheDir(), item.contract + ".png" );
        String url = "https://etherscan.io/token/" + item.contract;

        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url( url ).build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            final String findString = "<img class='u-sm-avatar mr-2' src='";

            String content = response.body().string();

            int idx = content.indexOf( findString );
            if( idx > -1 ) {
                idx = idx + findString.length();
                int endIdx = content.indexOf( "' />", idx );

                String imageUrl = "https://etherscan.io/" + content.substring( idx, endIdx );

                request = new Request.Builder().url( imageUrl ).build();
                response = httpClient.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                FileOutputStream fos = new FileOutputStream( imgFile );
                fos.write( response.body().bytes() );
                fos.close();

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        baseAdapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}