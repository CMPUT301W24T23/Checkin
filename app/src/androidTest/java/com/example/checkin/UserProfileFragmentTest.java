package com.example.checkin;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.CharMatcher.is;
import static java.util.function.Predicate.not;

/**
 * Instrumented test class for testing the UserProfileFragment.
 */
@RunWith(AndroidJUnit4.class)
public class UserProfileFragmentTest {
    private IdlingResource idlingResource;

    /**
     * Initializes Espresso Intents and launches the UserProfileFragment for testing.
     */
    @Before
    public void setUp() {
        Intents.init();
        FragmentScenario.launchInContainer(UserProfileFragment.class);
    }

    /**
     * Releases Espresso Intents and IdlingResource after the test completes.
     */
    @After
    public void tearDown() {
        // Release Espresso-Intents
        Intents.release();
        // Unregister the IdlingResource
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }

    /**
     * Test method for setting and checking the name in UserProfileFragment.
     */
    @Test
    public void testSetName() {
        String testName = "John Doe";
        onView(withId(R.id.nameEdit)).perform(replaceText(testName));

        // Verify that the name EditText displays the correct value
        onView(withId(R.id.nameEdit)).check(matches(withText(testName)));
    }

    /**
     * Test method for setting and checking the email in UserProfileFragment.
     */
    @Test
    public void testSetEmail() {
        String testEmail = "test@example.com";
        onView(withId(R.id.emailEdit)).perform(replaceText(testEmail));

        // Verify that the email EditText displays the correct value
        onView(withId(R.id.emailEdit)).check(matches(withText(testEmail)));
    }

    /**
     * Test method for setting and checking the phone in UserProfileFragment.
     */
    @Test
    public void testSetPhone() {
        String testPhone = "1234567890";
        onView(withId(R.id.phoneEdit)).perform(replaceText(testPhone));

        // Verify that the phone EditText displays the correct value
        onView(withId(R.id.phoneEdit)).check(matches(withText(testPhone)));
    }

    /**
     * Test method for setting and checking the homepage in UserProfileFragment.
     */
    @Test
    public void testSetHomepage() {
        String testHomepage = "http://example.com";
        onView(withId(R.id.homeEdit)).perform(replaceText(testHomepage));

        // Verify that the homepage EditText displays the correct value
        onView(withId(R.id.homeEdit)).check(matches(withText(testHomepage)));
    }
}


