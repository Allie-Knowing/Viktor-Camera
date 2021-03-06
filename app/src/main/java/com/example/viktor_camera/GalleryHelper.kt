package com.example.viktor_camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GalleryHelper(private val activity: Activity) {
    fun selectPhoto(startForResult: ActivityResultLauncher<Intent>) {
        val writePermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                0
            )
        }
        else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.apply {
                data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                type = "video/*"
            }
            startForResult.launch(intent)
        }
    }
}