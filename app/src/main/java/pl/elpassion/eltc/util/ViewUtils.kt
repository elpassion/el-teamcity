package pl.elpassion.eltc.util

import android.support.design.widget.Snackbar
import android.view.View
import android.view.ViewGroup

val ViewGroup.views get() = (0 until childCount).map { getChildAt(it) }

fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG) {
    val snack = Snackbar.make(this, message, length)
    snack.show()
}
