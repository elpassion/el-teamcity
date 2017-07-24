package pl.elpassion.eltc.util

import android.support.design.widget.Snackbar
import android.view.View

fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG) {
    val snack = Snackbar.make(this, message, length)
    snack.show()
}
