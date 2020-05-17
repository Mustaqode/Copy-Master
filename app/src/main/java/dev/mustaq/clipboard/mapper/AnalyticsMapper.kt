package dev.mustaq.clipboard.mapper

import android.annotation.SuppressLint
import dev.mustaq.clipboard.backyard.getOffensiveWords
import dev.mustaq.clipboard.db.ClipModel
import dev.mustaq.clipboard.enums.ContentType
import dev.mustaq.clipboard.models.AnalyticsModel

/**
 * Created by Mustaq Sameer on 05/04/20
 */

class AnalyticsMapper {

    companion object {

        private val linkPattern = arrayListOf(
            "https://", "http://", "www.", ".in", ".com",
            ".net", ".co.in", ".co", ".website", ".dev", ".sa", ".uk", ".ra", ".ly"
        )

        @SuppressLint("DefaultLocale")
        fun map(list: ArrayList<ClipModel>): AnalyticsModel {
            val offensiveWordsList = getOffensiveWords()
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
                }
            }
            return AnalyticsModel(
                list.size, offensiveWords.size, links.size, unsafeLinks.size
            )
        }

        @SuppressLint("DefaultLocale")
        fun map(clipModel: ClipModel): ContentType {

            var contentType: ContentType = ContentType.NORMAL
            val offensiveWords = getOffensiveWords()
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