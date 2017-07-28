package pl.elpassion.eltc

import android.app.Activity
import android.content.Intent
import android.net.Uri
import pl.elpassion.eltc.builds.BuildsActivity
import pl.elpassion.eltc.details.DetailsActivity
import pl.elpassion.eltc.login.LoginActivity

fun Activity.openLoginScreen() = open(LoginActivity::class.java)

fun Activity.openBuildsScreen() = open(BuildsActivity::class.java)

fun Activity.openDetailsScreen() = open(DetailsActivity::class.java)

fun Activity.openWebBrowser(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

private fun <T : Activity> Activity.open(activity: Class<T>) = open(Intent(this, activity))

private fun Activity.open(intent: Intent) {
    startActivity(intent)
    finish()
}