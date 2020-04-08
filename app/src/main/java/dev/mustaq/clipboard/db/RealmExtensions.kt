package dev.mustaq.clipboard.db

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.realm.*
import io.realm.exceptions.RealmException

/**
 * Created by Mustaq Sameer on 28/03/20
 */

fun getDefaultRealm() = Realm.getDefaultInstance()

fun Realm.transaction(realm: (Realm) -> Unit) = use { executeTransaction { realm(this) } }

inline fun <reified T : RealmObject> T.saveAndUpdate() {
    getDefaultRealm().transaction { realm ->
        realm.insertOrUpdate(this)
    }
}

inline fun <reified T : RealmObject> getManagedFindFirstAsync(): T? {
    return getDefaultRealm().where(T::class.java).findFirstAsync()
}

inline fun <reified T : RealmObject> findAllFromDb(): List<T> {
    getDefaultRealm().use { realm ->
        return realm.copyFromRealm<T>(realm.where(T::class.java).findAll())
    }
}

inline fun <reified T : RealmObject> findAllFromDb(query: RealmQuery<T>.() -> RealmQuery<T>): List<T> {
    getDefaultRealm().use { realm ->
        return realm.copyFromRealm<T>(realm.where(T::class.java).query().findAll())
    }
}

inline fun <reified T : RealmObject> findAllManagedObjectsFromDb(): RealmResults<T> {
    return getDefaultRealm().where(T::class.java).sort("insertedAt", Sort.DESCENDING).findAll()
}

fun <T : RealmObject> LifecycleOwner.realmLiveData(realmObject: T?, onChanged: (T) -> Unit) {
    val realmLiveData = RealmLiveData(realmObject)
    lifecycle.addObserver(realmLiveData)
    realmLiveData.observe(this, Observer {
        if (it != null) onChanged(it)
    })

}

/**
 * Deletes all objects from db and returns the status
 */

fun deleteAllFromDb(): Boolean {
    var deleteStatus = false
    try {
        getDefaultRealm().transaction { realm ->
            realm.deleteAll()
            deleteStatus = true
        }
    } catch (e: RealmException) {
        e.printStackTrace()
        deleteStatus = false
    }
    return deleteStatus
}


inline fun <reified T : RealmObject> deleteAllFromDb(realmObject: T?): Boolean {
    var deleteStatus = false
    val realmObjectList: RealmResults<T>? = findAllManagedObjectsFromDb()
    try {
        if (realmObjectList != null)
            getDefaultRealm().transaction { _ ->
                realmObjectList.deleteAllFromRealm()
                deleteStatus = true
            }
    } catch (e: RealmException) {
        e.printStackTrace()
        deleteStatus = false
    }
    return deleteStatus
}

inline fun <reified T : RealmObject> deleteItemFromDb(query: RealmQuery<T>.() -> RealmQuery<T>) {
    val item = query(getDefaultRealm().where(T::class.java)).findFirst()
    if (item !== null && RealmObject.isValid(item)) {
        getDefaultRealm().transaction {
            item.deleteFromRealm()
        }
    }
}



