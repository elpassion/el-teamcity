package pl.elpassion.eltc

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import pl.elpassion.eltc.builds.BuildsActivity
import pl.elpassion.eltc.details.DetailsActivity
import pl.elpassion.eltc.login.LoginActivity
import android.util.Pair as TPair

fun Activity.openLoginScreen() = open(LoginActivity::class.java)

fun Activity.openBuildsScreen() = open(BuildsActivity::class.java)

fun Activity.openDetailsScreen(bundle: Bundle?) = open(DetailsActivity::class.java, bundle)

fun Activity.openWebBrowser(url: String) =
        CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()
                .launchUrl(this, Uri.parse(url))

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

fun Activity.newTransitionAnimation(vararg views: View): ActivityOptions {
    val pairs = views.map { TPair(it, it.transitionName) }.toTypedArray()
    return ActivityOptions.makeSceneTransitionAnimation(this, *pairs)
}

fun AppCompatActivity.showBackArrowInToolbar() = supportActionBar?.setDisplayHomeAsUpEnabled(true)