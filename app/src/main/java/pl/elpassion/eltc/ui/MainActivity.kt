package pl.elpassion.eltc.ui

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.build_list.*
import kotlinx.android.synthetic.main.build_list.view.*
import kotlinx.android.synthetic.main.credentials.*
import pl.elpassion.eltc.*


class MainActivity : LifecycleActivity() {

    lateinit var model: MainModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        save.setOnClickListener {
            val credentials = getCredentials(user.text.toString(), password.text.toString())
            model.perform(SubmitCredentials(address.text.toString(), credentials))
        }
        initModel()
    }

    private fun getCredentials(user: String, password: String): String {
        val data = "$user:$password".toByteArray()
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    private fun initModel() {
        model = ViewModelProviders.of(this).get(MainModel::class.java)
        model.state.observe(this, Observer<AppState> { showState(it) })
    }

    private fun showState(state: AppState?) {
        when (state) {
            null -> {
                credentials.visibility = View.GONE; buildList.visibility = View.GONE
            }
            LoginState -> {
                credentials.visibility = View.VISIBLE; buildList.visibility = View.GONE
            }
            is BuildsState -> {
                credentials.visibility = View.GONE
                buildList.visibility = View.VISIBLE
                buildList.temporaryTextView.text = state.list.toString()
                Log.w("12345", state.toString())
            }
            // TODO: real implementation for Builds state case
            else -> Log.w("12345", state.toString()) // TODO: correctly display other states
        }
    }
}