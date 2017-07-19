package pl.elpassion.eltc.builds

import android.support.test.espresso.Espresso
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.elpassion.eltc.*
import pl.elpassion.eltc.R

@RunWith(AndroidJUnit4::class)
class BuildsActivityTest {

    @JvmField @Rule
    val activityRule = ActivityTestRule(BuildsActivity::class.java)

    @JvmField @Rule
    val intents = InitIntentsRule()

    val states: Subject<AppState> = BehaviorSubject.createDefault(InitialState)

    val model: TeamCityModel = mock { on { state } doReturn states }

    init {
        DI.provideTeamCityModel = { model }
    }

    @Test
    fun Display_loader_on_loading() {
        states.onNext(LoadingState)
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun Display_builds_screen_with_provided_data() {
        states.onNext(BuildsState(listOf(createBuild(number = 76)), emptyList()))
        onText("#76").isDisplayed()
    }

    @Test
    fun Hide_loader_on_new_data() {
        states.onNext(LoadingState)
        states.onNext(BuildsState(listOf(createBuild(number = 76)), emptyList()))
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
    fun Perform_logout_action() {
        states.onNext(BuildsState(emptyList(), emptyList()))
        Espresso.openContextualActionModeOverflowMenu()
        onText(R.string.logout).click()
        verify(model).perform(argThat { this is Logout })
    }
}