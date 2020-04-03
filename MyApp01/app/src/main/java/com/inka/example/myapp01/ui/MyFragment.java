package com.inka.example.myapp01.ui;

import android.view.View;

import com.inka.example.myapp01.R;

import androidx.fragment.app.Fragment;

public class MyFragment extends Fragment {
    protected void setVisibilityFloatingActionButton( int visibility ) {
        View view = getActivity().findViewById(R.id.fab);
        if (view != null) view.setVisibility( visibility );
    }
}
