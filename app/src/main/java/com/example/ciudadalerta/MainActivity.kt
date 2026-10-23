package com.example.ciudadalerta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ciudadalerta.ui.theme.CiudadalertaTheme
import com.example.ciudadalerta.presentation.navigation.AppNavigation
import com.example.ciudadalerta.presentation.permissions.PermissionGate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CiudadalertaTheme {
                PermissionGate {
                    AppNavigation()
                }
            }
        }
    }
}


