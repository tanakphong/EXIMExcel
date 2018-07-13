package com.deverdie.eximexcel;

import android.Manifest;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tv_result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AssetManager am = getAssets();
        try {
//            asci not support thai text
//            unicode new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-16LE")));text-unicode
//            unicode be new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-16")));text-unicode-be
//            utf8 new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));text-utf8
            InputStream is = am.open("text-unicode.txt");
            UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(is);
            UnicodeBOMInputStream.BOM cs = ubis.getBOM();
            Log.d("dlg", "getEncoding: " + cs.toString());
//            String charset;
//            switch (ubis.getBOM().toString()) {
//                case "UTF-8":
//                    charset = "UTF-8";
//                    break;
//                case "UTF-16 little-endian":
//                    charset = "UTF-16LE";
//                    break;
//                case "UTF-16 big-endian":
//                    charset = "UTF-16";
//                    break;
//                default:
//                    charset = "UTF-8";
//            }
            InputStreamReader isr = new InputStreamReader(ubis, ubis.getBOM().toString());

            BufferedReader reader = new BufferedReader(isr);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                try {
                    String[] lines = line.split(",", -1);
                    tvResult.setText(String.format("%s - %s", lines[0], lines[1]));
                } catch (Exception e) {
                    tvResult.setText("Error" + e.toString());
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(), response.getPermissionName() + " Granted.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), response.getPermissionName() + " Denied.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

}
