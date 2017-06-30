package pl.elpassion.eltc.ui

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.View
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.BasicViewHolder
import com.elpassion.android.commons.recycler.basic.asBasicMutableList
import kotlinx.android.synthetic.main.build_item.view.*
import kotlinx.android.synthetic.main.build_list.*
import kotlinx.android.synthetic.main.credentials.*
import pl.elpassion.eltc.*


class MainActivity : LifecycleActivity() {

    lateinit var model: MainModel
    private val builds = mutableListOf<Build>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        save.setOnClickListener {
            val credentials = getCredentials(user.text.toString(), password.text.toString())
            model.perform(SubmitCredentials(address.text.toString(), credentials))
        }
        initModel()
        model.perform(StartApp)
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
        log(state)
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
                showBuilds(state.list)
                log(state)
            }
        // TODO: real implementation for Builds state case
            else -> log(state) // TODO: correctly display other states
        }
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = basicAdapterWithLayoutAndBinder(builds.asBasicMutableList(), R.layout.build_item, this::bindItem)
    }

    private fun bindItem(holder: BasicViewHolder<Build>, item: Build) {
        holder.itemView.buildName.text = item.toString()
    }

    private fun showBuilds(builds: List<Build>) {
        this.builds.run { clear(); addAll(builds) }
        recyclerView.adapter.notifyDataSetChanged()
    }

}