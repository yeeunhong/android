package com.inka.example.myapp01.ui.freeboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.inka.example.myapp01.R;
import com.inka.example.myapp01.ui.MyFragment;
import com.inka.example.myapp01.ui.qrcode.CaptureForm;
import com.inka.example.myapp01.ui.qrcode.QrCreateFragment;

public class FreeBoardFragment extends Fragment {

    private FreeBoardViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(FreeBoardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_freeboard, container, false);
        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

         */

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.updateData();
            }
        });



        return root;
    }
}
