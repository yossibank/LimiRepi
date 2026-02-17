package jp.co.yahoo.yossibank.limirepi.feature.receipt.api

/**
 * Gemini APIプロンプト構築
 */
object PromptBuilder {

    /**
     * レシート解析用プロンプトを構築
     */
    fun buildReceiptAnalysisPrompt(): String {
        return """
レシート画像から次のJSON形式で情報を抽出してください（JSONのみ返すこと）：
{
  "storeName": "店舗名",
  "purchaseDate": "YYYY-MM-DD",
  "items": [{
    "name": "商品名",
    "price": 整数,
    "quantity": 整数,
    "category": "food/drink/snack/daily/other",
    "discount": {"name": "割引名", "percentage": 整数またはnull, "amount": 整数} または null
  }],
  "subtotalAmount": 整数またはnull,
  "taxBreakdowns": [{"label": "外8%/外10%/消費税8%/消費税10%など", "amount": 整数}],
  "taxAmount": 整数またはnull,
  "totalAmount": 整数またはnull
}
抽出ルール:
- 商品名の直下にある割引行（例: 割引, 〇%引き, -30円）は直前商品のdiscountとして紐付ける。
- itemsには実商品行のみを入れる。シール割引/割引/値引/〇%引き/クーポン/ポイント利用/小計/合計/預り/お釣り/税関連行は商品として追加しない。
- 「-」「▲」「%」「円引」「値引」「割引」を含む行は、商品行ではなく割引行として扱い、直前商品のdiscountにのみ反映する（新規itemを作らない）。
- item.nameには商品名のみを入れ、割引語（例: シール割引）や税語を含めない。
- discount.amountは正の整数で格納し、マイナス記号は除去する。
- discount.amountは必ず数値で返し、nullを禁止する（不明時は0、%のみ記載時はprice×quantity×percentage/100で算出）。
- 商品ごとの消費税は推定・保持しない。消費税は必ず税集計行から抽出する。
- subtotalAmountは「小計」行の金額を設定する（見つからない場合はnull）。
- taxBreakdownsは「外8%」「外10%」「消費税8%」「消費税10%」など存在する税内訳行のみを配列で返す。
- taxAmountはtaxBreakdowns.amountの合計値を設定する（税内訳が無ければ税合計行の値、なければnull）。
- totalAmountは支払合計（税込）を優先し、支払合計が無い場合は「subtotalAmount + taxAmount」で算出する。
- 見つからない項目はnull。金額・数量・割合は数値のみで返す（通貨記号や%記号は除去）。
        """.trimIndent()
    }
}
