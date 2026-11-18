package com.runit.ocrscanner

import java.util.Locale

object SsidParser {
    private val regex = Regex(
        """(?i)\b[A-Z0-9]{5,9}[_ ]+[A-Z0-9._ ]+"""
    )

    fun parse(text: String): String? {
        val ssid = regex.findAll(text)
            .map {
                it.value
            }
            .firstOrNull {
                val prefix = it.takeWhile { c ->
                    c != '_' && c != ' '
                }

                fuzzy(prefix, "VIETTEL")
            } ?: return null

        return ssid
            .replace(Regex("""[ _]+"""), "_")
            .replace(Regex("""(?i)_[A-Z0-9.]+[:：].*$"""), "")
            .replace(Regex("""(?i)(?:_(?:5G|2.4G))?$"""), "")
            .trim('_')
            .split('_')
            .take(3)
            .joinToString("_")
            .substringAfterLast('_')
            .takeLast(4)
            .takeIf {
                it.length > 3 &&
                        !"VIETTEL".contains(it, ignoreCase = true)
            }
    }

    private fun fuzzy(a: String, b: String): Boolean {
        val x = a.uppercase()
        val y = b.uppercase()

        val dp = IntArray(y.length + 1) { it }

        for (i in x.indices) {
            var prev = dp[0]
            dp[0] = i + 1

            for (j in y.indices) {
                val old = dp[j + 1]

                dp[j + 1] = minOf(
                    dp[j + 1] + 1,
                    dp[j] + 1,
                    prev + if (x[i] == y[j]) 0 else 1
                )

                prev = old
            }
        }

        return dp[y.length] <= 2
    }
}