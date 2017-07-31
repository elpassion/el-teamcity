package pl.elpassion.eltc.details

import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.support.test.espresso.Espresso.pressBackUnconditionally
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.intent.matcher.IntentMatchers.hasData
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.elpassion.eltc.*
import pl.elpassion.eltc.R
import pl.elpassion.eltc.builds.BuildsActivity
import java.util.*

class DetailsActivityTest : BaseActivityTest() {

    @JvmField @Rule
    val activityRule = ActivityTestRule(DetailsActivity::class.java)

    @Test
    fun Return_to_list_on_back_pressed() {
        states.onNext(LoadingDetailsState(createBuild()))
        pressBackUnconditionally()
        verify(model).perform(argThat { this is ReturnToList })
    }

    @Test
    fun Display_loader_on_loading_details() {
        states.onNext(LoadingDetailsState(createBuild()))
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun Hide_loader_on_details_loaded() {
        val build = createBuild()
        states.onNext(LoadingDetailsState(build))
        states.onNext(DetailsState(build, emptyList()))
        onId(R.id.loader).isNotDisplayed()
    }

    @Test
    fun Display_build_number() {
        states.onNext(DetailsState(createBuild(number = "76"), emptyList()))
        onText("#76").isDisplayed()
    }

    @Test
    fun Display_build_project_name() {
        states.onNext(DetailsState(createBuild(projectName = "Project 1"), emptyList()))
        onText("Project 1").isDisplayed()
    }

    @Test
    fun Display_build_status_text() {
        states.onNext(DetailsState(createBuild(statusText = "Tests passed: 543"), emptyList()))
        onText("Tests passed: 543").isDisplayed()
    }

    @Test
    fun Display_time_of_not_finished_build() {
        val startDate = Date(1501161085000)
        states.onNext(DetailsState(createBuild(startDate = startDate, finishDate = null), emptyList()))
        onText("Started at: 27 Jul 17 15:11:25").isDisplayed()
    }

    @Test
    fun Display_time_of_finished_build() {
        val startDate = Date(1501161085000)
        val finishDate = Date(1501162510000)
        states.onNext(DetailsState(createBuild(startDate = startDate, finishDate = finishDate), emptyList()))
        onText("Time: 27 Jul 17 15:11:25 - 15:35:10").isDisplayed()
    }

    @Test
    fun Display_time_of_build_finished_in_next_day() {
        val startDate = Date(1501161085000)
        val finishDate = Date(1501247484000)
        states.onNext(DetailsState(createBuild(startDate = startDate, finishDate = finishDate), emptyList()))
        onText("Time: 27 Jul 17 15:11:25 - 28 Jul 17 15:11:24").isDisplayed()
    }

    @Test
    fun Open_build_in_web_browser() {
        states.onNext(DetailsState(createBuild(), emptyList()))
        onId(R.id.open_in_browser).click()
        verify(model).perform(argThat { this is OpenInWebBrowser })
    }

    @Test
    fun Open_web_browser_with_proper_url() {
        val url = "http://teamcity/buildUrl"
        val intent = allOf(hasAction(Intent.ACTION_VIEW), hasData(Uri.parse(url)))
        intending(intent).respondWith(Instrumentation.ActivityResult(0, null))
        states.onNext(WebBrowserState(url = url))
        intended(intent)
    }

    @Test
    fun Display_changes_header_if_changes_available() {
        states.onNext(DetailsState(createBuild(), listOf(createChange())))
        onText(R.string.changes).isDisplayed()
    }

    @Test
    fun Do_not_display_changes_header_when_no_changes() {
        states.onNext(DetailsState(createBuild(), emptyList()))
        onText(R.string.changes).doesNotExist()
    }

    @Test
    fun Display_change_author_on_details_loaded() {
        val changes = listOf(createChange(username = "user1"))
        states.onNext(DetailsState(createBuild(), changes))
        onText("user1").isDisplayed()
    }

    @Test
    fun Display_change_comment_on_details_loaded() {
        val changes = listOf(createChange(comment = "Change 1 comment"))
        states.onNext(DetailsState(createBuild(), changes))
        onText("Change 1 comment").isDisplayed()
    }

    @Test
    fun Display_change_time_on_details_loaded() {
        val changes = listOf(createChange(date = Date(1501250150000)))
        states.onNext(DetailsState(createBuild(), changes))
        onText("28 Jul 17 15:55:50").isDisplayed()
    }

    @Test
    fun Display_change_version_on_details_loaded() {
        val changes = listOf(createChange(version = "f8dfs3mdsa93nfdakekfneak"))
        states.onNext(DetailsState(createBuild(), changes))
        onText("f8dfs3m").isDisplayed()
    }

    @Test
    fun Display_builds_screen_on_loading_builds() {
        states.onNext(LoadingBuildsState)
        checkIntent(BuildsActivity::class.java)
        Assert.assertTrue(activityRule.activity.isFinishing)
    }
}