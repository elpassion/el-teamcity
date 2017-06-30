package pl.elpassion.eltc.ui

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {
    val registry = LifecycleRegistry(this)
    override fun getLifecycle() = registry
}
