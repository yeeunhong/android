package com.inka.example.myapp01.ui.ethereum;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSession;
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
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.internal.Collection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListViewAdapter extends BaseAdapter {
    private static final String LOG_TAG = "ETH_LIST";

    private Map<String, ListViewItem> viewItemMap = new HashMap<>();
    private List<ListViewItem> viewItemList;

    private Map<Integer, Drawable> drawableIconMap = new HashMap<Integer,Drawable>();
    private final RealmConfiguration realmConfig;
    private final Realm realm;

    private final String apiKey = "PS1SWX5K3K6AVA46I62VHPR4ACERKZTISZ";

    private final String ethAddress;
    private final Activity context;
    public ListViewAdapter( Activity context, String ethAddress ) {
        this.context    = context;
        this.ethAddress = ethAddress;

        realmConfig = new RealmConfiguration.Builder()
                .name("eth.token.realm")
                .schemaVersion(0)
                .build();

        realm = Realm.getInstance( realmConfig );
    }

    @Override
    public int getCount() {
        viewItemList = new ArrayList<ListViewItem>( viewItemMap.values());
        Collections.sort(viewItemList);

        Log.d( LOG_TAG, "getCount " + viewItemList.size() );
        return viewItemMap.size();
    }

    @Override
    public Object getItem(int position) {
        return viewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 리스트에 항목을 추가한다.
     *
     * @param realmWrite
     * @param resDrawableIconID
     * @param tokenName
     * @param symbol
     * @param quantity
     * @return true:성공, false:실패
     */
    public void addItem( Realm realmWrite, int resDrawableIconID, String tokenName, String symbol, String contract, BigInteger quantity, boolean bAdd) {
        Drawable icon = null;
        if( !drawableIconMap.isEmpty()) {
            icon = drawableIconMap.get(resDrawableIconID);
        }
        if( icon == null ) {
            icon = context.getResources().getDrawable(resDrawableIconID);
            drawableIconMap.put( resDrawableIconID, icon );
        }
        addItem( realmWrite, icon, tokenName, symbol, contract, quantity, bAdd );
    }

    public void addItem(Realm realmWrite, Drawable icon, String tokenName, String symbol, String contract, BigInteger quantity, boolean bAdd) {
        Log.d( LOG_TAG, String.format( "addItem %s icon is %s", tokenName, icon == null ? "null" : "not null" ));

        ListViewItem item = viewItemMap.get( contract );
        if( item == null ) {
            item = new ListViewItem();
            //item.item = realmWrite.createObject( TokenItemData.class );
            item.item = new TokenItemData();
            item.item.quantity = "0";
        }
        // realmWrite.beginTransaction();
        item.iconDrawable       = icon;
        item.item.tokenName     = tokenName;
        item.item.symbol        = symbol;
        if( bAdd ) {
            item.item.quantity  = new BigInteger( item.item.quantity ).add( quantity ).toString();
        } else {
            item.item.quantity  = new BigInteger( item.item.quantity ).subtract( quantity ).toString();
        }
        // realmWrite.commitTransaction();

        viewItemMap.put( contract, item );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d( LOG_TAG, "getView " + position );

        final Context context = parent.getContext();

        if( convertView == null ) {
            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate( R.layout.listview_ethereum_item, parent, false );
        }

        ImageView ivIcon      = (ImageView) convertView.findViewById(R.id.imageIcon);
        TextView tvTokenName  = (TextView) convertView.findViewById(R.id.tokenName);
        TextView tvSymbol     = (TextView) convertView.findViewById(R.id.symbol);
        TextView tvQuantity   = (TextView) convertView.findViewById(R.id.tokenQuantity);

        ListViewItem item = viewItemList.get(position);

        ivIcon.setImageDrawable(item.iconDrawable );
        tvTokenName.setText(item.item.tokenName);
        tvSymbol.setText(item.item.symbol);
        tvQuantity.setText( new BigInteger( item.item.quantity ).divide( BigInteger.valueOf( 1000000000000000000l )).toString() );

        return convertView;
    }
    DecimalFormat quantityFormat = new DecimalFormat("####.##########");

    /**
     *
     */
    public void update() {
        Log.d( LOG_TAG, "update" );

        final RealmResults<TokenItemData> result = realm.where(TokenItemData.class).findAllAsync();
        result.addChangeListener(new RealmChangeListener<RealmResults<TokenItemData>>() {

            long blockNumber = 0;
            @Override
            public void onChange(RealmResults<TokenItemData> tokenItemData) {
                Log.d( LOG_TAG, "RealmResults:onChange " + tokenItemData.size() );

                for( TokenItemData itemData : tokenItemData ) {
                    blockNumber = Math.max( blockNumber, itemData.blockNumber );
                }

                if( result.isLoaded()) {
                    Log.d( LOG_TAG, "RealmResults:onChange isLoaded()" );

                    String requestUrl = requestURLFormat
                            .replace("<%address%>", ethAddress )
                            .replace("<%startblock%>", String.valueOf(blockNumber) )
                            .replace( "<%apikey%>", apiKey );
                    Log.d( LOG_TAG, "requestUrl : " + requestUrl );

                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                //.addHeader("x-api-key", RestTestCommon.API_KEY)
                                .url( requestUrl )
                                .build();

                        //비동기 처리 (enqueue 사용)
                        client.newCall(request).enqueue( requestCallback );
                    } catch (Exception e){
                        System.err.println(e.toString());
                    }
                }
            }
        });
    }
    final String requestURLFormat = "https://api.etherscan.io/api?module=account&action=tokentx&address=<%address%>&startblock=<%startblock%>&endblock=999999999&sort=desc&apikey=<%apikey%>&page=1&offset=100";

    Callback requestCallback = new Callback() {
        private static final String LOG_TAG = "REQUEST_CALLBACK";

        @Override
        public void onFailure(Call call, IOException e) {
            Log.d( LOG_TAG, "error + Connect Server Error is " + e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d( LOG_TAG, "onResponse" );

            Realm realmWrite = Realm.getInstance( realmConfig );
            try {
                JSONObject jsonObject = new JSONObject( response.body().string() );
                if( jsonObject.getString("status").compareTo("1")==0 && jsonObject.getString("message").compareTo("OK")==0) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for( int i = 0; i < jsonArray.length(); i++ ) {
                        JSONObject jsonItem = jsonArray.getJSONObject(i);

                        String totkeName    = jsonItem.getString("tokenName");
                        String tokenSymbol  = jsonItem.getString("tokenSymbol");
                        String contract     = jsonItem.getString("contractAddress");
                        //String fromAddr     = jsonItem.getString("from").toUpperCase();       // 출금 주소
                        String toAddr       = jsonItem.getString("to").toUpperCase();   // 입금 주소

                        String strQuantity  = jsonItem.getString("value");
                        BigInteger quantity = new BigInteger( strQuantity );

                        Log.d( LOG_TAG, String.format( "Response[%d] value:%s", i, strQuantity));

                        addItem( realmWrite, R.drawable.ic_menu_ethereum, totkeName,  tokenSymbol, contract, quantity, ethAddress.compareTo(toAddr) == 0 );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d( LOG_TAG, "notifyDataSetChanged()" );
                    notifyDataSetChanged();
                }
            });

            realmWrite.close();
            // Log.d( LOG_TAG,"Response Body is " + response.body().string());
        }
    };
}
