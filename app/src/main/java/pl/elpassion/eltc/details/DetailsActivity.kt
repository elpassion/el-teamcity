package pl.elpassion.eltc.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.change_item.view.*
import kotlinx.android.synthetic.main.details_activity.*
import kotlinx.android.synthetic.main.test_item.view.*
import pl.elpassion.eltc.*
import pl.elpassion.eltc.util.inflate
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)
        setSupportActionBar(toolbar)
        initModel()
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
                showChanges(state.changes)
                showTests(state.tests)
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

    private fun showChanges(changes: List<Change>) = changesContainer.run {
        removeAllViews()
        if (changes.isNotEmpty()) {
            addView(getChangesHeaderView())
            changes.forEach { addView(getChangeView(it)) }
        }
    }

    private fun showTests(tests: List<TestDetails>) {
        if (tests.isNotEmpty()) {
            testsContainer.addView(getTestsHeaderView())
            tests.forEach { testsContainer.addView(getTestView(it)) }
        }
    }

    private fun getChangesHeaderView() = inflate(R.layout.changes_header)

    private fun getChangeView(change: Change) = inflate(R.layout.change_item).apply {
        buildVersion.text = change.version.take(7)
        buildAuthor.text = change.username
        buildTime.text = change.date.toTime()
        buildComment.text = change.comment
    }

    private fun getTestsHeaderView() = inflate(R.layout.tests_header)

    private fun getTestView(test: TestDetails) = inflate(R.layout.test_item).apply {
        testName.text = test.name
        testStatusBg.setImageResource(getTestStatusBgResId(test))
        testStatusIcon.setImageResource(getTestStatusIconResId(test))
    }

    private fun getTestStatusBgResId(test: TestDetails) = when(test.status) {
        "SUCCESS" -> R.drawable.test_success_bg
        "FAILURE" -> R.drawable.test_failure_bg
        else -> R.drawable.test_ignored_bg
    }

    private fun getTestStatusIconResId(test: TestDetails) = when(test.status) {
        "SUCCESS" -> R.drawable.ic_success
        "FAILURE" -> R.drawable.ic_failure
        else -> R.drawable.ic_ignored
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

private fun Date.toTime() = SimpleDateFormat("d MMM YY HH:mm:ss", Locale.US).format(this)

private fun Date.toTimeWithoutDate() = SimpleDateFormat("HH:mm:ss", Locale.US).format(this)