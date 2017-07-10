package pl.elpassion.eltc.ui

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
class MainActivityTest {

    @JvmField @Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    val states: Subject<AppState> = BehaviorSubject.createDefault(InitialState)

    val model: TeamCityModel = mock { on { state } doReturn states }

    init {
        DI.provideTeamCityModel = { model }
    }

    @Test
    fun Do_not_display_any_screen_before_state_change() {
        onId(R.id.loginScreen).isNotDisplayed()
        onId(R.id.loadingScreen).isNotDisplayed()
        onId(R.id.buildsScreen).isNotDisplayed()
    }

    @Test
    fun Display_login_screen() {
        states.onNext(LoginState())
        onId(R.id.loginScreen).isDisplayed()
    }

    @Test
    fun Perform_submit_action() {
        states.onNext(LoginState())
        onText(R.string.login).click()
        verify(model).perform(argThat { this is SubmitCredentials })
    }

    @Test
    fun Display_loading_screen() {
        states.onNext(LoadingState)
        onId(R.id.loadingScreen).isDisplayed()
    }

    @Test
    fun Display_builds_screen_with_provided_data() {
        states.onNext(MainState(listOf(createBuild(number = 76)), emptyList()))
        onText("#76").isDisplayed()
    }

    @Test
    fun Perform_logout_action() {
        states.onNext(MainState(emptyList(), emptyList()))
        Espresso.openContextualActionModeOverflowMenu()
        onText(R.string.logout).click()
        verify(model).perform(argThat { this is Logout })
    }
}