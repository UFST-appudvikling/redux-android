package dk.ufst.arch

object ReduxAndroid {
    var debugMode = false
        private set

    fun init(debugMode: Boolean) {
        this.debugMode = debugMode
    }
}