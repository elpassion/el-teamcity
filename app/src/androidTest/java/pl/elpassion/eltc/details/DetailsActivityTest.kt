package pl.elpassion.eltc.details

import android.support.test.espresso.Espresso.pressBackUnconditionally
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.verify
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
    fun Display_build_number() {
        states.onNext(BuildDetailsState(createBuild(number = "76")))
        onText("#76").isDisplayed()
    }

    @Test
    fun Display_build_project_name() {
        states.onNext(BuildDetailsState(createBuild(projectName = "Project 1")))
        onText("Project 1").isDisplayed()
    }

    @Test
    fun Display_build_status_text() {
        states.onNext(BuildDetailsState(createBuild(statusText = "Tests passed: 543")))
        onText("Tests passed: 543").isDisplayed()
    }

    @Test
    fun Display_time_of_not_finished_build() {
        val startDate = Date(1501161085000)
        states.onNext(BuildDetailsState(createBuild(startDate = startDate, finishDate = null)))
        onText("Started at: 27 Jul 17 15:11:25").isDisplayed()
    }

    @Test
    fun Display_time_of_finished_build() {
        val startDate = Date(1501161085000)
        val finishDate = Date(1501162510000)
        states.onNext(BuildDetailsState(createBuild(startDate = startDate, finishDate = finishDate)))
        onText("Time: 27 Jul 17 15:11:25 - 15:35:10").isDisplayed()
    }

    @Test
    fun Display_time_of_build_finished_in_next_day() {
        val startDate = Date(1501161085000)
        val finishDate = Date(1501247484000)
        states.onNext(BuildDetailsState(createBuild(startDate = startDate, finishDate = finishDate)))
        onText("Time: 27 Jul 17 15:11:25 - 28 Jul 17 15:11:24").isDisplayed()
    }

    @Test
    fun Open_build_in_web_browser() {
        states.onNext(BuildDetailsState(createBuild()))
        onId(R.id.open_in_browser).click()
        verify(model).perform(argThat { this is OpenInWebBrowser })
    }

    @Test
    fun Return_to_list_on_back_pressed() {
        states.onNext(BuildDetailsState(createBuild()))
        pressBackUnconditionally()
        verify(model).perform(argThat { this is ReturnToList })
    }

    @Test
    fun Display_builds_screen_on_loading_builds_list() {
        states.onNext(LoadingState)
        checkIntent(BuildsActivity::class.java)
        Assert.assertTrue(activityRule.activity.isFinishing)
    }
}