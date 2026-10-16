package com.amix.tutor.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amix.tutor.ChatViewModel
import com.amix.tutor.R
import com.amix.tutor.data.ChatEntity
import com.amix.tutor.ui.theme.AmixBg
import com.amix.tutor.ui.theme.AmixLine
import com.amix.tutor.ui.theme.AmixMuted
import com.amix.tutor.ui.theme.AmixPanel
import com.amix.tutor.ui.theme.AmixPanel2
import com.amix.tutor.ui.theme.AmixText
import com.amix.tutor.ui.theme.AmixTheme
import java.util.Locale
import kotlinx.coroutines.launch

object Routes {
    const val CHAT = "chat"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val PRO = "pro"
    const val VOICE = "voice"
}

/** Язык выбирается в приложении (RU/EN), независимо от языка системы. */
@Composable
fun ProvideLanguage(lang: String, content: @Composable () -> Unit) {
    val base = LocalContext.current
    val localized = remember(lang, base) {
        val config = Configuration(base.resources.configuration)
        config.setLocale(Locale(lang))
        base.createConfigurationContext(config)
    }
    CompositionLocalProvider(LocalContext provides localized) { content() }
}

@Composable
fun AmixRoot(
    appVm: AppViewModel = hiltViewModel(),
    chatVm: ChatViewModel = hiltViewModel()
) {
    val lang by appVm.language.collectAsStateWithLifecycle()
    ProvideLanguage(lang) {
        AmixTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = AmixBg, contentColor = AmixText) {
                AmixShell(appVm, chatVm, lang)
            }
        }
    }
}

@Composable
private fun AmixShell(appVm: AppViewModel, chatVm: ChatViewModel, lang: String) {
    val nav = rememberNavController()
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: Routes.CHAT
    val chats by appVm.chats.collectAsStateWithLifecycle()

    fun go(target: String) {
        scope.launch { drawer.close() }
        nav.navigate(target) {
            launchSingleTop = true
            popUpTo(Routes.CHAT)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            DrawerContent(
                route = route,
                chats = chats,
                onClose = { scope.launch { drawer.close() } },
                onGo = { go(it) },
                onNewChat = {
                    chatVm.newChat()
                    go(Routes.CHAT)
                },
                onOpenChat = { id ->
                    chatVm.openChat(id)
                    go(Routes.CHAT)
                }
            )
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            BlackHoleBackground()
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .imePadding()
            ) {
                TopBar(
                    lang = lang,
                    onMenu = { scope.launch { drawer.open() } },
                    onLang = { appVm.toggleLanguage() },
                    onAccount = { /* вход появится на этапе «Регистрация» */ }
                )
                NavHost(
                    navController = nav,
                    startDestination = Routes.CHAT,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(Routes.CHAT) { ChatScreen(chatVm) }
                    composable(Routes.HISTORY) {
                        HistoryScreen(
                            chats = chats,
                            onOpen = { id ->
                                chatVm.openChat(id)
                                go(Routes.CHAT)
                            },
                            onClear = { chatVm.clearAllChats() }
                        )
                    }
                    composable(Routes.SETTINGS) {
                        SettingsScreen(
                            lang = lang,
                            onLang = { appVm.setLanguage(it) },
                            onWipe = { chatVm.clearAllChats() }
                        )
                    }
                    composable(Routes.PRO) { ProScreen() }
                    composable(Routes.VOICE) { VoiceScreen(onBack = { go(Routes.CHAT) }) }
                }
            }
        }
    }
}

@Composable
private fun TopBar(lang: String, onMenu: () -> Unit, onLang: () -> Unit, onAccount: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onMenu)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AmixLogo(40.dp)
            Spacer(Modifier.width(12.dp))
            Text("Amix", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.weight(1f))
        CircleButton(text = lang.uppercase(), onClick = onLang)
        Spacer(Modifier.width(8.dp))
        CircleButton(text = "☺", onClick = onAccount)
    }
}

@Composable
private fun CircleButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(AmixPanel2)
            .border(1.dp, AmixLine, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DrawerContent(
    route: String,
    chats: List<ChatEntity>,
    onClose: () -> Unit,
    onGo: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpenChat: (Long) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = AmixPanel,
        drawerContentColor = AmixText
    ) {
        Column(Modifier.fillMaxHeight().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AmixLogo(40.dp)
                Spacer(Modifier.width(12.dp))
                Text("Amix", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    "✕",
                    color = AmixMuted,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClose)
                        .padding(10.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onNewChat, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.new_chat), color = AmixText)
            }
            Spacer(Modifier.height(8.dp))
            DrawerItem("✧", stringResource(R.string.nav_chat), route == Routes.CHAT, null) { onGo(Routes.CHAT) }
            DrawerItem("＋", stringResource(R.string.new_chat), false, null, onNewChat)
            DrawerItem("◉", stringResource(R.string.nav_voice), route == Routes.VOICE, stringResource(R.string.voice_soon_short)) { onGo(Routes.VOICE) }
            DrawerItem("◷", stringResource(R.string.nav_history), route == Routes.HISTORY, null) { onGo(Routes.HISTORY) }
            DrawerItem("⚙", stringResource(R.string.nav_settings), route == Routes.SETTINGS, null) { onGo(Routes.SETTINGS) }
            DrawerItem("✦", stringResource(R.string.nav_pro), route == Routes.PRO, null) { onGo(Routes.PRO) }
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.recent_chats),
                color = AmixMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            LazyColumn(Modifier.weight(1f)) {
                items(chats, key = { it.id }) { chat ->
                    Text(
                        chat.title,
                        maxLines = 1,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenChat(chat.id) }
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* документы появятся на этапе «Документы» */ }) {
                    Text(stringResource(R.string.privacy), color = AmixMuted, fontSize = 13.sp)
                }
                Button(
                    onClick = { /* вход появится на этапе «Регистрация» */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AmixBg)
                ) {
                    Text(stringResource(R.string.login), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(icon: String, label: String, selected: Boolean, badge: String?, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(shape)
            .background(if (selected) AmixPanel2 else Color.Transparent)
            .border(1.dp, if (selected) AmixLine else Color.Transparent, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 18.sp, modifier = Modifier.width(30.dp))
        Text(label, fontSize = 15.sp, modifier = Modifier.weight(1f))
        if (badge != null) Chip(badge)
    }
}
