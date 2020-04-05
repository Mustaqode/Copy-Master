package dev.mustaq.clipboard.db

/**
 * Created by Mustaq Sameer on 29/03/20
 */

class DbManager {

    fun addCopiedTextToDb(clip: ClipModel) = clip.saveAndUpdate()

    fun getAllClipsFromDb(): List<ClipModel> = findAllManagedObjectsFromDb()

    fun deleteClipFromDb(clip: ClipModel): Boolean =
        deleteItemFromDb<ClipModel> { equalTo("copiedText", clip.copiedText) }

    fun deleteAllClips(): Boolean = deleteAllFromDb()

    fun addTriggerObject(trigger: TriggerModel) = trigger.saveAndUpdate()

    fun getTriggerObjectFromDb(): TriggerModel? = getManagedFindFirstAsync()
}