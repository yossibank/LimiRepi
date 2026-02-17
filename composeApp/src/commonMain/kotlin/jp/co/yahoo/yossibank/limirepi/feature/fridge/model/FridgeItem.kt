package jp.co.yahoo.yossibank.limirepi.feature.fridge.model

/**
 * 冷蔵庫の食材データモデル
 *
 * @param id 一意識別子
 * @param name 食材名
 * @param category カテゴリ
 * @param emoji 表示用絵文字
 * @param quantity 数量
 * @param remainingPercent 残量パーセント（0〜100）
 * @param expirationDate 賞味期限（yyyy-MM-dd形式）
 * @param daysUntilExpiration 期限までの残り日数（負の値は期限切れ）
 */
data class FridgeItem(
    val id: String,
    val name: String,
    val category: FridgeCategory,
    val emoji: String,
    val quantity: Int = 1,
    val remainingPercent: Int = 100,
    val expirationDate: String = "",
    val daysUntilExpiration: Int = Int.MAX_VALUE
) {
    /**
     * 期限切れかどうか
     */
    val isExpired: Boolean
        get() = daysUntilExpiration < 0

    /**
     * 3日以内に期限切れ
     */
    val isUrgent: Boolean
        get() = daysUntilExpiration in 0..3

    /**
     * 1週間以内に期限切れ
     */
    val isWarning: Boolean
        get() = daysUntilExpiration in 4..7

    /**
     * 期限表示テキスト
     */
    val expirationDisplayText: String
        get() = when {
            isExpired -> "！期限切れ"
            daysUntilExpiration == 0 -> "今日まで"
            daysUntilExpiration == Int.MAX_VALUE -> ""
            else -> "あと${daysUntilExpiration}日"
        }
}
