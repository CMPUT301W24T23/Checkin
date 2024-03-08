package com.example.checkin;

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

// need to comment out line 78 in Database class before testing
// as it causes the app to close due to unique id generated not being set yet
public class AttendeeViewTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Test
    public void testchangeattendee(){

        // click on attendee button
        onView(withId(R.id.attendeebtn)).perform(click());
        // check if switches to attendee view
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testbackbutton(){

        // click on attendee button
        onView(withId(R.id.attendeebtn)).perform(click());

        // click on back button
        onView(withId(R.id.backbtn)).perform(click());

        // check if it goes to homepage
        onView(withId(R.id.main_activity_page)).check(matches(isDisplayed()));


    }


    @Test
    public void testeventdinfo(){

        onView(withId(R.id.attendeebtn)).perform(click());
        // check if switches to attendee view
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        // click on event
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.events)).perform(click());
        // check if it switches to event details page
        onView(withId(R.id.eventdet_frag)).check(matches(isDisplayed()));
        // check if it shows name of event
        onView(withId(R.id.eventname_text)).check(matches((withText("Show"))));
        onView(withId(R.id.eventinfo)).check(matches((withText("Starts at 7"))));
    }

    @Test
    public void testannouncements(){

        onView(withId(R.id.attendeebtn)).perform(click());
        // check if switches to attendee view
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        // click on event
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.events)).perform(click());
        // check if it switches to event details page
        onView(withId(R.id.eventdet_frag)).check(matches(isDisplayed()));
        // check if it shows name of event
        onView(withId(R.id.eventmessg)).perform(click());
        onView(withId(R.id.announce_frag)).check(matches(isDisplayed()));
    }




}