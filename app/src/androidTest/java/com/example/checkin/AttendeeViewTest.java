package com.example.checkin;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import static java.util.EnumSet.allOf;

import android.Manifest;
import android.content.Context;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;


import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;




//https://developer.android.com/training/testing/espresso/idling-resource
// https://stackoverflow.com/a/41638243
@RunWith(AndroidJUnit4.class)
@LargeTest

public class AttendeeViewTest {


    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule permissionRule2 = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);


    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);



    private ViewIdlingResource idlingResource = new ViewIdlingResource(R.id.progress);

    // test whether view switches to attendee view of app
    @Test
    public void testchangeattendee(){
        // start idling resource
        idlingResource.increment();
        onView(withId(R.id.attendeebtn)).perform(click());

        // Check if the attendee view is displayed
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        // pause for data loading to complete and be visible
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();


        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));
        // unregister idling resource
        IdlingRegistry.getInstance().unregister(idlingResource);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    // test back button
    @Test
    public void testbackbutton(){

        idlingResource.increment();

        // Perform click action to navigate to the attendee view
        onView(withId(R.id.attendeebtn)).perform(click());
        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));

        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));


        // pause for data loading to complete and be visible
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();


        // click on back button
        onView(withId(R.id.backbtn)).check(matches(isDisplayed())).perform(click());

        // check if it goes to homepage
        onView(withId(R.id.main_activity_page)).check(matches(isDisplayed()));
        IdlingRegistry.getInstance().unregister(idlingResource);

    }


    // test event details when clicking on event
    @Test
    public void testeventinfo(){

        idlingResource.increment();
        // click on attendee button
        onView(withId(R.id.attendeebtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));
        onView(withId(R.id.browseeventsbtn)).perform(click());

        // wait for data loaded to be visible
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on first event in browse events
        onView(withText("Charity Event")).perform(click());


       // check event details fragment and if it matches with the event that was clicked on
        onView(withId(R.id.eventdet_frag)).check(matches(isDisplayed()));
        onView(withId(R.id.eventname_text)).check(matches(withText("Charity Event")));
    }



    // test signing up for an event
    @Test
    public void testsignupbutton(){

        idlingResource.increment();
        // click on attendee button
        onView(withId(R.id.attendeebtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));
        onView(withId(R.id.browseeventsbtn)).perform(click());

        // wait for event data to load and be visible
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);


        // click on event listed
        onView(withText("Charity Event")).perform(click());


        // click on event, and sign up for event
        onView(withId(R.id.eventdet_frag)).check(matches(isDisplayed()));
        onView(withId(R.id.eventname_text)).check(matches(withText("Charity Event")));
        onView(withId(R.id.signupbtn)).perform(click());
        //onView(withText("Sign Up Successful")).inRoot(new ToastMatcher())
              //  .check(matches(isDisplayed()));


    }

    // test announcements page fragment
    @Test
    public void testAnnouncementsList() {

        idlingResource.increment();
        // click on attendee button
        onView(withId(R.id.attendeebtn)).perform(click());

        // wait for event data to load and be visible
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        onView(withId(R.id.messages2)).perform(click());

        // Check if the announcements fragment  is displayed
        onView(withId(R.id.announce_frag)).check(matches(isDisplayed()));
    }



}