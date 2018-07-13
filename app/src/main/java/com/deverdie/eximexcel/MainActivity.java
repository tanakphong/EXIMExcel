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

import java.io.IOException;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

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
            InputStream is = am.open("months.xlsx");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet s = wb.getSheet(0);
            int row = s.getRows();
            int col = s.getColumns();
            StringBuilder stringBuilder = new StringBuilder();
            for (int r = 0; r < row; r++) {
                for (int c = 0; c < col; c++) {
                    Cell cell = s.getCell(c, r);
                    stringBuilder.append(cell.getContents());
                }
            }
            tvResult.setText(stringBuilder.toString());
        } catch (IOException e) {
            Log.e(TAG, "dlg: "+ e.toString());
        } catch (BiffException e) {
            Log.e(TAG, "dlg: "+ e.toString());
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
