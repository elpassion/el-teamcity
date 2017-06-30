package pl.elpassion.eltc

import android.util.Log

fun Any.log(message: Any?) = Log.w(javaClass.simpleName, message.toString())