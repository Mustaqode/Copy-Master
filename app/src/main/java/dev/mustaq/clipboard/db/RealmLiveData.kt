package dev.mustaq.clipboard.db

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.OnLifecycleEvent
import dev.mustaq.clipboard.helper.Trigger
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject

/**
 * Created by Mustaq Sameer on 05/04/20
 */
class RealmLiveData<T : RealmObject>(private val realmObject: T?) : LiveData<T>(),
    LifecycleObserver {

    private val realmListener = RealmChangeListener<T> { realmObject ->
        if (RealmObject.isValid(realmObject)) value = realmObject
    }

    init {
        realmObject?.addChangeListener(realmListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun closeRealm() {
        if (realmObject?.isValid == true)
            realmObject.removeChangeListener(realmListener)
        if (realmObject?.realm?.isClosed == false)
            realmObject.realm?.close()
    }

}