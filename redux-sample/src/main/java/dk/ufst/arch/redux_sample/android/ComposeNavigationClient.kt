package dk.ufst.arch.redux_sample.android

import androidx.navigation.NavController
import dk.ufst.arch.redux_sample.domain.environment.NavigationArg
import dk.ufst.arch.redux_sample.domain.environment.NavigationClient
import dk.ufst.arch.redux_sample.domain.environment.NavigationDestination
import java.util.*

class ComposeNavigationClient(private val navController: NavController): NavigationClient {
    private val argumentStack: Stack<NavigationArg?> = Stack()

    override fun navigate(dst: NavigationDestination, arg: NavigationArg?) {
        argumentStack.push(arg)
        navController.navigate(dst.name)
    }

    override fun pop() {
        argumentStack.pop()
        navController.popBackStack()
    }

    override fun getArgument(): NavigationArg? {
        return if(argumentStack.isNotEmpty()) {
            argumentStack.peek()
        } else {
            null
        }
    }
}