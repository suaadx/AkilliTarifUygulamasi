package com.example.akillitarifuygulamasi.data.ai

object TextNormalizer {

    /**
     * Normalize text into a clean comparable form
     * - lowercase
     * - Turkish char normalization
     * - punctuation removal
     * - whitespace cleanup
     */
    fun normalize(input: String?): String {
        if (input.isNullOrBlank()) return ""

        var s = input.lowercase()

        // Turkish character normalization
        s = s
            .replace("ğ", "g")
            .replace("ü", "u")
            .replace("ş", "s")
            .replace("ı", "i")
            .replace("ö", "o")
            .replace("ç", "c")

        // Remove punctuation/symbols
        s = s.replace(Regex("[^a-z0-9\\s]"), " ")

        // Collapse spaces
        s = s.replace(Regex("\\s+"), " ").trim()

        return s
    }

    /**
     * Normalize a list of strings (ingredients, etc.)
     */
    fun normalizeList(items: List<String>): List<String> =
        items.flatMap { tokenize(it) }.filter { it.isNotBlank() }

    /**
     * 🔑 TOKENIZER + SIMPLE TURKISH STEMMING
     *
     * Examples:
     * - kremali   -> krema
     * - tereyagli -> tereyag
     * - kizartmasi -> kizartma
     */
    fun tokenize(input: String?): List<String> {
        if (input.isNullOrBlank()) return emptyList()

        val base = normalize(input)

        val rawTokens = base.split(" ")

        val tokens = mutableSetOf<String>()

        for (token in rawTokens) {
            if (token.length < 3) continue

            tokens += token

            // --- common Turkish suffix stripping (VERY IMPORTANT) ---
            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")
            tokens += token.removeSuffix("lu")
            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("siz")
            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")
            tokens += token.removeSuffix("li")

            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")
            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")

            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")

            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")

            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")

            // food-specific suffixes
            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")
            tokens += token.removeSuffix("li")
            tokens += token.removeSuffix("lu")

            tokens += token.removeSuffix("masi")
            tokens += token.removeSuffix("mesi")
            tokens += token.removeSuffix("si")
            tokens += token.removeSuffix("i")
        }

        return tokens.filter { it.isNotBlank() }
    }
}
