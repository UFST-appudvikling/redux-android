@file:Suppress("unused")

package dk.ufst.arch

typealias Effect<Action> = ()->Action?
typealias ReducerFunc<Value, Action, Environment> = (Value, Action, Environment) -> Array<Effect<Action>>
typealias Effects<Action> = Array<Effect<Action>>

class ReducerBuilder <Action> {
    internal var effects: MutableList<Effect<Action>> = mutableListOf()
    fun effect(fx: Effect<Action>) {
        effects.add(fx)
    }
}

fun <Action> reducer(reducerBuilder: ReducerBuilder<Action>.() -> Unit) : Effects<Action> {
    val builder = ReducerBuilder<Action>().apply(reducerBuilder)
    return builder.effects.toTypedArray()
}


/**
 * Helper function to reduce clutter when you only want to run a single effect
 * from a reducer
 */
@Deprecated("Use the reducer builder function and effect() instead",
    ReplaceWith("effect")
)
fun <Action> singleEffect(result : ()->Action) : Array<Effect<Action>> {
    return arrayOf(
        effect@{
            result.invoke()
        }
    )
}

/**
 * Combines several reducers into one
 */
fun <Value, Action, Environment> combine(
    vararg reducers: ReducerFunc<Value, Action, Environment>
) : ReducerFunc<Value, Action, Environment> {
    return inner@{ value, action, env ->
        val effects = reducers.flatMap { it(value,action, env).asIterable() }
        effects.toTypedArray()
    }
}

/**
 * Takes a reducer and a variable number of high order reducers
 * The highorder reducers are invoked the reducer in turn and accumulated
 * in one reducer, which is then run
 */
fun <Value, Action, Environment> compose(
    reducer: ReducerFunc<Value,Action, Environment>,
    vararg highOrderReducers: (ReducerFunc<Value,Action, Environment>) -> ReducerFunc<Value,Action, Environment>
) : ReducerFunc<Value, Action, Environment> {
    return { value, action,env ->
        var finalReducer = reducer
        highOrderReducers.forEach {
            finalReducer = it.invoke(finalReducer)
        }
        finalReducer(value,action, env)
    }
}

inline fun <GlobalValue, LocalValue, reified GlobalAction, reified LocalAction, GlobalEnvironment, LocalEnvironment> pullback(
    crossinline reducer : ReducerFunc<LocalValue, LocalAction, LocalEnvironment>,
    crossinline get : (GlobalValue)->LocalValue,
    crossinline set : (GlobalValue, LocalValue)->Unit,
    crossinline getEnv : (GlobalEnvironment)->LocalEnvironment
) : ReducerFunc<GlobalValue, GlobalAction, GlobalEnvironment> {
    return { globalValue, globalAction, globalEnv ->
        val localValue = get(globalValue)
        val localEnv = getEnv(globalEnv)
        var localEffects = emptyArray<Effect<LocalAction>>()
        if(globalAction is LocalAction) {
            localEffects = reducer(localValue, globalAction as LocalAction, localEnv)
            set(globalValue, localValue)
        }
        val globalEffects =  localEffects.map { localEffect ->
            val f : Effect<GlobalAction>  = inner@{
                val localAction = localEffect()
                if(localAction is GlobalAction) {
                    return@inner localAction
                }
                return@inner null
            }
            f
        }
        globalEffects.toTypedArray()
    }
}
