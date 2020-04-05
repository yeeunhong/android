package com.inka.example.myapp01.ui.ethereum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.inka.example.myapp01.R;
import com.inka.example.myapp01.ui.MyFragment;
import com.inka.example.myapp01.ui.gallery.GalleryViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

public class EthereumFragment extends MyFragment {

    private EthereumViewModel viewModel = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if( viewModel == null ) {
            viewModel = new EthereumViewModel(getActivity(), "0xceBa559A8bBB37bEBf410Ab3959ACeB298084B8b");
        }
        View root = inflater.inflate(R.layout.fragment_ethereum, container, false);

        ListView listView = root.findViewById(R.id.lst_ethereumList);
        listView.setAdapter( viewModel.getListAdapter());

        viewModel.updateList();
        setVisibilityFloatingActionButton(View.INVISIBLE);
        return root;
    }
}
