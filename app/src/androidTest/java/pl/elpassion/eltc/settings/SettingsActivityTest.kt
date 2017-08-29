package pl.elpassion.eltc.settings

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onToolbarBackArrow
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.verify
import org.junit.Rule
import org.junit.Test
import pl.elpassion.eltc.BaseActivityTest
import pl.elpassion.eltc.ReturnToList
import pl.elpassion.eltc.SettingsState

class SettingsActivityTest : BaseActivityTest() {

    @JvmField
    @Rule
    val activityRule = ActivityTestRule(SettingsActivity::class.java)

    @Test
    fun Display_back_arrow_in_toolbar() {
        onToolbarBackArrow().isDisplayed()
    }

    @Test
    fun Return_to_list_on_back_arrow_click() {
        states.onNext(SettingsState(Settings.DEFAULT))
        onToolbarBackArrow().click()
        verify(model).perform(argThat { this is ReturnToList })
    }
}