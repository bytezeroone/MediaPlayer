package com.bytezeroone.mediaplayer.ui.media_player

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bytezeroone.mediaplayer.util.UiEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MediaPlayerScreen(
    playerButtonSize: Dp = 54.dp,
    onEvent: (MediaPlayerEvent) -> Unit,
    viewModel: MediaPlayerViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect() { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        event.message
                    )
                }
                else -> Unit
            }
        }
    }

    val flag = viewModel.audioFlag
    val audioFlag = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val audioFinish = viewModel.audioFinish

    var sliderPosition by remember {
        mutableStateOf(2000f)
    }
    val valueRange by remember {
        mutableStateOf(viewModel.valueRange)
    }
    val colour1 by remember {
        viewModel.buttonColor1
    }
    val colour2 by remember {
        viewModel.buttonColor2
    }
    val colourDefault by remember {
        viewModel.buttonColorDefault
    }

    val colors = listOf(
        viewModel.buttonColor1.value,
        viewModel.buttonColor2.value,
        viewModel.buttonColorDefault.value
    )

    var current by remember { mutableStateOf(colors[2]) }
    var current2 by remember { mutableStateOf(colors[2]) }
    Scaffold(
        scaffoldState = scaffoldState
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    imageVector = if (!audioFinish.value) {
                        if (flag) {
                            Icons.Filled.PlayCircleFilled
                        } else {
                            Icons.Filled.PauseCircleFilled
                        }
                    } else {
                        Icons.Filled.PlayCircleFilled
                    },
                    contentDescription = "Play / Pause Icon",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(playerButtonSize)
                        .semantics { role = Role.Button }
                        .clickable {
                            if (audioFlag.value) {
                                onEvent(MediaPlayerEvent.OnPlayButtonClick)
                                scope.launch {
                                    delay(100)
                                    viewModel.getMediaDuration()
                                }
                                audioFlag.value = false
                            } else {
                                audioFlag.value = true
                                onEvent(MediaPlayerEvent.OnPlayButtonClick)
                            }
                        }
                )

            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Crossfade(
                    targetState = current,
                    animationSpec = tween(durationMillis = sliderPosition.toInt())
                ) { color ->
                    Button(
                        onClick = {
                            onEvent(MediaPlayerEvent.OnSong1Click)
                            current = colour1
                            current2 = colourDefault
                        },
                        colors = ButtonDefaults.buttonColors(color),
                    )
                    {
                        Text(text = "Song 1")
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Crossfade(
                    targetState = current2,
                    animationSpec = tween(durationMillis = sliderPosition.toInt())
                ) { color2 ->
                    Button(
                        onClick = {
                            onEvent(MediaPlayerEvent.OnSong2Click)
                            current2 = colour2
                            current = colourDefault
                        },
                        colors = ButtonDefaults.buttonColors(color2),
                    ) {
                        Text(text = "Song 2")
                    }
                }

            }
            Slider(
                value = sliderPosition,
                valueRange = valueRange,
                steps = 7,
                onValueChange = {
                    sliderPosition = it
                },
            )
        }
    }
}