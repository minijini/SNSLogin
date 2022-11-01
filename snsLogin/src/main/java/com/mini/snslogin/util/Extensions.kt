package com.mini.snslogin.util

internal fun Float.scale(): Float {
    return this * 3.4f
}

internal fun <T> List<T>?.toSafe() : List<T> {
    return this ?: emptyList()
}

internal fun Boolean?.toSafe() : Boolean {
    return this ?: false
}

internal fun Int?.toSafe() : Int{
    return this ?: 0
}

internal fun String?.toSafe() : String{
    return this ?: ""
}

internal fun Long?.toSafe() : Long{
    return this ?: 0
}
