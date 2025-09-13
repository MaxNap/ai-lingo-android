package com.ailingo.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


data class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Home", Icons.Outlined.Home),
        NavItem("Learn", Icons.Outlined.MenuBook),
        NavItem("Studio", Icons.Outlined.Extension),
        NavItem("Profile", Icons.Outlined.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0E2442),
                    selectedTextColor = Color(0xFF0E2442),
                    indicatorColor = Color(0x141E78FF)
                )
            )
        }
    }
}
