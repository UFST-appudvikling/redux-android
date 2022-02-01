package dk.ufst.arch.redux_sample.domain.environment

class NavigationClientMock: NavigationClient {
    override fun navigate(dst: NavigationDestination, arg: NavigationArg?) {
    }

    override fun pop() {
    }

    override fun getArgument(): NavigationArg? {
        return null
    }
}