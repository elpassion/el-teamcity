package pl.elpassion.eltc.settings

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBackUnconditionally
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.widget.Switch
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pl.elpassion.eltc.*
import pl.elpassion.eltc.R
import pl.elpassion.eltc.builds.BuildsActivity

class SettingsActivityTest : BaseActivityTest() {

    @JvmField
    @Rule
    val activityRule = ActivityTestRule(SettingsActivity::class.java)

    @Test
    fun Display_back_arrow_in_toolbar() {
        onToolbarBackArrow().isDisplayed()
    }

    @Test
    fun Return_to_list_on_back_pressed() {
        states.onNext(SettingsState(Settings.DEFAULT))
        pressBackUnconditionally()
        verify(model).perform(argThat { this is ReturnToList })
    }

    @Test
    fun Return_to_list_on_back_arrow_click() {
        states.onNext(SettingsState(Settings.DEFAULT))
        onToolbarBackArrow().click()
        verify(model).perform(argThat { this is ReturnToList })
    }

    @Test
    fun Display_builds_screen_on_loading_builds() {
        states.onNext(LoadingBuildsState)
        checkIntent(BuildsActivity::class.java)
        assertTrue(activityRule.activity.isFinishing)
    }

    @Test
    fun Display_screen_title() {
        onText(R.string.settings).isDisplayed()
    }

    @Test
    fun Display_notifications_preference() {
        onText(R.string.notifications).isDisplayed()
    }

    @Test
    fun Display_current_value_of_notifications_preference() {
        states.onNext(SettingsState(Settings.DEFAULT.copy(
                areNotificationsEnabled = false)))
        onView(allOf(
                withParent(withParent(hasDescendant(withText(R.string.notifications)))),
                isAssignableFrom(Switch::class.java)))
                .isNotChecked()
    }

    @Test
    fun Display_notifications_frequency_preference() {
        onText(R.string.notifications_frequency).isDisplayed()
    }

    @Test
    fun Display_notifications_frequency_summary() {
        states.onNext(SettingsState(Settings.DEFAULT.copy(
                notificationsFrequencyInMinutes = 15)))
        onText("Every 15 minutes").isDisplayed()
    }

    @Test
    fun Check_current_value_in_notifications_frequency_dialog() {
        states.onNext(SettingsState(Settings.DEFAULT.copy(
                notificationsFrequencyInMinutes = 30)))
        onText("Every 30 minutes").click()
        onText("Every 30 minutes").isChecked()
    }

    @Test
    fun Refresh_settings_on_preference_changed() {
        states.onNext(SettingsState(Settings.DEFAULT))
        onText(R.string.notifications_frequency).click()
        onText("Every 1 hour").click()
        verify(model).perform(argThat { this is RefreshSettings })
    }
}