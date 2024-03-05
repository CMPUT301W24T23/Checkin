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
public class OrganizerViewTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Test
    public void testchangeorganizer(){

        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());
        // check if switches to organizer view
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testbackbutton(){

        // click on attendee button
        onView(withId(R.id.organizerbtn)).perform(click());


        // click on back button
        onView(withId(R.id.backbtn)).perform(click());

        // check if it goes to homepage
        onView(withId(R.id.main_activity_page)).check(matches(isDisplayed()));


    }

    @Test
    public void testeventinfo(){

        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());
        // check if switches to organizer view
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // click on event
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.events)).perform(click());
        // check if it switches to event details page
        onView(withId(R.id.eventdet_org)).check(matches(isDisplayed()));
        // check if it shows name of event
        onView(withId(R.id.eventname_text)).check(matches((withText("Show"))));
        onView(withId(R.id.eventdetails_txt)).check(matches((withText("Starts at 7, ends at 9 PM"))));
    }

    @Test
    public void testshareqrcode(){

        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());
        // check if switches to organizer view
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // click on event
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.events)).perform(click());
        // check if it switches to event details page
        onView(withId(R.id.eventdet_org)).check(matches(isDisplayed()));
        // check if it shows name of event
        onView(withId(R.id.codebtn)).perform(click());
        onView(withId(R.id.sharecode_frag)).check(matches(isDisplayed()));
        onView(withId(R.id.imageCode)).check(matches(isDisplayed()));
    }

    @Test
    public void testcheckedin(){

        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());
        // check if switches to organizer view
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // click on event
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.events)).perform(click());
        // check if it switches to event details page
        onView(withId(R.id.eventdet_org)).check(matches(isDisplayed()));
        // check if it shows name of event
        onView(withId(R.id.attendeeslistbtn)).perform(click());
        // check if it shows attendees list fragment
        onView(withId(R.id.attendeeslisted_frag)).check(matches(isDisplayed()));
        // click on checked in attendees
        onView(withId(R.id.checkedinbtn)).perform(click());
        // check to see checked in list of attendees
        onView(withId(R.id.checkinlist_frag)).check(matches(isDisplayed()));
    }




}