package dev.mustaq.clipboard.db

import io.realm.RealmResults

/**
 * Created by Mustaq Sameer on 29/03/20
 */

class DbManager {

    fun addCopiedTextToDb(clip: ClipModel) = clip.saveAndUpdate()

    fun getAllClipsFromDb(): RealmResults<ClipModel> = findAllManagedObjectsFromDb()

    fun deleteClipFromDb(clip: ClipModel): Boolean  =
        deleteItemFromDb<ClipModel> { equalTo("copiedText", clip.copiedText ) }

    fun deleteAllClips(): Boolean = deleteAllFromDb()

}