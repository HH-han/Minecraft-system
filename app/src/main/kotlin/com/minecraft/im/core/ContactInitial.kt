package com.minecraft.im.core

/**
 * 取联系人名称的索引首字母：英文取大写首字母，汉字按 GBK 编码区间映射到拼音首字母，其余归入 "#"。
 */
private val PINYIN_BOUNDARY = intArrayOf(
    -20319, -20284, -19776, -19219, -18711, -18527, -18240, -17923, -17418, -16475,
    -16213, -15641, -15166, -14923, -14915, -14631, -14150, -14091, -13319, -12839,
    -12557, -11848, -11056, -10240, -9500, -9221, -9120
)

private const val LETTERS = "ABCDEFGHJKLMNOPQRSTWXYZ"

fun contactInitial(name: String?): String {
    val first = name?.trim()?.firstOrNull() ?: return "#"
    if (first.isLetter() && first.code < 128) return first.uppercase()
    val gbk = runCatching { first.toString().toByteArray(charset("GBK")) }.getOrNull()
    if (gbk == null || gbk.size != 2) return "#"
    val code = (gbk[0].toInt() and 0xFF) * 256 + (gbk[1].toInt() and 0xFF) - 65536
    if (code < -20319 || code > -10247) return "#"
    var index = -1
    for (i in PINYIN_BOUNDARY.indices) {
        if (code >= PINYIN_BOUNDARY[i]) index = i
    }
    return LETTERS.getOrNull(index)?.toString() ?: "#"
}
