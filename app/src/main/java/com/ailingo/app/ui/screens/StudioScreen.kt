package com.ailingo.app.ui.screens

import android.R.attr.titleTextStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ailingo.app.ui.screens.ChatViewModel
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.ailingo.app.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(vm: ChatViewModel = viewModel()) {

    val messages by vm.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("AI Studio")
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xE61AB8E2),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(8.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.role == "user"
                        ChatBubble(text = msg.content, isUser = isUser)
                    }
                }

                Row(modifier = Modifier.padding(8.dp)) {
                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Practice here...") }
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        if (input.isNotBlank()) {
                            vm.sendMessage(input)
                            input = ""
                        }
                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x99CB39C3)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    val bg = if (isUser) Color(0xFF1AB8E2) else MaterialTheme.colorScheme.secondary
    val align = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = align
    ) {
        Surface(
            color = bg,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

