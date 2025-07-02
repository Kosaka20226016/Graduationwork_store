package jp.ac.nkc_cta09.graduationwork_store

import android.content.Intent
import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import jp.ac.nkc_cta09.graduationwork_store.BarcodeScannerHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)

        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(this, "カメラの権限が必要です", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    private var isScanned = false

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analysis = ImageAnalysis.Builder()
                .setTargetResolution(android.util.Size(1280, 720)) // ★ 解像度を上げる
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(ContextCompat.getMainExecutor(this),
                BarcodeScannerHelper { value ->
                    runOnUiThread {
                        if (!isScanned) {
                            isScanned = true // 1回だけ反応させる
                            Toast.makeText(this, "読み取り結果: $value", Toast.LENGTH_LONG).show()

                            val intent = Intent(this, DetailActivity::class.java)
                            intent.putExtra("barcode_value", value)
                            startActivity(intent)
                            finish()

                        }
                    }
                })

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)

        }, ContextCompat.getMainExecutor(this))
    }

}
