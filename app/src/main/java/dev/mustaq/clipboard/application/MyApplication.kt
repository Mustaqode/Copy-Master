package dev.mustaq.clipboard.application

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Mustaq Sameer on 28/03/20
 */

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        configRealm()
    }

    private fun configRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

}