package com.bytezeroone.mediaplayer.ui.media_player

sealed class MediaPlayerEvent {
    object OnPlayButtonClick: MediaPlayerEvent()
    object OnPauseButtonClick: MediaPlayerEvent()
    object OnSong1Click: MediaPlayerEvent()
    object OnSong2Click: MediaPlayerEvent()
    object OnSliderDrag: MediaPlayerEvent()
}