package dk.ufst.arch

object ReduxAndroid {
    var debugMode = false
        private set

    private var isInitialized = false

    fun init(debugMode: Boolean) {
        if(isInitialized) {
            throw IllegalStateException("You can only call ReduxAndroid.init once")
        }
        this.debugMode = debugMode
        isInitialized = true
    }
}