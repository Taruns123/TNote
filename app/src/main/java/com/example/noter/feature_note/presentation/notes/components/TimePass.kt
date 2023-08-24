package com.example.noter.feature_note.presentation.notes.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePass(){

val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()
Scaffold(
snackbarHost = { SnackbarHost(snackbarHostState) },
floatingActionButton = {
    var clickCount by remember { mutableStateOf(0) }
    ExtendedFloatingActionButton(
        onClick = {
            // show snackbar as a suspend function
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Snackbar # ${++clickCount}"
                )
            }
        }
    ) { Text("Show snackbar") }
},
content = { innerPadding ->
    Text(
        text = "Body content",
        modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
    )
}
)
}
