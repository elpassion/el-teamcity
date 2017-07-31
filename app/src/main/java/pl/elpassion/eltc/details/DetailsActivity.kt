package pl.elpassion.eltc.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.change_item.view.*
import kotlinx.android.synthetic.main.details_activity.*
import pl.elpassion.eltc.*
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

    private fun showChanges(changes: List<Change>) {
        changesContainer.removeAllViews()
        changes.forEach { changesContainer.addView(getChangeView(it)) }
    }

    private fun getChangeView(change: Change) =
            View.inflate(this, R.layout.change_item, null).apply {
                version.text = change.version.take(7)
                author.text = change.username
                time.text = change.date.toTime()
                comment.text = change.comment
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
    get() = if (finishDate != null) {
        "Time: $totalTime"
    } else {
        "Started at: $startTime"
    }

private val Build.totalTime: String
    get() = "$startTime - $finishTime"

private val Build.startTime: String?
    get() = startDate?.toTime()

private val Build.finishTime: String?
    get() = if (didLastOneDay()) finishDate?.toTimeWithoutDate() else finishDate?.toTime()

private fun Build.didLastOneDay() = startDate?.day == finishDate?.day

private fun Date.toTime() = SimpleDateFormat("d MMM YY HH:mm:ss", Locale.US).format(this)

private fun Date.toTimeWithoutDate() = SimpleDateFormat("HH:mm:ss", Locale.US).format(this)