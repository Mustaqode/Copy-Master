package dev.mustaq.clipboard.db

import io.realm.RealmResults

/**
 * Created by Mustaq Sameer on 29/03/20
 */

class DbManager {

    fun addCopiedFreshTextToDb(clip: ClipModel) =
        clip.saveAndUpdateFreshEntry { equalTo("copiedText", clip.copiedText) }

    fun addCopiedTextToDb(clip: ClipModel) = clip.saveAndUpdate()

    fun getAllManagedClipsFromDb(): RealmResults<ClipModel> = findAllManagedObjectsFromDb()

    fun getAllClipsFromDb(): List<ClipModel> = findAllFromDb()

    fun deleteClipFromDb(clip: ClipModel): Boolean =
        deleteItemFromDb<ClipModel> { equalTo("copiedText", clip.copiedText) }

    fun deleteAllClips(): Boolean = deleteAllFromDb()

}