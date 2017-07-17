package pl.elpassion.eltc.login

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.login_activity.*
import pl.elpassion.eltc.*
import pl.elpassion.eltc.builds.BuildsActivity

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        initModel()
        save.setOnClickListener {
            val credentials = getCredentials(user.text.toString(), password.text.toString())
            model.perform(SubmitCredentials(address.text.toString(), credentials))
        }
        model.perform(StartApp)
    }

    override fun showState(state: AppState?) {
        when (state) {
            is LoadingState -> {
                loader.show()
            }
            is BuildsState -> {
                startActivity(Intent(this, BuildsActivity::class.java))
                finish()
            }
        }
    }

    private fun getCredentials(user: String, password: String): String {
        val data = "$user:$password".toByteArray()
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }
}