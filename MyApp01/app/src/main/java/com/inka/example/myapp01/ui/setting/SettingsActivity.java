package com.inka.example.myapp01.ui.setting;

import android.os.Bundle;

import com.inka.example.myapp01.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.setTitle( R.string.action_settings );
    }
}
