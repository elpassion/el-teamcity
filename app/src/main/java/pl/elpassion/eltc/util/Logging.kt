package pl.elpassion.eltc.util

import android.util.Log

fun Any.log(message: Any?) = Log.w(javaClass.simpleName, message.toString())