package pl.elpassion.eltc.details

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.elpassion.eltc.*

@RunWith(AndroidJUnit4::class)
class DetailsActivityTest {

    @JvmField @Rule
    val activityRule = ActivityTestRule(DetailsActivity::class.java)

    val states: Subject<AppState> = BehaviorSubject.createDefault(InitialState)

    val model: TeamCityModel = mock { on { state } doReturn states }

    init {
        DI.provideTeamCityModel = { model }
    }

    @Test
    fun Display_build_number_if_available() {
        states.onNext(BuildDetailsState(createBuild(number = "76")))
        onText("#76").isDisplayed()
    }
}