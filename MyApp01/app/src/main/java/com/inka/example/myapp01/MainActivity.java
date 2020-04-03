package com.inka.example.myapp01;

import android.Manifest;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.inka.example.myapp01.ui.qrcode.CaptureForm;
import com.inka.example.myapp01.ui.setting.SettingsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MyApp01";

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;

    String permission_list [] = {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long sTime = System.currentTimeMillis();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setCaptureActivity(CaptureForm.class);
                intentIntegrator.setBeepEnabled(false); //바코드 인식시 소리
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt( MainActivity.this.getResources().getString( R.string.qrcode_prompt_text));
                intentIntegrator.initiateScan();
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                 */
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_qrcreate, R.id.nav_mqtt, R.id.nav_ethereum)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        checkPermission();

        try {
            Thread.sleep(3000 - ( System.currentTimeMillis() - sTime ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    long backKeyPressedTime; //앱종료 위한 백버튼 누른시간

    @Override
    public void onBackPressed() {
        if( drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer( GravityCompat.START);
        } else {
            //1번째 백버튼 클릭
            if(System.currentTimeMillis()>backKeyPressedTime+2000){
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(this, getString(R.string.APP_CLOSE_BACK_BUTTON), Toast.LENGTH_SHORT).show();

            } else {
                finish();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if( item.getItemId() == R.id.action_settings ) {
            startActivity( new Intent( this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater flater = getMenuInflater();
        flater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult( requestCode, resultCode, data );
        if( result != null ) {
            if( result.getContents() == null ) {
                Toast.makeText( this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText( this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult( requestCode, resultCode, data );
        }
    }



    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);
            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == 0 ) {
            for( int i = 0; i < grantResults.length; i++ ) {
                if( grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText( getApplicationContext(), permissions[i] + " 권한설정이 필요합니다.", Toast.LENGTH_LONG ).show();
                    finish();
                }
            }
        }
    }
}
