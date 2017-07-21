package pl.elpassion.eltc

import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.mockito.stubbing.OngoingStubbing

fun <T> TestObserver<T>.assertLastValue(value: T) {
    assertEquals(value, values().last())
}

fun <T> OngoingStubbing<Single<T>>.thenNever(): OngoingStubbing<Single<T>> =
        thenReturn(Single.never())

fun <T> OngoingStubbing<Single<T>>.thenJust(value: T): OngoingStubbing<Single<T>> =
        thenReturn(Single.just(value))

fun <T> OngoingStubbing<Single<T>>.thenError(exception: Exception = RuntimeException()): OngoingStubbing<Single<T>> =
        thenReturn(Single.error(exception))
