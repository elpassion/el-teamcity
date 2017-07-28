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

class BuildsActivityTest : BaseActivityTest() {

    @JvmField @Rule
    val activityRule = ActivityTestRule(BuildsActivity::class.java)

    @Test
    fun Display_loader_on_loading() {
        states.onNext(LoadingState)
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun Display_build_number_if_available() {
        states.onNext(BuildsState(listOf(createBuild(number = "76")), emptyList()))
        onText("#76").isDisplayed()
    }

    @Test
    fun Display_state_of_finished_build() {
        states.onNext(BuildsState(listOf(createBuild(state = "finished")), emptyList()))
        onTextStartingWith("Build finished").isDisplayed()
    }

    @Test
    fun Display_state_of_queued_build() {
        states.onNext(BuildsState(listOf(createBuild(state = "queued")), emptyList()))
        onTextStartingWith("Build queued").isDisplayed()
    }

    @Test
    fun Display_state_of_running_build() {
        states.onNext(BuildsState(listOf(createBuild(state = "running")), emptyList()))
        onTextStartingWith("Build started").isDisplayed()
    }

    @Test
    fun Display_success_status_of_finished_build() {
        states.onNext(BuildsState(listOf(createBuild(status = "SUCCESS")), emptyList()))
        onImage(R.drawable.build_success_bg).isDisplayed()
        onImage(R.drawable.ic_success).isDisplayed()
    }

    @Test
    fun Display_failure_status_of_finished_build() {
        states.onNext(BuildsState(listOf(createBuild(status = "FAILURE")), emptyList()))
        onImage(R.drawable.build_failure_bg).isDisplayed()
        onImage(R.drawable.ic_failure).isDisplayed()
    }

    @Test
    fun Display_progress_status_of_started_build() {
        states.onNext(BuildsState(listOf(createBuild(state = "running")), emptyList()))
        onId(R.id.buildStatusBg).isNotDisplayed()
        onId(R.id.buildStatusIcon).isNotDisplayed()
        onId(R.id.buildProgressBar).isDisplayed()
    }

    @Test
    fun Display_status_of_queued_build() {
        states.onNext(BuildsState(listOf(createBuild(state = "queued")), emptyList()))
        onImage(R.drawable.build_queued_bg).isDisplayed()
        onImage(R.drawable.ic_queued).isDisplayed()
    }

    @Test
    fun Do_not_display_build_name_for_queued_builds() {
        states.onNext(BuildsState(listOf(createBuild(state = "queued")), emptyList()))
        onId(R.id.buildName).isNotDisplayed()
    }

    @Test
    fun Hide_loader_on_new_data() {
        states.onNext(LoadingState)
        states.onNext(BuildsState(listOf(createBuild(number = "76")), emptyList()))
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
        states.onNext(BuildsState(listOf(selectedBuild), emptyList()))
        onText("#7").click()
        verify(model).perform(argThat {
            this is SelectBuild && selectedBuild == selectedBuild
        })
    }

    @Test
    fun Do_not_select_queued_build_on_click() {
        val selectedBuild = createBuild(state = "queued")
        states.onNext(BuildsState(listOf(selectedBuild), emptyList()))
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
    fun Perform_logout_action() {
        states.onNext(BuildsState(emptyList(), emptyList()))
        Espresso.openContextualActionModeOverflowMenu()
        onText(R.string.logout).click()
        verify(model).perform(argThat { this is Logout })
    }
}