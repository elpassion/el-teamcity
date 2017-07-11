package pl.elpassion.eltc.init

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.elpassion.android.commons.espresso.InitIntentsRule
import com.elpassion.android.commons.espresso.checkIntent
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.elpassion.eltc.*
import pl.elpassion.eltc.builds.BuildsActivity
import pl.elpassion.eltc.login.LoginActivity

@RunWith(AndroidJUnit4::class)
class InitialActivityTest {

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
        assertFalse(activityRule.activity.isFinishing)
    }

    @Test
    fun Display_login_screen() {
        states.onNext(LoginState())
        checkIntent(LoginActivity::class.java)
    }

    @Test
    fun Display_builds_screen_with_provided_data() {
        states.onNext(MainState(listOf(createBuild(number = 76)), emptyList()))
        checkIntent(BuildsActivity::class.java)
        onText("#76").isDisplayed()
    }
}