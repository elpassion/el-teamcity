package pl.elpassion.eltc

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.widget.Switch
import com.elpassion.android.commons.espresso.matchers.withImage
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers.startsWith
import org.hamcrest.core.IsNot.not

fun ViewInteraction.isDisplayedEffectively(): ViewInteraction =
        check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

fun ViewInteraction.isClickable(): ViewInteraction = check(matches(ViewMatchers.isClickable()))

fun ViewInteraction.isNotClickable(): ViewInteraction = check(matches(not(ViewMatchers.isClickable())))

fun onTextStartingWith(text: String): ViewInteraction = onView(withText(startsWith(text)))

fun onImage(@DrawableRes imageId: Int): ViewInteraction = onView(withImage(imageId))

fun onSwitchPreference(@StringRes textId: Int): ViewInteraction = onView(allOf(
        withParent(withParent(hasDescendant(withText(textId)))),
        isAssignableFrom(Switch::class.java)))