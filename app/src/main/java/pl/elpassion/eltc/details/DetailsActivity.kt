package pl.elpassion.eltc.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.elpassion.android.view.show
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
            is LoadingDetailsState -> {
                loader.show()
                showBuild(state.build)
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
    get() = SimpleDateFormat("d MMM YY HH:mm:ss", Locale.US).format(startDate)

private val Build.finishTime: String?
    get() = if (startDate?.day == finishDate?.day) {
        SimpleDateFormat("HH:mm:ss", Locale.US).format(finishDate)
    } else {
        SimpleDateFormat("d MMM YY HH:mm:ss", Locale.US).format(finishDate)
    }