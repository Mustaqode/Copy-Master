package dev.mustaq.clipboard.db

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.exceptions.RealmException

/**
 * Created by Mustaq Sameer on 28/03/20
 */

fun getDefaultRealm() = Realm.getDefaultInstance()

fun Realm.transaction(realm: (Realm) -> Unit) = use { executeTransaction { realm(this) } }

fun <T : RealmObject?> T.saveAndUpdate() {
    getDefaultRealm().transaction { realm ->
        realm.insertOrUpdate(this)
    }
}

inline fun <reified T : RealmObject> findAllFromDb(): List<T> {
    getDefaultRealm().use {realm ->
        return realm.copyFromRealm<T>(realm.where(T::class.java).findAll())
    }
}

inline fun <reified T: RealmObject> findAllManagedObjectsFromDb() : List<T> {
    getDefaultRealm().use { realm ->
         return realm.where(T::class.java).findAll()
    }
}

/**
 * Deletes all objects from db and returns the status
 */
fun deleteAllFromDb() : Boolean {
    var deleteStatus = false
    try {
        getDefaultRealm().transaction { realm ->
            realm.removeAllChangeListeners()
            realm.deleteAll()
            deleteStatus = true
        }
    } catch (e: RealmException) {
        e.printStackTrace()
        deleteStatus = false
    }
    return deleteStatus
}

fun <T: RealmObject> deleteObjectFromDb(clazz: Class<T>) {
    getDefaultRealm().transaction { realm ->
        realm.delete(clazz)
    }
}

fun <T : RealmObject> T?.safeClose() {
    try {
        if (this?.isValid == true) {
            if (isManaged) removeAllChangeListeners()
            if (!realm.isClosed) realm.close()
            Log.d("Realm Object is closed", this::class.java.simpleName)
        }
    } catch (e: RealmException) {
        e.printStackTrace()
    }
}

