package com.example.checkin;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test class for testing the UserProfileFragment.
 */
@RunWith(AndroidJUnit4.class)
public class UserProfileFragmentTest {
    /**
     * Initializes Espresso Intents and launches the UserProfileFragment for testing.
     */
    @Before
    public void setUp() {
        Intents.init();
        FragmentScenario.launchInContainer(UserProfileFragment.class);

    }
    /**
     * Releases Espresso Intents after the test completes.
     */
    @After
    public void tearDown() {
        // Release Espresso-Intents
        Intents.release();
    }
    /**
     * Test method for setting the name in UserProfileFragment.
     */
    @Test
    public void testSetName() {
//        // Set up the initial state of the fragment
//        UserProfileFragment fragment = new UserProfileFragment();
//        FragmentScenario<UserProfileFragment> scenario = FragmentScenario.launchInContainer(UserProfileFragment.class);
//
//        // Perform action: set the name and save
//        String testName = "John Doe";
//        scenario.onFragment(fragment -> {
//            fragment.setName(testName);
//            fragment.saveUserProfile();
//        });
//
//        // Check the result: verify that the name EditText displays the correct value
//        scenario.onFragment(fragment -> {
//            assertEquals(testName, fragment.getName());
//            onView(withId(R.id.nameEdit)).check(matches(withText(testName)));
//        });
    }

    /**
     * Test method for setting the country in UserProfileFragment.
     */
    @Test
    public void testSetPhone() {
        String testPhone = "123";
        onView(withId(R.id.phoneEdit)).perform(ViewActions.replaceText(testPhone));
        onView(withId(R.id.saveButton)).perform(click());
        onView(withId(R.id.phoneEdit)).check(matches(withText(testPhone)));
    }
    /**
     * Test method for setting the homepage in UserProfileFragment.
     */
    @Test
    public void testSetHomepage() {
        String testHomepage = "https://example.com";
        onView(withId(R.id.homeEdit)).perform(ViewActions.replaceText(testHomepage));
        onView(withId(R.id.saveButton)).perform(click());
        onView(withId(R.id.homeEdit)).check(matches(withText(testHomepage)));
    }


}


