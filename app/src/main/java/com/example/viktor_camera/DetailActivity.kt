package com.example.viktor_camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.viktor_camera.databinding.ActivityDetailBinding
import com.foreveryone.knowing.base.BaseActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class DetailActivity : BaseActivity<ActivityDetailBinding>(R.layout.activity_detail) {
    private lateinit var exoPlayer: ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        val intent = intent
        val uri = intent.getStringExtra("uri")

        exoPlayer = ExoPlayer.Builder(this).build()
        binding.exoplayer.player = exoPlayer

        val factory: DataSource.Factory = DefaultDataSource.Factory(this)
        val mediaItem = MediaItem.fromUri(Uri.parse(uri))
        //var mediaSource: ProgressiveMediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(uri)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onStop() {
        super.onStop()

        binding.exoplayer.player = null
        exoPlayer.release()
    }

    private fun selectVideo(startForResult: ActivityResultLauncher<Intent>){
        val intent = Intent(Intent.ACTION_PICK)
        intent.apply {
            data = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            type = ""
        }
    }
}