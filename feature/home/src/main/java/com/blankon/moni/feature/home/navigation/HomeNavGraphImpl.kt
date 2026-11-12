package com.blankon.moni.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.blankon.moni.core.navigation.base.BaseNavGraph
import com.blankon.moni.core.navigation.helper.composableScreen
import com.blankon.moni.core.navigation.helper.navigateTo
import com.blankon.moni.core.navigation.route.HomeGraph
import com.blankon.moni.core.navigation.route.HomeGraph.HomeDataClassRoute
import com.blankon.moni.core.navigation.route.HomeGraph.HomeDataTypeRoute
import com.blankon.moni.core.navigation.route.HomeGraph.HomeFetchApiRoute
import com.blankon.moni.core.navigation.route.HomeGraph.HomeLandingRoute
import com.blankon.moni.feature.home.screen.HomeDataClassScreen
import com.blankon.moni.feature.home.screen.HomeDataTypeScreen
import com.blankon.moni.feature.home.screen.HomeFetchApiScreen
import com.blankon.moni.feature.home.screen.HomeLandingScreen
import com.blankon.moni.feature.home.screen.NoteEditorScreen
import javax.inject.Inject

class HomeNavGraphImpl @Inject constructor() : BaseNavGraph {
    override fun NavGraphBuilder.createGraph(navController: NavController) {
        composableScreen<HomeLandingRoute> {
            HomeLandingScreen(
                onAddNote = {
                    navController.navigateTo(HomeGraph.NoteEditorRoute())
                },
                onAIClick = {
                    // Navigate to AI Assistant
                },
                onNoteClick = { noteId ->
                    navController.navigateTo(HomeGraph.NoteEditorRoute(noteId))
                },
                navController = navController
            )
        }
        composableScreen<HomeGraph.NoteEditorRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<HomeGraph.NoteEditorRoute>()
            NoteEditorScreen(
                navController = navController,
                noteId = route.noteId
            )
        }
        composableScreen<HomeDataTypeRoute> {
            HomeDataTypeScreen(navController)
        }
        composableScreen<HomeDataClassRoute>(
            customArgs = HomeDataClassRoute.typeMap
        ) {
            HomeDataClassScreen(navController)
        }
        composableScreen<HomeFetchApiRoute> {
            HomeFetchApiScreen(navController)
        }
    }
}