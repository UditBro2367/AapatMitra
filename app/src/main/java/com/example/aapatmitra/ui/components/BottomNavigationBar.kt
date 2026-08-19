package com.example.aapatmitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.aapatmitra.model.Language
import com.example.aapatmitra.model.Translations
import com.example.aapatmitra.ui.theme.CrimsonRed

@Composable
fun BottomNavigationBar(
    currentLang: Language,
    activeNavTab: String,
    onTabSelected: (String) -> Unit
) {
    val t = Translations.get(currentLang)

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bottom_nav_bar"),
        containerColor = Color(0xFF0F172A),
        contentColor = Color(0xFF94A3B8),
        tonalElevation = 8.dp
    ) {
        // Tab 1: Dispatch / Emergency SOS
        NavigationBarItem(
            selected = activeNavTab == "main",
            onClick = { onTabSelected("main") },
            icon = {
                Icon(
                    imageVector = if (activeNavTab == "main") Icons.Filled.LocalHospital else Icons.Outlined.LocalHospital,
                    contentDescription = t.navDispatch
                )
            },
            label = { Text(text = t.navDispatch) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = CrimsonRed,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("nav_item_dispatch")
        )

        // Tab 2: Family Circle Portal
        NavigationBarItem(
            selected = activeNavTab == "family",
            onClick = { onTabSelected("family") },
            icon = {
                Icon(
                    imageVector = if (activeNavTab == "family") Icons.Filled.People else Icons.Outlined.People,
                    contentDescription = t.navFamily
                )
            },
            label = { Text(text = t.navFamily) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color(0xFF3B82F6),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("nav_item_family")
        )

        // Tab 3: Health Vault / Profile
        NavigationBarItem(
            selected = activeNavTab == "profile",
            onClick = { onTabSelected("profile") },
            icon = {
                Icon(
                    imageVector = if (activeNavTab == "profile") Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                    contentDescription = t.navProfile
                )
            },
            label = { Text(text = t.navProfile) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color(0xFF10B981),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}
