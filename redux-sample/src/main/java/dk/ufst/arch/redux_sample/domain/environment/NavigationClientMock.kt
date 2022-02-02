package dk.ufst.arch.redux_sample.domain.environment

import dk.ufst.arch.Effect

class NavigationClientMock: NavigationClient {
    override fun navigate(dst: NavigationDestination, arg: NavigationArg?) {
    }

    override fun <Action> navigateFx(
        dst: NavigationDestination,
        arg: NavigationArg?
    ): Array<Effect<Action>> {
        return emptyArray()
    }

    override fun pop() {
    }

    override fun <Action> popFx(): Array<Effect<Action>> {
        return emptyArray()
    }

    override fun getArgument(key: String): NavigationArg? {
        return null
    }
}