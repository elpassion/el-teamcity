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
        states.onNext(newDetailsState(build))
        onId(R.id.loader).isNotDisplayed()
    }

    @Test
    fun Display_build_number() {
        states.onNext(newDetailsState(build = createBuild(number = "76")))
        onText("#76").isDisplayed()
    }

    @Test
    fun Display_build_project_name() {
        states.onNext(newDetailsState(build = createBuild(projectName = "Project 1")))
        onText("Project 1").isDisplayed()
    }

    @Test
    fun Display_build_status_text() {
        states.onNext(newDetailsState(build = createBuild(statusText = "Tests passed: 543")))
        onText("Tests passed: 543").isDisplayed()
    }

    @Test
    fun Display_time_of_not_finished_build() {
        val startDate = Date(1501161085000)
        states.onNext(newDetailsState(
                build = createBuild(startDate = startDate, finishDate = null)))
        onText("Started at: 27 Jul 17 15:11:25").isDisplayed()
    }

    @Test
    fun Display_time_of_finished_build() {
        val startDate = Date(1501161085000)
        val finishDate = Date(1501162510000)
        states.onNext(newDetailsState(
                build = createBuild(startDate = startDate, finishDate = finishDate)))
        onText("Time: 27 Jul 17 15:11:25 - 15:35:10").isDisplayed()
    }

    @Test
    fun Display_time_of_build_finished_in_next_day() {
        val startDate = Date(1501161085000)
        val finishDate = Date(1501247484000)
        states.onNext(newDetailsState(
                build = createBuild(startDate = startDate, finishDate = finishDate)))
        onText("Time: 27 Jul 17 15:11:25 - 28 Jul 17 15:11:24").isDisplayed()
    }

    @Test
    fun Open_build_in_web_browser() {
        states.onNext(newDetailsState())
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
        states.onNext(newDetailsState(changes = listOf(createChange())))
        onText(R.string.changes).isDisplayed()
    }

    @Test
    fun Do_not_display_changes_header_when_no_changes() {
        states.onNext(newDetailsState())
        onText(R.string.changes).doesNotExist()
    }

    @Test
    fun Display_change_author_on_details_loaded() {
        states.onNext(newDetailsState(changes = listOf(createChange(username = "user1"))))
        onText("user1").isDisplayed()
    }

    @Test
    fun Display_change_comment_on_details_loaded() {
        states.onNext(newDetailsState(changes = listOf(createChange(comment = "Change 1 comment"))))
        onText("Change 1 comment").isDisplayed()
    }

    @Test
    fun Display_change_time_on_details_loaded() {
        states.onNext(newDetailsState(changes = listOf(createChange(date = Date(1501250150000)))))
        onText("28 Jul 17 15:55:50").isDisplayed()
    }

    @Test
    fun Display_change_version_on_details_loaded() {
        states.onNext(newDetailsState(
                changes = listOf(createChange(version = "f8dfs3mdsa93nfdakekfneak"))))
        onText("f8dfs3m").isDisplayed()
    }

    @Test
    fun Display_builds_screen_on_loading_builds() {
        states.onNext(LoadingBuildsState)
        checkIntent(BuildsActivity::class.java)
        Assert.assertTrue(activityRule.activity.isFinishing)
    }

    @Test
    fun Display_tests_header_if_tests_available() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails())))
        onText(R.string.tests).isDisplayed()
    }

    @Test
    fun Do_not_display_tests_header_when_no_tests() {
        states.onNext(newDetailsState(tests = emptyList()))
        onText(R.string.tests).doesNotExist()
    }

    @Test
    fun Display_test_suite_on_details_loaded() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(
                name = "pl.elpassion.eltc.SampleTest.Display something"))))
        onText("pl.elpassion.eltc.SampleTest").isDisplayed()
    }

    @Test
    fun Display_test_case_on_details_loaded() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(
                name = "pl.elpassion.eltc.SampleTest.Display something"))))
        onText("Display something").isDisplayed()
    }

    @Test
    fun Display_success_status_of_passed_test() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "SUCCESS"))))
        onImage(R.drawable.test_success_bg).isDisplayed()
        onImage(R.drawable.ic_success).isDisplayed()
    }

    @Test
    fun Display_failure_status_of_failed_test() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "FAILURE"))))
        onImage(R.drawable.test_failure_bg).isDisplayed()
        onImage(R.drawable.ic_failure).isDisplayed()
    }

    @Test
    fun Display_ignored_status_of_ignored_test() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "UNKNOWN"))))
        onText(R.string.ignored).click()
        onImage(R.drawable.test_ignored_bg).isDisplayed()
        onImage(R.drawable.ic_ignored).isDisplayed()
    }

    @Test
    fun Display_ignored_tests_header_if_ignored_tests_available() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "UNKNOWN"))))
        onText(R.string.ignored).isDisplayed()
    }

    @Test
    fun Do_not_display_ignored_tests_header_when_no_ignored_tests() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "SUCCESS"))))
        onText(R.string.ignored).doesNotExist()
    }

    @Test
    fun Display_passed_tests_header_if_passed_tests_available() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "SUCCESS"))))
        onText(R.string.passed).isDisplayed()
    }

    @Test
    fun Do_not_display_passed_tests_header_when_no_passed_tests() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "FAILURE"))))
        onText(R.string.passed).doesNotExist()
    }

    @Test
    fun Display_failed_tests_header_if_failed_tests_available() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "FAILURE"))))
        onText(R.string.failed).isDisplayed()
    }

    @Test
    fun Do_not_display_failed_tests_header_when_no_failed_tests() {
        states.onNext(newDetailsState(tests = listOf(createTestDetails(status = "SUCCESS"))))
        onText(R.string.failed).doesNotExist()
    }

    @Test
    fun Collapse_ignored_tests_on_details_loaded() {
        states.onNext(newDetailsState(tests = listOf(
                createTestDetails(name = "Ignored test", status = "UNKNOWN"))))
        onText("Ignored test").doesNotExist()
    }

    private fun newDetailsState(build: Build = createBuild(),
                                changes: List<Change> = emptyList(),
                                tests: List<TestDetails> = emptyList()) =
            DetailsState(build, changes, tests)
}