package pl.elpassion.eltc.builds

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.build_item.view.*
import kotlinx.android.synthetic.main.builds_activity.*
import org.ocpsoft.prettytime.PrettyTime
import pl.elpassion.eltc.*
import pl.elpassion.eltc.login.LoginActivity
import java.util.*

class BuildsActivity : BaseActivity() {

    private val builds = mutableListOf<Build>()
    private val prettyTime = PrettyTime(Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.builds_activity)
        setSupportActionBar(toolbar)
        setupRecyclerView()
        initModel()
        swipeToRefreshBuildsList.setOnRefreshListener {
            model.perform(RefreshList)
        }
    }

    override fun showState(state: AppState?) {
        when (state) {
            is LoadingState -> loader.show()
            is LoginState -> openLoginScreen()
            is BuildsState -> {
                loader.hide()
                swipeToRefreshBuildsList.isRefreshing = false
                showBuilds(state.builds)
            }
            is SelectProjectsDialogState -> {
                showSelectProjectsDialog(state.projects)
            }
        }
    }

    private fun setupRecyclerView() {
        buildsListRecyclerView.setHasFixedSize(true)
        buildsListRecyclerView.layoutManager = LinearLayoutManager(this)
        buildsListRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        buildsListRecyclerView.adapter = basicAdapterWithLayoutAndBinder(builds, R.layout.build_item, this::bindItem)
    }

    @SuppressLint("SetTextI18n")
    private fun bindItem(holder: ViewHolderBinder<Build>, item: Build) = with(holder.itemView) {
        if (item.number != null) {
            buildNumber.text = "#${item.number}"
            buildNumber.show()
        } else {
            buildNumber.hide()
        }
        projectName.text = item.buildType.projectName
        if (item.branchName.isNullOrBlank()) {
            branchName.hide()
        } else {
            branchName.text = item.branchName
            branchName.show()
        }
        if (item.state == "queued") {
            buildName.hide()
        } else {
            buildName.text = item.statusText
            buildName.show()
        }
        buildDetails.text = getBuildDetails(item)
        if (item.state == "running") {
            buildStatusBg.hide()
            buildStatusIcon.hide()
            buildProgressBar.show()
        } else {
            buildProgressBar.hide()
            buildStatusBg.setImageResource(getBuildStatusBgResId(item))
            buildStatusBg.show()
            buildStatusIcon.setImageResource(getBuildStatusIconResId(item))
            buildStatusIcon.show()
        }
    }

    private fun getBuildDetails(item: Build) = when (item.state) {
        "queued" -> "Build queued ${prettyTime.format(item.queuedDate)}"
        "running" -> "Build started ${prettyTime.format(item.startDate)}"
        "finished" -> "Build finished ${prettyTime.format(item.finishDate)}"
        else -> null
    }

    private fun getBuildStatusBgResId(item: Build) = when {
        item.state == "queued" -> R.drawable.build_queued_bg
        item.status == "SUCCESS" -> R.drawable.build_success_bg
        else -> R.drawable.build_failure_bg
    }

    private fun getBuildStatusIconResId(item: Build): Int = when {
        item.state == "queued" -> R.drawable.ic_queued
        item.status == "SUCCESS" -> R.drawable.ic_success
        else -> R.drawable.ic_failure
    }

    private fun openLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showBuilds(builds: List<Build>) {
        this.builds.run { clear(); addAll(builds) }
        buildsListRecyclerView.adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.select_projects -> model.perform(SelectProjects)
            R.id.auto_refresh -> {
                val checked = !item.isChecked
                item.isChecked = checked
                model.perform(AutoRefresh(checked))
            }
            R.id.logout -> model.perform(Logout)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showSelectProjectsDialog(projects: List<SelectableProject>) {
        SelectProjectsDialog(projects, {
            model.perform(SubmitProjects(it))
        }, {
            model.perform(RefreshList)
        }).show(fragmentManager, "select_projects")
    }
}
