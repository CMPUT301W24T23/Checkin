package com.example.checkin;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MessagesTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testAnnouncementsListVisible() {
        // Check if the announcements list is displayed
        onView(withId(R.id.announcements_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButton() {
        // Click on back button
        onView(withId(R.id.backbtn)).perform(click());

        // Check if the previous fragment container view is displayed
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));
    }

    public void testNavigationToAnnouncementsFragment() {
        // Click on the "Announcements" item in the bottom navigation bar
        onView(withId(R.id.messages2)).perform(click());

        // Check if the Announcements fragment is displayed
        onView(withId(R.id.announcements_list)).check(matches(isDisplayed()));
    }


}
