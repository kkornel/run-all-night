package com.example.kornel.alphaui;

import android.view.View;
import android.widget.EditText;

import com.example.kornel.alphaui.login.RegisterActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChangeTextBehaviorTest {
    static final String STRING_TO_BE_TYPED = "Kornel";
    static final String HINT_TO_BE_CHECKED = "Password";

    @Rule
    public ActivityTestRule<RegisterActivity> activityScenarioRule
            = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void validation_resultIsOneOfTheValidStrings() {
        // Type a valid string and click on the button.
        onView(withId(R.id.firstNameEditText))
                .perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.registerButton)).perform(click());

        // Check that the correct sign is displayed.
        onView(withId(R.id.inputValidationError)).check(matches(isDisplayed()));
    }

    @Test
    public void editText_canBeTypedInto() {
        onView(withId(R.id.emailEditText))
                .perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard())
                .check(matches(withText(STRING_TO_BE_TYPED)));
    }

    @Test
    public void hint_isDisplayedInEditText() {
        onView(withId(R.id.passwordEditText)).check(matches(withHint(HINT_TO_BE_CHECKED)));
    }

    public static Matcher<View> withHint(final String expectedHint) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }

                String hint = ((EditText) view).getHint().toString();

                return expectedHint.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

}