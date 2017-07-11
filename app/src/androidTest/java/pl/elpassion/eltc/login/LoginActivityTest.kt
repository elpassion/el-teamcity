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
    fun Display_builds_screen_with_provided_data() {
        states.onNext(MainState(listOf(createBuild(number = 76)), emptyList()))
        checkIntent(BuildsActivity::class.java)
    }
}