package pl.elpassion.eltc.util

import io.reactivex.Scheduler

data class SchedulersSupplier(val backgroundScheduler: Scheduler, val uiScheduler: Scheduler)