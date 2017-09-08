package pl.elpassion.eltc.details

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithConstructors
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.details_activity.*
import pl.elpassion.eltc.*
import pl.elpassion.eltc.details.util.toTime
import pl.elpassion.eltc.details.util.toTimeWithoutDate
import pl.elpassion.eltc.details.viewholder.*
import pl.elpassion.eltc.util.textView

class DetailsActivity : BaseActivity() {

    private val allItems = mutableListOf<Any>()
    private val visibleItems = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)
        setSupportActionBar(toolbar)
        showBackArrowInToolbar()
        toolbar.textView.transitionName = getString(R.string.build_number_transition)
        setupRecyclerView()
        initModel()
    }

    private fun setupRecyclerView() {
        detailsRecyclerView.setHasFixedSize(true)
        detailsRecyclerView.layoutManager = LinearLayoutManager(this)
        detailsRecyclerView.adapter = basicAdapterWithConstructors(visibleItems) { position ->
            when (visibleItems[position]) {
                is DetailsSection -> R.layout.section_item to ::SectionViewHolder
                is TestsSection -> R.layout.tests_section_item to getTestsSectionViewHolder()
                is ProblemOccurrence -> R.layout.problem_item to ::ProblemViewHolder
                is Change -> R.layout.change_item to ::ChangeViewHolder
                is TestDetails -> R.layout.test_item to ::TestDetailsViewHolder
                else -> throw IllegalStateException()
            }
        }
    }

    private fun getTestsSectionViewHolder() = { itemView: View ->
        TestsSectionViewHolder(itemView, onSectionClick)
    }

    private val onSectionClick: (TestsSection) -> Unit = { section ->
        switchTests(section, predicate = { it.matchesSection(section.name) })
    }

    private fun TestDetails.matchesSection(name: String) = when (name) {
        getString(R.string.failed) -> isFailed
        getString(R.string.ignored) -> isIgnored
        getString(R.string.passed) -> isPassed
        else -> throw IllegalStateException()
    }

    private fun switchTests(section: TestsSection, predicate: (TestDetails) -> Boolean) {
        val tests = allItems.filterIsInstance<TestDetails>().filter(predicate)
        val sectionIndex = visibleItems.indexOf(section)
        val positionStart = sectionIndex + 1
        if (section.isExpanded) {
            visibleItems.removeAll(tests)
            detailsRecyclerView.adapter.notifyItemRangeRemoved(positionStart, tests.count())
            section.isExpanded = false
        } else {
            visibleItems.addAll(positionStart, tests)
            detailsRecyclerView.adapter.notifyItemRangeInserted(positionStart, tests.count())
            section.isExpanded = true
        }
        detailsRecyclerView.adapter.notifyItemChanged(sectionIndex)
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
                showDetails(state.changes, state.tests, state.problems)
                if (state.isWebBrowserVisible) {
                    openWebBrowser(state.build.webUrl)
                }
            }
            is LoadingBuildsState -> openBuildsScreen()
        }
    }

    private fun showBuild(build: Build) {
        toolbar.title = "#${build.number}"
        projectName.text = build.buildType.projectName
        buildStatusText.text = build.statusText
        buildTime.text = build.time
    }

    private fun showDetails(changes: List<Change>, tests: List<TestDetails>, problems: List<ProblemOccurrence>) {
        allItems.run {
            clear()
            addProblems(problems)
            addChanges(changes)
            addTests(tests)
        }
        visibleItems.run {
            clear()
            addAll(allItems.filterNot { it is TestDetails && it.isIgnored })
        }
        detailsRecyclerView.adapter.notifyDataSetChanged()
    }

    private fun MutableList<Any>.addProblems(problems: List<ProblemOccurrence>) {
        if (problems.isNotEmpty()) {
            add(DetailsSection(getString(R.string.problems)))
            addAll(problems)
        }
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
            addFailedTests(tests)
            addIgnoredTests(tests)
            addPassedTests(tests)
        }
    }

    private fun MutableList<Any>.addFailedTests(tests: List<TestDetails>) {
        if (tests.any(TestDetails::isFailed)) {
            val failedTests = tests.filter(TestDetails::isFailed)
            add(TestsSection(getString(R.string.failed), count = failedTests.count()))
            addAll(failedTests)
        }
    }

    private fun MutableList<Any>.addIgnoredTests(tests: List<TestDetails>) {
        if (tests.any(TestDetails::isIgnored)) {
            val ignoredTests = tests.filter(TestDetails::isIgnored)
            add(TestsSection(getString(R.string.ignored), count = ignoredTests.count(), isExpanded = false))
            addAll(ignoredTests)
        }
    }

    private fun MutableList<Any>.addPassedTests(tests: List<TestDetails>) {
        if (tests.any(TestDetails::isPassed)) {
            val passedTests = tests.filter(TestDetails::isPassed)
            add(TestsSection(getString(R.string.passed), count = passedTests.count()))
            addAll(passedTests)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> model.perform(ReturnToList)
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