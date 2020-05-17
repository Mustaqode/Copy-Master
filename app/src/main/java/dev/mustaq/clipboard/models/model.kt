package dev.mustaq.clipboard.models

/**
 * Created by Mustaq Sameer on 05/04/20
 */

data class AnalyticsModel(
    val totalClips: Int,
    val offensiveWords: Int?,
    val links: Int?,
    val unsafeLinks: Int
)