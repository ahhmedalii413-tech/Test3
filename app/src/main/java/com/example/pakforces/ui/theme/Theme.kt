package com.example.pakforces.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * The currently selected force identity — drives the entire colour palette
 * of the app. Switching force at runtime recomposes the whole UI in the
 * new palette.
 */
enum class ForceTheme { GENERIC, ARMY, AIR_FORCE, NAVY }

private fun lightScheme(force: ForceTheme) = when (force) {
    ForceTheme.GENERIC -> lightColorScheme(
        primary = GenericPrimary, onPrimary = GenericOnPrimary,
        primaryContainer = GenericPrimaryContainer, onPrimaryContainer = GenericOnPrimaryContainer,
        secondary = GenericSecondary, onSecondary = GenericOnSecondary,
        background = GenericBackground, onBackground = GenericOnBackground,
        surface = GenericSurface, onSurface = GenericOnSurface,
        surfaceVariant = GenericSurfaceVariant, onSurfaceVariant = GenericOnSurfaceVariant,
        outline = GenericOutline, error = GenericError, onError = GenericOnError,
    )
    ForceTheme.ARMY -> lightColorScheme(
        primary = ArmyPrimary, onPrimary = ArmyOnPrimary,
        primaryContainer = ArmyPrimaryContainer, onPrimaryContainer = ArmyOnPrimaryContainer,
        secondary = ArmySecondary, onSecondary = ArmyOnSecondary,
        secondaryContainer = ArmySecondaryContainer, onSecondaryContainer = ArmyOnSecondaryContainer,
        tertiary = ArmyTertiary, onTertiary = ArmyOnTertiary,
        background = ArmyBackground, onBackground = ArmyOnBackground,
        surface = ArmySurface, onSurface = ArmyOnSurface,
        surfaceVariant = ArmySurfaceVariant, onSurfaceVariant = ArmyOnSurfaceVariant,
        outline = ArmyOutline, error = ArmyError, onError = ArmyOnError,
    )
    ForceTheme.AIR_FORCE -> lightColorScheme(
        primary = AirForcePrimary, onPrimary = AirForceOnPrimary,
        primaryContainer = AirForcePrimaryContainer, onPrimaryContainer = AirForceOnPrimaryContainer,
        secondary = AirForceSecondary, onSecondary = AirForceOnSecondary,
        secondaryContainer = AirForceSecondaryContainer, onSecondaryContainer = AirForceOnSecondaryContainer,
        tertiary = AirForceTertiary, onTertiary = AirForceOnTertiary,
        background = AirForceBackground, onBackground = AirForceOnBackground,
        surface = AirForceSurface, onSurface = AirForceOnSurface,
        surfaceVariant = AirForceSurfaceVariant, onSurfaceVariant = AirForceOnSurfaceVariant,
        outline = AirForceOutline, error = AirForceError, onError = AirForceOnError,
    )
    ForceTheme.NAVY -> lightColorScheme(
        primary = NavyPrimary, onPrimary = NavyOnPrimary,
        primaryContainer = NavyPrimaryContainer, onPrimaryContainer = NavyOnPrimaryContainer,
        secondary = NavySecondary, onSecondary = NavyOnSecondary,
        secondaryContainer = NavySecondaryContainer, onSecondaryContainer = NavyOnSecondaryContainer,
        tertiary = NavyTertiary, onTertiary = NavyOnTertiary,
        background = NavyBackground, onBackground = NavyOnBackground,
        surface = NavySurface, onSurface = NavyOnSurface,
        surfaceVariant = NavySurfaceVariant, onSurfaceVariant = NavyOnSurfaceVariant,
        outline = NavyOutline, error = NavyError, onError = NavyOnError,
    )
}

private fun darkScheme(force: ForceTheme) = when (force) {
    ForceTheme.GENERIC -> darkColorScheme(
        primary = GenericPrimaryDark, onPrimary = GenericOnPrimaryDark,
        primaryContainer = GenericPrimaryContainerDark, onPrimaryContainer = GenericOnPrimaryContainerDark,
        secondary = GenericSecondaryDark, onSecondary = GenericOnSecondaryDark,
        background = GenericBackgroundDark, onBackground = GenericOnBackgroundDark,
        surface = GenericSurfaceDark, onSurface = GenericOnSurfaceDark,
        surfaceVariant = GenericSurfaceVariantDark, onSurfaceVariant = GenericOnSurfaceVariantDark,
        outline = GenericOutlineDark,
    )
    ForceTheme.ARMY -> darkColorScheme(
        primary = ArmyPrimaryDark, onPrimary = ArmyOnPrimaryDark,
        primaryContainer = ArmyPrimaryContainerDark, onPrimaryContainer = ArmyOnPrimaryContainerDark,
        secondary = ArmySecondaryDark, onSecondary = ArmyOnSecondaryDark,
        secondaryContainer = ArmySecondaryContainerDark, onSecondaryContainer = ArmyOnSecondaryContainerDark,
        tertiary = ArmyTertiaryDark, onTertiary = ArmyOnTertiaryDark,
        background = ArmyBackgroundDark, onBackground = ArmyOnBackgroundDark,
        surface = ArmySurfaceDark, onSurface = ArmyOnSurfaceDark,
        surfaceVariant = ArmySurfaceVariantDark, onSurfaceVariant = ArmyOnSurfaceVariantDark,
        outline = ArmyOutlineDark,
    )
    ForceTheme.AIR_FORCE -> darkColorScheme(
        primary = AirForcePrimaryDark, onPrimary = AirForceOnPrimaryDark,
        primaryContainer = AirForcePrimaryContainerDark, onPrimaryContainer = AirForceOnPrimaryContainerDark,
        secondary = AirForceSecondaryDark, onSecondary = AirForceOnSecondaryDark,
        secondaryContainer = AirForceSecondaryContainerDark, onSecondaryContainer = AirForceOnSecondaryContainerDark,
        tertiary = AirForceTertiaryDark, onTertiary = AirForceOnTertiaryDark,
        background = AirForceBackgroundDark, onBackground = AirForceOnBackgroundDark,
        surface = AirForceSurfaceDark, onSurface = AirForceOnSurfaceDark,
        surfaceVariant = AirForceSurfaceVariantDark, onSurfaceVariant = AirForceOnSurfaceVariantDark,
        outline = AirForceOutlineDark,
    )
    ForceTheme.NAVY -> darkColorScheme(
        primary = NavyPrimaryDark, onPrimary = NavyOnPrimaryDark,
        primaryContainer = NavyPrimaryContainerDark, onPrimaryContainer = NavyOnPrimaryContainerDark,
        secondary = NavySecondaryDark, onSecondary = NavyOnSecondaryDark,
        secondaryContainer = NavySecondaryContainerDark, onSecondaryContainer = NavyOnSecondaryContainerDark,
        tertiary = NavyTertiaryDark, onTertiary = NavyOnTertiaryDark,
        background = NavyBackgroundDark, onBackground = NavyOnBackgroundDark,
        surface = NavySurfaceDark, onSurface = NavyOnSurfaceDark,
        surfaceVariant = NavySurfaceVariantDark, onSurfaceVariant = NavyOnSurfaceVariantDark,
        outline = NavyOutlineDark,
    )
}

@Composable
fun PakForcesTheme(
    forceTheme: ForceTheme = ForceTheme.GENERIC,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // we ship our own elite palette by default
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> darkScheme(forceTheme)
        else -> lightScheme(forceTheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PakForcesTypography,
        shapes = PakForcesShapes,
        content = content,
    )
}
