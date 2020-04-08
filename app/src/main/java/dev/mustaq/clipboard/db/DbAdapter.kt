package dev.mustaq.clipboard.db

/**
 * Created by Mustaq Sameer on 29/03/20
 */


fun addCopiedTextToDb(clip: ClipModel) = clip.saveAndUpdate()

fun getAllClipsFromDb(): List<ClipModel> = findAllFromDb()

fun deleteClipFromDb(clip: ClipModel) =
    deleteItemFromDb<ClipModel> { equalTo("copiedText", clip.copiedText) }

fun deleteAllClips(): Boolean = deleteAllFromDb()

fun deleteAllClipsFromDb(): Boolean = deleteAllFromDb(ClipModel())

fun addTriggerObject() = TriggerModel().saveAndUpdate()

fun getTriggerObjectFromDb(): TriggerModel? = getManagedFindFirstAsync()

fun getAllStarredClips(): List<ClipModel> = findAllFromDb {
    equalTo("isStarred", true)
}
