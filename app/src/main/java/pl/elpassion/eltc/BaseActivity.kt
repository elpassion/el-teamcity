package pl.elpassion.eltc

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers

abstract class BaseActivity : RxAppCompatActivity() {

    val model by lazy { DI.provideTeamCityModel() }

    protected fun initModel() {
        model.state
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log("STATE: $it")
                    showState(it)
                }
    }

    abstract fun showState(state: AppState?)
}
