package pl.elpassion.eltc.init

import android.content.Intent
import android.os.Bundle
import pl.elpassion.eltc.*
import pl.elpassion.eltc.builds.BuildsActivity
import pl.elpassion.eltc.login.LoginActivity

class InitialActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.perform(StartApp)
    }

    override fun showState(state: AppState?) {
        when (state) {
            is LoginState -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            is BuildsState -> {
                startActivity(Intent(this, BuildsActivity::class.java))
                finish()
            }
        }
    }
}