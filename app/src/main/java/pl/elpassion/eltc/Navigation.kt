package pl.elpassion.eltc

import android.app.Activity
import android.content.Intent
import pl.elpassion.eltc.builds.BuildsActivity
import pl.elpassion.eltc.details.DetailsActivity
import pl.elpassion.eltc.login.LoginActivity

fun Activity.openLoginScreen() = open(LoginActivity::class.java)

fun Activity.openBuildsScreen() = open(BuildsActivity::class.java)

fun Activity.openDetailsScreen() = open(DetailsActivity::class.java)

private fun <T : Activity> Activity.open(activity: Class<T>) {
    startActivity(Intent(this, activity))
    finish()
}