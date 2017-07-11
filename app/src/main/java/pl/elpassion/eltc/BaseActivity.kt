package pl.elpassion.eltc

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {

    lateinit var model: MainModel

    private val registry = LifecycleRegistry(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initModel()
    }

    override fun getLifecycle() = registry

    private fun initModel() {
        model = ViewModelProviders.of(this).get(MainModel::class.java)
        model.state.observe(this, Observer<AppState> { showState(it) })
    }

    abstract fun showState(state: AppState?)
}
