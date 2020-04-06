package dev.mustaq.clipboard.mapper

import android.annotation.SuppressLint
import dev.mustaq.clipboard.db.ClipModel
import dev.mustaq.clipboard.models.AnalyticsModel

/**
 * Created by Mustaq Sameer on 05/04/20
 */

class AnalyticsMapper {

    fun getOffensiveWords(): ArrayList<String> {
        return arrayListOf(
            "Fuck", "Dick", "Pussy", "Bitch", "Porn", "Ass", "Asshole", "blowjob", "bloody", "*",
            "retarded", "negro", "bastard", "cum", "dildo", "pimp", "gigolo"
        )
    }

    companion object {
        @SuppressLint("DefaultLocale")
        fun map(list: ArrayList<ClipModel>): AnalyticsModel {
            val offensiveWordsList = AnalyticsMapper().getOffensiveWords()
            val offensiveWords: ArrayList<ClipModel> = arrayListOf()
            val links: ArrayList<ClipModel> = arrayListOf()
            val unsafeLinks: ArrayList<ClipModel> = arrayListOf()
            val linkPattern = arrayListOf("https://", "http://", "www.")

            if (list.isNotEmpty()) {
                for (item in list) {
                    val clip = item.copiedText.toLowerCase().trim()

                    /**
                     * Take count on number of clips that contains offensive words.
                     */
                    for (word in offensiveWordsList) {
                        if (clip.contains(word.toLowerCase().trim())) {
                            offensiveWords.add(item)
                            break
                        }
                    }

                    /**
                     * Take count on number of clips that contains links.
                     */
                    for (link in linkPattern) {
                        if (clip.contains(link)) {
                            links.add(item)
                            break
                        }
                    }

                    /**
                     * Take count on number of clips that contains unsafe links.
                     */
                    if (clip.contains("http://"))
                        unsafeLinks.add(item)
                }
            }
            return AnalyticsModel(
                list.size, offensiveWords.size, links.size, unsafeLinks.size
            )
        }

        inline fun <T> Iterable<T>.takeWhileOnce(predicate: (T) -> Boolean): T? {
            var predicationSuccess = 0
            var match: T? = null
            for (item in this) {
                if (predicate(item)) {
                    match = item
                    predicationSuccess = 1
                }
                if (predicationSuccess == 1) break
            }
            return match
        }
    }
}