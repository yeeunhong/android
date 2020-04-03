package com.inka.example.myapp01.ui.qrcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.inka.example.myapp01.R;
import com.inka.example.myapp01.ui.MyFragment;
import com.inka.example.myapp01.ui.home.HomeViewModel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class QrCreateFragment extends MyFragment implements View.OnClickListener {

    ImageView imageQrCode   = null;
    EditText  qrcodeEdit    = null;
    Bitmap    qrcodeBitmap  = null;
    Button    btnCreateQrcode = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_qrcreate, container, false);

        imageQrCode = ( ImageView ) root.findViewById( R.id.imageQrCode);
        qrcodeEdit  = ( EditText ) root.findViewById( R.id.edit_qrcodeText);

        int btnIDs[] = { R.id.btn_createQrcode, R.id.btn_saveQrcode };
        for( int i = 0; i < btnIDs.length; i++ ) {
            Button btn = ( Button ) root.findViewById( btnIDs[i] );
            if( btn != null ) {
                btn.setOnClickListener( this );
                btnCreateQrcode = btn;
            }
        }
        setVisibilityFloatingActionButton(View.INVISIBLE);
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_createQrcode:
                onCreateQrcode();
                break;
            case R.id.btn_saveQrcode:
                onSaveQrcode();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onSaveQrcode() {
        if( qrcodeBitmap != null ) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String FileName = "IMG_" + timeStamp + ".png";
            String uri = MediaStore.Images.Media.insertImage( QrCreateFragment.this.getContext().getContentResolver(), qrcodeBitmap, FileName, "QRCode" );
            if( uri != null ) {
                Toast.makeText( getContext(), "saved : " + uri, Toast.LENGTH_LONG ).show();
            }
        }
    }

    private void onCreateQrcode() {
        if( imageQrCode != null ) {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                String text = new String(qrcodeEdit.getText().toString().getBytes("UTF-8"), "ISO-8859-1");
                int imgSize = (btnCreateQrcode.getWidth() + text.length() * 5) / 2;
                BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, imgSize, imgSize);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                qrcodeBitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageQrCode.setImageBitmap( qrcodeBitmap );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
