package pl.elpassion.eltc.details

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithConstructors
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.details_activity.*
import pl.elpassion.eltc.*
import pl.elpassion.eltc.details.util.toTime
import pl.elpassion.eltc.details.util.toTimeWithoutDate
import pl.elpassion.eltc.details.viewholder.ChangeViewHolder
import pl.elpassion.eltc.details.viewholder.SectionViewHolder
import pl.elpassion.eltc.details.viewholder.TestDetailsViewHolder

class DetailsActivity : BaseActivity() {

    private val items = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)
        setSupportActionBar(toolbar)
        setupRecyclerView()
        initModel()
    }

    private fun setupRecyclerView() {
        detailsRecyclerView.setHasFixedSize(true)
        detailsRecyclerView.layoutManager = LinearLayoutManager(this)
        detailsRecyclerView.adapter = basicAdapterWithConstructors(items) { position ->
            when (items[position]) {
                is DetailsSection -> R.layout.section_item to ::SectionViewHolder
                is Change -> R.layout.change_item to ::ChangeViewHolder
                is TestDetails -> R.layout.test_item to ::TestDetailsViewHolder
                else -> throw IllegalStateException()
            }
        }
    }

    override fun onBackPressed() {
        model.perform(ReturnToList)
    }

    override fun showState(state: AppState?) {
        when (state) {
            is LoadingDetailsState -> loader.show()
            is DetailsState -> {
                loader.hide()
                showBuild(state.build)
                showDetails(state.changes, state.tests)
            }
            is LoadingBuildsState -> openBuildsScreen()
            is WebBrowserState -> openWebBrowser(state.url)
        }
    }

    private fun showBuild(build: Build) {
        toolbar.title = "#${build.number}"
        projectName.text = build.buildType.projectName
        buildStatusText.text = build.statusText
        buildTime.text = build.time
    }

    private fun showDetails(changes: List<Change>, tests: List<TestDetails>) {
        items.run {
            clear()
            addChanges(changes)
            addTests(tests)
        }
        detailsRecyclerView.adapter.notifyDataSetChanged()
    }

    private fun MutableList<Any>.addChanges(changes: List<Change>) {
        if (changes.isNotEmpty()) {
            add(DetailsSection(getString(R.string.changes)))
            addAll(changes)
        }
    }

    private fun MutableList<Any>.addTests(tests: List<TestDetails>) {
        if (tests.isNotEmpty()) {
            add(DetailsSection(getString(R.string.tests)))
            if (tests.any { it.status == "UNKNOWN" }) {
                add(DetailsSection(getString(R.string.ignored)))
                addAll(tests.filter { it.status == "UNKNOWN" })
            }
            if (tests.any { it.status == "SUCCESS" }) {
                add(DetailsSection(getString(R.string.passed)))
            }
            addAll(tests.filter { it.status != "UNKNOWN" })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.open_in_browser -> model.perform(OpenInWebBrowser)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}

private val Build.time: String
    get() = if (finishDate != null) "Time: $totalTime" else "Started at: $startTime"

private val Build.totalTime: String
    get() = "$startTime - $finishTime"

private val Build.startTime: String?
    get() = startDate?.toTime()

private val Build.finishTime: String?
    get() = if (didLastOneDay()) finishDate?.toTimeWithoutDate() else finishDate?.toTime()

private fun Build.didLastOneDay() = startDate?.day == finishDate?.day