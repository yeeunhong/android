package com.inka.example.myapp01.ui.ethereum;

import android.graphics.drawable.Drawable;

import java.math.BigInteger;

public class ListViewItem implements Comparable<ListViewItem> {
    public Drawable iconDrawable;
    public TokenItemData item;

    @Override
    public int compareTo( ListViewItem listViewItem ) {
        return item.compareTo( listViewItem.item );
    }
}