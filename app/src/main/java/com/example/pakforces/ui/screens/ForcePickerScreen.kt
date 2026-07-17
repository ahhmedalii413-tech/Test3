package com.example.pakforces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pakforces.data.model.Force

private data class ForceOption(
    val force: Force,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradient: List<Color>,
)

@Composable
fun ForcePickerScreen(onForceSelected: (Force) -> Unit) {
    val options = listOf(
        ForceOption(Force.ARMY, "Pakistan Army", "Verbal · GK · Strong on PMA / ISSB patterns", Icons.Filled.MilitaryTech,
            listOf(Color(0xFF4A5A2A), Color(0xFF6E7F3F))),
        ForceOption(Force.AIR_FORCE, "Pakistan Air Force", "English · Science · Technical aptitude focus", Icons.Filled.Flight,
            listOf(Color(0xFF1F4E79), Color(0xFF3A78B0))),
        ForceOption(Force.NAVY, "Pakistan Navy", "Math · Non-Verbal · Spatial reasoning", Icons.Filled.Sailing,
            listOf(Color(0xFF0F2E4C), Color(0xFF1B4A75))),
    )

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        ) {
            Spacer(Modifier.height(40.dp))
            Text("Select your force", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("Each force has a distinct question bank and colour identity.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.height(28.dp))

            options.forEach { opt ->
                ForceCard(opt, onClick = { onForceSelected(opt.force) })
                Spacer(Modifier.height(14.dp))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ForceCard(opt: ForceOption, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(opt.gradient))
            .clickable { onClick() }
            .padding(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(opt.icon, contentDescription = opt.title, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(opt.title, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(opt.subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Start preparing", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}
