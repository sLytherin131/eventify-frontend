package com.example.utils

fun String.truncate(maxLength: Int): String =
    if (this.length > maxLength) this.take(maxLength) + "..." else this
