package com.ailingo.app.ui.screens

import android.R.color.black
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ailingo.app.R

/* Profile Page:
        --
* */
@Composable
fun ProfileScreen() {
    val gradientBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF1AB8E2),
                Color(0xFFCB39C3),
                Color(0xFF1AB8E2)
            )
        )
    }
    val borderWidth = 4.dp

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        //
        Image(
            painter = painterResource(id = R.drawable.ailingo_logo), // Image is just a screenshot of the logo, so positioning is *slightly* off. Need to create correct image to replace
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(
                    BorderStroke(borderWidth, gradientBrush),
                    CircleShape
                )
                .padding(borderWidth)
        )
        Text("Name Here")
        Text("Level: 1")
        Spacer(modifier = Modifier.height(25.dp))
        PlaceholderGrid()
    }
}
@Composable
fun PlaceholderGrid() {
    val items = List(3) { "Item ${it + 1}" } // Placeholder items
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 3 columns
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.SpaceEvenly,
            contentPadding = PaddingValues(8.dp),
        ) {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Text(text = "Badge")
                    Image(
                        painter = painterResource(id = R.drawable.profile_temp),
                        contentDescription = "Profile Picture",
                    )
                }
            }
        }
    }}

@Preview
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
    PlaceholderGrid()
}
