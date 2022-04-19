package com.bytezeroone.mediaplayer.ui.media_player

import android.app.Application
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytezeroone.mediaplayer.R
import com.bytezeroone.mediaplayer.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaPlayerViewModel
@Inject constructor(
    private val application: Application,
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    private var songList = listOf(
        R.raw.audio1,
        R.raw.audio2
    )
    private var songIndex = 0

    var valueRange by mutableStateOf(2000f..10000f)

    var mediaPlayer: MediaPlayer? = null

    private var currentDuration: CountDownTimer? = null
    private var currentMinutes by mutableStateOf(0)

    var songFinish by mutableStateOf(false)
        private set

    val job1 = viewModelScope
    val job2 = viewModelScope

    var audioFlag by mutableStateOf(true)
        private set

    fun getMediaDuration() {
        try {
            currentDuration = object : CountDownTimer(mediaPlayer!!.duration.toLong(), 500) {
                override fun onTick(milliSec: Long) {
                    currentMinutes = mediaPlayer!!.currentPosition
                }

                override fun onFinish() {
                    songFinish = true
                    when (songFinish) {
                        true -> {
                            audioFlag = true
                        }
                        else -> Unit
                    }
                    when (songIndex) {
                        0 -> {
                            job1.launch {
                                song2Choose(songList[1], color = buttonColor2.value)
                                songStart()
                                buttonColor2
                                job2.cancel()
                            }
                        }
                        1 -> {
                            job2.launch {
                                song1Choose(songList[0], color = buttonColor1.value)
                                songStart()
                                buttonColor2
                                songIndex = 1
                                job1.cancel()
                            }
                        }
                    }
                }

            }

            currentDuration!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onEvent(event: MediaPlayerEvent) {
        when (event) {
            is MediaPlayerEvent.OnPlayButtonClick -> {
                when (mediaPlayer) {
                    null -> viewModelScope.launch {
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "Choose song"
                            )
                        )
                    }
                    else -> {
                        when (mediaPlayer!!.isPlaying) {
                            true -> {
                                songPause()
                                job1.cancel()
                                job2.cancel()
                            }
                            else -> {
                                songStart()
                                getMediaDuration()
                            }
                        }
                    }
                }

            }
            is MediaPlayerEvent.OnSong1Click -> {
                    song1Choose(
                        songList[songIndex],
                        color = buttonColor1.value
                    )
            }
            MediaPlayerEvent.OnSong2Click -> {
                song2Choose(
                    songList[songIndex],
                    color = buttonColor2.value
                )
            }
            is MediaPlayerEvent.OnPauseButtonClick -> {
                //songPause()
            }
            else -> Unit
        }
    }

    var audioFinish = mutableStateOf(false)
        private set

    var buttonColorDefault = mutableStateOf(Color.Gray)
        private set

    var buttonColor1 = mutableStateOf(Color.Yellow)
        private set

    var buttonColor2 = mutableStateOf(Color.Green)
        private set

    private fun song1Choose(id: Int, color: Color) {
        if (mediaPlayer != null) {
            mediaPlayer?.pause()
        }
        songIndex = 0
        mediaPlayer = MediaPlayer.create(application.applicationContext, id)
        audioFlag = true
        when (buttonColor1.value) {
            Color.Gray -> {
                buttonColor1.value = color
                buttonColor2.value = Color.Gray
            }
            Color.Yellow -> Unit
        }
        Log.d("asdsad", "${buttonColor1.value}")
    }

    private fun song2Choose(id: Int, color: Color) {
        if (mediaPlayer != null) {
            mediaPlayer?.pause()
        }
        songIndex = 1
        mediaPlayer = MediaPlayer.create(application.applicationContext, id)
        audioFlag = true
        when (buttonColor2.value) {
            Color.Gray -> {
                buttonColor2.value = color
                buttonColor1.value = Color.Gray
            }
            Color.Yellow -> Unit
        }
        Log.d("asdsad", "${buttonColor2.value}")
    }

    private fun songStart() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer?.stop()
            mediaPlayer?.prepare()
            audioFlag = true
        }
        try {
            mediaPlayer!!.start()
            audioFlag = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        getMediaDuration()
    }


    private fun songPause() {
        try {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer?.pause()
                audioFlag = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}