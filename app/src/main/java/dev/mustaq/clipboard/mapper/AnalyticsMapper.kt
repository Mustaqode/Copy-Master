package dev.mustaq.clipboard.mapper

import android.annotation.SuppressLint
import dev.mustaq.clipboard.db.ClipModel
import dev.mustaq.clipboard.enums.ContentType
import dev.mustaq.clipboard.models.AnalyticsModel

/**
 * Created by Mustaq Sameer on 05/04/20
 */

class AnalyticsMapper {

    //We may query it from shared preference in the future
    fun getOffensiveWords(): ArrayList<String> = arrayListOf(
        "Fuck", "Dick", "Pussy", "Bitch", "Porn", "Ass", "Asshole", "blowjob", "bloody", "*",
        "retarded", "negro", "bastard", "cum", "dildo", "pimp", "gigolo"
    )

    companion object {

        private val linkPattern = arrayListOf(
            "https://", "http://", "www.", ".in", ".com",
            ".net", ".co.in", ".co", ".website", ".dev", ".sa", ".uk", ".ra", ".ly"
        )

        private val unsafeLinksPattern = arrayListOf("http://", "www.", "bit.", ".ly")

        @SuppressLint("DefaultLocale")
        fun map(list: ArrayList<ClipModel>): AnalyticsModel {
            val offensiveWordsList = AnalyticsMapper().getOffensiveWords()
            val offensiveWords: ArrayList<ClipModel> = arrayListOf()
            val links: ArrayList<ClipModel> = arrayListOf()
            val unsafeLinks: ArrayList<ClipModel> = arrayListOf()

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
                    for (link in unsafeLinksPattern) {
                        if (clip.contains(link.toLowerCase().trim())) {
                            unsafeLinks.add(item)
                            break
                        }
                    }
                }
            }
            return AnalyticsModel(
                list.size, offensiveWords.size, links.size, unsafeLinks.size
            )
        }

        @SuppressLint("DefaultLocale")
        fun map(clipModel: ClipModel): ContentType {

            var contentType: ContentType = ContentType.NORMAL
            val offensiveWords = AnalyticsMapper().getOffensiveWords()
            val clip = clipModel.copiedText.toLowerCase().trim()

            offensiveWords.filter { word ->
                clip.contains(word.toLowerCase().trim())
            }.apply {
                if (this.isNotEmpty()) contentType = ContentType.OFFENSIVE
                else {
                    linkPattern.filter { link ->
                        clip.contains(link)
                    }.apply {
                        if (this.isNotEmpty())
                            contentType = ContentType.LINK
                    }
                }
            }
            return contentType
        }
    }
}