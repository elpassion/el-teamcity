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
        states.onNext(MainState(listOf(createBuild(number = 76)), emptyList()))
        onText("#76").isDisplayed()
    }

    @Test
    fun Hide_loader_on_new_data() {
        states.onNext(LoadingState)
        states.onNext(MainState(listOf(createBuild(number = 76)), emptyList()))
        onId(R.id.loader).isNotDisplayed()
    }

    @Test
    fun Perform_logout_action() {
        states.onNext(MainState(emptyList(), emptyList()))
        Espresso.openContextualActionModeOverflowMenu()
        onText(R.string.logout).click()
        verify(model).perform(argThat { this is Logout })
    }
}