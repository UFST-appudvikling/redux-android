package dk.ufst.arch.redux_sample

import androidx.navigation.NavController
import dk.ufst.arch.AppAction
import dk.ufst.arch.ReducerFunc
import dk.ufst.arch.redux_sample.domain.AppEnvironment
import dk.ufst.arch.redux_sample.domain.AppState
import dk.ufst.arch.redux_sample.domain.contacts.ContactsAction
import dk.ufst.arch.redux_sample.ui.Screens

fun navigation(navController: NavController)
        : (ReducerFunc<AppState, AppAction, AppEnvironment>) -> ReducerFunc<AppState, AppAction, AppEnvironment> =
    {
        { state, action, env ->
            val effects = it(state, action, env).toMutableList()

            if (action is ContactsAction.ContactTapped) {
                navController.navigate(Screens.Messages.name)
            }

            effects.toTypedArray()
        }
    }
