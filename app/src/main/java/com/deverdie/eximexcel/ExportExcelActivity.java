package com.deverdie.eximexcel;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExportExcelActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "dlg";
    private EditText etWrite;
    private Button btnWrite, btnRead;
    private TextView tvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_excel);

        checkPermission();

        etWrite = findViewById(R.id.et_write);
        btnWrite = findViewById(R.id.btn_write);
        btnRead = findViewById(R.id.btn_read);
        tvResult = findViewById(R.id.tv_result);

        btnWrite.setOnClickListener(this);
        btnRead.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_write:
                writeExcel();
                break;
            case R.id.btn_read:
                readExcel();
                break;
        }
    }

    private void writeExcel() {
        String extStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String packageName = getApplicationContext().getPackageName();
        String path = extStorage + "/Android/data/" + packageName;

        File fileOrFolder = new File(path);

        if (!fileOrFolder.exists()) {
            fileOrFolder.mkdirs();
        }

        try {

            WritableWorkbook workbook = Workbook.createWorkbook(new File(path + File.separator + "output.xls"));

            WritableSheet sheet = workbook.createSheet("First Sheet", 0);

            Label label = new Label(0, 0, etWrite.getText().toString());

            sheet.addCell(label);

            workbook.write();

            workbook.close();
        } catch (IOException e) {
            Log.e(TAG, "writeExcel: " + e.toString());
        } catch (WriteException e) {
            e.printStackTrace();
        }

    }

    private void readExcel() {
        String extStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String packageName = getApplicationContext().getPackageName();
        String path = extStorage + "/Android/data/" + packageName;

        try {
            Workbook workbook = Workbook.getWorkbook(new File(path + File.separator + "output.xls"));
            Sheet sheet = workbook.getSheet(0);
            Cell cell = sheet.getCell(0, 0);
            tvResult.setText(cell.getContents());
            workbook.close();
        } catch (IOException e) {
            Log.e(TAG, "dlg: " + e.toString());
        } catch (BiffException e) {
            Log.e(TAG, "dlg: " + e.toString());
            e.printStackTrace();
        }
    }

    private void checkPermission() {
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
