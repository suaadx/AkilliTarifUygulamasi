package com.example.akillitarifuygulamasi.data.ai

// =======================================================
// ✅ HealthTag (موحّد + mapping ذكي)
// =======================================================
enum class HealthTag(val aliases: List<String>) {

    DIYABET(listOf("diyabet", "seker", "diabet")),
    HIPERTANSIYON(listOf("tansiyon", "hipertansiyon")),
    KALP(listOf("kalp")),
    KOLESTEROL(listOf("kolesterol")),
    GLUTEN(listOf("gluten", "colyak", "çölyak")),
    LAKTOZ(listOf("laktoz")),
    ANEMI(listOf("anemi", "kansizlik", "kansızlık")),
    BOBREK(listOf("bobrek", "böbrek")),
    GASTRIT_REFLU(listOf("gastrit", "reflu", "reflü")),
    ZAYIFLAMA(listOf("zayiflama", "zayıflama", "diyet"));

    companion object {
        fun from(raw: String?): HealthTag? {
            if (raw.isNullOrBlank() || raw.equals("NONE", true)) return null
            val value = raw.lowercase()
            return entries.firstOrNull { tag ->
                tag.aliases.any { value.contains(it) }
            }
        }
    }
}

// =======================================================
// Rule Model
// =======================================================
data class HealthRule(
    val bannedKeywords: Set<String>,      // ❌ ممنوع
    val exceptions: Set<String>,          // ✅ فقط حالات خاصة حقيقية
    val preferredKeywords: Set<String>    // ⭐ مفضّل
)

// =======================================================
// ✅ AI Health Rules (INGREDIENT-BASED ONLY)
// =======================================================
object AIHealthRules {

    val rules: Map<HealthTag, HealthRule> = mapOf(

        // ===================================================
        // 1) DIYABET (سكري)
        // ===================================================
        HealthTag.DIYABET to HealthRule(
            bannedKeywords = setOf(
                // سكريات مباشرة
                "seker", "toz seker", "bal", "pekmez", "surup",
                // حلويات وكربوهيدرات عالية
                "tatli", "pasta", "kek", "dondurma",
                "irmik", "un", "bulgur",
                // مشروبات
                "kola", "gazoz", "meyve suyu",
                // فواكه عالية السكر
                "hurma", "incir", "uzum"
            ),
            exceptions = setOf(
                // فقط محليات بديلة حقيقية
                "stevia", "sekersiz", "diyabetik", "zero"
            ),
            preferredKeywords = setOf(
                "yulaf", "tam tahil",
                "sebze", "salata",
                "baklagil", "mercimek", "nohut",
                "protein", "yumurta"
            )
        ),

        // ===================================================
        // 2) HIPERTANSIYON (ضغط)
        // ===================================================
        HealthTag.HIPERTANSIYON to HealthRule(
            bannedKeywords = setOf(
                "tuz", "tuzlu",
                "salamura", "tursu",
                "sucuk", "sosis", "salami",
                "cips", "konserve",
                "soya sosu"
            ),
            exceptions = setOf(
                "tuzsuz", "az tuzlu", "dusuk sodyum"
            ),
            preferredKeywords = setOf(
                "zeytinyagi", "limon",
                "sarmisak", "maydanoz",
                "sebze", "ev yapimi"
            )
        ),

        // ===================================================
        // 3) KALP (قلب)
        // ===================================================
        HealthTag.KALP to HealthRule(
            bannedKeywords = setOf(
                "kizartma", "derin yag",
                "tereyagi", "margarin",
                "krema", "kaymak",
                "fast food"
            ),
            exceptions = setOf(
                "firinda", "izgara", "buharda"
            ),
            preferredKeywords = setOf(
                "zeytinyagi", "balik",
                "ceviz", "avokado",
                "sebze", "kurubaklagil"
            )
        ),

        // ===================================================
        // 4) KOLESTEROL
        // ===================================================
        HealthTag.KOLESTEROL to HealthRule(
            bannedKeywords = setOf(
                "sakatat", "kuyruk yagi",
                "islenmis et",
                "sucuk", "sosis",
                "tereyagi", "margarin",
                "kizartma"
            ),
            exceptions = setOf(
                "izgara", "firinda", "zeytinyagi"
            ),
            preferredKeywords = setOf(
                "yulaf", "balik",
                "kurubaklagil",
                "sebze", "badem", "ceviz"
            )
        ),

        // ===================================================
        // 5) GLUTEN
        // ===================================================
        HealthTag.GLUTEN to HealthRule(
            bannedKeywords = setOf(
                "bugday", "un", "ekmek",
                "makarna", "bulgur",
                "irmik", "arpa", "cavdar"
            ),
            exceptions = setOf(
                "glutensiz", "karabugday",
                "kinoa", "pirinc unu", "misir unu"
            ),
            preferredKeywords = setOf(
                "kinoa", "pirinc",
                "misir", "patates",
                "sebze"
            )
        ),

        // ===================================================
        // 6) LAKTOZ
        // ===================================================
        HealthTag.LAKTOZ to HealthRule(
            bannedKeywords = setOf(
                "sut", "krem",
                "kaymak", "peynir",
                "yogurt", "dondurma"
            ),
            exceptions = setOf(
                "laktozsuz",
                "badem susu", "soya susu",
                "hindistan cevizi"
            ),
            preferredKeywords = setOf(
                "laktozsuz",
                "badem susu", "soya susu"
            )
        ),

        // ===================================================
        // 7) ANEMI
        // ===================================================
        HealthTag.ANEMI to HealthRule(
            bannedKeywords = emptySet(),
            exceptions = emptySet(),
            preferredKeywords = setOf(
                "kirmizi et", "karaciger",
                "yumurta", "ispanak",
                "mercimek", "tahin",
                "kuru uzum"
            )
        ),

        // ===================================================
        // 8) BOBREK
        // ===================================================
        HealthTag.BOBREK to HealthRule(
            bannedKeywords = setOf(
                "cips", "konserve",
                "hazir", "salamura",
                "tursu", "soya sosu",
                "kuruyemis tuzlu"
            ),
            exceptions = setOf(
                "tuzsuz", "az tuzlu"
            ),
            preferredKeywords = setOf(
                "haslama", "buharda",
                "ev yapimi"
            )
        ),

        // ===================================================
        // 9) GASTRIT / REFLU
        // ===================================================
        HealthTag.GASTRIT_REFLU to HealthRule(
            bannedKeywords = setOf(
                "aci", "pul biber", "isot",
                "sirke", "tursu",
                "kola", "kahve",
                "cikolata",
                "domates sos", "narenciye"
            ),
            exceptions = setOf(
                "acisiz", "az aci"
            ),
            preferredKeywords = setOf(
                "yulaf", "muz",
                "patates",
                "haslama", "buharda"
            )
        ),

        // ===================================================
        // 10) ZAYIFLAMA
        // ===================================================
        HealthTag.ZAYIFLAMA to HealthRule(
            bannedKeywords = setOf(
                "kizartma",
                "tatli", "pasta", "kek",
                "cikolata",
                "krema", "mayonez",
                "fast food", "cips"
            ),
            exceptions = setOf(
                "firinda", "izgara", "haslama"
            ),
            preferredKeywords = setOf(
                "protein",
                "salata", "sebze",
                "izgara", "firinda"
            )
        )
    )
}
