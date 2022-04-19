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
    var songIndex = 0

    var valueRange by mutableStateOf(2000f..10000f)

    var mediaPlayer: MediaPlayer? = null

    private var currentDuration: CountDownTimer? = null
    private var currentMinutes by mutableStateOf(0)

    var songFinish by mutableStateOf(false)
        private set


    var audioFlag by mutableStateOf(true)
        private set


    var audioFinish = mutableStateOf(false)
        private set

    var buttonColorDefault = mutableStateOf(Color.Gray)
        private set

    var buttonColor1 = mutableStateOf(Color.Yellow)
        private set

    var buttonColor2 = mutableStateOf(Color.Green)
        private set

    fun getMediaDuration() {
        try {
            currentDuration = object : CountDownTimer(mediaPlayer!!.duration.toLong(), 500) {
                override fun onTick(milliSec: Long) {
                    currentMinutes = mediaPlayer!!.currentPosition
                }

                override fun onFinish() {
                    songFinish = true
                    //audioFlag = true
                    /*when (songFinish) {
                        true -> {
                            audioFlag = true
                        }
                        else -> Unit
                    }*/
                    /*when (songIndex) {
                        0 -> {
                                song2Choose(songList[1], color = buttonColor2.value)
                                songStart()
                                buttonColor2
                        }
                        1 -> {
                                song1Choose(songList[0], color = buttonColor1.value)
                                songStart()
                                buttonColor2
                                songIndex = 1
                        }
                    }*/
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
                    else -> Unit
                }
                when (mediaPlayer!!.isPlaying) {
                    true -> {
                        songPause()
                    }
                    else -> {
                        songStart()
                        //getMediaDuration()
                    }
                }

            }
            is MediaPlayerEvent.OnSong1Click -> {
                songStop()
                song1Choose(
                    songList[0],
                    color = buttonColor1.value
                )
            }
            MediaPlayerEvent.OnSong2Click -> {
                songStop()
                song2Choose(
                    songList[1],
                    color = buttonColor2.value
                )
            }
            is MediaPlayerEvent.OnPauseButtonClick -> {
                //songPause()
            }
            else -> Unit
        }
    }

    private fun song1Choose(id: Int, color: Color) {
        try {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer?.stop()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        mediaPlayer = MediaPlayer.create(application.applicationContext, id)
        audioFlag = true
        when (buttonColor1.value) {
            Color.Gray -> {
                buttonColor1.value = color
                buttonColor2.value = Color.Gray
            }
            Color.Yellow -> Unit
        }
        Log.d("asdsad", "$songIndex")
    }

    private fun song2Choose(id: Int, color: Color) {
        try {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer?.stop()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        mediaPlayer = MediaPlayer.create(application.applicationContext, id)
        songIndex = 1
        audioFlag = true
        when (buttonColor2.value) {
            Color.Gray -> {
                buttonColor2.value = color
                buttonColor1.value = Color.Gray
            }
            Color.Yellow -> Unit
        }
        Log.d("asdsad", "$songIndex")
    }

    private fun songStart() {
        if (mediaPlayer!!.isPlaying) {
            audioFlag = true
        }
        try {
            mediaPlayer!!.start()
            audioFlag = false
        } catch (e: NullPointerException) {
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
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun songStop() {
        try {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer?.stop()
                audioFlag = true

            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}