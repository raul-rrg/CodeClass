package com.codeclass.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.ui.AppNavGraph
import com.codeclass.app.ui.theme.CodeClassTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Instancia del DataStore para leer el token guardado
        val tokenDataStore = TokenDataStore(applicationContext)

        setContent {
            CodeClassTheme {
                val navController = rememberNavController()

                // Observa el token — si existe el usuario está autenticado
                val token by tokenDataStore.token.collectAsState(initial = null)
                val role  by tokenDataStore.role.collectAsState(initial = null)
                val isAuthenticated = token != null
                val isTeacher = role?.lowercase() == "teacher"

                AppNavGraph(
                    navController   = navController,
                    isAuthenticated = isAuthenticated,
                    isTeacher       = isTeacher,
                )
            }
        }
    }
}
