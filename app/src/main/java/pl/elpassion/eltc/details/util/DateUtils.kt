package pl.elpassion.eltc.details.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.toTime(): String = SimpleDateFormat("d MMM yy HH:mm:ss", Locale.US).format(this)

fun Date.toTimeWithoutDate(): String = SimpleDateFormat("HH:mm:ss", Locale.US).format(this)