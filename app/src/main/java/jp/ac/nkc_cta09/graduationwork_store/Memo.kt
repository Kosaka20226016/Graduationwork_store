package jp.ac.nkc_cta09.graduationwork_store

data class Memo(
    val id: String? = null, // ← 追加
    val date: String = "",
    val style: String = "",
    val length: String = "",
    val purpose: String = "",
    val staff: String = "",
    val memo: String = ""
)

