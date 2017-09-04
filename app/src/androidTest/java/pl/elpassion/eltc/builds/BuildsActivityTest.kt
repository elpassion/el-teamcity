package pl.elpassion.eltc.builds

import android.support.test.espresso.Espresso
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pl.elpassion.eltc.*
import pl.elpassion.eltc.R
import pl.elpassion.eltc.details.DetailsActivity
import pl.elpassion.eltc.settings.Settings
import pl.elpassion.eltc.settings.SettingsActivity

class BuildsActivityTest : BaseActivityTest() {

    @JvmField @Rule
    val activityRule = ActivityTestRule(BuildsActivity::class.java)

    @Test
    fun Display_loader_on_loading_builds() {
        states.onNext(LoadingBuildsState)
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun Display_build_number_if_available() {
        states.onNext(newBuildsState(builds = listOf(createBuild(number = "76"))))
        onText("#76").isDisplayed()
    }

    @Test
    fun Display_state_of_finished_build() {
        states.onNext(newBuildsState(builds = listOf(createBuild(state = State.FINISHED))))
        onTextStartingWith("Build finished").isDisplayed()
    }

    @Test
    fun Display_state_of_queued_build() {
        states.onNext(newBuildsState(builds = listOf(createBuild(state = State.QUEUED))))
        onTextStartingWith("Build queued").isDisplayed()
    }

    @Test
    fun Display_state_of_running_build() {
        states.onNext(newBuildsState(builds = listOf(createBuild(state = State.RUNNING))))
        onTextStartingWith("Build started").isDisplayed()
    }

    @Test
    fun Display_success_status_of_finished_build() {
        states.onNext(newBuildsState(builds = listOf(createBuild(status = Status.SUCCESS))))
        onImage(R.drawable.build_success_bg).isDisplayed()
        onImage(R.drawable.ic_success).isDisplayed()
    }

    @Test
    fun Display_failure_status_of_finished_build() {
        states.onNext(newBuildsState(builds = listOf(createBuild(status = Status.FAILURE))))
        onImage(R.drawable.build_failure_bg).isDisplayed()
        onImage(R.drawable.ic_failure).isDisplayed()
    }

    @Test
    fun Display_progress_status_of_started_build() {
        states.onNext(newBuildsState(builds = listOf(createBuild(state = State.RUNNING))))
        onId(R.id.buildStatusBg).isNotDisplayed()
        onId(R.id.buildStatusIcon).isNotDisplayed()
        onId(R.id.buildProgressBar).isDisplayed()
    }

    @Test
    fun Display_status_of_queued_build() {
        states.onNext(newBuildsState(builds = listOf(createBuild(state = State.QUEUED))))
        onImage(R.drawable.build_queued_bg).isDisplayed()
        onImage(R.drawable.ic_queued).isDisplayed()
    }

    @Test
    fun Do_not_display_build_name_for_queued_builds() {
        states.onNext(newBuildsState(builds = listOf(createBuild(state = State.QUEUED))))
        onId(R.id.buildName).isNotDisplayed()
    }

    @Test
    fun Hide_loader_on_new_data() {
        states.onNext(LoadingBuildsState)
        states.onNext(newBuildsState(builds = listOf(createBuild(number = "76"))))
        onId(R.id.loader).isNotDisplayed()
    }

    @Test
    fun Display_dialog_with_projects_list_on_select_projects_action() {
        states.onNext(SelectProjectsDialogState(listOf(
                createSelectableProject(name = "Project 1"),
                createSelectableProject(name = "Project 2"),
                createSelectableProject(name = "Project 3"))))
        onText("Project 1").isDisplayed()
        onText("Project 2").isDisplayed()
        onText("Project 3").isDisplayed()
    }

    @Test
    fun Select_multiple_projects_in_projects_dialog() {
        states.onNext(SelectProjectsDialogState(listOf(
                createSelectableProject(name = "Project 1"),
                createSelectableProject(name = "Project 2"),
                createSelectableProject(name = "Project 3"))))
        onText("Project 1").click()
        onText("Project 3").click()
        onText("Project 1").isChecked()
        onText("Project 2").isNotChecked()
        onText("Project 3").isChecked()
    }

    @Test
    fun Check_already_selected_projects_in_projects_dialog() {
        states.onNext(SelectProjectsDialogState(listOf(
                createSelectableProject(name = "Project 1", isSelected = true),
                createSelectableProject(name = "Project 2"),
                createSelectableProject(name = "Project 3", isSelected = true))))
        onText("Project 1").isChecked()
        onText("Project 2").isNotChecked()
        onText("Project 3").isChecked()
    }

    @Test
    fun Submit_empty_projects_list_on_show_all_projects() {
        states.onNext(SelectProjectsDialogState(listOf(
                createSelectableProject(name = "Project 1", isSelected = true),
                createSelectableProject(name = "Project 2"),
                createSelectableProject(name = "Project 3", isSelected = true))))
        onText(R.string.show_all).click()
        verify(model).perform(argThat { this is SubmitProjects && projects.isEmpty() })
    }

    @Test
    fun Submit_selected_projects_list_on_dialog_confirm_action() {
        states.onNext(SelectProjectsDialogState(listOf(
                createSelectableProject(name = "Project 1"),
                createSelectableProject(name = "Project 2", isSelected = true),
                createSelectableProject(name = "Project 3"))))
        onText(R.string.ok).click()
        verify(model).perform(argThat {
            this is SubmitProjects && projects == listOf(createProject(name = "Project 2"))
        })
    }

    @Test
    fun Refresh_list_on_dialog_dismiss() {
        states.onNext(SelectProjectsDialogState(listOf(
                createSelectableProject(name = "Project 1"),
                createSelectableProject(name = "Project 2", isSelected = true),
                createSelectableProject(name = "Project 3"))))
        Espresso.pressBack()
        verify(model).perform(argThat { this is RefreshList })
    }

    @Test
    fun Select_build_on_click() {
        val selectedBuild = createBuild(number = "7")
        states.onNext(newBuildsState(builds = listOf(selectedBuild)))
        onText("#7").click()
        verify(model).perform(argThat {
            this is SelectBuild && selectedBuild == selectedBuild
        })
    }

    @Test
    fun Do_not_select_queued_build_on_click() {
        val selectedBuild = createBuild(state = State.QUEUED)
        states.onNext(newBuildsState(builds = listOf(selectedBuild)))
        onTextStartingWith("Build queued").click()
        verify(model, never()).perform(argThat { this is SelectBuild })
    }

    @Test
    fun Open_build_details() {
        states.onNext(LoadingDetailsState(createBuild()))
        checkIntent(DetailsActivity::class.java)
        assertTrue(activityRule.activity.isFinishing)
    }

    @Test
    fun Open_settings_on_action_click() {
        states.onNext(newBuildsState())
        Espresso.openContextualActionModeOverflowMenu()
        onText(R.string.settings).click()
        verify(model).perform(argThat { this is OpenSettings })
    }

    @Test
    fun Display_settings_screen() {
        states.onNext(SettingsState(Settings.DEFAULT))
        checkIntent(SettingsActivity::class.java)
        assertTrue(activityRule.activity.isFinishing)
    }

    @Test
    fun Perform_logout_action() {
        states.onNext(newBuildsState())
        Espresso.openContextualActionModeOverflowMenu()
        onText(R.string.logout).click()
        verify(model).perform(argThat { this is Logout })
    }
}