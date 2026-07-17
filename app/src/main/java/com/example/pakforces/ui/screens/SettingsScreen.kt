package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pakforces.data.repo.TestLength
import com.example.pakforces.ui.viewmodel.AppViewModel
import com.example.pakforces.ui.viewmodel.AppViewModelFactory

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.Factory!!)
    val state by appViewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            Spacer(Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Spacer(Modifier.width(8.dp))
                Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(Modifier.height(20.dp))

            // ── Theme ──
            SectionTitle("Appearance")
            SettingRow(Icons.Filled.DarkMode, "Dark theme", if (state.darkModeSchedule) "Auto (sunset/sunrise)" else if (state.darkTheme == true) "On" else "Off") {
                Switch(
                    checked = state.darkTheme ?: false,
                    onCheckedChange = { appViewModel.setDarkTheme(it) },
                    enabled = !state.darkModeSchedule,
                )
            }
            Spacer(Modifier.height(8.dp))
            SettingRow(Icons.Filled.Palette, "Auto dark mode (sunset → sunrise)", "Light by day, dark by night") {
                Switch(
                    checked = state.darkModeSchedule,
                    onCheckedChange = { appViewModel.setDarkModeSchedule(it) },
                )
            }
            Spacer(Modifier.height(8.dp))
            SettingRow(Icons.Filled.Visibility, "Color-blind mode", "Shapes added to colours (accessibility)") {
                Switch(
                    checked = state.colorBlindMode,
                    onCheckedChange = { appViewModel.setColorBlindMode(it) },
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Sound & Haptics ──
            SectionTitle("Sound & Haptics")
            SettingRow(Icons.Filled.GraphicEq, "Sound effects", "Correct/wrong feedback sounds") {
                Switch(
                    checked = state.soundEnabled,
                    onCheckedChange = { appViewModel.setSoundEnabled(it) },
                )
            }
            Spacer(Modifier.height(8.dp))
            SettingRow(Icons.Filled.Vibration, "Haptic feedback", "Vibration on answers") {
                Switch(
                    checked = state.hapticEnabled,
                    onCheckedChange = { appViewModel.setHapticEnabled(it) },
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Language ──
            SectionTitle("Language")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Language, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Explanations language", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("en" to "English", "ur_roman" to "Roman Urdu").forEach { (code, label) ->
                            OutlinedButton(
                                onClick = { appViewModel.setLanguage(code) },
                                modifier = Modifier.weight(1f).height(44.dp),
                                colors = if (state.language == code) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                         else ButtonDefaults.outlinedButtonColors(),
                            ) { Text(label) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Notifications ──
            SectionTitle("Notifications")
            SettingRow(Icons.Filled.Notifications, "Daily reminder", "Rozana ${state.reminderHour}:00 par notification") {
                Switch(
                    checked = state.dailyReminder,
                    onCheckedChange = { appViewModel.setDailyReminder(it) },
                )
            }
            Spacer(Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().clickable { },
            ) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Notifications, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reminder time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Choose when the daily reminder arrives", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Text("${state.reminderHour}:00", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }
            }
            // Quick time presets
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(8, 18, 20, 21).forEach { h ->
                    OutlinedButton(
                        onClick = { appViewModel.setReminderHour(h) },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = if (state.reminderHour == h) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                 else ButtonDefaults.outlinedButtonColors(),
                    ) { Text("${h}:00", style = MaterialTheme.typography.labelSmall) }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Test length ──
            SectionTitle("Default test length")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TestLength.entries.forEach { tl ->
                    val selected = state.testLength == tl
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.weight(1f).height(88.dp).clickable { appViewModel.setTestLength(tl) },
                    ) {
                        Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Speed, null, tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.height(4.dp))
                                Text(tl.display, style = MaterialTheme.typography.labelMedium, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── About ──
            SectionTitle("About")
            SettingRow(Icons.Filled.Info, "Pak Forces Prep — Elite Edition v3", "1,090+ verified questions · 16 features") {}
            Spacer(Modifier.height(8.dp))
            Text(
                "Hand-authored question banks covering Verbal & Non-Verbal Intelligence, English, Mathematics, Pakistan Studies, Islamic Studies, General Knowledge, and Everyday Science for all three forces. Real exam simulation, SRS revision, adaptive analytics, gamification, and more.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            trailing()
        }
    }
}
