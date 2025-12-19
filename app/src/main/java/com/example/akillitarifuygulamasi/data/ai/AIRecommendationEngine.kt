package com.example.akillitarifuygulamasi.data.ai

import kotlin.math.max
import kotlin.random.Random
import com.example.akillitarifuygulamasi.data.ai.HealthGate


private const val DEBUG_AI = true

object AIRecommendationEngine {

    // ---------------------------------------------
    // ⭐ Result wrapper (for scoring + explainability)
    // ---------------------------------------------
    data class ScoredRecipe<T>(
        val recipe: T,
        val score: Int,
        val reasons: List<String> = emptyList()
    )

    /**
     * Rule-Based + Behavioral Recommendation Engine
     *
     * ✅ HEALTH FILTERING = INGREDIENTS ONLY
     * ❌ description NOT used for health decisions
     */
    fun <T> recommend(
        allRecipes: List<T>,
        ingredientsMap: Map<Int, List<String>>,
        userHealth: HealthTag?,
        viewedIds: Set<Int> = emptySet(),
        favoriteIds: Set<Int> = emptySet(),
        ratedStars: Map<Int, Int> = emptyMap(),
        mealFilter: Set<String>? = null,
        idOf: (T) -> Int,
        titleOf: (T) -> String,
        mealOf: (T) -> String?,
        descriptionOf: (T) -> String?
    ): List<T> {

        val rule = userHealth?.let { AIHealthRules.rules[it] }

        if (DEBUG_AI) {
            android.util.Log.d(
                "AI_DEBUG",
                "START recommend | userHealth=$userHealth | hasRule=${rule != null}"
            )
        }

        // --------------------------------------------------
        // 1) Meal filter
        // --------------------------------------------------
        val filteredByMeal = if (mealFilter.isNullOrEmpty()) {
            allRecipes
        } else {
            allRecipes.filter { recipe ->
                val meal = TextNormalizer.normalize(mealOf(recipe) ?: "")
                meal.isNotBlank() && meal in mealFilter
            }
        }

        // --------------------------------------------------
        // 2) Health rules + scoring (INGREDIENTS ONLY)
        // --------------------------------------------------
        val scoredRecipes = filteredByMeal.mapNotNull { recipe ->

            val id = idOf(recipe)
            val title = titleOf(recipe)
            val ingredients = ingredientsMap[id].orEmpty()

            // ❌ لا مكونات = استبعاد (لو عنده حالة صحية)
            if (userHealth != null && ingredients.isEmpty()) {
                if (DEBUG_AI) {
                    android.util.Log.d(
                        "AI_DEBUG",
                        "EXCLUDED(no_ingredients) id=$id title=$title"
                    )
                }
                return@mapNotNull null
            }

            val haystack = buildIngredientsHaystack(ingredients)

            // =====================================================
            // 🔒 GLOBAL HEALTH GATE (HARD REJECT)
            // =====================================================
            val globalHealthResult = HealthGate.check(
                textRaw = title + " " + ingredients.joinToString(" "),
                userHealthTags = userHealth?.let { setOf(it) } ?: emptySet()
            )

            if (!globalHealthResult.allowed) {
                if (DEBUG_AI) {
                    android.util.Log.d(
                        "AI_FILTER",
                        "REJECT $title | ${globalHealthResult.reason}"
                    )
                }
                return@mapNotNull null
            }

            // =====================================================
            // 🔍 RULE-LEVEL INGREDIENT CHECK (SOFT RULES)
            // =====================================================
            val healthResult =
                rule?.let { passesHealthRules(haystack, it) } ?: PassResult(true)

            if (!healthResult.pass) {
                if (DEBUG_AI) {
                    android.util.Log.d(
                        "AI_DEBUG",
                        "EXCLUDED(banned) id=$id title=$title reasons=${healthResult.reasons}"
                    )
                }
                return@mapNotNull null
            }

            // =====================================================
            // ⭐ SCORING
            // =====================================================
            var score = 0
            val reasons = mutableListOf<String>()

            // Preferred ingredients
            if (rule != null) {
                val preferredHits = countHits(haystack, rule.preferredKeywords)
                if (preferredHits > 0) {
                    score += preferredHits * 3
                    reasons += "preferred+$preferredHits"
                }
            }

            // Favorites
            if (id in favoriteIds) {
                score += 25
                reasons += "favorite+25"
            }

            // Ratings
            ratedStars[id]?.let { stars ->
                score += stars * 6
                reasons += "rated:${stars}★"
            }

            // Viewed
            if (id in viewedIds) {
                score += 5
                reasons += "viewed+5"
            }

            // Ingredient richness
            score += max(0, ingredients.size - 3)

            // Randomness
            score += Random.nextInt(0, 7)
            reasons += "rand+"

            ScoredRecipe(
                recipe = recipe,
                score = score,
                reasons = reasons + healthResult.reasons + "ingCount=${ingredients.size}"
            )
        }


        // --------------------------------------------------
        // 3) Sort by score
        // --------------------------------------------------
        val sorted = scoredRecipes.sortedByDescending { it.score }

        // --------------------------------------------------
        // 4) Diversity
        // --------------------------------------------------
        val diversified = diversify(sorted)

        if (DEBUG_AI) {
            diversified.forEach {
                android.util.Log.d(
                    "AI_DEBUG",
                    "OK title=${titleOf(it.recipe)} | score=${it.score} | reasons=${it.reasons}"
                )
            }
        }

        return diversified.map { it.recipe }
    }

