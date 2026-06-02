package com.pb123.hub.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pb123.hub.guides.chrono.ChronoScreen
import com.pb123.hub.guides.securisation.SecurisationScreen
import com.pb123.hub.hub.HubScreen

/** Routes for every screen of the hub. Add one constant per new guide. */
object Routes {
    const val HUB = "hub"
    const val SECURISATION = "securisation"
    const val CHRONO = "chrono"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HUB,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(280))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(280))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(280))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(280))
        },
    ) {
        composable(Routes.HUB) {
            HubScreen(onOpenRoute = { route -> navController.navigate(route) })
        }
        composable(Routes.SECURISATION) {
            SecurisationScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.CHRONO) {
            ChronoScreen(onBack = { navController.popBackStack() })
        }
    }
}
