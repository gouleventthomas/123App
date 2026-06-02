package com.pb123.hub.hub

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector
import com.pb123.hub.navigation.Routes

/**
 * A guide entry shown on the hub home screen.
 *
 * To add a new guide: add a [Guide] to [hubGuides], create its screen, register
 * a route in [Routes] and wire it in the NavHost.
 */
data class Guide(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String?,
) {
    val available: Boolean get() = route != null
}

/** The catalogue of guides available in the hub. */
val hubGuides: List<Guide> = listOf(
    Guide(
        title = "Sécurisation client",
        description = "Trame d'appel : contrôle dossier, assurance, RDV, immobilisation, courtoisie.",
        icon = Icons.Filled.Shield,
        route = Routes.SECURISATION,
    ),
    Guide(
        title = "Pose pare-brise",
        description = "Étapes de dépose / repose et points de contrôle qualité.",
        icon = Icons.Filled.Build,
        route = null,
    ),
    Guide(
        title = "Calibrage ADAS",
        description = "Procédure de recalibrage des caméras et capteurs.",
        icon = Icons.Filled.CenterFocusStrong,
        route = null,
    ),
    Guide(
        title = "Procédure assurance",
        description = "Déclaration de sinistre et gestion des dossiers verte / jaune / rouge.",
        icon = Icons.Filled.VerifiedUser,
        route = null,
    ),
    Guide(
        title = "Check-list livraison",
        description = "Contrôle final et restitution du véhicule au client.",
        icon = Icons.AutoMirrored.Filled.Assignment,
        route = null,
    ),
)
