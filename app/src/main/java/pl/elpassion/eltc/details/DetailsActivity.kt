package pl.elpassion.eltc.details

import android.os.Bundle
import kotlinx.android.synthetic.main.details_activity.*
import pl.elpassion.eltc.*

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
            is BuildDetailsState -> showDetails(state.build)
            is LoadingState -> openBuildsScreen()
        }
    }

    private fun showDetails(build: Build) {
        toolbar.title = "#${build.number}"
    }
}