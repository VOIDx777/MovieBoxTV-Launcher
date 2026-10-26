package com.void.movieboxtv.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.void.movieboxtv.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerView = PlayerView(this).apply {
            useController = true
            keepScreenOn = true
        }
        setContentView(playerView)

        val subjectId = intent.getStringExtra("SUBJECT_ID") ?: return
        resolveAndPlay(subjectId)
    }

    private fun resolveAndPlay(id: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val playInfo = RetrofitClient.api.getStreamInfo(id)
                val streamUrl = playInfo.getResolvedUrl()

                if (streamUrl.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PlayerActivity, "Stream URL not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    initPlayer(streamUrl, playInfo.headers)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PlayerActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initPlayer(url: String, headers: Map<String, String>?) {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory().apply {
            setAllowCrossProtocolRedirects(true)
            headers?.forEach { (key, value) ->
                setDefaultRequestProperty(key, value)
            }
        }

        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(httpDataSourceFactory)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().also { exo ->
                playerView.player = exo
                val mediaItem = MediaItem.fromUri(Uri.parse(url))
                exo.setMediaItem(mediaItem)
                exo.prepare()
                exo.playWhenReady = true
            }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
