package com.bytezeroone.mediaplayer.util

import android.os.Message

sealed class UiEvent {
    data class ShowSnackBar(
        val message: String
    ): UiEvent()
}
