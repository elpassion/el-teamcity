package pl.elpassion.eltc.login

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.elpassion.eltc.*
import pl.elpassion.eltc.R
import pl.elpassion.eltc.builds.BuildsActivity

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @JvmField @Rule
    val activityRule = ActivityTestRule(LoginActivity::class.java)

    @JvmField @Rule
    val intents = InitIntentsRule()

    val states: Subject<AppState> = BehaviorSubject.createDefault(InitialState)

    val model: TeamCityModel = mock { on { state } doReturn states }

    init {
        DI.provideTeamCityModel = { model }
    }

    @Test
    fun Do_not_display_any_screen_before_state_change() {
        Assert.assertFalse(activityRule.activity.isFinishing)
    }

    @Test
    fun Do_not_show_login_form_before_login_state() {
        onId(R.id.loginForm).isNotDisplayed()
    }

    @Test
    fun Show_login_form_on_login_state() {
        states.onNext(LoginState())
        onId(R.id.loginForm).isDisplayed()
    }

    @Test
    fun Perform_submit_action() {
        states.onNext(LoginState())
        onText(R.string.login).click()
        verify(model).perform(argThat { this is SubmitCredentials })
    }

    @Test
    fun Display_loader_on_loading() {
        states.onNext(LoadingState)
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun Hide_loader_on_login_error() {
        states.onNext(LoadingState)
        states.onNext(LoginState(error = LoginState.Error.UNKNOWN_HOST))
        onId(R.id.loader).isNotDisplayed()
    }

    @Test
    fun Display_error_message_on_invalid_credentials() {
        states.onNext(LoginState(error = LoginState.Error.INVALID_CREDENTIALS))
        onText(LoginState.Error.INVALID_CREDENTIALS.message).isDisplayedEffectively()
    }

    @Test
    fun Display_error_message_on_unknown_host() {
        states.onNext(LoginState(error = LoginState.Error.UNKNOWN_HOST))
        onText(LoginState.Error.UNKNOWN_HOST.message).isDisplayedEffectively()
    }

    @Test
    fun Display_error_message_on_network_problem() {
        states.onNext(LoginState(error = LoginState.Error.NETWORK_PROBLEM))
        onText(LoginState.Error.NETWORK_PROBLEM.message).isDisplayedEffectively()
    }

    @Test
    fun Display_builds_screen_with_provided_data() {
        states.onNext(BuildsState(listOf(createBuild(number = 76)), emptyList()))
        checkIntent(BuildsActivity::class.java)
        onText("#76").isDisplayed()
    }

    @Test
    fun Disable_login_button_on_pending_api_result() {
        states.onNext(LoginState())
        onText(R.string.login).click()
        onText(R.string.login).isNotClickable()
    }

    @Test
    fun Enable_login_button_on_api_result_received() {
        states.onNext(LoginState())
        onText(R.string.login).click()
        states.onNext(LoginState(error = LoginState.Error.INVALID_CREDENTIALS))
        onText(R.string.login).isClickable()
    }
}