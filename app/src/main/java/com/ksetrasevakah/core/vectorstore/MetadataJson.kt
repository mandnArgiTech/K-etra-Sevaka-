package com.ksetrasevakah.core.vectorstore

/**
 * Minimal JSON object encoding for flat string maps (JVM + Android; avoids stubbed android.org.json in unit tests).
 */
internal fun metadataToJson(metadata: Map<String, String>): String {
    if (metadata.isEmpty()) return "{}"
    return buildString {
        append('{')
        metadata.entries.forEachIndexed { i, (k, v) ->
            if (i > 0) append(',')
            append('"').append(jsonEscape(k)).append("\":\"").append(jsonEscape(v)).append('"')
        }
        append('}')
    }
}

internal fun jsonToMetadataMap(json: String): Map<String, String> {
    val t = json.trim()
    if (t.isEmpty() || t == "{}") return emptyMap()
    if (!t.startsWith('{') || !t.endsWith('}')) return emptyMap()
    val inner = t.substring(1, t.length - 1).trim()
    if (inner.isEmpty()) return emptyMap()
    val result = mutableMapOf<String, String>()
    var i = 0
    while (i < inner.length) {
        while (i < inner.length && inner[i].isWhitespace()) i++
        if (i >= inner.length) break
        if (inner[i] != '"') break
        i++
        val key = StringBuilder()
        while (i < inner.length) {
            when (val c = inner[i]) {
                '\\' -> {
                    i++
                    if (i < inner.length) key.append(inner[i])
                    i++
                }
                '"' -> {
                    i++
                    break
                }
                else -> {
                    key.append(c)
                    i++
                }
            }
        }
        while (i < inner.length && (inner[i].isWhitespace() || inner[i] == ':')) i++
        if (i >= inner.length || inner[i] != '"') break
        i++
        val value = StringBuilder()
        while (i < inner.length) {
            when (val c = inner[i]) {
                '\\' -> {
                    i++
                    if (i < inner.length) value.append(inner[i])
                    i++
                }
                '"' -> {
                    i++
                    break
                }
                else -> {
                    value.append(c)
                    i++
                }
            }
        }
        result[key.toString()] = value.toString()
        while (i < inner.length && (inner[i].isWhitespace() || inner[i] == ',')) i++
    }
    return result
}

private fun jsonEscape(s: String): String = buildString(s.length + 4) {
    for (c in s) {
        when (c) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> append(c)
        }
    }
}
