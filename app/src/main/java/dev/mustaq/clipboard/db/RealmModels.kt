package dev.mustaq.clipboard.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Mustaq Sameer on 28/03/20
 */

open class ClipModel (
    @PrimaryKey
    var copiedText: String = ""
): RealmObject()