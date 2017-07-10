package pl.elpassion.eltc

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {
    private val registry = LifecycleRegistry(this)
    override fun getLifecycle() = registry
}
