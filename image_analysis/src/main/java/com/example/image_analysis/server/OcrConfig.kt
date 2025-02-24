// 模型类型枚举
enum class OcrModel {
    BANK_CARD,
    GENERAL_TEXT,
    // 可扩展其他模型
}

// 基础配置类
sealed class OcrConfig {
    abstract val minQuality: Int
    abstract val maxSizeMB: Int

    data class BankCardConfig(
        override val minQuality: Int = 70,
        override val maxSizeMB: Int = 2,
        val enableCardCrop: Boolean = true,
        val requiredFields: Set<String> = setOf("cardNumber")
    ) : OcrConfig()

    data class TextConfig(
        override val minQuality: Int = 60,
        override val maxSizeMB: Int = 3,
        val minTextHeight: Int = 24,
        val outputProbability: Boolean = true
    ) : OcrConfig()
}