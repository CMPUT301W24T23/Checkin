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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.widget.ListView;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest

// need to comment out line 78 in Database class before testing
// as it causes the app to close due to unique id generated not being set yet
public class AttendeeViewTest {

    @Mock
    private Database mockDatabase;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
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
        // Define a mock event list that you want to return when updateEvent() is called
        Event event = new Event("Test Event1", "1234");
        ArrayList<Event> mockEventList = new ArrayList<>();
        mockEventList.add(new Event("Show", "1234"));




        onView(withId(R.id.attendeebtn)).perform(click());
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        doReturn(mockEventList).when(mockDatabase).updateEvent(event);

        Espresso.onView(withId(R.id.progress)).check(matches(isDisplayed()));

        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.events)).perform(click());
        onView(withId(R.id.eventdet_frag)).check(matches(isDisplayed()));
        onView(withId(R.id.eventname_text)).check(matches(withText("Test Event1")));
    }

    @Test
    public void testannouncements() {

        onView(withId(R.id.attendeebtn)).perform(click());
        // check if switches to attendee view
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        // click on event
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.events)).perform(click());
        // check if it switches to event details page
        onView(withId(R.id.eventdet_frag)).check(matches(isDisplayed()));
        // check if it shows name of event
    }





}