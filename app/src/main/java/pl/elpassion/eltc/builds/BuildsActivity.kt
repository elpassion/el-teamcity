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
        swipeToRefreshBuildsList.setOnRefreshListener {
            model.perform(RefreshList)
        }
    }

    override fun showState(state: AppState?) {
        when (state) {
            is InitialState -> {
                model.perform(RefreshList)
            }
            is LoadingState -> {
                loader.show()
            }
            is LoginState -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
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
        projectName.text = item.buildType.projectName
        buildNumber.text = "#${item.number}"
        buildName.text = item.statusText
        buildDetails.text = "Build ${item.state} ${prettyTime.format(item.finishDate)}"
        buildStatusIcon.setImageResource(when (item.status) {
            "SUCCESS" -> R.drawable.build_success_icon
            else -> R.drawable.build_failure_icon
        })
    }

    private fun showBuilds(builds: List<Build>) {
        this.builds.run { clear(); addAll(builds) }
        buildsListRecyclerView.adapter.notifyDataSetChanged()
    }

    private fun showLoginDetails(state: LoginState) {
        state.errorMessage?.let { coordinator.snack(it) }
        // TODO: set login screen textViews
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.select_projects -> { model.perform(SelectProjects); true }
        R.id.auto_refresh -> {
            val checked = !item.isChecked
            item.isChecked = checked
            model.perform(AutoRefresh(checked))
            true
        }
        R.id.logout -> { model.perform(Logout); true }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showSelectProjectsDialog(projects: List<Project>) {
        SelectProjectsDialog(projects, {
            model.perform(SubmitProject(it))
        }).show(fragmentManager, "select_projects")
    }
}
