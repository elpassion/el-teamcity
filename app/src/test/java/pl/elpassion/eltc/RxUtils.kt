package pl.elpassion.eltc

import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals

fun <T> TestObserver<T>.assertLastValue(value: T) {
    assertEquals(value, values().last())
}