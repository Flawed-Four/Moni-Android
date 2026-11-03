package com.blankon.moni.feature.info.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.blankon.moni.core.navigation.base.BaseNavGraph
import com.blankon.moni.core.navigation.helper.composableScreen
import com.blankon.moni.core.navigation.route.InfoGraph.InfoLandingRoute
import com.blankon.moni.feature.info.screen.InfoLandingScreen
import javax.inject.Inject

class InfoNavGraphImpl @Inject constructor() : BaseNavGraph {
    override fun NavGraphBuilder.createGraph(navController: NavController) {
        composableScreen<InfoLandingRoute>(
            enterTransition = fadeIn(),
            popExitTransition = fadeOut()
        ) { InfoLandingScreen() }
    }
}