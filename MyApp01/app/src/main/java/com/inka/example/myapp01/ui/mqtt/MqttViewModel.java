package com.inka.example.myapp01.ui.mqtt;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MqttViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MqttViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}