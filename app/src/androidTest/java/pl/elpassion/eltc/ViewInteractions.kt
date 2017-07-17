package pl.elpassion.eltc

import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility

fun ViewInteraction.isDisplayedEffectively(): ViewInteraction =
        check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))