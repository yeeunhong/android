package com.inka.example.myapp01.ui.mqtt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inka.example.myapp01.R;
import com.inka.example.myapp01.ui.MyFragment;
import com.inka.example.myapp01.ui.slideshow.SlideshowViewModel;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

public class MqttFragment extends Fragment implements IMqttActionListener, IMqttMessageListener, View.OnClickListener {

    private static final String LOG_TAG = "MQTT";
    private static MqttAndroidClient mqttAndroidClient;
    private MqttViewModel viewModel;

    private final String TOPIC = "inka";
    private EditText mqttText = null;
    private ListView mqttList = null;
    private ArrayList mqttListData = new ArrayList();
    private ArrayAdapter mqttListAdapter = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(MqttViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mqtt, container, false);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String serverAddr = pref.getString( "chatting_server_addr", "127.0.0.1:1883" );

        if( mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {
            mqttAndroidClient = new MqttAndroidClient(this.getContext(), "tcp://" + serverAddr, MqttClient.generateClientId());
        }
        try {
            IMqttToken token = mqttAndroidClient.connect(getMqttConnectionOption());
            token.setActionCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mqttText = (EditText) root.findViewById(R.id.edit_mqttText);
        mqttList = (ListView) root.findViewById(R.id.lst_mqttList);
        if(mqttList!=null) {
            mqttList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL); // 자동으로 스크롤를 제일 마지막으로 옮긴다.
            mqttList.setStackFromBottom(true);                                  // 추가 항목을 아래에서 부터 채운다.

            mqttListAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mqttListData ) ;
            mqttList.setAdapter( mqttListAdapter );
        }

        int btnIDs[] = {R.id.btn_mqttSend };
        for( int i = 0; i < btnIDs.length; i++ ) {
            Button btn = ( Button ) root.findViewById( btnIDs[i] );
            if( btn != null ) {
                btn.setOnClickListener( this );
            }
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        if( mqttAndroidClient != null ) {
            try {
                mqttAndroidClient.disconnect();
                mqttAndroidClient.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mqttAndroidClient = null;
        }
        super.onDestroyView();
    }

    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setWill("aaa", "I am going offline".getBytes(), 1, true);
        return mqttConnectOptions;
    }

    private DisconnectedBufferOptions getDisconnectedBufferOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(true);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        mqttAndroidClient.setBufferOpts( getDisconnectedBufferOptions());   // 연결에 성공

        Toast.makeText( getActivity(), "Connected", Toast.LENGTH_LONG).show();
        Log.e( LOG_TAG, "Success connection " );
        try {
            mqttAndroidClient.unsubscribe(TOPIC);
            mqttAndroidClient.subscribe(TOPIC, 0, this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.e( LOG_TAG, "Failure : " + exception.toString());
        Toast.makeText( getActivity(), "Failure : " + exception.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String strArrived = new String( message.getPayload());

        Log.d( LOG_TAG, String.format( "arrived : [%s] %s", topic, strArrived ));
        mqttListData.add( strArrived );
        getActivity().runOnUiThread( new Runnable(){
            @Override
            public void run() {
                mqttListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch( v.getId()) {
            case R.id.btn_mqttSend : onSendMessage(); break;
        }
    }

    private void onSendMessage() {
        if( mqttAndroidClient == null ) return;
        try {
            mqttAndroidClient.publish( TOPIC, mqttText.getText().toString().getBytes(), 0, false );
            mqttText.getText().clear();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
