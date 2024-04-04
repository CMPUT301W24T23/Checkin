package com.example.checkin;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
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

import org.junit.After;
import org.junit.Before;
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

public class AttendeeViewTest {

    @Mock
    private Database mockDatabase;

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule permissionRule2 = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);


    private ViewIdlingResource idlingResource = new ViewIdlingResource(R.id.progress);
    @Test
    public void testchangeattendee(){

        idlingResource.increment();
        onView(withId(R.id.attendeebtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        // Check if the attendee view is displayed
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));


        IdlingRegistry.getInstance().unregister(idlingResource);


    }

    @Test
    public void testbackbutton(){

        idlingResource.increment();

        // Perform click action to navigate to the attendee view
        onView(withId(R.id.attendeebtn)).perform(click());
        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));

        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);
        // click on back button


        // click on back button
        onView(withId(R.id.backbtn)).check(matches(isDisplayed())).perform(click());


        // check if it goes to homepage
        onView(withId(R.id.main_activity_page)).check(matches(isDisplayed()));

        IdlingRegistry.getInstance().unregister(idlingResource);


    }


    @Test
    public void testeventinfo(){
        // Define a mock event list
        Event event = new Event("Test Event1", "1234");
        ArrayList<Event> mockEventList = new ArrayList<>();
        mockEventList.add(event);



        idlingResource.increment();
        onView(withId(R.id.attendeebtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);


        EventArrayAdapter arrayadapter = new EventArrayAdapter(InstrumentationRegistry.getInstrumentation().getTargetContext(), mockEventList);
        scenario.getScenario().onActivity(activity -> {
            View rootView = activity.findViewById(android.R.id.content);

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            AttendeeFragment1 fragment = (AttendeeFragment1) fragmentManager.findFragmentByTag("attendee_fragment_tag");
            if (fragment != null && fragment.getView() != null) {

                ListView listView = fragment.getView().findViewById(R.id.events);
                listView.setAdapter(arrayadapter);
            }
        });




       // doReturn(mockEventList).when(mockDatabase).updateEvent(event);

        //onData(instanceOf(Event.class))
             //   .inAdapterView(withId(R.id.events))
               // .atPosition(1)
               // .perform(click());


        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.events)).perform(click());
        onView(withId(R.id.eventdet_frag)).check(matches(isDisplayed()));
        onView(withId(R.id.eventname_text)).check(matches(withText("Test Event1")));

    }



    @Test
    public void testAnnouncementsList() {
        idlingResource.increment();
        onView(withId(R.id.attendeebtn)).perform(click());

        // Wait for the progress bar to be displayed
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.atten_view)).check(matches(isDisplayed()));

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        onView(withId(R.id.messages2)).perform(click());

        // Check if the announcements fragment is displayed
        onView(withId(R.id.announce_frag)).check(matches(isDisplayed()));
    }








}