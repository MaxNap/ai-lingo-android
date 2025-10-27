package com.ailingo.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

// Alert Template — To be expanded and modified for each button
/*
    Selection/Switch?
*/
@Composable
fun AlertDialog(
    onDismiss:() -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { /*TODO - Check*/ },
        modifier = Modifier.height(250.dp),

        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // If we want to include an icon ↓
                //Icon(imageVector = Icons.Default.Info, contentDescription = "Privacy" )
                Text(
                    text = "Edit Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },

        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { /*TODO - Specific for each button*/ },
                    modifier = Modifier
                        .width(180.dp)
                        .padding(10.dp)
                    ) {
                    Text(text = "Confirm Change")
                }
            }
        }
    )
}