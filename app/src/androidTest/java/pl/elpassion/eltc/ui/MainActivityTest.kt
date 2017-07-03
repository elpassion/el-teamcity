package pl.elpassion.eltc.ui

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.elpassion.eltc.*

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @JvmField @Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    val states: Subject<AppState> = BehaviorSubject.createDefault(InitialState)

    init {
        DI.provideTeamCityModel = { mock { on { state }.thenReturn(states) } }
    }

    @Test
    fun Display_login_screen() {
        states.onNext(LoginState())
        onText(R.string.login).isDisplayed()
    }
}