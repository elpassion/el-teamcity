package pl.elpassion.eltc

import android.arch.lifecycle.ViewModelProviders
import android.util.Log
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle

abstract class BaseActivity : RxAppCompatActivity() {

    lateinit var model: MainModel

    protected fun initModel() {
        model = ViewModelProviders.of(this).get(MainModel::class.java)
        model.state
                .bindToLifecycle(this)
                .subscribe {
                    Log.w("NEW STATE", it.toString())
                    showState(it)
                }
    }

    abstract fun showState(state: AppState?)
}
