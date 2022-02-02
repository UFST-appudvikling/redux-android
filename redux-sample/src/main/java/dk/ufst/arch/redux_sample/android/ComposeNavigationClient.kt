package dk.ufst.arch.redux_sample.android

import android.os.Handler
import android.os.Looper
import androidx.navigation.NavController
import dk.ufst.arch.Effect
import dk.ufst.arch.redux_sample.domain.environment.NavigationArg
import dk.ufst.arch.redux_sample.domain.environment.NavigationClient
import dk.ufst.arch.redux_sample.domain.environment.NavigationDestination
import dk.ufst.arch.singleEffect
import kotlin.collections.HashMap

class ComposeNavigationClient(private val navController: NavController): NavigationClient {
    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val argumentMap: MutableMap<String, NavigationArg> = HashMap()

    override fun navigate(dst: NavigationDestination, arg: NavigationArg?) {
        handler.post {
            arg?.let {
                argumentMap[dst.name] = it
            }
            navController.navigate(dst.name)
        }
    }

    override fun <Action> navigateFx(
        dst: NavigationDestination,
        arg: NavigationArg?
    ): Array<Effect<Action>> {
        return singleEffect {
            navigate(dst, arg)
            null
        }
    }

    override fun pop() {
        handler.post {
            navController.popBackStack()
        }
    }

    override fun <Action> popFx(): Array<Effect<Action>> {
        return singleEffect {
            pop()
            null
        }
    }

    override fun getArgument(key: String): NavigationArg? {
        return argumentMap[key]
    }
}