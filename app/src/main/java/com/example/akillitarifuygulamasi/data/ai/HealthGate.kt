package com.example.akillitarifuygulamasi.data.ai

import android.util.Log

data class HealthCheckResult(
    val allowed: Boolean,
    val reason: String
)

object HealthGate {

    // ===============================
    // 🧠 Debug storage (للتقرير)
    // ===============================
    private val rejectedDebug = mutableListOf<String>()

    // تفريغ التقرير (نستدعيها قبل بدء الفلترة)
    fun clearDebug() {
        rejectedDebug.clear()
    }

    // طباعة ملخص التقرير
    fun logSummary(source: String = "HEALTH_GATE") {
        Log.d("AI_SUMMARY", "[$source] Rejected count = ${rejectedDebug.size}")
        rejectedDebug.forEach {
            Log.d("AI_SUMMARY", it)
        }
    }

    // ===============================
    // 🔍 الفلترة الصحية
    // ===============================
    fun check(
        textRaw: String,
        userHealthTags: Set<HealthTag>
    ): HealthCheckResult {

        // لا توجد قيود صحية
        if (userHealthTags.isEmpty()) {
            return HealthCheckResult(true, "no health restriction")
        }

        val text = TextNormalizer.normalize(textRaw)

        for (tag in userHealthTags) {

            val rule = AIHealthRules.rules[tag] ?: continue

            // 1) Exceptions → السماح
            if (rule.exceptions.any { text.contains(it) }) {
                continue
            }

            // 2) Banned → رفض
            val banned = rule.bannedKeywords.firstOrNull {
                text.contains(it)
            }

            if (banned != null) {
                val reason = "${tag.name} banned: $banned"

                // 🧾 تخزين سبب الرفض للتقرير
                rejectedDebug.add(
                    "❌ [$tag] \"$banned\" in \"$textRaw\""
                )

                return HealthCheckResult(
                    allowed = false,
                    reason = reason
                )
            }
        }

        return HealthCheckResult(true, "ok")
    }
}
