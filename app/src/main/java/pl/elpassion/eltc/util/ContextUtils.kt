package pl.elpassion.eltc.util

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

inline fun Context.inflate(@LayoutRes layoutResId: Int,
                           parent: ViewGroup? = null,
                           attachToRoot: Boolean = false): View
        = LayoutInflater.from(this).inflate(layoutResId, parent, attachToRoot)