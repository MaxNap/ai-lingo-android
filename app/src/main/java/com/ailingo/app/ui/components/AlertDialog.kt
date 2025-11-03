package com.ailingo.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment


// Alert Template — To be expanded and modified for each button
/*
    Selection/Switch?
*/
@Composable
fun AlertDialog(
    title: String,
    body: @Composable () -> Unit,
    confirmText: String = "OK",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
//    onDismiss:() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                body()
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, modifier = Modifier.padding(8.dp)) {
                Text(confirmText)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, modifier = Modifier.padding(8.dp)) {
                Text(dismissText)
            }
        }
    )

//    androidx.compose.material3.AlertDialog(
//        onDismissRequest = onDismiss,
//        confirmButton = { /*TODO - Check*/ },
//        modifier = Modifier.height(250.dp),
//
//        title = {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // If we want to include an icon ↓
//                //Icon(imageVector = Icons.Default.Info, contentDescription = "Privacy" )
//                Text(
//                    text = "Edit Password",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp
//                )
//            }
//        },
//
//        text = {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Button(
//                    onClick = { /*TODO - Specific for each button*/ },
//                    modifier = Modifier
//                        .width(180.dp)
//                        .padding(10.dp)
//                    ) {
//                    Text(text = "Confirm Change")
//                }
//            }
//        }
//    )
}