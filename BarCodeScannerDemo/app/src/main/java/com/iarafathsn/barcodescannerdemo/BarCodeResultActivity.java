package com.iarafathsn.barcodescannerdemo;

import static com.iarafathsn.barcodescannerdemo.util.CommonUtil.ROW_BARCODE;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class BarCodeResultActivity extends AppCompatActivity {
    private TextView tvRowString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_result);

        tvRowString = findViewById(R.id.tvRowString);

        setContentData();
    }

    private void setContentData() {
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String rowBarcode = intent.getStringExtra(ROW_BARCODE);
        tvRowString.setText("Barcode row data: " + rowBarcode);
    }
}