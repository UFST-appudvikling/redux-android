package dk.ufst.arch

import androidx.lifecycle.*

@Suppress("unused")
abstract class ReduxViewModel<LocalValue, LocalAction> : ViewModel(), DefaultLifecycleObserver {
    private val value = MutableLiveData<LocalValue>()
    abstract val store : LocalStore<LocalValue, LocalAction>

    private val subscription : (LocalValue)->Unit = { state ->
        value.value = state
    }

    override fun onCreate(owner: LifecycleOwner) {
        store.subscribe(subscription)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        store.desubscribe(subscription)
    }

    fun state(): LiveData<LocalValue> {
        return value
    }
}