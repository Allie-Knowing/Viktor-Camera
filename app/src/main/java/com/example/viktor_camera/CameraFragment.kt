package com.example.viktor_camera

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.viktor_camera.base.BaseFragment
import com.example.viktor_camera.databinding.FragmentCameraBinding

@SuppressLint("RestrictedApi")
class CameraFragment : BaseFragment<FragmentCameraBinding>(R.layout.fragment_camera) {

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(requireContext()) }
    private lateinit var cameraProvider: ProcessCameraProvider

    private val videoCapture: VideoCapture by lazy {
        VideoCapture.Builder()
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setTargetRotation(Surface.ROTATION_0)
            .setCameraSelector(cameraSelector)
            .build()
    }

    private val cameraSelector: CameraSelector by lazy {
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    }
    private val contextExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}