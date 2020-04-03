package com.inka.example.myapp01.ui.ethereum;

import android.app.Activity;
import android.content.Context;
import android.widget.ListAdapter;

import com.inka.example.myapp01.R;

import java.text.DecimalFormat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EthereumViewModel extends ViewModel {

    private ListViewAdapter adapter;

    public EthereumViewModel( Activity context ) {
        adapter = new ListViewAdapter(context);
    }

    public ListAdapter getListAdapter() {
        return adapter;
    }

    /**
     * 리스트 내용을 갱신한다.
     */
    public void updateList() {
        adapter.update();
        adapter.notifyDataSetChanged();
    }
}