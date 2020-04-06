package com.inka.example.myapp01.ui.ethereum;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inka.example.myapp01.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
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

    ExecutorService executorService = Executors.newFixedThreadPool(4);

    private Map<Integer, Drawable> drawableIconMap = new HashMap<Integer,Drawable>();
    public static RealmConfiguration realmConfig;

    private final String apiKey = "PS1SWX5K3K6AVA46I62VHPR4ACERKZTISZ";

    private final String ethAddress;
    private final Activity context;

    private long lastBlockNumber = 0;

    public ListViewAdapter( Activity context, String ethAddress ) {
        this.context    = context;
        this.ethAddress = ethAddress.toLowerCase();

        realmConfig = new RealmConfiguration.Builder()
                .name("eth.token.realm")
                .schemaVersion(1)
                .build();

        try ( Realm realm = Realm.getInstance( realmConfig )) {
            RealmResults<TokenItemData> tokenItemDatas = realm.where(TokenItemData.class).findAll();

            viewItemMap.clear();
            for( TokenItemData itemData : tokenItemDatas ) {
                TokenItemData newItemData = new TokenItemData();
                newItemData.copyObject( itemData );

                viewItemMap.put( itemData.contract, new ListViewItem( newItemData ));
                lastBlockNumber = Math.max( lastBlockNumber, itemData.blockNumber );
            }

            Log.d( LOG_TAG, String.format("Realm okenItemData count %d, %d", tokenItemDatas.size(), lastBlockNumber ));
        }
    }

    RealmChangeListener realmChangeListener = new RealmChangeListener<RealmResults<TokenItemData>>() {
        @Override
        public void onChange(RealmResults<TokenItemData> tokenItemData) {
            Log.d( LOG_TAG, "Realm okenItemData count " + tokenItemData.size() );
            if( !tokenItemData.isLoaded()) return;

            viewItemMap.clear();
            for( TokenItemData itemData : tokenItemData ) {
                viewItemMap.put( itemData.contract, new ListViewItem( itemData ));
                lastBlockNumber = Math.max( lastBlockNumber, itemData.blockNumber );
            }
        }
    };

    @Override
    public int getCount() {
        int ret = viewItemList == null ? 0 : viewItemList.size();

        // Log.d( LOG_TAG, "getCount " + ret );
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return viewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public void setFilterText(String filterText) {
        filterText = filterText.toLowerCase(Locale.getDefault());
        ArrayList<ListViewItem> allDatas = new ArrayList<ListViewItem>(viewItemMap.values());

        if( filterText.length() == 0 ) {
            viewItemList = allDatas;

        } else {
            viewItemList.clear();
            for (ListViewItem item : allDatas) {
                if (item.item.tokenName.toLowerCase().contains(filterText) || item.item.symbol.toLowerCase().contains(filterText)) {
                    viewItemList.add(item);
                }
            }
        }

        Collections.sort(viewItemList);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    class ViewHolder
    {
        public ImageView ivIcon;
        public TextView tvTokenName;
        public TextView tvSymbol;
        public TextView tvQuantity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.d( LOG_TAG, "getView " + position );

        ViewHolder viewHolder;
        if( convertView == null ) {
            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate( R.layout.listview_ethereum_item, parent, false );

            viewHolder = new ViewHolder();
            viewHolder.ivIcon      = (ImageView) convertView.findViewById(R.id.imageIcon);
            viewHolder.tvTokenName  = (TextView) convertView.findViewById(R.id.tokenName);
            viewHolder.tvSymbol     = (TextView) convertView.findViewById(R.id.symbol);
            viewHolder.tvQuantity   = (TextView) convertView.findViewById(R.id.tokenQuantity);

            convertView.setTag( viewHolder );
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ListViewItem item = viewItemList.get(position);
        item.context     = context;
        item.baseAdapter = this;

        viewHolder.ivIcon.setImageDrawable(item.iconDrawable);
        viewHolder.tvTokenName.setText(item.item.tokenName);
        viewHolder.tvSymbol.setText(item.item.symbol);
        viewHolder.tvQuantity.setText( new BigInteger( item.item.quantity ).divide( BigInteger.valueOf( 1000000000000000000l )).toString() );

        File imgFile = new File( context.getCacheDir(), item.item.iconFilename + ".png" );
        Glide.with(context).load(imgFile).placeholder(R.drawable.ic_menu_ethereum).centerCrop().into(viewHolder.ivIcon);

        if( !imgFile.exists()) {
            if( imgFile.length() < 10 ) imgFile.delete();

            executorService.execute(item);      // 토큰에 해당하는 icon 을 web 에서 다운로드 받는다. background 에서
        }

        return convertView;
    }
    DecimalFormat quantityFormat = new DecimalFormat("####.##########");

    /**
     * etherscan.io 에서 eth 주소에 해당하는 코인들의 전송 이력을 받아와서 정보를 갱신 시켜 준다.
     */
    public void update() {
        Log.d( LOG_TAG, "update" );

        String requestUrl = requestURLFormat
                .replace("<%address%>", ethAddress )                                    // 요청할 이더리움 주소
                .replace("<%startblock%>", String.valueOf( lastBlockNumber + 1 ) )      // 마지막으로 요청한 블럭 번호 이후 부터 요청
                .replace( "<%apikey%>", apiKey );                                       // etherscan.io API KEY
        Log.d( LOG_TAG, "requestUrl : " + requestUrl );

        try {
            OkHttpClient client = new OkHttpClient();                                           // OkHttpClient lib 이용
            Request request = new Request.Builder()
                    .url( requestUrl )
                    .build();

            //비동기 처리 (enqueue 사용)
            client.newCall(request).enqueue( requestCallback );                                 // 비동기 request
        } catch (Exception e){
            System.err.println(e.toString());
        }

    }
    final String requestURLFormat = "https://api.etherscan.io/api?module=account&action=tokentx&address=<%address%>&startblock=<%startblock%>&endblock=999999999&sort=asc&apikey=<%apikey%>&page=1";

    Callback requestCallback = new Callback() {
        private static final String LOG_TAG = "REQUEST_CALLBACK";

        @Override
        public void onFailure(Call call, IOException e) {
            Log.d( LOG_TAG, "error + Connect Server Error is " + e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d( LOG_TAG, "onResponse" );

            try {
                JSONObject jsonObject = new JSONObject( response.body().string() );
                if( jsonObject.getString("status").compareTo("1")==0 && jsonObject.getString("message").compareTo("OK")==0) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for( int i = 0; i < jsonArray.length(); i++ ) {
                        try {
                            addItem(jsonArray.getJSONObject(i));
                        } catch( JSONException e ) { e.printStackTrace();}
                    }
                    // 남은 항목들이 있을 수 있어서 3초 후에 다음 데이터를 요청한다.
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){ // Non UI Thread 에서 handler의 postDelayed 함수를 호출하기 위한 방법
                        @Override
                        public void run() {             // Handler 는 Main Thread 가 아니면 post 를 요청할 수 없다. Main looper 에 연결하여 준다.
                            update();
                        }
                    }, 3000 );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewItemList = new ArrayList<ListViewItem>( viewItemMap.values());
                    Collections.sort(viewItemList);

                    notifyDataSetChanged();
                }
            });

            executorService.execute( RealmDataUpdate );
            // Log.d( LOG_TAG,"Response Body is " + response.body().string());
        }
    };

    /**
     *
     * @param jsonItem
     * @throws JSONException
     */
    private void addItem(JSONObject jsonItem) throws JSONException {
        String totkeName    = jsonItem.getString("tokenName");
        String tokenSymbol  = jsonItem.getString("tokenSymbol");
        String contract     = jsonItem.getString("contractAddress");
        //String fromAddr     = jsonItem.getString("from").toUpperCase();       // 출금 주소
        String toAddr       = jsonItem.getString("to").toLowerCase();   // 입금 주소
        String blockNumber  = jsonItem.getString("blockNumber");

        String strQuantity  = jsonItem.getString("value");
        BigInteger quantity = new BigInteger( strQuantity );

        addItem( R.drawable.ic_menu_ethereum, totkeName,  tokenSymbol, contract, Long.valueOf(blockNumber), quantity, ethAddress.compareTo(toAddr) == 0 );
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
    public void addItem( int resDrawableIconID, String tokenName, String symbol, String contract, long blockNumber, BigInteger quantity, boolean bAdd) {
        Drawable icon = null;
        if( !drawableIconMap.isEmpty()) {
            icon = drawableIconMap.get(resDrawableIconID);
        }
        if( icon == null ) {
            icon = context.getResources().getDrawable(resDrawableIconID);
            drawableIconMap.put( resDrawableIconID, icon );
        }
        addItem( icon, tokenName, symbol, contract, blockNumber, quantity, bAdd );
    }

    public void addItem( Drawable icon, String tokenName, String symbol, String contract, long blockNumber, BigInteger quantity, boolean bAdd) {
        // Log.d( LOG_TAG, String.format( "addItem %s icon is %s", tokenName, icon == null ? "null" : "not null" ));

        ListViewItem item = viewItemMap.get(contract);
        if (item == null) {
            item = new ListViewItem( new TokenItemData() );
        }

        item.item.quantity = "0";
        item.iconDrawable = icon;
        item.item.tokenName = tokenName;
        item.item.symbol = symbol;
        item.item.contract = contract;
        item.item.blockNumber = blockNumber;
        if (bAdd) {
            item.item.quantity = new BigInteger(item.item.quantity).add(quantity).toString();
        } else {
            item.item.quantity = new BigInteger(item.item.quantity).subtract(quantity).toString();
        }
        lastBlockNumber = Math.max( lastBlockNumber, blockNumber );
        viewItemMap.put(contract, item);
    }

    Runnable RealmDataUpdate = new Runnable(){

        @Override
        public void run() {
            try ( Realm realm = Realm.getInstance( realmConfig )) {
                Log.d( LOG_TAG,"RealmDataUpdate count " + viewItemMap.size());

                Set<String> keys = viewItemMap.keySet();
                for( String key : keys ) {
                    ListViewItem item = viewItemMap.get( key );

                    realm.beginTransaction();
                    TokenItemData tokenItemData = realm.where(TokenItemData.class).equalTo("contract", item.item.contract).findFirst();
                    if( tokenItemData == null ) {
                        tokenItemData = realm.createObject(TokenItemData.class, item.item.contract);
                    }
                    tokenItemData.symbol    = item.item.symbol;
                    tokenItemData.tokenName = item.item.tokenName;
                    tokenItemData.quantity  = item.item.quantity;
                    tokenItemData.blockNumber = item.item.blockNumber;
                    realm.commitTransaction();
                }
            }
        }
    };
}
