package pl.elpassion.eltc.builds

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.build_item.view.*
import kotlinx.android.synthetic.main.builds_activity.*
import org.ocpsoft.prettytime.PrettyTime
import pl.elpassion.eltc.*
import pl.elpassion.eltc.util.notificationManager
import java.util.*
import android.util.Pair as TPair

class BuildsActivity : BaseActivity() {

    private val builds = mutableListOf<Build>()
    private val prettyTime = PrettyTime(Locale.US)
    private var options: ActivityOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.builds_activity)
        setSupportActionBar(toolbar)
        setupRecyclerView()
        initModel()
        swipeToRefreshBuildsList.setOnRefreshListener {
            model.perform(RefreshList)
        }
        checkExtras(intent.extras)
        notificationManager.cancelAll()
        scheduleRecapService()
    }

    private fun checkExtras(bundle: Bundle?) {
        val build = bundle?.getParcelable<Build>(BUILD_KEY)
        if (build != null) {
            model.perform(SelectBuild(build))
        }
    }

    override fun showState(state: AppState?) {
        when (state) {
            is LoadingBuildsState -> loader.show()
            is LoginState -> {
                cancelRecapService()
                openLoginScreen()
            }
            is BuildsState -> {
                loader.hide()
                swipeToRefreshBuildsList.isRefreshing = false
                showBuilds(state.builds)
            }
            is LoadingDetailsState -> openDetailsScreen(options?.toBundle())
            is SelectProjectsDialogState -> {
                showSelectProjectsDialog(state.projects)
            }
        }
    }

    private fun setupRecyclerView() {
        buildsRecyclerView.setHasFixedSize(true)
        buildsRecyclerView.layoutManager = LinearLayoutManager(this)
        buildsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        buildsRecyclerView.adapter = basicAdapterWithLayoutAndBinder(builds, R.layout.build_item, this::bindItem)
    }

    @SuppressLint("SetTextI18n")
    private fun bindItem(holder: ViewHolderBinder<Build>, item: Build) = with(holder.itemView) {
        projectName.text = item.buildType.projectName
        bindBuildNumber(buildNumber, item.number)
        bindBranchName(branchName, item.branchName)
        bindBuildName(buildName, item)
        buildDetails.text = getBuildDetails(item)
        bindBuildStatus(buildStatusBg, buildStatusIcon, buildProgressBar, item)
        setOnClickListener {
            if (item.state != "queued") {
                options = newTransitionAnimation(buildNumber, buildName, projectName, buildDetails)
                model.perform(SelectBuild(item))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindBuildNumber(view: TextView, number: String?) {
        if (number != null) {
            view.text = "#$number"
            view.show()
        } else {
            view.hide()
        }
    }

    private fun bindBranchName(view: TextView, branchName: String?) {
        if (branchName.isNullOrBlank()) {
            view.hide()
        } else {
            view.text = branchName
            view.show()
        }
    }

    private fun bindBuildName(view: TextView, item: Build) {
        if (item.state == "queued") {
            view.hide()
        } else {
            view.text = item.statusText
            view.show()
        }
    }

    private fun bindBuildStatus(bg: ImageView, icon: ImageView, loader: ProgressBar, item: Build) {
        if (item.state == "running") {
            bg.hide()
            icon.hide()
            loader.progress = 0
            loader.show()
        } else {
            loader.hide()
            bg.setImageResource(getBuildStatusBgResId(item))
            bg.show()
            icon.setImageResource(getBuildStatusIconResId(item))
            icon.show()
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
        item.status == Status.SUCCESS -> R.drawable.build_success_bg
        else -> R.drawable.build_failure_bg
    }

    private fun getBuildStatusIconResId(item: Build): Int = when {
        item.state == "queued" -> R.drawable.ic_queued
        item.status == Status.SUCCESS -> R.drawable.ic_success
        else -> R.drawable.ic_failure
    }

    private fun showBuilds(builds: List<Build>) {
        this.builds.run { clear(); addAll(builds) }
        buildsRecyclerView.adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.builds_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.select_projects -> model.perform(SelectProjects)
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

    companion object {
        const val BUILD_KEY = "build_key"
    }
}
