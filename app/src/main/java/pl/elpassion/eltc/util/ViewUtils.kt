package pl.elpassion.eltc.util

import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, length).show()
}

val Toolbar.textView: TextView get() = views.find { it is TextView } as TextView

val ViewGroup.views: List<View> get() = (0 until childCount).map { getChildAt(it) }