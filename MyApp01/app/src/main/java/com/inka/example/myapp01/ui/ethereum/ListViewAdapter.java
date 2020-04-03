package com.inka.example.myapp01.ui.ethereum;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inka.example.myapp01.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>();
    private Map<Integer, Drawable> drawableIconMap = new HashMap<Integer,Drawable>();

    private final Activity context;
    public ListViewAdapter( Activity context ) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 리스트에 항목을 추가한다.
     * @param icon
     * @param tokenName
     * @param symbol
     * @param quantity
     * @return true:성공, false:실패
     */
    public boolean addItem(int resDrawableIconID, String tokenName, String symbol, double quantity ) {
        Drawable icon = null;
        if( !drawableIconMap.isEmpty()) {
            icon = drawableIconMap.get(resDrawableIconID);
        }
        if( icon == null ) {
            icon = context.getResources().getDrawable(resDrawableIconID);
            drawableIconMap.put( resDrawableIconID, icon );
        }
        return addItem( icon, tokenName, symbol, quantity );
    }
    public boolean addItem(Drawable icon, String tokenName, String symbol, double quantity ) {
        ListViewItem item = new ListViewItem();
        item.iconDrawable   = icon;
        item.tokenName      = tokenName;
        item.symbol         = symbol;
        item.quantity       = quantity;

        return listViewItemList.add( item );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if( convertView == null ) {
            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate( R.layout.listview_ethereum_item, parent, false );
        }

        ImageView ivIcon      = (ImageView) convertView.findViewById(R.id.imageIcon);
        TextView tvTokenName  = (TextView) convertView.findViewById(R.id.tokenName);
        TextView tvSymbol     = (TextView) convertView.findViewById(R.id.symbol);
        TextView tvQuantity   = (TextView) convertView.findViewById(R.id.tokenQuantity);

        ListViewItem item = listViewItemList.get(position);

        ivIcon.setImageDrawable(item.iconDrawable );
        tvTokenName.setText(item.tokenName);
        tvSymbol.setText(item.symbol);
        tvQuantity.setText( quantityFormat.format(item.quantity));

        return convertView;
    }
    DecimalFormat quantityFormat = new DecimalFormat("####.##########");

    /**
     *
     */
    public void update() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    //.addHeader("x-api-key", RestTestCommon.API_KEY)
                    .url(requestURL)
                    .build();

            //비동기 처리 (enqueue 사용)
            client.newCall(request).enqueue( requestCallback );
        } catch (Exception e){
            System.err.println(e.toString());
        }
    }
    final String requestURL = "https://api.etherscan.io/api?module=account&action=tokentx&address=0xceba559a8bbb37bebf410ab3959aceb298084b8b&startblock=0&endblock=999999999&sort=desc&apikey=PS1SWX5K3K6AVA46I62VHPR4ACERKZTISZ&page=1&offset=100";

    Callback requestCallback = new Callback() {
        private static final String LOG_TAG = "REQUEST_CALLBACK";

        @Override
        public void onFailure(Call call, IOException e) {
            Log.d( LOG_TAG, "error + Connect Server Error is " + e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                JSONObject jsonObject = new JSONObject( response.body().string() );
                if( jsonObject.getString("status").compareTo("1")==0 && jsonObject.getString("message").compareTo("OK")==0) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for( int i = 0; i < jsonArray.length(); i++ ) {
                        JSONObject jsonItem = jsonArray.getJSONObject(i);

                        String totkeName    = jsonItem.getString("tokenName");
                        String tokenSymbol  = jsonItem.getString("tokenSymbol");
                        String strQuantity  = jsonItem.getString("value");

                        Log.d( LOG_TAG, String.format( "Response[%d] value:%s", i, strQuantity));
                        //strQuantity = strQuantity.substring(0,strQuantity.length()-6);

                        double quantity     = Double.valueOf( strQuantity );
                        quantity = quantity / 10000000000000000.0;

                        addItem(R.drawable.ic_menu_ethereum, totkeName,  tokenSymbol, quantity );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

            // Log.d( LOG_TAG,"Response Body is " + response.body().string());
        }
    };
}
