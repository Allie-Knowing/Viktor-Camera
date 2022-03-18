package com.example.viktor_camera

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.viktor_camera.base.BaseFragment
import com.example.viktor_camera.databinding.FragmentCameraBinding
import java.lang.Exception
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.camera.core.CameraX
import java.lang.IllegalStateException

@SuppressLint("RestrictedApi")
class CameraFragment : BaseFragment<FragmentCameraBinding>(R.layout.fragment_camera) {

    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(requireContext()) }
    private lateinit var cameraProvider: ProcessCameraProvider

    private val videoCapture: VideoCapture by lazy {
        VideoCapture.Builder()
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setTargetRotation(Surface.ROTATION_0)
//            .setCameraSelector(cameraSelector)
            .build()
    }

//    private val cameraSelector: CameraSelector by lazy {
//        CameraSelector.Builder().requireLensFacing(lensFacing).build()
//    }
    private val contextExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }

    private val requestPermissionsLauncher = registerForActivityResult(
        RequestPermission()
     ) { isGranted ->
        if(isGranted) setupCamera()
        else Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
    }

    private var lensFacing = CameraSelector.LENS_FACING_BACK

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermission()

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
    }

    private fun setupCamera() {
//        lensFacing = when {
//            hasBackCamera() -> CameraSelector.LENS_FACING_BACK
//            hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
//            else -> throw IllegalStateException("Back and front camera are unavailable")
//        }
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

    private fun requestPermission() {
        requestPermissionsLauncher.launch(Manifest.permission.CAMERA)
        requestPermissionsLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissionsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionsLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}