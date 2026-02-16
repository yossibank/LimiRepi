package jp.co.yahoo.yossibank.limirepi.navigation

sealed interface TabAction {
    data object OpenCamera : TabAction
}
