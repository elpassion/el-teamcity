package pl.elpassion.eltc.ui

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable.just
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.elpassion.eltc.DI
import pl.elpassion.eltc.LoginState
import pl.elpassion.eltc.R

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @JvmField @Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        DI.provideTeamCityModel = { mock { on { state }.thenReturn(just(LoginState())) } }
    }

    @Test
    fun Example_test() {
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("pl.elpassion.eltc", appContext.getPackageName())
    }

    @Test
    fun Display_login_screen() {
        onText(R.string.login).isDisplayed()
    }
}