package com.example.viktor_camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.viktor_camera.databinding.ActivityMainBinding
import com.foreveryone.knowing.base.BaseActivity
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RestrictedApi")
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(applicationContext) }
    private lateinit var cameraProvider: ProcessCameraProvider

    private val videoCapture: VideoCapture by lazy {
        VideoCapture.Builder()
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }
    private val contextExecutor by lazy { ContextCompat.getMainExecutor(applicationContext) }

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var isRecording = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (allPermissionsGranted()) {
            setupCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.rotateBtn.setOnClickListener{
            lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            try{
                bindPreview()
            } catch (e : Exception){
                e.printStackTrace()
            }
        }
        binding.captureBtn.setOnClickListener{
            if(!isRecording) {
                isRecording = true
                recordVideo()
            }
            else {
                isRecording = false
                videoCapture.stopRecording()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider.shutdown()
    }

    private fun setupCamera() {
        val cameraProvider = cameraProviderFuture.get()
        cameraProviderFuture.addListener(Runnable {
            this.cameraProvider = cameraProvider
            bindPreview()
        }, contextExecutor)
    }

    private fun bindPreview() {
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val preview: Preview = Preview.Builder()
            .build()
            .apply {
                this.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

        cameraProvider.unbindAll()
        try {
            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, videoCapture)
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            setupCamera()
        } else {
            Toast.makeText(
                this,
                "Permissions not granted by the user.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun recordVideo() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val file = getOutputDirectory(this)
            val output = VideoCapture.OutputFileOptions.Builder(file).build()
            videoCapture.startRecording(
                output,
                contextExecutor,
                object : VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                        Toast.makeText(
                            applicationContext,
                            "Video has been saved successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("123","저장됨 outputFileResults.savedUri: ${outputFileResults.savedUri}")
                    }

                    override fun onError(
                        videoCaptureError: Int,
                        message: String,
                        cause: Throwable?
                    ) {
                        Toast.makeText(
                            applicationContext,
                            "Video has been saved Failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("CameraX Log", message)
                    }
                }
            )
        }
    }

    private fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val fileName = getFileName()
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val file = if (mediaDir != null && mediaDir.exists()) mediaDir
        else filesDir
        return File(file, "$fileName.mp4")
    }

    private fun getFileName(): String {
        val now = Date(System.currentTimeMillis())
        val simpleDate = SimpleDateFormat("yyyyMMdd_hhmmss")
        return simpleDate.format(now)
    }
}