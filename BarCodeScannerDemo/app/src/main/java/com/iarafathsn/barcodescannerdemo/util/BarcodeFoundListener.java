package com.iarafathsn.barcodescannerdemo.util;

import com.google.mlkit.vision.barcode.common.Barcode;

public interface BarcodeFoundListener {
    public void onValidBarcodeFound(Barcode barcode);
}
