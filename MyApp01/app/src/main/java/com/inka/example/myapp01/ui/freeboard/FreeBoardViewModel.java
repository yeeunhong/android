package com.inka.example.myapp01.ui.freeboard;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.inka.example.myapp01.ui.ethereum.ListViewItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FreeBoardViewModel extends ViewModel {

    private static final String LOG_TAG = "FREE_BOARD";
    private MutableLiveData<String> mText;

    public FreeBoardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");


    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateData() {
        //String requestUrl = "https://community.coinbit.co.kr/bbs/board.php?bo_table=free";
        String requestUrl = "https://community.coinbit.co.kr/bbs/board.php";
        Log.d( LOG_TAG, "requestUrl : " + requestUrl );


        OkHttpClient client = new OkHttpClient();                                           // OkHttpClient lib 이용
        Request request = new Request.Builder()
                    .url( requestUrl )
                    .build();

        //비동기 처리 (enqueue 사용)
        client.newCall(request).enqueue( requestCallback );                                 // 비동기 request
    }

    Callback requestCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d( LOG_TAG, "error + Connect Server Error is " + e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d( LOG_TAG, "onResponse" );

            String strResponse = response.body().string();
            Log.d( LOG_TAG,"Response Body is " + strResponse );
        }
    };
}