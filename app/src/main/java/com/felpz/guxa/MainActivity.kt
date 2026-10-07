package com.felpz.guxa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.felpz.guxa.auth.GoogleAuthManager
import com.felpz.guxa.auth.GuxaUser
import com.felpz.guxa.notifications.NotificationChannels
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationChannels.create(this)
        setContent { GuxaTheme { GuxaApp() } }
    }
}

@Composable
private fun GuxaApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = remember { GoogleAuthManager(context) }
    var user by remember { mutableStateOf<GuxaUser?>(null) }
    var authError by remember { mutableStateOf<String?>(null) }
    var tab by remember { mutableStateOf(0) }

    if (user == null) {
        LoginScreen(
            error = authError,
            onGoogle = {
                authError = null
                scope.launch {
                    auth.signIn(context)
                        .onSuccess { user = it }
                        .onFailure { authError = it.message ?: "Falha no login." }
                }
            }
        )
        return
    }

    Scaffold(
        containerColor = Color(0xFF0B0C0F),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = Color(0xFF121419)
            ) {
                NavigationBarItem(tab == 0, { tab = 0 }, icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Chats") })
                NavigationBarItem(tab == 1, { tab = 1 }, icon = { Icon(Icons.Default.People, null) }, label = { Text("Servidores") })
                NavigationBarItem(tab == 2, { tab = 2 }, icon = { Icon(Icons.Default.Notifications, null) }, label = { Text("Avisos") })
                NavigationBarItem(tab == 3, { tab = 3 }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Config") })
            }
        }
    ) { padding ->
        when (tab) {
            0 -> HomeScreen(Modifier.padding(padding), user!!)
            1 -> ServersScreen(Modifier.padding(padding))
            2 -> NotificationsScreen(Modifier.padding(padding))
            else -> SettingsScreen(Modifier.padding(padding), user!!) { user = null }
        }
    }
}

@Composable
private fun LoginScreen(error: String?, onGoogle: () -> Unit) {
    Surface(Modifier.fillMaxSize(), color = Color(0xFF0B0C0F)) {
        Column(
            Modifier.fillMaxSize().padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Guxa", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Text("Comunicação sem complicação.", color = Color(0xFF9DA3AE))
            Spacer(Modifier.height(36.dp))
            Button(onClick = onGoogle, Modifier.fillMaxWidth()) {
                Text("Continuar com Google")
            }
            if (error != null) {
                Spacer(Modifier.height(16.dp))
                Text(error, color = Color(0xFFFF8A8A))
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Login Google preparado. Falta somente configurar o OAuth.",
                color = Color(0xFF737985)
            )
        }
    }
}

@Composable
private fun HomeScreen(modifier: Modifier, user: GuxaUser) {
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Olá, " + (user.displayName ?: "você"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("Guxa", color = Color(0xFF8D93A0))
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, "Notificações")
            }
        }

        Spacer(Modifier.height(18.dp))
        Text("Atividade", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        ActivityCard("💬", "João mencionou você", "#desenvolvimento")
        ActivityCard("📞", "Ana iniciou uma chamada", "Sala de vídeo")
        ActivityCard("📢", "DevForge publicou um anúncio", "Há 4 minutos")

        Spacer(Modifier.height(18.dp))
        CallPreview()
    }
}

@Composable
private fun ActivityCard(icon: String, title: String, subtitle: String) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15181E))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color(0xFF8D93A0))
            }
        }
    }
}

@Composable
private fun CallPreview() {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15181E))) {
        Column(Modifier.padding(18.dp)) {
            Text("Sala de voz", fontWeight = FontWeight.Bold)
            Text("3 pessoas online", color = Color(0xFF8D93A0))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}) {
                    Icon(Icons.Default.Call, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Entrar")
                }
                OutlinedButton(onClick = {}) {
                    Icon(Icons.Default.ScreenShare, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Tela")
                }
            }
        }
    }
}

@Composable
private fun ServersScreen(modifier: Modifier) {
    val servers = listOf("DevForge", "Roblox", "Guxa Community", "Gaming")
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Text("Servidores", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(servers) { server ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable {},
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF15181E))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(44.dp).background(Color(0xFF252A33), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(server.take(1), fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(server, fontWeight = FontWeight.SemiBold)
                            Text("Canais • chamadas • comunidade", color = Color(0xFF8D93A0))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsScreen(modifier: Modifier) {
    val notifications = listOf(
        "João mencionou você em #desenvolvimento",
        "Ana iniciou uma chamada de vídeo",
        "DevForge publicou um novo anúncio",
        "Você recebeu um novo convite"
    )

    LazyColumn(modifier.fillMaxSize().padding(18.dp)) {
        item {
            Text("Notificações", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
        }
        items(notifications) {
            Card(
                Modifier.fillMaxWidth().padding(vertical = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF15181E))
            ) {
                Text(it, Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun SettingsScreen(modifier: Modifier, user: GuxaUser, onLogout: () -> Unit) {
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Text("Configurações", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        Text(user.email ?: "Conta Google")
        Text(user.displayName ?: "", color = Color(0xFF8D93A0))
        Spacer(Modifier.height(24.dp))
        Text("Notificações: menções, mensagens, chamadas e servidores")
        Spacer(Modifier.height(12.dp))
        Text("Calls: áudio, vídeo e compartilhamento de tela", color = Color(0xFF8D93A0))
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onLogout) { Text("Sair") }
    }
}

@Composable
private fun GuxaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Color(0xFF8B7CFF),
            secondary = Color(0xFF6C63FF),
            background = Color(0xFF0B0C0F),
            surface = Color(0xFF121419)
        ),
        content = content
    )
}
