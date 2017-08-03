package pl.elpassion.eltc

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import pl.elpassion.eltc.builds.BuildsActivity
import pl.elpassion.eltc.details.DetailsActivity
import pl.elpassion.eltc.login.LoginActivity

fun Activity.openLoginScreen() = open(LoginActivity::class.java)

fun Activity.openBuildsScreen() = open(BuildsActivity::class.java)

fun Activity.openDetailsScreen(bundle: Bundle?) = open(DetailsActivity::class.java, bundle)

fun Activity.openWebBrowser(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

private fun <T : Activity> Activity.open(activity: Class<T>, bundle: Bundle? = null) =
        open(Intent(this, activity), bundle)

private fun Activity.open(intent: Intent, bundle: Bundle? = null) {
    if (bundle != null) {
        startActivity(intent, bundle)
    } else {
        startActivity(intent)
    }
    finish()
}