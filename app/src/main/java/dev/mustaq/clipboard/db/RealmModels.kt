package dev.mustaq.clipboard.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Mustaq Sameer on 28/03/20
 */

open class ClipModel (
    @PrimaryKey
    var copiedText: String = "",
    var insertedAt: Long = System.currentTimeMillis(),
    var isStarred: Boolean = false
): RealmObject()

open class TriggerModel(
    @PrimaryKey
    var id: Int = 0,
    var item: String = UUID.randomUUID().toString()
) : RealmObject()

open class FreshInstallModel(
    @PrimaryKey
    var id: Int = 0,
    var isOpened: Boolean = false
) : RealmObject()
