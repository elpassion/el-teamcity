package pl.elpassion.eltc

import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import pl.elpassion.eltc.util.log

abstract class BasePreferenceFragmentCompat : RxPreferenceFragmentCompat() {

    val model by lazy { DI.provideTeamCityModel() }

    protected fun initModel() {
        model.state
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log("STATE: $it")
                    if (!isDetached) {
                        showState(it)
                    }
                }
    }

    abstract fun showState(state: AppState?)
}