    // ==================================================
    // =================== HELPERS ======================
    // ==================================================

    private data class PassResult(
        val pass: Boolean,
        val reasons: List<String> = emptyList()
    )

    /**
     * ✅ INGREDIENTS ONLY haystack
     */
    private fun buildIngredientsHaystack(ingredients: List<String>): String {
        return TextNormalizer.normalizeList(ingredients).joinToString(" ").trim()
    }

    /**
     * 🚨 FIXED HEALTH FILTER LOGIC
     *
     * ✔ exceptions remove false positives ONLY
     * ❌ exceptions DO NOT cancel real banned ingredients
     */
    private fun passesHealthRules(
        haystack: String,
        rule: HealthRule
    ): PassResult {

        var cleaned = " " + TextNormalizer.normalize(haystack) + " "

        // 1️⃣ remove exception phrases first (prevent false positives)
        for (ex in rule.exceptions) {
            val nex = " " + TextNormalizer.normalize(ex) + " "
            if (nex.isNotBlank()) {
                cleaned = cleaned.replace(nex, " ")
            }
        }

        cleaned = " " + cleaned.trim().replace(Regex("\\s+"), " ") + " "

        // 2️⃣ check banned keywords on CLEANED text
        val bannedHit = rule.bannedKeywords.firstOrNull { bad ->
            val nbad = " " + TextNormalizer.normalize(bad) + " "
            nbad.isNotBlank() && cleaned.contains(nbad)
        }

        return if (bannedHit != null) {
            PassResult(
                pass = false,
                reasons = listOf("banned:$bannedHit")
            )
        } else {
            PassResult(true)
        }
    }

    private fun countHits(
        haystack: String,
        keywords: Set<String>
    ): Int {
        val normalized = TextNormalizer.normalize(haystack)
        var count = 0
        for (k in keywords) {
            val nk = TextNormalizer.normalize(k)
            if (nk.isNotBlank() && normalized.contains(nk)) {
                count++
            }
        }
        return count
    }

    /**
     * ✅ Diversity strategy
     */
    private fun <T> diversify(
        sorted: List<ScoredRecipe<T>>
    ): List<ScoredRecipe<T>> {

        if (sorted.size <= 10) return sorted

        val guaranteed = sorted.take(3)

        val topPoolSize = minOf(25, sorted.size)
        val pool = sorted
            .take(topPoolSize)
            .filterNot { it in guaranteed }
            .toMutableList()

        pool.shuffle(Random(System.currentTimeMillis()))

        val pick = pool.take(10)
        val rest = sorted.filterNot { it in guaranteed || it in pick }

        return buildList {
            addAll(guaranteed)
            addAll(pick)
            addAll(rest)
        }
    }
}
