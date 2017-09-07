package pl.elpassion.eltc.util

import io.reactivex.Scheduler

data class SchedulersSupplier(val subscribeOn: Scheduler, val observeOn: Scheduler)