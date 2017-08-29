package pl.elpassion.eltc.settings

import android.os.Bundle
import kotlinx.android.synthetic.main.settings_activity.*
import pl.elpassion.eltc.AppState
import pl.elpassion.eltc.BaseActivity
import pl.elpassion.eltc.R
import pl.elpassion.eltc.showBackArrowInToolbar

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(toolbar)
        showBackArrowInToolbar()
        initModel()
    }

    override fun showState(state: AppState?) = Unit
}