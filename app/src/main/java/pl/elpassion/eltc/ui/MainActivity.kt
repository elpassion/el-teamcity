package pl.elpassion.eltc.ui

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.build_list.*
import kotlinx.android.synthetic.main.credentials.*
import pl.elpassion.eltc.AppState
import pl.elpassion.eltc.Builds
import pl.elpassion.eltc.NoCredentials
import pl.elpassion.eltc.R


class MainActivity: LifecycleActivity() {

    lateinit var model: MainModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initModel()
    }

    private fun initModel() {
        model = ViewModelProviders.of(this).get(MainModel::class.java)
        model.state.observe(this, Observer<AppState> { showState(it) })
    }

    private fun showState(state: AppState?) {
        when(state) {
            null -> { credentials.visibility = View.GONE; buildList.visibility = View.GONE }
            NoCredentials -> { credentials.visibility = View.VISIBLE; buildList.visibility = View.GONE }
            is Builds -> { credentials.visibility = View.GONE; buildList.visibility = View.VISIBLE }
        }
    }
}