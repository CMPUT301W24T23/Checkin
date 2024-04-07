package com.example.checkin;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.Manifest;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Or;

import java.util.ArrayList;


// Tests for organizer perspective of app
// https://developer.android.com/training/testing/espresso/idling-resource
// https://stackoverflow.com/a/41638243
@RunWith(AndroidJUnit4.class)
@LargeTest

public class OrganizerViewTest {

    @Rule
    public GrantPermissionRule permissionRule2 = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    private ViewIdlingResource idlingResource = new ViewIdlingResource(R.id.progress);


    // test if it switches to organizer perspective
    @Test
    public void testchangeorganizer(){
        // start idling resource
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());
        // Wait for the progress bar to be displayed
       // onView(withId(R.id.progress)).check(matches(isDisplayed()));

        // start idling resource
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        // unregister idling resource
        IdlingRegistry.getInstance().unregister(idlingResource);

        // check if switches to organizer view
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));
    }


    // tests back button
    @Test
    public void testbackbutton(){

        idlingResource.increment();
        // Perform click action to navigate to the organizer view
        onView(withId(R.id.organizerbtn)).perform(click());
        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));

        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // set up idling resource so data loads
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on back button
        onView(withId(R.id.backbtn)).check(matches(isDisplayed())).perform(click());

        // check if it goes to homepage
        onView(withId(R.id.main_activity_page)).check(matches(isDisplayed()));

    }


    // tests messages fragment
    @Test
    public void testMessages() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on messages from nav bar
        onView(withId(R.id.messages)).perform(click());

        // Check if the announcements options fragment is displayed
        onView(withId(R.id.messageslisted_frag)).check(matches(isDisplayed()));

    }

    // test milestones fragment
    @Test
    public void testMilestones() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on messages from navbar
        onView(withId(R.id.messages)).perform(click());

        // wait for data to load
        idlingResource.increment();
        // Check if the announcements fragment is displayed
        onView(withId(R.id.messageslisted_frag)).check(matches(isDisplayed()));
        // click on view milestones button
        onView(withId(R.id.milestonebtn)).perform(click());
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);
        // check if milestone fragment is displayed
        onView(withId(R.id.milestones_frag)).check(matches(isDisplayed()));

    }

    // test sending notifications fragment
    @Test
    public void testNotitificationFragment() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());


        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // click on messages from navbar
        onView(withId(R.id.messages)).perform(click());

        idlingResource.increment();
        // Check if the announcements fragment is displayed
        onView(withId(R.id.messageslisted_frag)).check(matches(isDisplayed()));
        // click on send notification button
        onView(withId(R.id.sendmssgbtn)).perform(click());
        // wait for event data to load
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);
        // check if select event to send message to fragment is displayed
        onView(withId(R.id.selectmessagesfrag)).check(matches(isDisplayed()));

    }

    // test attendee options fragment
    @Test
    public void testAttendees() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());


        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        //click on attendees button from nav bar
        onView(withId(R.id.attendees)).perform(click());

        // Check if the events for selecting attendee options fragment is displayed
        onView(withId(R.id.chooseeventfrag)).check(matches(isDisplayed()));
    }

    // add event to events list for testing
    public void addevent(){
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        // Wait for the progress bar to be displayed
        //onView(withId(R.id.progress)).check(matches(isDisplayed()));
        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        ArrayList<Event> events = new ArrayList<>();
        Event mockevent = new Event("1234");
        mockevent.setEventname("BasketBall Game");
        mockevent.setEventDetails("Starts early");
        mockevent.setEventDate("2024-09-08");
        mockevent.setLocation("Gym");
        mockevent.setEventTime("4:00");
        mockevent.setQrcodeid("12345abc");
        events.add(mockevent);


        // Launch OrganizerViewActivity to add mockevent
        ActivityScenario<OrganizerView> organizerActivityScenario = ActivityScenario.launch(OrganizerView.class);

        EventArrayAdapter arrayadapter = new EventArrayAdapter(InstrumentationRegistry.getInstrumentation().getTargetContext(), events);
        organizerActivityScenario.onActivity(activity -> {

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            OrganizerFragment1 fragment = (OrganizerFragment1) fragmentManager.findFragmentByTag("organizer_fragment_tag");
            fragment.addEvent(mockevent);
            ListView listView = fragment.getView().findViewById(R.id.events);
            listView.setAdapter(arrayadapter);
        });
        idlingResource.increment();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        onView(withText("BasketBall Game")).perform(click());

    }

    // test sharing qr code
    @Test
    public void testshareqrcode(){


        // add event for testinf
        addevent();

        // check if it switches to event details page
        onView(withId(R.id.eventdet_org)).check(matches(isDisplayed()));
        // click on event qr code button
        onView(withId(R.id.codebtn)).perform(click());
        // check if share qr code fragment is displayed
        onView(withId(R.id.sharecode_frag)).check(matches(isDisplayed()));
        // check if qr code image is displayed
        onView(withId(R.id.imageCode)).check(matches(isDisplayed()));
    }

    // test viewing signed up attendees
    @Test
    public void testseeattendees(){

        addevent();
        // check if it switches to event details page
        onView(withId(R.id.eventdet_org)).check(matches(isDisplayed()));

        // click on event attendees page
        onView(withId(R.id.attendeeslistbtn)).perform(click());
        // check if it shows attendee options
        onView(withId(R.id.attendeeslisted_frag)).check(matches(isDisplayed()));

        // click on signed up attendees
        onView(withId(R.id.signedinbtn)).perform(click());
        // check to see signed up list of attendees
        onView(withId(R.id.signinlist_frag)).check(matches(isDisplayed()));

    }

    // test checking geolocation tracking
    @Test
    public void testGeoLocationTracking() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // Click on the button to add an event poster
        onView(withId(R.id.addeventbtn)).perform(click());

        // click on the CheckBox to enable GeoLocation tracking
        onView(withId(R.id.checkbox_geo_tracking)).perform(click());
        // assuming there is a button or switch for enabling GeoLocation tracking
        // click on the button/switch to enable GeoLocation tracking
        //onView(withId(R.id.checkbox_geo_tracking)).perform(click());

        // check if the CheckBox is checked
        onView(withId(R.id.checkbox_geo_tracking)).check(matches(isChecked()));
    }


    @Test
    public void testAddEventPoster() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // Click on the button to add an event poster
        onView(withId(R.id.addeventbtn)).perform(click());

        // Click on the button to add an event poster
        onView(withId(R.id.btnAddPoster)).perform(click());


        //onView(withId(R.id.ivEventPoster)).perform(click());

        // Check if the ImageView for the event poster is displayed
        //onView(withId(R.id.ivEventPoster)).check(matches(isDisplayed()));
    }

    // test adding event
    @Test
    public void testAddEvent() {
        idlingResource.increment();
        // click on organizer button
        onView(withId(R.id.organizerbtn)).perform(click());

        onView(withId(R.id.org_view)).check(matches(isDisplayed()));

        // wait for event data to load
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IdlingRegistry.getInstance().register(idlingResource);
        idlingResource.decrement();
        idlingResource.reset();
        IdlingRegistry.getInstance().unregister(idlingResource);

        // Click on the button to add an event poster
        onView(withId(R.id.addeventbtn)).perform(click());

        // Check if the add event is displayed
        onView(withId(R.id.createfragment)).check(matches(isDisplayed()));

    }

    // Test for Event Name
    @Test
    public void testEventName() {
        onView(withId(R.id.organizerbtn)).perform(click());
        onView(withId(R.id.addeventbtn)).perform(click());
        onView(withId(R.id.etEventName)).perform(ViewActions.typeText("Test Event"));
        onView(withId(R.id.etEventName)).check(matches(withText("Test Event")));
    }

    // Test for Event Time
    @Test
    public void testEventTime() {
        onView(withId(R.id.organizerbtn)).perform(click());
        onView(withId(R.id.addeventbtn)).perform(click());
        onView(withId(R.id.etEventTime)).perform(click());
        // Assume the time picker is set to 12:00 by default
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etEventTime)).check(matches(withText("12:00")));
    }

    // Test for Event Date
    @Test
    public void testEventDate() {
        onView(withId(R.id.organizerbtn)).perform(click());
        onView(withId(R.id.addeventbtn)).perform(click());
        onView(withId(R.id.etEventDate)).perform(click());
        // Assume the date picker is set to a specific date (e.g., "Sep 8, 2024") by default
        onView(withText("OK")).perform(click());
        onView(withId(R.id.etEventDate)).check(matches(withText("Sep 8, 2024")));
    }


    // Test for Event Details
    @Test
    public void testEventDetails() {
        onView(withId(R.id.organizerbtn)).perform(click());
        onView(withId(R.id.addeventbtn)).perform(click());
        onView(withId(R.id.etEventdetails)).perform(ViewActions.typeText("This is a test event."));
        onView(withId(R.id.etEventdetails)).check(matches(withText("This is a test event.")));
    }

    // Test for Event Location
    @Test
    public void testEventLocation() {
        onView(withId(R.id.organizerbtn)).perform(click());
        onView(withId(R.id.addeventbtn)).perform(click());
        onView(withId(R.id.etlocation)).perform(ViewActions.typeText("Test Location"));
        onView(withId(R.id.etlocation)).check(matches(withText("Test Location")));
    }

}
