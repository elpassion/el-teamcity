package pl.elpassion.eltc.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.build_item.view.*
import kotlinx.android.synthetic.main.builds_screen.*
import kotlinx.android.synthetic.main.loading_screen.*
import kotlinx.android.synthetic.main.login_screen.*
import org.ocpsoft.prettytime.PrettyTime
import pl.elpassion.eltc.*
import java.util.*


class MainActivity : BaseActivity() {

    lateinit var model: MainModel
    private val builds = mutableListOf<Build>()
    private val prettyTime = PrettyTime(Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupRecyclerView()
        save.setOnClickListener {
            val credentials = getCredentials(user.text.toString(), password.text.toString())
            model.perform(SubmitCredentials(address.text.toString(), credentials))
        }
        swipeToRefreshBuildsList.setOnRefreshListener {
            model.perform(RefreshList)
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
        swipeToRefreshBuildsList.isRefreshing = false
        log(state)
        when (state) {
            null -> screens.showOneChild(null)
            is InitialState -> screens.showOneChild(null)
            is LoadingState -> screens.showOneChild(loadingScreen)
            is LoginState -> {
                screens.showOneChild(loginScreen)
                showLoginDetails(state)
            }
            is MainState -> {
                screens.showOneChild(buildsScreen)
                showBuilds(state.builds)
            }
            is SelectProjectsDialogState -> showSelectProjectsDialog(state.projects)
        }
    }

    private fun setupRecyclerView() {
        buildsListRecyclerView.setHasFixedSize(true)
        buildsListRecyclerView.layoutManager = LinearLayoutManager(this)
        buildsListRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        buildsListRecyclerView.adapter = basicAdapterWithLayoutAndBinder(builds, R.layout.build_item, this::bindItem)
    }

    @SuppressLint("SetTextI18n")
    private fun bindItem(holder: ViewHolderBinder<Build>, item: Build) {
        holder.itemView.projectName.text = item.buildType.projectName
        holder.itemView.buildId.text = "#${item.id}"
        holder.itemView.buildName.text = item.statusText
        holder.itemView.buildDetails.text = "Build ${item.state} ${prettyTime.format(item.finishDate)}"
        holder.itemView.buildStatusIcon.setImageResource(when (item.status) {
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
        else -> super.onOptionsItemSelected(item)
    }

    private fun showSelectProjectsDialog(projects: List<Project>) {
        SelectProjectsDialog(projects, {
            model.perform(SubmitProject(it))
        }).show(fragmentManager, "select_projects")
    }
}
