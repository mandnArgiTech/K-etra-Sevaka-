package com.ksetrasevakah.core.vectorstore.tokenizer

/**
 * BERT-style WordPiece tokenizer backed by a `vocab.txt` (one token per line, index = id).
 */
class WordPieceTokenizer(private val vocab: Map<String, Int>) {

    private val unkId: Int = vocab[VocabSpecial.UNK] ?: 100
    private val clsId: Int = vocab["[CLS]"] ?: 101
    private val sepId: Int = vocab["[SEP]"] ?: 102
    private val padId: Int = vocab[VocabSpecial.PAD] ?: vocab[""] ?: 0

    fun encode(text: String, maxLength: Int = 128): Pair<LongArray, LongArray> {
        val basic = basicTokenize(text.lowercase())
        val pieces = mutableListOf<Int>()
        pieces.add(clsId)
        for (word in basic) {
            if (pieces.size >= maxLength - 1) break
            pieces.addAll(wordPieceIds(word))
        }
        pieces.add(sepId)

        val inputIds = LongArray(maxLength) { padId.toLong() }
        val attentionMask = LongArray(maxLength) { 0L }
        val len = minOf(pieces.size, maxLength)
        for (i in 0 until len) {
            inputIds[i] = pieces[i].toLong()
            attentionMask[i] = 1L
        }
        return inputIds to attentionMask
    }

    private fun basicTokenize(text: String): List<String> {
        val out = mutableListOf<String>()
        val sb = StringBuilder()
        for (ch in text) {
            if (ch.isWhitespace()) {
                if (sb.isNotEmpty()) {
                    out.add(sb.toString())
                    sb.clear()
                }
            } else if (isPunctuation(ch)) {
                if (sb.isNotEmpty()) {
                    out.add(sb.toString())
                    sb.clear()
                }
                out.add(ch.toString())
            } else {
                sb.append(ch)
            }
        }
        if (sb.isNotEmpty()) out.add(sb.toString())
        return out.filter { it.isNotBlank() }
    }

    private fun isPunctuation(c: Char): Boolean {
        return c in CHARS || !c.isLetterOrDigit()
    }

    private fun wordPieceIds(token: String): List<Int> {
        if (token.isEmpty()) return emptyList()
        if (vocab.containsKey(token)) return listOf(vocab.getValue(token))

        val output = mutableListOf<Int>()
        var start = 0
        while (start < token.length) {
            var end = token.length
            var found: String? = null
            while (start < end) {
                val substr = token.substring(start, end)
                val piece = if (start > 0) "##$substr" else substr
                if (vocab.containsKey(piece)) {
                    found = piece
                    break
                }
                end--
            }
            if (found == null) return listOf(unkId)
            output.add(vocab.getValue(found))
            start = end
        }
        return output
    }

    companion object {
        private val CHARS = setOf('`', '"', ',', '.', ';', ':', '!', '?', '(', ')', '[', ']', '{', '}', '-', '_')

        fun fromVocabLines(lines: List<String>): WordPieceTokenizer {
            val map = HashMap<String, Int>(lines.size)
            lines.forEachIndexed { index, line ->
                val token = line.trim()
                if (token.isNotEmpty()) map[token] = index
            }
            return WordPieceTokenizer(map)
        }
    }

    private object VocabSpecial {
        /** Matches bert-base-uncased / MiniLM `vocab.txt` specials */
        val UNK: String = buildString { append('<'); append("unk"); append('>') }
        val PAD: String = buildString { append('<'); append("pad"); append('>') }
    }
}
