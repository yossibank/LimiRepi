package jp.co.yahoo.yossibank.limirepi.feature.fridge.data

import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeCategory
import jp.co.yahoo.yossibank.limirepi.feature.fridge.model.FridgeItem

object FridgePreview {
    val items = listOf(
        // 葉物野菜
        FridgeItem(
            id = "1",
            name = "ほうれん草",
            category = FridgeCategory.LEAFY_VEGETABLE,
            emoji = "🥬",
            quantity = 1,
            remainingPercent = 0,
            daysUntilExpiration = -2
        ),
        FridgeItem(
            id = "2",
            name = "キャベツ",
            category = FridgeCategory.LEAFY_VEGETABLE,
            emoji = "🥬",
            quantity = 1,
            remainingPercent = 15,
            daysUntilExpiration = 1
        ),
        FridgeItem(
            id = "3",
            name = "レタス",
            category = FridgeCategory.LEAFY_VEGETABLE,
            emoji = "🥗",
            quantity = 1,
            remainingPercent = 90,
            daysUntilExpiration = 3
        ),
        // 根菜
        FridgeItem(
            id = "4",
            name = "人参",
            category = FridgeCategory.ROOT_VEGETABLE,
            emoji = "🥕",
            quantity = 2,
            remainingPercent = 80,
            daysUntilExpiration = 5
        ),
        FridgeItem(
            id = "5",
            name = "玉ねぎ",
            category = FridgeCategory.ROOT_VEGETABLE,
            emoji = "🧅",
            quantity = 4,
            remainingPercent = 100,
            daysUntilExpiration = 20
        ),
        FridgeItem(
            id = "6",
            name = "じゃがいも",
            category = FridgeCategory.ROOT_VEGETABLE,
            emoji = "🥔",
            quantity = 7,
            remainingPercent = 95,
            daysUntilExpiration = 15
        ),
        // 果物
        FridgeItem(
            id = "7",
            name = "りんご",
            category = FridgeCategory.FRUIT,
            emoji = "🍎",
            quantity = 3,
            remainingPercent = 100,
            daysUntilExpiration = 10
        ),
        FridgeItem(
            id = "8",
            name = "トマト",
            category = FridgeCategory.FRUIT,
            emoji = "🍅",
            quantity = 5,
            remainingPercent = 70,
            daysUntilExpiration = 4
        ),
        FridgeItem(
            id = "9",
            name = "バナナ",
            category = FridgeCategory.FRUIT,
            emoji = "🍌",
            quantity = 6,
            remainingPercent = 55,
            daysUntilExpiration = 2
        ),
        // きのこ類
        FridgeItem(
            id = "10",
            name = "しいたけ",
            category = FridgeCategory.MUSHROOM,
            emoji = "🍄",
            quantity = 8,
            remainingPercent = 65,
            daysUntilExpiration = 3
        ),
        FridgeItem(
            id = "11",
            name = "えのき",
            category = FridgeCategory.MUSHROOM,
            emoji = "🍄",
            quantity = 2,
            remainingPercent = 40,
            daysUntilExpiration = 2
        ),
        // 肉類
        FridgeItem(
            id = "12",
            name = "豚バラ肉",
            category = FridgeCategory.MEAT,
            emoji = "🥩",
            quantity = 1,
            remainingPercent = 10,
            daysUntilExpiration = 1
        ),
        FridgeItem(
            id = "13",
            name = "鶏もも肉",
            category = FridgeCategory.MEAT,
            emoji = "🍗",
            quantity = 1,
            remainingPercent = 85,
            daysUntilExpiration = 2
        ),
        FridgeItem(
            id = "14",
            name = "牛肉薄切り",
            category = FridgeCategory.MEAT,
            emoji = "🥩",
            quantity = 1,
            remainingPercent = 45,
            daysUntilExpiration = 1
        ),
        FridgeItem(
            id = "15",
            name = "豚ひき肉",
            category = FridgeCategory.MEAT,
            emoji = "🥩",
            quantity = 1,
            remainingPercent = 20,
            daysUntilExpiration = 0
        ),
        // 魚介類
        FridgeItem(
            id = "16",
            name = "鮭の切り身",
            category = FridgeCategory.FISH,
            emoji = "🐟",
            quantity = 2,
            remainingPercent = 100,
            daysUntilExpiration = 3
        ),
        FridgeItem(
            id = "17",
            name = "サバ",
            category = FridgeCategory.FISH,
            emoji = "🐟",
            quantity = 2,
            remainingPercent = 75,
            daysUntilExpiration = 2
        ),
        FridgeItem(
            id = "18",
            name = "エビ",
            category = FridgeCategory.FISH,
            emoji = "🦐",
            quantity = 11,
            remainingPercent = 50,
            daysUntilExpiration = 1
        ),
        // 加工肉
        FridgeItem(
            id = "19",
            name = "ハム",
            category = FridgeCategory.PROCESSED_MEAT,
            emoji = "🥓",
            quantity = 8,
            remainingPercent = 60,
            daysUntilExpiration = 7
        ),
        FridgeItem(
            id = "20",
            name = "ベーコン",
            category = FridgeCategory.PROCESSED_MEAT,
            emoji = "🥓",
            quantity = 1,
            remainingPercent = 45,
            daysUntilExpiration = 5
        ),
        // 乳製品
        FridgeItem(
            id = "21",
            name = "牛乳",
            category = FridgeCategory.DAIRY,
            emoji = "🥛",
            quantity = 1,
            remainingPercent = 30,
            daysUntilExpiration = 4
        ),
        FridgeItem(
            id = "22",
            name = "ヨーグルト",
            category = FridgeCategory.DAIRY,
            emoji = "🥛",
            quantity = 4,
            remainingPercent = 80,
            daysUntilExpiration = 7
        ),
        FridgeItem(
            id = "23",
            name = "チーズ",
            category = FridgeCategory.DAIRY,
            emoji = "🧀",
            quantity = 1,
            remainingPercent = 55,
            daysUntilExpiration = 10
        ),
        FridgeItem(
            id = "24",
            name = "バター",
            category = FridgeCategory.DAIRY,
            emoji = "🧈",
            quantity = 1,
            remainingPercent = 25,
            daysUntilExpiration = 30
        ),
        // 卵
        FridgeItem(
            id = "25",
            name = "卵",
            category = FridgeCategory.EGG,
            emoji = "🥚",
            quantity = 6,
            remainingPercent = 60,
            daysUntilExpiration = 14
        ),
        // 豆腐・大豆製品
        FridgeItem(
            id = "26",
            name = "豆腐",
            category = FridgeCategory.TOFU_SOY,
            emoji = "🧈",
            quantity = 2,
            remainingPercent = 40,
            daysUntilExpiration = 3
        ),
        FridgeItem(
            id = "27",
            name = "納豆",
            category = FridgeCategory.TOFU_SOY,
            emoji = "🥢",
            quantity = 3,
            remainingPercent = 100,
            daysUntilExpiration = 5
        ),
        FridgeItem(
            id = "28",
            name = "油揚げ",
            category = FridgeCategory.TOFU_SOY,
            emoji = "🍲",
            quantity = 4,
            remainingPercent = 70,
            daysUntilExpiration = 4
        ),
        // 作り置き
        FridgeItem(
            id = "29",
            name = "カレー",
            category = FridgeCategory.PREPARED,
            emoji = "🍛",
            quantity = 1,
            remainingPercent = 40,
            daysUntilExpiration = 2
        ),
        FridgeItem(
            id = "30",
            name = "煮物",
            category = FridgeCategory.PREPARED,
            emoji = "🍲",
            quantity = 1,
            remainingPercent = 30,
            daysUntilExpiration = 1
        ),
        FridgeItem(
            id = "31",
            name = "ポテトサラダ",
            category = FridgeCategory.PREPARED,
            emoji = "🥗",
            quantity = 1,
            remainingPercent = 60,
            daysUntilExpiration = 1
        ),
        // 残り物
        FridgeItem(
            id = "32",
            name = "唐揚げ",
            category = FridgeCategory.LEFTOVER,
            emoji = "🍗",
            quantity = 5,
            remainingPercent = 80,
            daysUntilExpiration = 0
        ),
        FridgeItem(
            id = "33",
            name = "ご飯",
            category = FridgeCategory.LEFTOVER,
            emoji = "🍚",
            quantity = 3,
            remainingPercent = 100,
            daysUntilExpiration = 1
        ),
        // 冷凍食品
        FridgeItem(
            id = "34",
            name = "冷凍うどん",
            category = FridgeCategory.FROZEN_FOOD,
            emoji = "🍜",
            quantity = 3,
            remainingPercent = 100,
            daysUntilExpiration = 90
        ),
        FridgeItem(
            id = "35",
            name = "冷凍餃子",
            category = FridgeCategory.FROZEN_FOOD,
            emoji = "🥟",
            quantity = 2,
            remainingPercent = 75,
            daysUntilExpiration = 45
        ),
        FridgeItem(
            id = "36",
            name = "アイスクリーム",
            category = FridgeCategory.FROZEN_FOOD,
            emoji = "🍨",
            quantity = 5,
            remainingPercent = 100,
            daysUntilExpiration = 180
        ),
        // 冷凍保存
        FridgeItem(
            id = "37",
            name = "冷凍ブロッコリー",
            category = FridgeCategory.FROZEN_HOMEMADE,
            emoji = "🥦",
            quantity = 1,
            remainingPercent = 50,
            daysUntilExpiration = 60
        ),
        FridgeItem(
            id = "38",
            name = "冷凍ミックスベジタブル",
            category = FridgeCategory.FROZEN_HOMEMADE,
            emoji = "🥕",
            quantity = 1,
            remainingPercent = 40,
            daysUntilExpiration = 30
        ),
        // 調味料
        FridgeItem(
            id = "39",
            name = "醤油",
            category = FridgeCategory.SEASONING,
            emoji = "🫙",
            quantity = 1,
            remainingPercent = 20,
            daysUntilExpiration = 180
        ),
        FridgeItem(
            id = "40",
            name = "味噌",
            category = FridgeCategory.SEASONING,
            emoji = "🫙",
            quantity = 1,
            remainingPercent = 45,
            daysUntilExpiration = 90
        ),
        FridgeItem(
            id = "41",
            name = "塩",
            category = FridgeCategory.SEASONING,
            emoji = "🧂",
            quantity = 1,
            remainingPercent = 80,
            daysUntilExpiration = 365
        ),
        // ソース・油
        FridgeItem(
            id = "42",
            name = "マヨネーズ",
            category = FridgeCategory.SAUCE_OIL,
            emoji = "🫙",
            quantity = 1,
            remainingPercent = 35,
            daysUntilExpiration = 60
        ),
        FridgeItem(
            id = "43",
            name = "ケチャップ",
            category = FridgeCategory.SAUCE_OIL,
            emoji = "🫙",
            quantity = 1,
            remainingPercent = 60,
            daysUntilExpiration = 120
        ),
        FridgeItem(
            id = "44",
            name = "オリーブオイル",
            category = FridgeCategory.SAUCE_OIL,
            emoji = "🫙",
            quantity = 1,
            remainingPercent = 50,
            daysUntilExpiration = 200
        ),
        // 飲料
        FridgeItem(
            id = "45",
            name = "オレンジジュース",
            category = FridgeCategory.BEVERAGE,
            emoji = "🧃",
            quantity = 1,
            remainingPercent = 70,
            daysUntilExpiration = 5
        ),
        FridgeItem(
            id = "46",
            name = "コーラ",
            category = FridgeCategory.BEVERAGE,
            emoji = "🥤",
            quantity = 3,
            remainingPercent = 100,
            daysUntilExpiration = 90
        ),
        // その他
        FridgeItem(
            id = "47",
            name = "パン",
            category = FridgeCategory.OTHER,
            emoji = "🍞",
            quantity = 1,
            remainingPercent = 50,
            daysUntilExpiration = 3
        ),
        FridgeItem(
            id = "48",
            name = "ジャム",
            category = FridgeCategory.OTHER,
            emoji = "🫙",
            quantity = 1,
            remainingPercent = 45,
            daysUntilExpiration = 90
        ),
        FridgeItem(
            id = "49",
            name = "ハム",
            category = FridgeCategory.OTHER,
            emoji = "🥓",
            quantity = 1,
            remainingPercent = 20,
            daysUntilExpiration = 2
        ),
        FridgeItem(
            id = "50",
            name = "わかめ",
            category = FridgeCategory.OTHER,
            emoji = "🥬",
            quantity = 1,
            remainingPercent = 90,
            daysUntilExpiration = 120
        )
    )

}
