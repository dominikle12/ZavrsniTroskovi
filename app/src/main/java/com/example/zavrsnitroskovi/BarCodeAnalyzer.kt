package com.example.zavrsnitroskovi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarCodeAnalyzer(private val context: Context) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

            // Process image searching for barcodes
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_PDF417,
                                    Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if(barcodes.isNotEmpty()){
                        val firstBarcode = barcodes.first()
                        Log.d("BARKOD", firstBarcode.rawValue.toString())
                        val intent = Intent(context, ScannedReceiptActivity::class.java)
                        intent.putExtra("barcodeData", firstBarcode.rawValue)
                        context.startActivity(intent)
                        
                    }
                }
                .addOnFailureListener { image.close() }

        }
        image.close()
    }
}

