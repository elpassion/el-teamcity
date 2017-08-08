package pl.elpassion.eltc.util

import android.util.Log

typealias Logger = (String?, String) -> Unit

val androidLogger: Logger = { tag, message -> Log.v(tag, message) }

val testLogger: Logger = { _, message -> println(message) }

var logger: Logger = androidLogger

fun Any.log(message: Any) = logger(javaClass.simpleName, message.toString())